package org.heg.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.heg.ai.Assistant;
import org.heg.ai.BlockingAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.heg.ai.Assistant.*;

@Service
public class JJChatService {

    private static final Logger LOG = LoggerFactory.getLogger(JJChatService.class);

    private final ObjectProvider<ContentRetriever> contentRetrieverProvider;
    private final boolean ragEnabled;
    private final Resource referenceDocument;

    public JJChatService(
            ObjectProvider<ContentRetriever> contentRetrieverProvider,
            @Value("${app.rag.enabled:true}") boolean ragEnabled,
            @Value("classpath:data/blanche-jaune.md") Resource referenceDocument) {
        this.contentRetrieverProvider = contentRetrieverProvider;
        this.ragEnabled = ragEnabled;
        this.referenceDocument = referenceDocument;
    }

    public Flux<String> composeAnswer(String query) {

        StreamingChatModel streamingChatModel = OpenAiStreamingChatModel.builder()
                .apiKey(API_KEY)
                .modelName(CHAT_MODEL_NAME)
                .baseUrl(URL)
                .timeout(Duration.ofMinutes(2))
                .maxTokens(1024)
                .temperature(0.0)
                .logRequests(false)
                .logResponses(false)
                .build();

        // Build the chatbot thanks to the AIService builder
        // The chatbot must be in streaming mode with memory and RAC activated with the
        // previous contentRetriever
        var assistantBuilder = AiServices.builder(Assistant.class).streamingChatModel(streamingChatModel);
        if (ragEnabled) {
            assistantBuilder.contentRetriever(sourceAwareRetriever(contentRetrieverProvider.getObject(), source -> {}));
        }
        Assistant assistant = assistantBuilder.build();

        // Send a prompt
        LOG.info("💬: %s".formatted(query));

        TokenStream tokenStream = ragEnabled
                ? assistant.chat(query)
                : assistant.askWithContext(readReferenceContent(), query);

        LOG.info("🤖: Response en cours...");
        return Flux.create(emitter -> {
            tokenStream
                    .onCompleteResponse(response -> {
                        emitter.complete();
                        LOG.info("🤖: Response done");
                    })
                    .onPartialResponse(emitter::next)
                    .onError(emitter::error)
                    .start();
        });
    }

    public ChatAnswer composeAnswerSync(String query) {
        var chatModel = OpenAiChatModel.builder()
                .apiKey(API_KEY)
                .modelName(CHAT_MODEL_NAME)
                .baseUrl(URL)
                .timeout(Duration.ofMinutes(2))
                .maxTokens(1024)
                .temperature(0.0)
                .logRequests(false)
                .logResponses(false)
                .build();

        LOG.info("💬: %s".formatted(query));
        String response;
        List<String> sources;
        var assistantBuilder = AiServices.builder(BlockingAssistant.class).chatModel(chatModel);
        if (ragEnabled) {
            Set<String> retrievedSources = new LinkedHashSet<>();
            assistantBuilder.contentRetriever(sourceAwareRetriever(
                    contentRetrieverProvider.getObject(), retrievedSources::add));
            response = assistantBuilder.build().chat(query);
            sources = List.copyOf(retrievedSources);
        } else {
            response = assistantBuilder.build().askWithContext(readReferenceContent(), query);
            sources = List.of("blanche-jaune.md");
        }

        LOG.info("🤖: Response done");
        return new ChatAnswer(response, sources);
    }

    private ContentRetriever sourceAwareRetriever(
            ContentRetriever contentRetriever, Consumer<String> onSourceRetrieved) {
        return query -> contentRetriever.retrieve(query).stream()
                .map(content -> {
                    String source = content.textSegment().metadata().getString("source");
                    if (source == null || source.isBlank()) {
                        return content;
                    }

                    onSourceRetrieved.accept(source);
                    TextSegment segment = content.textSegment();
                    TextSegment sourcedSegment = TextSegment.from(
                            "[Source: " + source + "]\n" + segment.text(), segment.metadata());
                    return Content.from(sourcedSegment, content.metadata());
                })
                .toList();
    }

    private String readReferenceContent() {
        try (InputStream input = referenceDocument.getInputStream()) {
            return "[Source: blanche-jaune.md]\n" + new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read the blanche-jaune.md reference document", e);
        }
    }
}

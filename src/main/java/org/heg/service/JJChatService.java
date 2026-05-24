package org.heg.service;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.heg.ai.Assistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.heg.ai.Assistant.*;

@Service
public class JJChatService {

    private static final Logger LOG = LoggerFactory.getLogger(JJChatService.class);

    final ContentRetriever contentRetriever;

    public JJChatService(ContentRetriever contentRetriever) {
        this.contentRetriever = contentRetriever;
    }

    public Flux<String> composeAnswer(String query) {

        StreamingChatModel streamingChatModel = OpenAiStreamingChatModel.builder()
                .apiKey(API_KEY)
                .modelName(FREE_CHAT_MODEL_NAME)
                .baseUrl(URL)
                .maxTokens(512)
                .temperature(0.0)
                .logRequests(false)
                .logResponses(false)
                .build();

        // Build the chatbot thanks to the AIService builder
        // The chatbot must be in streaming mode with memory and RAC activated with the
        // previous contentRetriever
        Assistant assistant = AiServices.builder(Assistant.class)
                .streamingChatModel(streamingChatModel)
                .contentRetriever(contentRetriever)
                .build();

        // Send a prompt
        LOG.info("💬: %s".formatted(query));

        TokenStream tokenStream = assistant.chat(query);

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
}

package org.heg.service;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.heg.ai.Assistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.heg.ai.Assistant.*;

@Service
public class ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

    public Flux<String> composeAnswer(String query) {

        StreamingChatModel streamingChatModel = OpenAiStreamingChatModel.builder()
                .apiKey(API_KEY)
                .modelName(FREE_CHAT_MODEL_NAME_NEMOTRON)
                .baseUrl(URL)
                .maxTokens(512)
                .temperature(0.0)
                .logRequests(false)
                .logResponses(false)
                .build();

        // Create the memory store "in memory"
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // Build the chatbot thanks to the AIService builder
        Assistant assistant = AiServices.builder(Assistant.class)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
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

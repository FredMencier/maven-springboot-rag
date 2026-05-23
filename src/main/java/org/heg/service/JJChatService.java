package org.heg.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import org.heg.ai.Assistant;
import org.heg.dto.TechniquesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static org.heg.ai.Assistant.*;

@Service
public class JJChatService {

    private static final Logger LOG = LoggerFactory.getLogger(JJChatService.class);

    final ContentRetriever contentRetriever;

    public JJChatService(ContentRetriever contentRetriever) {
        this.contentRetriever = contentRetriever;
    }

    public void composeAnswer(String query) {

        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(API_KEY)
                .modelName(FREE_CHAT_MODEL_NAME)
                .baseUrl(URL)
                .maxTokens(512)
                .temperature(0.0)
                .logRequests(false)
                .logResponses(false)
                .build();

        // Create the memory store "in memory"
        //ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // Build the chatbot thanks to the AIService builder
        // The chatbot must be in streaming mode with memory and RAC activated with the
        // previous contentRetriever
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                //.chatMemory(chatMemory)
                .contentRetriever(contentRetriever)
                .build();

        // Send a prompt
        LOG.info("💬: %s".formatted(query));
        TechniquesResponse techniquesResponse = assistant.chat(query);

        LOG.info("🤖: %s".formatted(techniquesResponse.toString()));
    }
}

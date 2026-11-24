package org.heg.service;

import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LangChain4jHttpClientInitializationTest {

    @Test
    void buildsOpenAiChatClientWithoutTheSpringRestClientStarter() {
        assertDoesNotThrow(() -> OpenAiChatModel.builder()
                .apiKey("test-key")
                .modelName("openai/gpt-6-luna-pro")
                .baseUrl("https://api.kilo.ai/api/gateway/")
                .build());
    }

    @Test
    void buildsOllamaEmbeddingClientWithoutTheSpringRestClientStarter() {
        assertDoesNotThrow(() -> OllamaEmbeddingModel.builder()
                .modelName("qwen3-embedding")
                .baseUrl("http://localhost:11434")
                .build());
    }
}

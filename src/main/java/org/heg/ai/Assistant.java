package org.heg.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface Assistant {

    String CHAT_MODEL_NAME = "openai/gpt-6-luna-pro";
    String RAG_MODEL_NAME = "qwen3-embedding";
    String OLLAMA_URL = System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://localhost:11434");
    String URL = "https://api.kilo.ai/api/gateway/";

    String API_KEY = System.getenv("API_KEY") != null
            ? System.getenv("API_KEY")
            : "YOUR_API_KEY_HERE";

    @SystemMessage(fromResource = "system-prompt.md")
    TokenStream chat(String message);

    @SystemMessage(fromResource = "system-prompt.md")
    @UserMessage("""
            Voici le document de référence :
            ---
            {{fileContent}}
            ---

            Question : {{question}}
            """)
    TokenStream askWithContext(@V("fileContent") String fileContent, @V("question") String question);
}

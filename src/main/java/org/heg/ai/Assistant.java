package org.heg.ai;

import dev.langchain4j.service.SystemMessage;
import org.heg.dto.TechniquesResponse;

public interface Assistant {

    String CHAT_MODEL_NAME = "claude-sonnet-4.6";
    String FREE_CHAT_MODEL_NAME = "nemotron-3-nano-omni-30b-a3b-reasoning:free";
    String RAG_MODEL_NAME = "text-embedding-3-small";
    String URL = "https://api.kilo.ai/api/gateway/";

    String API_KEY = System.getenv("API_KEY") != null
            ? System.getenv("API_KEY")
            : "YOUR_API_KEY_HERE";


    @SystemMessage("Tu es un assistant virtuel specialiste du Judo et du Jujitsu. Tu réponds aux questions.")
    TechniquesResponse chat(String message);
}

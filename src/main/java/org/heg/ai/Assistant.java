package org.heg.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface Assistant {

    String CHAT_MODEL_NAME = "claude-sonnet-4.6";
    String FREE_CHAT_MODEL_NAME_NEMOTRON = "nemotron-3-nano-omni-30b-a3b-reasoning:free";
    String URL = "https://api.kilo.ai/api/gateway/";

    String API_KEY = System.getenv("API_KEY") != null
            ? System.getenv("API_KEY")
            : "YOUR_API_KEY_HERE";


    @SystemMessage("Tu réponds aux questions en francais en parlant comme Napoleon.")
    TokenStream chat(String message);
}

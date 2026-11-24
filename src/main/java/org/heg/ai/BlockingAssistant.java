package org.heg.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface BlockingAssistant {

    @SystemMessage(fromResource = "system-prompt.md")
    String chat(String message);

    @SystemMessage(fromResource = "system-prompt.md")
    @UserMessage("""
            Voici le document de référence :
            ---
            {{fileContent}}
            ---

            Question : {{question}}
            """)
    String askWithContext(@V("fileContent") String fileContent, @V("question") String question);
}

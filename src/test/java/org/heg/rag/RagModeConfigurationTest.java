package org.heg.rag;

import dev.langchain4j.rag.content.retriever.ContentRetriever;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class RagModeConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(JJRAGConfig.class);

    @Test
    void disablesVectorIndexWhenRagModeIsFalse() {
        contextRunner.withPropertyValues("app.rag.enabled=false").run(context ->
                assertThat(context).doesNotHaveBean(ContentRetriever.class));
    }
}

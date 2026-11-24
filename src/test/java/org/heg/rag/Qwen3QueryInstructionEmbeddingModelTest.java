package org.heg.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class Qwen3QueryInstructionEmbeddingModelTest {

    @Test
    void addsTaskInstructionToQueries() {
        EmbeddingModel delegate = mock(EmbeddingModel.class);
        Qwen3QueryInstructionEmbeddingModel model = new Qwen3QueryInstructionEmbeddingModel(delegate);

        model.embed("Quelles sont les techniques de hanche ?");

        verify(delegate).embed("Instruct: Given a French question about judo or jujitsu, "
                + "retrieve relevant passages from French documents that answer it.\n"
                + "Query:Quelles sont les techniques de hanche ?");
    }

    @Test
    void embedsDocumentsWithoutAnInstructionPrefix() {
        EmbeddingModel delegate = mock(EmbeddingModel.class);
        Qwen3QueryInstructionEmbeddingModel model = new Qwen3QueryInstructionEmbeddingModel(delegate);
        TextSegment segment = TextSegment.from("O-goshi est une technique de hanche.");

        model.embedAll(List.of(segment));

        verify(delegate).embedAll(List.of(segment));
    }
}

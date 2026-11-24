package org.heg.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.util.List;

final class Qwen3QueryInstructionEmbeddingModel implements EmbeddingModel {

    private static final String QUERY_INSTRUCTION =
            "Given a French question about judo or jujitsu, retrieve relevant passages from French documents that answer it.";

    private final EmbeddingModel delegate;

    Qwen3QueryInstructionEmbeddingModel(EmbeddingModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public Response<Embedding> embed(String text) {
        return delegate.embed("Instruct: " + QUERY_INSTRUCTION + "\nQuery:" + text);
    }

    @Override
    public Response<Embedding> embed(TextSegment textSegment) {
        return delegate.embed(textSegment);
    }

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        return delegate.embedAll(textSegments);
    }

    @Override
    public int dimension() {
        return delegate.dimension();
    }

    @Override
    public String modelName() {
        return delegate.modelName();
    }
}

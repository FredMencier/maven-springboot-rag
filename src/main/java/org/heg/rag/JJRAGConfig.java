package org.heg.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.heg.service.JJChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static org.heg.ai.Assistant.*;

@Configuration
public class JJRAGConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JJChatService.class);

    @Bean
    public ContentRetriever computeRAG() {
        long startRagCompute = System.currentTimeMillis();
        // Load the document and split it into chunks
        DocumentParser documentParser = new TextDocumentParser();
        Path p = Paths.get("src/main/resources/techniques.md").toAbsolutePath();

        Document document = loadDocument(p.toString(), documentParser);
        DocumentSplitter splitter = DocumentSplitters.recursive(8000, 50);

        List<TextSegment> segments = splitter.split(document);

        // Do the embeddings with AI Endpoint model using OpenAI compatibility and store
        // them in an in memory embedding store
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(API_KEY)
                .modelName(RAG_MODEL_NAME)
                .baseUrl(URL)
                .build();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        // Store the vectors in the in memory store, see
        // https://docs.langchain4j.dev/integrations/embedding-stores/in-memory
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);

        EmbeddingStoreContentRetriever embeddingStoreContentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.1)
                .build();

        LOG.info("RAG compute in %s seconds".formatted((System.currentTimeMillis() - startRagCompute) / 1000));
        return embeddingStoreContentRetriever;
    }
}

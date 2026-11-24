package org.heg.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;
import static org.heg.ai.Assistant.*;

@Configuration
public class JJRAGConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JJRAGConfig.class);

    //https://github.com/devrel-workshop/ai-as-lib-workshop/blob/main/solutions/chatbot/java/java-langchain4j/ImageGenerationMCPChatbot.java

    @Bean
    @ConditionalOnProperty(prefix = "app.rag", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ContentRetriever computeRAG() {
        long startRagCompute = System.currentTimeMillis();

        // Recupère le chemin des pdfDocuments
        Path p = Paths.get("src/main/resources/data").toAbsolutePath();

        // Compute embeddings locally with Ollama and store them in memory.
        EmbeddingModel embeddingModel = new Qwen3QueryInstructionEmbeddingModel(
                OllamaEmbeddingModel.builder()
                        .modelName(RAG_MODEL_NAME)
                        .baseUrl(OLLAMA_URL)
                        .build());

        // Store the vectors in the in memory store, see
        // https://docs.langchain4j.dev/integrations/embedding-stores/in-memory
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        DocumentParser tikaDocParser = new ApacheTikaDocumentParser();
        List<Document> allDocuments = loadDocuments(p.toString(), tikaDocParser);
        if (allDocuments.isEmpty()) {
            throw new IllegalStateException("No RAG documents found in " + p);
        }

        DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);
        int totalSegments = 0;
        for (Document document : allDocuments) {
            String source = document.metadata().getString("file_name");
            if (source != null && !source.isBlank()) {
                document.metadata().put("source", source);
            }
            List<TextSegment> segments = splitter.split(document);
            totalSegments += segments.size();
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            embeddingStore.addAll(embeddings, segments);
            LOG.info("Document loaded: %s".formatted(document.metadata()));
        }
        LOG.info("Indexed {} documents into {} segments", allDocuments.size(), totalSegments);

        EmbeddingStoreContentRetriever embeddingStoreContentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.65)
                .build();

        LOG.info("RAG compute in %s seconds".formatted((System.currentTimeMillis() - startRagCompute) / 1000));
        return embeddingStoreContentRetriever;
    }
}

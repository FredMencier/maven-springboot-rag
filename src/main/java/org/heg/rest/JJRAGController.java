package org.heg.rest;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.heg.service.JJChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

@RestController
public class JJRAGController {

    private static final Logger LOG = LoggerFactory.getLogger(JJRAGController.class);

    final JJChatService JJChatService;

    public JJRAGController(JJChatService JJChatService) {
        this.JJChatService = JJChatService;
    }

    @PostMapping(path = "/JudoJourney/ask", produces = "application/json")
    public Flux<String> comnposeAnswer(@RequestBody String query) {
        if (query == null || query.isEmpty()) {
            LOG.error("Query is empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query is empty");
        }
        try {
            return JJChatService.composeAnswer(query);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping(path = "/JudoJourney/ask/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatAnswerResponse composeAnswerSync(@RequestBody String query) {
        if (query == null || query.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query is empty");
        }
        try {
            var chatAnswer = JJChatService.composeAnswerSync(query);
            Parser parser = Parser.builder().build();
            Node document = parser.parse(chatAnswer.answer());
            HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).build();
            return new ChatAnswerResponse(renderer.render(document), chatAnswer.sources());
        } catch (Exception e) {
            LOG.error("Synchronous answer generation failed", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

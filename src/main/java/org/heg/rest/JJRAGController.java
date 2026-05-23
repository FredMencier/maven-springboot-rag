package org.heg.rest;

import org.heg.service.JJChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class JJRAGController {

    private static final Logger LOG = LoggerFactory.getLogger(JJRAGController.class);

    final JJChatService JJChatService;

    public JJRAGController(JJChatService JJChatService) {
        this.JJChatService = JJChatService;
    }

    @PostMapping(path = "/JudoJourney/ask", produces = "application/json")
    public void comnposeAnswer(@RequestBody String query) {
        if (query == null || query.isEmpty()) {
            LOG.error("Query is empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query is empty");
        }
        try {
            JJChatService.composeAnswer(query);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

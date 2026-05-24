package org.heg.rest;

import org.heg.service.ChatService;
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
public class ChatController {

    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);

    final ChatService ChatService;

    public ChatController(ChatService ChatService) {
        this.ChatService = ChatService;
    }

    @PostMapping(path = "/chat/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> composeAnswer(@RequestBody String query) {
        if (query == null || query.isEmpty()) {
            LOG.error("Query is empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query is empty");
        }
        try {
            return ChatService.composeAnswer(query);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

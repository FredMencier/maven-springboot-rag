package org.heg.rest;

import java.util.List;

public record ChatAnswerResponse(String answerHtml, List<String> sources) {
}

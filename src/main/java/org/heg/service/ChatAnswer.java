package org.heg.service;

import java.util.List;

public record ChatAnswer(String answer, List<String> sources) {
}

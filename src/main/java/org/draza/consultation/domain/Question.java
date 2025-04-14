package org.draza.consultation.domain;

import java.util.List;

public record Question(int id, String question, List<Answer> answers) {
}

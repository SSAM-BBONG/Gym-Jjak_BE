package com.ssambbong.gymjjak.chatbot.application.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record RoutinePreferenceContext(
        String goal,
        String daysPerWeek,
        String location,
        String pendingQuestion,
        List<ChatbotQuickReply> quickReplies
) {
    public static RoutinePreferenceContext empty() {
        return new RoutinePreferenceContext(null, null, null, null, List.of());
    }

    public static RoutinePreferenceContext fromJson(ObjectMapper objectMapper, String value) {
        try {
            return objectMapper.readValue(value, RoutinePreferenceContext.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid routine preference context", exception);
        }
    }

    public RoutinePreferenceContext apply(String questionId, String value) {
        if (!questionId.equals(pendingQuestion) || quickReplies.stream().noneMatch(reply -> reply.value().equals(value))) {
            throw new IllegalArgumentException("Invalid quick reply selection");
        }
        return switch (questionId) {
            case "ROUTINE_GOAL" -> new RoutinePreferenceContext(value, daysPerWeek, location, null, List.of());
            case "ROUTINE_DAYS_PER_WEEK" -> new RoutinePreferenceContext(goal, value, location, null, List.of());
            case "ROUTINE_LOCATION" -> new RoutinePreferenceContext(goal, daysPerWeek, value, null, List.of());
            default -> throw new IllegalArgumentException("Unsupported routine question");
        };
    }

    public RoutinePreferenceContext withPendingQuickReplies(List<ChatbotQuickReply> replies) {
        String questionId = replies.isEmpty() ? null : replies.get(0).questionId();
        return new RoutinePreferenceContext(goal, daysPerWeek, location, questionId, List.copyOf(replies));
    }

    public String toJson(ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot serialize routine preference context", exception);
        }
    }
}

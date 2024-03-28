package com.example.profile.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum ErrorMessage {

    ENTITY_NOT_FOUND("Entity with %s not found"),
    FORBIDDEN("You are not allowed to do that");

    private final String message;

    public String formatWith(Object... params) {
        return message.formatted(params);
    }
}

package com.example.profile.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum ErrorMessage {

    ENTITY_NOT_FOUND("Entity %s not found"),
    ALREADY_FOLLOWING("Profile %s is already following profile %s"),
    NOT_FOLLOWING("Profile %s isn't following profile %s");

    private final String message;

    public String formatWith(Object... params) {
        return message.formatted(params);
    }
}

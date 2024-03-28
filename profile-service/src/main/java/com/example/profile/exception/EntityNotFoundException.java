package com.example.profile.exception;

import static com.example.profile.message.ErrorMessage.ENTITY_NOT_FOUND;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Object entityProperty) {
        super(ENTITY_NOT_FOUND.formatWith(entityProperty));
    }
}

package com.example.profile.exception;

import static com.example.profile.message.ErrorMessage.FORBIDDEN;

public class ActionNotAllowedException extends RuntimeException {
    public ActionNotAllowedException() {
        super(FORBIDDEN.message());
    }
}

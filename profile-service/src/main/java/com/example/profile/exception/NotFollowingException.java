package com.example.profile.exception;

import static com.example.profile.message.ErrorMessage.NOT_FOLLOWING;

public class NotFollowingException extends RuntimeException {

    public <T> NotFollowingException(T followeeId, T profileId) {
        super(NOT_FOLLOWING.formatWith(followeeId, profileId));
    }
}

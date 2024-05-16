package com.example.profile.exception;

import static com.example.profile.message.ErrorMessage.ALREADY_FOLLOWING;

public class AlreadyFollowingException extends RuntimeException {

    public <T> AlreadyFollowingException(T followeeId, T profileId) {
        super(ALREADY_FOLLOWING.formatWith(followeeId, profileId));
    }
}

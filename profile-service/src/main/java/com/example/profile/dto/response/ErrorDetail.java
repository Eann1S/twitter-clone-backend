package com.example.profile.dto.response;

public record ErrorDetail(
        String field,
        String message
) {
}

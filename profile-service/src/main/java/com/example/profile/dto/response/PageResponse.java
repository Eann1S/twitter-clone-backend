package com.example.profile.dto.response;

import lombok.Getter;

import java.util.List;

public record PageResponse<T>(
        @Getter
        List<T> content,
        int totalPages,
        int totalElements,
        int number,
        int size,
        boolean last,
        boolean first,
        boolean empty
) {
}

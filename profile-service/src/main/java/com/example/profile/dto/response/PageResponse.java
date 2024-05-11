package com.example.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageResponse<T> {

    private List<T> content;
    private int totalPages;
    private int totalElements;
    private int number;
    private int size;
    private boolean last;
    private boolean first;
    private boolean empty;

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public int getTotalElements() {
        return content.size();
    }
}

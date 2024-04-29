package com.example.profile.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateProfileRequest(
        @Email(message = "{email.invalid}") @NotBlank(message = "{email.not-blank}")
        String email,

        @Size(min = 5, max = 25, message = "{username.size}") @NotBlank(message = "{username.not-blank}")
        String username,

        @NotNull(message = "{joinDate.not-null}") LocalDate joinDate
) {

}

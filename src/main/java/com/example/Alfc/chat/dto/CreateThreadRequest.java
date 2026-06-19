package com.example.Alfc.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateThreadRequest(
        @NotBlank String username
) {}

package com.ssambbong.gymjjak.part.presentation.api.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePartRequest(@NotBlank String name) {}

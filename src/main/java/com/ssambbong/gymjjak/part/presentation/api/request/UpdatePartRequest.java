package com.ssambbong.gymjjak.part.presentation.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePartRequest(@NotBlank String name) {}

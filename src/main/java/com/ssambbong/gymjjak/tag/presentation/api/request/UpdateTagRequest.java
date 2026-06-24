package com.ssambbong.gymjjak.tag.presentation.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateTagRequest(@NotBlank String name) {}

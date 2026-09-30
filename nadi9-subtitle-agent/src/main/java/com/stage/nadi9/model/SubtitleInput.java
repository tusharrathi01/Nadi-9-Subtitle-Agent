package com.stage.nadi9.model;

import jakarta.validation.constraints.NotBlank;

public record SubtitleInput(
        @NotBlank String subtitleId,
        @NotBlank String sourceIn,
        @NotBlank String sourceOut,
        @NotBlank String speaker,
        @NotBlank String sceneId,
        @NotBlank String sourceText
) {}

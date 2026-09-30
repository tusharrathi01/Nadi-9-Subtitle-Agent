package com.stage.nadi9.model;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CorrectionRequest(
        @NotBlank String correctionId,
        @NotBlank String term,
        @NotBlank String newMeaning,
        @NotBlank String reason,
        List<String> affectedEvidenceIds
) {}

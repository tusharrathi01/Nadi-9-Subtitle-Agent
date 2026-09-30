package com.stage.nadi9.model;

import java.util.List;

public record CorrectionResult(
        String correctionId,
        String status,
        List<String> impactedSubtitleIds,
        String message
) {}

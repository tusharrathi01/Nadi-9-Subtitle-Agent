package com.stage.nadi9.model;

import java.util.List;

public record SubtitleDecision(
        String subtitleId,
        String sourceText,
        String nadi9Text,
        double confidence,
        String decision,
        String confidenceReason,
        List<EvidenceReference> evidence,
        List<String> assumptions,
        List<String> conflicts,
        List<VerificationFinding> findings,
        String reviewQuestion,
        String sourceIn,
        String sourceOut
) {}

package com.stage.nadi9.model;

public record VerificationFinding(
        String code,
        String severity,
        String message
) {}

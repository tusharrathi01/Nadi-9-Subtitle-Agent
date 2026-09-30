package com.stage.nadi9.model;

public record EvidenceItem(
        String id,
        String sourceType,
        String title,
        String author,
        String date,
        String scope,
        double reliability,
        String text
) {}

package com.stage.nadi9.provider;

import java.util.List;

public record ProviderResult(
        String nadi9Text,
        double confidence,
        String confidenceReason,
        List<String> assumptions,
        List<String> conflicts,
        String reviewQuestion
) {}

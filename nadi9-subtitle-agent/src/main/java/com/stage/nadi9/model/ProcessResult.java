package com.stage.nadi9.model;

import java.util.List;

public record ProcessResult(
        String runId,
        String releaseRecommendation,
        List<SubtitleDecision> decisions,
        RunState runState
) {}

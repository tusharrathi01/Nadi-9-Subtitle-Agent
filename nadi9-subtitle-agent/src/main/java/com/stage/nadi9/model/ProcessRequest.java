package com.stage.nadi9.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ProcessRequest(
        String episodeId,
        @NotEmpty @Valid List<SubtitleInput> subtitles
) {}

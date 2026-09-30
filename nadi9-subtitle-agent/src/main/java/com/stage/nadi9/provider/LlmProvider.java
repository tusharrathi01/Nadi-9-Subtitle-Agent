package com.stage.nadi9.provider;

import com.stage.nadi9.model.EvidenceItem;
import com.stage.nadi9.model.SubtitleInput;

import java.util.List;

public interface LlmProvider {
    ProviderResult decide(SubtitleInput input, List<EvidenceItem> evidence);
}

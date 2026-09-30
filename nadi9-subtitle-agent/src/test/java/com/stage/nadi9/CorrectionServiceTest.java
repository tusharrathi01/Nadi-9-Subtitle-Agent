package com.stage.nadi9;

import com.stage.nadi9.model.CorrectionRequest;
import com.stage.nadi9.model.ProcessRequest;
import com.stage.nadi9.model.SubtitleInput;
import com.stage.nadi9.model.CorrectionResult;
import com.stage.nadi9.provider.MockLlmProvider;
import com.stage.nadi9.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CorrectionServiceTest {

    private SubtitleAgentService service;

    @BeforeEach
    void setUp() {
        EvidenceRepository repo = new EvidenceRepository(new ObjectMapper());
        service = new SubtitleAgentService(
                new EvidenceRetriever(repo),
                new MockLlmProvider(),
                new Verifier()
        );
    }

    @Test
    void correctionIdentifiesImpactedSubtitle() {
        service.process(new ProcessRequest("EP1", List.of(
                new SubtitleInput("S1", "00:00:01", "00:00:03", "A", "SC1", "Where are you going?")
        )));

        CorrectionResult result = service.applyCorrection(new CorrectionRequest(
                "C1", "tira", "new-form", "linguist correction", List.of("expert:E01")
        ));

        assertEquals("REPLAN_REQUIRED", result.status());
        assertEquals(List.of("S1"), result.impactedSubtitleIds());
    }
}

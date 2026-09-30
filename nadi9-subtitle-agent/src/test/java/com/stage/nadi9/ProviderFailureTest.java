package com.stage.nadi9;

import com.stage.nadi9.model.*;
import com.stage.nadi9.provider.*;
import com.stage.nadi9.service.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProviderFailureTest {

    @Test
    void providerFailureAbstains() {
        EvidenceRepository repo = new EvidenceRepository(new com.fasterxml.jackson.databind.ObjectMapper());

        LlmProvider failing = (input, evidence) -> {
            throw new ProviderException("simulated outage");
        };

        SubtitleAgentService service = new SubtitleAgentService(
                new EvidenceRetriever(repo),
                failing,
                new Verifier()
        );

        ProcessResult result = service.process(new ProcessRequest("EP1", List.of(
                new SubtitleInput("S1", "00:00:01", "00:00:03", "A", "SC1", "Where are you going?")
        )));

        assertEquals("HUMAN_REVIEW_REQUIRED", result.releaseRecommendation());
        assertEquals("HUMAN_REVIEW", result.decisions().get(0).decision());
        assertTrue(result.runState().getEvents().stream()
                .anyMatch(e -> e.startsWith("provider_failure")));
    }
}

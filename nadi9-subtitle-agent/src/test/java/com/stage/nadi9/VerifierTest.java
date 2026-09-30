package com.stage.nadi9;

import com.stage.nadi9.model.*;
import com.stage.nadi9.service.Verifier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VerifierTest {

    private final Verifier verifier = new Verifier();

    @Test
    void detectsConflict() {
        SubtitleDecision decision = new SubtitleDecision(
                "S1", "elder brother", "ama-ru", 0.5,
                "HUMAN_REVIEW", "conflict", List.of(),
                List.of(), List.of("A vs B"), List.of(),
                "Which form?", "00:00:00", "00:00:02"
        );

        List<VerificationFinding> findings = verifier.verify(decision, List.of());
        assertTrue(findings.stream().anyMatch(f -> f.code().equals("CONFLICTING_EVIDENCE")));
    }

    @Test
    void detectsUnsupportedTerm() {
        SubtitleDecision decision = new SubtitleDecision(
                "S2", "purple", "purple", 0.9,
                "AUTO_DRAFT", "guess", List.of(),
                List.of(), List.of(), List.of(),
                "", "00:00:00", "00:00:02"
        );

        List<VerificationFinding> findings = verifier.verify(decision, List.of());
        assertTrue(findings.stream().anyMatch(f -> f.code().equals("UNSUPPORTED_TERM")));
    }
}

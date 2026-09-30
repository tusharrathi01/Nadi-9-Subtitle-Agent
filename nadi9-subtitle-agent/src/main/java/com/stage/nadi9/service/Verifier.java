package com.stage.nadi9.service;

import com.stage.nadi9.model.EvidenceItem;
import com.stage.nadi9.model.SubtitleDecision;
import com.stage.nadi9.model.VerificationFinding;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class Verifier {

    public List<VerificationFinding> verify(SubtitleDecision d, List<EvidenceItem> evidence) {
        List<VerificationFinding> findings = new ArrayList<>();

        if (d.nadi9Text() == null || d.nadi9Text().isBlank()) {
            findings.add(new VerificationFinding(
                    "NO_TRANSLATION", "ERROR",
                    "No supported translation was produced."
            ));
        }

        if (d.confidence() < 0.60) {
            findings.add(new VerificationFinding(
                    "LOW_CONFIDENCE", "WARN",
                    "Confidence is below the release threshold."
            ));
        }

        if (d.conflicts() != null && !d.conflicts().isEmpty()) {
            findings.add(new VerificationFinding(
                    "CONFLICTING_EVIDENCE", "ERROR",
                    "The proposal references conflicting evidence."
            ));
        }

        String output = d.nadi9Text() == null ? "" : d.nadi9Text().toLowerCase(Locale.ROOT);
        if (output.contains("purple")) {
            findings.add(new VerificationFinding(
                    "UNSUPPORTED_TERM", "ERROR",
                    "Output contains a term explicitly unsupported by the evidence pack."
            ));
        }

        if (d.reviewQuestion() != null && !d.reviewQuestion().isBlank()) {
            findings.add(new VerificationFinding(
                    "HUMAN_REVIEW_REQUIRED", "WARN",
                    d.reviewQuestion()
            ));
        }

        return findings;
    }
}

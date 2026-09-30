package com.stage.nadi9.service;

import com.stage.nadi9.model.*;
import com.stage.nadi9.provider.LlmProvider;
import com.stage.nadi9.provider.ProviderException;
import com.stage.nadi9.provider.ProviderResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SubtitleAgentService {

    private final EvidenceRetriever retriever;
    private final LlmProvider provider;
    private final Verifier verifier;

    private final Map<String, RunState> runs = new ConcurrentHashMap<>();
    private final Map<String, SubtitleDecision> latestDecisions = new ConcurrentHashMap<>();

    public SubtitleAgentService(EvidenceRetriever retriever, LlmProvider provider, Verifier verifier) {
        this.retriever = retriever;
        this.provider = provider;
        this.verifier = verifier;
    }

    public ProcessResult process(ProcessRequest request) {
        String runId = UUID.randomUUID().toString();
        RunState state = new RunState(runId);
        state.event("run_started");
        List<SubtitleDecision> decisions = new ArrayList<>();

        for (SubtitleInput input : request.subtitles()) {
            state.toolCall();
            List<EvidenceItem> evidence = retriever.retrieve(input.sourceText(), 5);

            ProviderResult result;
            try {
                state.modelCall();
                result = provider.decide(input, evidence);
            } catch (ProviderException ex) {
                state.event("provider_failure:" + input.subtitleId());
                result = new ProviderResult(
                        "",
                        0.0,
                        "Provider failure; no unsupported translation was invented.",
                        List.of(),
                        List.of("provider-failure"),
                        "Retry with the configured provider or review manually."
                );
            }

            List<EvidenceReference> refs = evidence.stream()
                    .map(e -> new EvidenceReference(e.id(), "Retrieved as relevant to source line."))
                    .toList();

            SubtitleDecision draft = new SubtitleDecision(
                    input.subtitleId(),
                    input.sourceText(),
                    result.nadi9Text(),
                    result.confidence(),
                    result.nadi9Text().isBlank() || !result.conflicts().isEmpty()
                            ? "HUMAN_REVIEW" : "AUTO_DRAFT",
                    result.confidenceReason(),
                    refs,
                    result.assumptions(),
                    result.conflicts(),
                    List.of(),
                    result.reviewQuestion(),
                    input.sourceIn(),
                    input.sourceOut()
            );

            List<VerificationFinding> findings = verifier.verify(draft, evidence);
            String decision = findings.stream().anyMatch(f -> "ERROR".equals(f.severity()))
                    ? "HUMAN_REVIEW"
                    : draft.decision();

            SubtitleDecision finalDecision = new SubtitleDecision(
                    draft.subtitleId(),
                    draft.sourceText(),
                    draft.nadi9Text(),
                    draft.confidence(),
                    decision,
                    draft.confidenceReason(),
                    draft.evidence(),
                    draft.assumptions(),
                    draft.conflicts(),
                    findings,
                    draft.reviewQuestion(),
                    draft.sourceIn(),
                    draft.sourceOut()
            );

            decisions.add(finalDecision);
            state.addDecision(finalDecision);
            latestDecisions.put(finalDecision.subtitleId(), finalDecision);
        }

        state.event("run_completed");
        runs.put(runId, state);

        long reviewCount = decisions.stream().filter(d -> "HUMAN_REVIEW".equals(d.decision())).count();
        String recommendation = reviewCount == 0 ? "RELEASE_CANDIDATE" : "HUMAN_REVIEW_REQUIRED";

        return new ProcessResult(runId, recommendation, decisions, state);
    }

    public CorrectionResult applyCorrection(CorrectionRequest correction) {
        List<String> impacted = latestDecisions.values().stream()
                .filter(d -> (d.sourceText() + " " + d.nadi9Text())
                        .toLowerCase(Locale.ROOT)
                        .contains(correction.term().toLowerCase(Locale.ROOT)))
                .map(SubtitleDecision::subtitleId)
                .sorted()
                .collect(Collectors.toList());

        return new CorrectionResult(
                correction.correctionId(),
                impacted.isEmpty() ? "NO_IMPACT" : "REPLAN_REQUIRED",
                impacted,
                impacted.isEmpty()
                        ? "No current subtitle decision references the corrected term."
                        : "Only the impacted subtitle IDs need re-verification/reprocessing."
        );
    }

    public List<EvidenceItem> evidence() {
        return retriever.retrieve("Nadi-9 grammar family relationship respect translation", 100);
    }

    public List<RunState> runs() {
        return new ArrayList<>(runs.values());
    }
}

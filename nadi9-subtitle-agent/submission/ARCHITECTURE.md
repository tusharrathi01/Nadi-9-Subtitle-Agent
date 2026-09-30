# Architecture Note

## 1. Planning

The pipeline processes each subtitle independently after retrieving evidence. This keeps the correction blast radius small and allows targeted reprocessing.

## 2. Memory

`RunState` records:

- run ID
- model/tool call counts
- events
- subtitle decisions

A production version can persist this state in PostgreSQL or an event store.

## 3. Retrieval

`EvidenceRetriever` uses deterministic lexical scoring over structured evidence records. The real assignment evidence should replace the mock JSON.

This is intentionally simple for a take-home MVP. A production system could add embeddings, hybrid retrieval and reranking, but those should not override source trust rules.

## 4. Source precedence

No single source wins merely because it is larger or newer. Evidence carries:

- source type
- author
- date
- scope
- reliability

Conflicts remain visible in the decision object.

## 5. Translation

`LlmProvider` is an abstraction. The default mock provider is deterministic and evaluator-friendly. `HttpLlmProvider` is an optional OpenAI-compatible integration.

## 6. Verification

Verification is separate from generation. It checks:

- missing output
- low confidence
- conflicting evidence
- unsupported terms
- explicit review requests

This separation is important because the assignment asks for checks that can disagree with the translation step.

## 7. Abstention

If evidence is insufficient, the system returns `HUMAN_REVIEW` with a focused review question rather than fabricating a translation.

## 8. Change handling

A correction identifies impacted subtitle IDs by term reference. Only affected lines need reprocessing/reverification.

## 9. Failure recovery

Provider failures become review items. The system does not manufacture a replacement translation.

## 10. Trust boundaries

The LLM is not treated as a source of truth. The evidence pack is the authority for Nadi-9. The LLM can reason over evidence but cannot create unsupported facts.

## 11. Scaling

For thousands of episodes:

- persist evidence and run state
- use hybrid retrieval
- cache stable evidence retrieval
- queue subtitle batches
- maintain evidence/version IDs
- calculate dependency graphs from claims to subtitles
- reprocess only impacted nodes

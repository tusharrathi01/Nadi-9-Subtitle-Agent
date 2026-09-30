# Nadi-9 Evidence-Grounded Subtitle Agent

A Spring Boot 3.2.5 reference implementation for the STAGE-style **"Learn a Dialect That Does Not Exist"** challenge.

## Important scope note

The assignment PDF was available, but the separate Nadi-9 evidence pack described by the brief (20 approved examples, 5 audio interviews, grammar note, Dictionary A/B, viewer feedback, expert notes, and episode package) was **not present as separate uploaded files in this conversation**.

Therefore this repository includes a **small clearly-labeled mock evidence pack** so the project is runnable. Replace `src/main/resources/evidence/` with the real company-supplied evidence pack before submission.

Do not present the mock evidence as company-provided evidence.

## What this implementation demonstrates

- Source inventory and precedence metadata
- Evidence retrieval with source/type/keyword scoring
- Structured language hypotheses
- Evidence-grounded subtitle decisions
- Independent verification
- Abstention / `HUMAN_REVIEW`
- Conflict detection
- Unsupported-term detection
- Correction event and targeted reprocessing
- Tool/provider failure handling
- Provider abstraction with deterministic mock mode
- Optional OpenAI-compatible HTTP provider
- Structured audit/run state
- JSONL subtitle decisions
- SRT output
- Review queue
- Final release recommendation
- JUnit tests for the required failure classes

The assignment emphasizes reasoning under uncertainty, evidence, verification, safe abstention, change handling and auditability rather than fluent translation.

## Architecture

```text
                   +----------------------+
                   | POST /api/v1/process |
                   +----------+-----------+
                              |
                              v
                    +------------------+
                    | EvidenceLoader   |
                    +--------+---------+
                             |
                             v
                    +------------------+
                    | EvidenceRetriever|
                    +--------+---------+
                             |
                             v
                    +------------------+
                    | Planning/Memory  |
                    +--------+---------+
                             |
                             v
                    +------------------+
                    | LLM Provider     |
                    | Mock / HTTP      |
                    +--------+---------+
                             |
                             v
                    +------------------+
                    | Independent      |
                    | Verifier         |
                    +----+---------+---+
                         |         |
                    PASS |         | REVIEW
                         v         v
                    SRT/JSONL   Review Queue
                         |
                         v
                  Final Recommendation
```

## Run

### Requirements

- Java 17+
- Maven 3.9+

### Start with deterministic mock provider

```bash
mvn spring-boot:run
```

The default provider is `mock`, so no API key is needed.

### Process the sample episode

```bash
curl -X POST http://localhost:8080/api/v1/process \
  -H "Content-Type: application/json" \
  --data @sample_run/request.json
```

### Health

```bash
curl http://localhost:8080/api/v1/health
```

## Optional LLM provider

The code contains a generic OpenAI-compatible HTTP provider. Configure:

```bash
NADI9_PROVIDER=http
NADI9_LLM_URL=http://localhost:11434/v1/chat/completions
NADI9_LLM_MODEL=your-model
NADI9_LLM_API_KEY=
```

For the take-home, the evaluator should be able to use mock/replay mode without your personal API key.

## Main endpoints

### POST `/api/v1/process`

Processes an episode and returns:

- subtitle decisions
- evidence references
- assumptions
- conflicts
- verification findings
- review queue
- final release recommendation

### POST `/api/v1/corrections`

Applies a correction to a language rule and identifies/reprocesses only affected subtitles.

Example:

```json
{
  "correctionId": "CORR-001",
  "term": "tira",
  "newMeaning": "informal you",
  "reason": "Linguist correction",
  "affectedEvidenceIds": ["expert:E03"]
}
```

### GET `/api/v1/evidence`

Returns loaded evidence metadata.

### GET `/api/v1/hypotheses`

Returns current language hypotheses.

### GET `/api/v1/health`

Health/status endpoint.

## Project structure

```text
src/main/java/com/stage/nadi9/
  Nadi9Application.java
  controller/
  model/
  service/
  provider/
  repository/
  verification/

src/main/resources/
  application.yml
  evidence/

src/test/java/com/stage/nadi9/
  EvidenceRetrieverTest.java
  VerifierTest.java
  CorrectionServiceTest.java
  ProviderFailureTest.java

sample_run/
  request.json
  subtitles.srt
  subtitle_decisions.jsonl
  learned_rules.json
  review_queue.json
  final_report.md

submission/
  README.md
  ARCHITECTURE.md
  AI_COLLABORATION.md
  KNOWN_LIMITATIONS.md
```

## Evaluation mapping

| Assignment requirement | Implementation |
|---|---|
| Runnable CLI/API | Spring Boot REST API |
| Structured run state | `RunState`, JSON outputs |
| Evidence references | `EvidenceReference` |
| Provider abstraction/mock | `LlmProvider`, `MockLlmProvider`, `HttpLlmProvider` |
| Conflict test | `VerifierTest` |
| Unsupported term test | `VerifierTest` |
| Correction event | `CorrectionServiceTest` |
| Tool failure | `ProviderFailureTest` |
| Sample run | `sample_run/` |
| Architecture note | `submission/ARCHITECTURE.md` |
| AI collaboration log | `submission/AI_COLLABORATION.md` |
| Known limitations | `submission/KNOWN_LIMITATIONS.md` |

## Security/privacy

The project does not upload evidence automatically. The default mode is deterministic mock mode. If an external LLM provider is enabled, review its retention/training policy before sending assignment material.

## Before submitting

1. Replace mock evidence with the actual supplied Nadi-9 evidence pack.
2. Run the full test suite.
3. Run one real sample end-to-end.
4. Inspect every generated decision.
5. Record what AI tools actually helped you with in `AI_COLLABORATION.md`.
6. Do not claim that mock evidence is company evidence.
7. Add screenshots of your own successful run if useful.

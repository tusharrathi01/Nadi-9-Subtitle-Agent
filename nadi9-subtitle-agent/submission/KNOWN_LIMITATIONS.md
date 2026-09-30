# Known Limitations

1. The repository contains mock evidence because the actual Nadi-9 evidence pack was not present in the files available to this build.
2. Retrieval is lexical rather than embedding-based.
3. The HTTP LLM provider assumes an OpenAI-compatible JSON response.
4. Correction impact analysis is term-based in this MVP.
5. Subtitle reading-speed validation is intentionally minimal.
6. Audio transcription is represented by the provider abstraction; no speech-to-text engine is bundled.
7. Run state is in-memory.
8. The mock provider contains only a few deterministic examples.

Before submission, replace mock evidence and mock outputs with the actual assignment pack and run the complete end-to-end flow.

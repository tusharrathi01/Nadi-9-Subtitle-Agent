package com.stage.nadi9.provider;

import com.stage.nadi9.model.EvidenceItem;
import com.stage.nadi9.model.SubtitleInput;

import java.util.List;
import java.util.Locale;

public class MockLlmProvider implements LlmProvider {

    @Override
    public ProviderResult decide(
            SubtitleInput input,
            List<EvidenceItem> evidence) {

        String text = input.sourceText()
                .toLowerCase(Locale.ROOT);

        if (text.contains("where are you going")) {
            return new ProviderResult(
                    "Nako tira vesh?",
                    0.86,
                    "Direct support from mock dictionary and grammar evidence.",
                    List.of(
                            "Speaker is using a neutral relationship form."
                    ),
                    List.of(),
                    ""
            );
        }

        if (text.contains("elder brother")) {
            return new ProviderResult(
                    "Nako tira?",
                    0.54,
                    "The mock evidence contains conflicting kinship guidance.",
                    List.of(
                            "Speaker addresses an older sibling."
                    ),
                    List.of(
                            "dictionary:A:term-44 vs dictionary:B:term-19"
                    ),
                    "Which kinship form applies in this relationship?"
            );
        }

        if (text.contains("purple")) {
            return new ProviderResult(
                    "",
                    0.18,
                    "No supported vocabulary was retrieved for the requested term.",
                    List.of(),
                    List.of(
                            "unsupported-term:purple"
                    ),
                    "What is the supported Nadi-9 term for 'purple'?"
            );
        }

        return new ProviderResult(
                "",
                0.20,
                "No sufficiently supported translation was found.",
                List.of(),
                List.of(
                        "insufficient-evidence"
                ),
                "Which evidence should be used to translate this line?"
        );
    }
}
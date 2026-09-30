package com.stage.nadi9.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stage.nadi9.model.EvidenceItem;
import com.stage.nadi9.model.SubtitleInput;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HttpLlmProvider implements LlmProvider {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final String url;
    private final String model;
    private final String apiKey;

    public HttpLlmProvider(
            String url,
            String model,
            String apiKey) {

        this.url = url;
        this.model = model;
        this.apiKey = apiKey;
    }

    @Override
    public ProviderResult decide(
            SubtitleInput input,
            List<EvidenceItem> evidence) {

        try {

            String evidenceText = evidence.stream()
                    .map(e -> e.id()
                            + " | "
                            + e.sourceType()
                            + " | "
                            + e.text())
                    .reduce("", (a, b) -> a + "\n" + b);

            String prompt = """
                    You are a cautious junior linguist.

                    Nadi-9 is fictional.
                    Use ONLY the supplied evidence.

                    Never invent vocabulary or grammar.

                    If evidence is insufficient or conflicting,
                    return an empty nadi9Text and a focused reviewQuestion.

                    Return JSON with:
                    nadi9Text,
                    confidence,
                    confidenceReason,
                    assumptions,
                    conflicts,
                    reviewQuestion.

                    SOURCE:
                    %s

                    EVIDENCE:
                    %s
                    """.formatted(
                    input.sourceText(),
                    evidenceText
            );

            String body = mapper.writeValueAsString(
                    java.util.Map.of(
                            "model",
                            model,

                            "temperature",
                            0,

                            "messages",
                            List.of(
                                    java.util.Map.of(
                                            "role",
                                            "system",
                                            "content",
                                            "Return JSON only."
                                    ),

                                    java.util.Map.of(
                                            "role",
                                            "user",
                                            "content",
                                            prompt
                                    )
                            )
                    )
            );

            HttpRequest.Builder builder =
                    HttpRequest.newBuilder(URI.create(url))
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(body)
                            );

            if (apiKey != null && !apiKey.isBlank()) {
                builder.header(
                        "Authorization",
                        "Bearer " + apiKey
                );
            }

            HttpResponse<String> response =
                    client.send(
                            builder.build(),
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() >= 400) {
                throw new ProviderException(
                        "LLM provider returned HTTP "
                                + response.statusCode()
                );
            }

            JsonNode root =
                    mapper.readTree(response.body());

            String content =
                    root.path("choices")
                            .path(0)
                            .path("message")
                            .path("content")
                            .asText();

            JsonNode result =
                    mapper.readTree(content);

            return new ProviderResult(
                    result.path("nadi9Text")
                            .asText(""),

                    result.path("confidence")
                            .asDouble(0.0),

                    result.path("confidenceReason")
                            .asText(""),

                    mapper.convertValue(
                            result.path("assumptions"),
                            mapper.getTypeFactory()
                                    .constructCollectionType(
                                            List.class,
                                            String.class
                                    )
                    ),

                    mapper.convertValue(
                            result.path("conflicts"),
                            mapper.getTypeFactory()
                                    .constructCollectionType(
                                            List.class,
                                            String.class
                                    )
                    ),

                    result.path("reviewQuestion")
                            .asText("")
            );

        } catch (Exception e) {

            throw new ProviderException(
                    "LLM provider failure: "
                            + e.getMessage()
            );
        }
    }
}
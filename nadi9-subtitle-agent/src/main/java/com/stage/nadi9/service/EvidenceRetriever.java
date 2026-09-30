package com.stage.nadi9.service;

import com.stage.nadi9.model.EvidenceItem;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvidenceRetriever {

    private final EvidenceRepository repository;

    public EvidenceRetriever(EvidenceRepository repository) {
        this.repository = repository;
    }

    public List<EvidenceItem> retrieve(String query, int limit) {
        Set<String> tokens = tokenize(query);
        return repository.load().stream()
                .map(e -> Map.entry(e, score(e, tokens)))
                .filter(x -> x.getValue() > 0)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double score(EvidenceItem e, Set<String> tokens) {
        String haystack = (e.title() + " " + e.scope() + " " + e.text()).toLowerCase(Locale.ROOT);
        long hits = tokens.stream().filter(haystack::contains).count();
        return hits + e.reliability() * 0.1;
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT)
                        .replaceAll("[^a-z0-9-]", " ")
                        .split("\\s+"))
                .filter(s -> s.length() >= 3)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}

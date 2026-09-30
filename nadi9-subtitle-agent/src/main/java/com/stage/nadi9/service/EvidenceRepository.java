package com.stage.nadi9.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stage.nadi9.model.EvidenceItem;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.List;

@Repository
public class EvidenceRepository {
    private final ObjectMapper mapper;

    public EvidenceRepository(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<EvidenceItem> load() {
        try (InputStream in = new ClassPathResource("evidence/evidence.json").getInputStream()) {
            return mapper.readValue(in, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load evidence pack", e);
        }
    }
}

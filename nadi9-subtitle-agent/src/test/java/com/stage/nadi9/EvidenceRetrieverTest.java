package com.stage.nadi9;

import com.stage.nadi9.model.EvidenceItem;
import com.stage.nadi9.service.EvidenceRepository;
import com.stage.nadi9.service.EvidenceRetriever;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceRetrieverTest {

    @Test
    void retrievesRelevantEvidence() {
        EvidenceRepository repository = new EvidenceRepository(new com.fasterxml.jackson.databind.ObjectMapper());
        EvidenceRetriever retriever = new EvidenceRetriever(repository);

        List<EvidenceItem> result = retriever.retrieve("Where are you going?", 5);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(e -> e.id().contains("where")));
    }
}

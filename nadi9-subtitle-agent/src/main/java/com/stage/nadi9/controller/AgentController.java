package com.stage.nadi9.controller;

import com.stage.nadi9.model.*;
import com.stage.nadi9.service.SubtitleAgentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AgentController {

    private final SubtitleAgentService service;

    public AgentController(SubtitleAgentService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public java.util.Map<String, Object> health() {
        return java.util.Map.of("status", "UP", "service", "nadi9-subtitle-agent");
    }

    @PostMapping("/process")
    public ProcessResult process(@Valid @RequestBody ProcessRequest request) {
        return service.process(request);
    }

    @PostMapping("/corrections")
    public CorrectionResult correction(@Valid @RequestBody CorrectionRequest request) {
        return service.applyCorrection(request);
    }

    @GetMapping("/evidence")
    public List<EvidenceItem> evidence() {
        return service.evidence();
    }
}


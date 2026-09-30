package com.stage.nadi9.model;

import java.util.ArrayList;
import java.util.List;

public class RunState {
    private final String runId;
    private int modelCalls;
    private int toolCalls;
    private final List<String> events = new ArrayList<>();
    private final List<SubtitleDecision> decisions = new ArrayList<>();

    public RunState(String runId) {
        this.runId = runId;
    }

    public String getRunId() { return runId; }
    public int getModelCalls() { return modelCalls; }
    public int getToolCalls() { return toolCalls; }
    public List<String> getEvents() { return events; }
    public List<SubtitleDecision> getDecisions() { return decisions; }

    public void modelCall() { modelCalls++; }
    public void toolCall() { toolCalls++; }
    public void event(String value) { events.add(value); }
    public void addDecision(SubtitleDecision value) { decisions.add(value); }
}

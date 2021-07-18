package org.decta.bpmnparser;

import org.jgrapht.Graph;

public interface BpmnParser {

    void drawSignalsRelations(String path);

    Graph<String, SignalNameEdge> getGraph(String path);
}

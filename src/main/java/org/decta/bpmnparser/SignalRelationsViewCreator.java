package org.decta.bpmnparser;

import org.jgrapht.Graph;

public interface SignalRelationsViewCreator {

    void drawSignalRelations(Graph<String, SignalNameEdge> graph);
}

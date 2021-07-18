package org.decta.bpmnparser;

import org.jgrapht.Graph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BpmnParserImplTest {

    private static final String PATH = "src/test/resources/bpmn";

    @Autowired
    BpmnParserImpl bpmnParser;

    @Test
    void drawSignalsRelationsWithEmptyGraph() {
        bpmnParser.drawSignalsRelations("somePathWithNoBpmnFiles");
        assertDoesNotThrow((ThrowingSupplier<Throwable>) Throwable::new);
    }

    @Test
    void drawSignalsRelations() {
        Graph<String, SignalNameEdge> graph = bpmnParser.getGraph(PATH);

        assertNotNull(graph);
        assertEquals(3, graph.vertexSet().size());
        assertTrue(graph.vertexSet().contains("AcquiringMain"));
        assertTrue(graph.vertexSet().contains("Common_Acquiring_EOD"));
        assertTrue(graph.vertexSet().contains("SendBIProcessingNotifications"));

        assertEquals(2, graph.edgeSet().size());

        Set<SignalNameEdge> edges = graph.getAllEdges("Common_Acquiring_EOD", "AcquiringMain");
        assertEquals(1, edges.size());
        for (SignalNameEdge edge : edges) {
            assertEquals("AcquiringCompleted", edge.getSignalName());
            assertEquals("Common_Acquiring_EOD", edge.getSource());
            assertEquals("AcquiringMain", edge.getTarget());
        }

        edges = graph.getAllEdges("Common_Acquiring_EOD", "SendBIProcessingNotifications");
        assertEquals(1, edges.size());
        for (SignalNameEdge edge : edges) {
            assertEquals("AcquiringCompleted", edge.getSignalName());
            assertEquals("Common_Acquiring_EOD", edge.getSource());
            assertEquals("SendBIProcessingNotifications", edge.getTarget());
        }

    }
}
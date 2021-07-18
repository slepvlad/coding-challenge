package org.decta.bpmnparser;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@Import({
        SignalRelationsViewCreatorImpl.class
})
@TestPropertySource(properties = {"path.output=src/test/resources/graph.png"})
class SignalRelationsViewCreatorImplTest {

    private static final String PATH = "src/test/resources/graph.png";

    @Autowired
    SignalRelationsViewCreator viewCreator;

    @Test
    void drawSignalRelations() {
        File imgFile = new File(PATH);
        imgFile.deleteOnExit();
        Graph<String, SignalNameEdge> graph = new DefaultDirectedGraph<>(SignalNameEdge.class);
        graph.addVertex("vertex1");
        graph.addVertex("vertex2");
        graph.addEdge("vertex1", "vertex2", new SignalNameEdge("signalName"));
        viewCreator.drawSignalRelations(graph);
        assertTrue(imgFile.exists());
    }
}
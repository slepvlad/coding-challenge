package org.decta.bpmnparser;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class SignalRelationsViewCreatorImpl implements SignalRelationsViewCreator{
    private final Logger logger = LoggerFactory.getLogger(BpmnParserImpl.class);

    @Value("${path.output}")
    private String outputPath;

    @Override
    public void drawSignalRelations(Graph<String, SignalNameEdge> graph) {
        logger.info("Start drawing graph");
        if(graph.vertexSet().isEmpty() || graph.edgeSet().isEmpty()) {
            logger.info("Grath is empty. There is nothing to draw");
            return;
        }
        try {
            JGraphXAdapter<String, SignalNameEdge> graphAdapter = new JGraphXAdapter<>(graph);
            mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
            layout.execute(graphAdapter.getDefaultParent());

            BufferedImage image =
                    mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
            File imgFile = new File(outputPath);
            imgFile.createNewFile();
            ImageIO.write(image, "PNG", imgFile);
            logger.info("Created graph scheme in the path {}", outputPath);
        } catch (IOException e) {
            logger.error("Error drawing graph with cause {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

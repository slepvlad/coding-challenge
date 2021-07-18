package org.decta;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BpmnParserImpl implements BpmnParser {

    private static final String BPMN_PROCESS = "process";
    private static final String SIGNAL_EVENT_DEFINITION = "signalEventDefinition";
    private static final String SIGNAL_REF = "signalRef";

    private final FileReader fileReader;

    public BpmnParserImpl(FileReader fileReader) {
        this.fileReader = fileReader;
    }


    @Override
    public List<Signal> parse(String path) {
        List<File> files = fileReader.readFile(path);
        List<Signal> signals = new ArrayList<>();
        for (File file : files) {
            try {
                SAXBuilder sax = new SAXBuilder();
                Document doc = sax.build(file);
                Element rootNode = doc.getRootElement();
                List<Content> list = rootNode.getContent();
                for (Content content : list) {
                    if (content instanceof Element element) {
                        signals.addAll(parseElement(element, file.getName()));
                    }
                }

            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return signals;
    }

    @Override
    public void getMatchedSignals(List<Signal> signals) {
        Map<String, List<Signal>> map = signals.stream()
                .collect(Collectors.groupingBy(Signal::signalName));
        draw(map);
    }

    private void draw(Map<String, List<Signal>> signalNameToSignals) {
        Graph<String, SignalNameEdge> graph = new DefaultDirectedGraph<>(SignalNameEdge.class);
        for (String signalName : signalNameToSignals.keySet()) {
            List<Signal> inputSignals = new ArrayList<>();
            List<Signal> outputSignals = new ArrayList<>();
            signalNameToSignals.get(signalName)
                    .forEach(signal -> {
                        if (Direction.INPUT.equals(signal.direction())) {
                            inputSignals.add(signal);
                        }
                        if (Direction.OUTPUT.equals(signal.direction())) {
                            outputSignals.add(signal);
                        }
                    });

            if (inputSignals.size() > 0 && outputSignals.size() > 0) {
                signalNameToSignals.get(signalName).forEach(signal -> {
                    graph.addVertex(signal.processorName());
                });

                for (Signal output : outputSignals) {
                    for (Signal input : inputSignals) {
                        graph.addEdge(output.processorName(), input.processorName(), new SignalNameEdge(signalName));
                    }
                }
            }
        }
        draw(graph);
    }

    private void draw(Graph<String, SignalNameEdge> graph) {
        try {
            JGraphXAdapter<String, SignalNameEdge> graphAdapter = new JGraphXAdapter<>(graph);
            mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
            layout.execute(graphAdapter.getDefaultParent());

            BufferedImage image =
                    mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
            File imgFile = new File("src/main/resources/graph.png");
            imgFile.createNewFile();
            ImageIO.write(image, "PNG", imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Signal> parseElement(Element element, String fileName) {
        List<Signal> signals = new ArrayList<>();
        if (BPMN_PROCESS.equals(element.getName())) {
            List<Content> processContentList = element.getContent();
            for (Content processContent : processContentList) {
                if (processContent instanceof Element processElement) {
                    List<Element> elements = getSignalElements(processElement);
                    for (Element signalElement : elements) {
                        Direction direction = Direction.getDirection(((Element) processContent).getName());
                        Signal signal = new Signal(fileName, signalElement.getAttributeValue(SIGNAL_REF), direction);
                        signals.add(signal);
                    }
                }
            }
        }
        return signals;
    }

    private List<Element> getSignalElements(Element processElement) {
        return processElement.getContent().stream()
                .filter(item -> item instanceof Element)
                .map(item -> (Element) item)
                .filter(item -> BpmnParserImpl.SIGNAL_EVENT_DEFINITION.equals((item).getName()))
                .collect(Collectors.toList());
    }
}

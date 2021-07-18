package org.decta.bpmnparser;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BpmnParserImpl implements BpmnParser {

    private final Logger logger = LoggerFactory.getLogger(BpmnParserImpl.class);

    private static final String BPMN_PROCESS = "process";
    private static final String SIGNAL_EVENT_DEFINITION = "signalEventDefinition";
    private static final String SIGNAL_REF = "signalRef";

    private final FileReader fileReader;
    private final SignalRelationsViewCreator viewCreator;

    public BpmnParserImpl(FileReader fileReader, SignalRelationsViewCreator viewCreator) {
        this.fileReader = fileReader;
        this.viewCreator = viewCreator;
    }

    @Override
    public void drawSignalsRelations(String path) {
        Graph<String, SignalNameEdge> graph = getGraph(path);
        viewCreator.drawSignalRelations(graph);
    }

    @Override
    public Graph<String, SignalNameEdge> getGraph(String path) {
        List<Signal> signals = parse(path);
        return createGraph(signals);
    }

    private List<Signal> parse(String path) {
        logger.info("Start parse files ....");
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
                        signals.addAll(getSignals(element, file.getName()));
                    }
                }

            } catch (Throwable e) {
                logger.error("Error parsing files with cause: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        logger.info("Found {} signals {}", signals.size(), signals);
        return signals;
    }

    private Graph<String, SignalNameEdge> createGraph(List<Signal> signals) {
        logger.info("Start creating graph");
        Map<String, List<Signal>> signalNameToSignals = signals.stream()
                .collect(Collectors.groupingBy(Signal::signalName));

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
        logger.info("Graph created: {}", graph);
        return graph;
    }

    private List<Signal> getSignals(Element element, String fileName) {
        List<Signal> signals = new ArrayList<>();
        if (BPMN_PROCESS.equals(element.getName())) {
            List<Content> processContentList = element.getContent();
            for (Content processContent : processContentList) {
                if (processContent instanceof Element processElement) {
                    List<Element> elements = getSignalElements(processElement);
                    for (Element signalElement : elements) {
                        Direction direction = Direction.getDirection(((Element) processContent).getName());
                        Signal signal = new Signal(getProcessName(fileName),
                                signalElement.getAttributeValue(SIGNAL_REF),
                                direction);
                        signals.add(signal);
                    }
                }
            }
        }
        return signals;
    }

    private String getProcessName(String fileName) {
        String extPattern = "(?<!^)[.].*";
        return fileName.replaceAll(extPattern, "");
    }

    private List<Element> getSignalElements(Element processElement) {
        return processElement.getContent().stream()
                .filter(item -> item instanceof Element)
                .map(item -> (Element) item)
                .filter(item -> BpmnParserImpl.SIGNAL_EVENT_DEFINITION.equals((item).getName()))
                .collect(Collectors.toList());
    }
}

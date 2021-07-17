package org.decta;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class BpmnParserImpl implements BpmnParser {

    private static final String BPMN_PROCESS = "process";
    private static final String SIGNAL_EVENT_DEFINITION = "signalEventDefinition";

    private final FileReader fileReader;

    public BpmnParserImpl(FileReader fileReader) {
        this.fileReader = fileReader;
    }


    @Override
    public List<Signal> parse(String path) {
        List<File> files = fileReader.readFile(path);
        Map<String, Set<String>> processorNameToSignalDefinitions = new HashMap<>();
        List<Signal> signals = new ArrayList<>();
        for (File file : files) {
            //System.out.println(file.getPath());
            try {
                SAXBuilder sax = new SAXBuilder();
                Document doc = sax.build(file);
                Element rootNode = doc.getRootElement();
                List<Content> list = rootNode.getContent();
                for (Content content : list) {
                    if (content instanceof Element element) {
                        if ("signal".equals(element.getName())) {
                            Map<String, String> map = element.getAttributes()
                                    .stream()
                                    .collect(Collectors.toMap(Attribute::getName, Attribute::getValue));
                            System.out.println(map);
                            if (Objects.nonNull(map.get("scope")) && map.size() == 3) {
                                processorNameToSignalDefinitions.computeIfAbsent(file.getName(), k -> new HashSet<>());
                                processorNameToSignalDefinitions.get(file.getName()).add(map.get("id"));
                            }
                        } else if (BPMN_PROCESS.equals(element.getName())) {
                            List<Content> processContentList = element.getContent();
                            for (Content processContent : processContentList) {
                                if (processContent instanceof Element processElement) {
                                    List<Element> elements = processElement.getContent().stream()
                                            .filter(item -> item instanceof Element)
                                            .map(item -> (Element) item)
                                            .filter(item -> SIGNAL_EVENT_DEFINITION.equals((item).getName()))
                                            .collect(Collectors.toList());

                                    if (!elements.isEmpty()) {
                                        System.out.println(elements);
                                        for (Element signalElement : elements) {
                                            Direction direction = Direction.getDirection(((Element) processContent).getName());
                                            Signal signal = new Signal(file.getName(),
                                                    signalElement.getAttributeValue("signalRef"),
                                                    direction);
                                            signals.add(signal);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return signals;
    }

 /*   public void readFile(List<File> bpmnFiles, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            //ToDo fix this ugly
            if (files != null) {
                for (File value : files) {
                    readFile(bpmnFiles, value);
                }
            }
        } else {
            if (BPMN_EXTENSION.equals(getExtension(file.getName()))) {
                bpmnFiles.add(file);
            }
        }
    }

    private String getExtension(String filename) {
        if (Objects.isNull(filename) || filename.isEmpty()) {
            return EMPTY_STRING;
        }
        if (!filename.contains(".")) {
            return EMPTY_STRING;
        }

        String[] fileNames = filename.split("\\.");

        if (filename.length() < 2) {
            return EMPTY_STRING;
        }

        return fileNames[fileNames.length - 1];
    }*/
}

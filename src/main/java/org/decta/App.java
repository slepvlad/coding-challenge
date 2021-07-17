package org.decta;

import java.util.List;
import java.util.Objects;

public class App {

    private static final String PATH = "src/main/resources/bpmn";

    public static void main(String[] args) {
        String pathForScan = PATH;
        if(args.length != 0 && Objects.nonNull(args[0])) {
            pathForScan = args[0];
        }

        FileReader fileReader = new FileReaderImpl();
        BpmnParser parser = new BpmnParserImpl(fileReader);
        List<Signal> signals = parser.parse(pathForScan);
        System.out.println(signals);
    }
}

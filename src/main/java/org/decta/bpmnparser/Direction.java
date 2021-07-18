package org.decta.bpmnparser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum Direction {
    INPUT(Arrays.asList("intermediateCatchEvent", "startEvent", "boundaryEvent")),
    OUTPUT(Arrays.asList("intermediateThrowEvent"));

    private final List<String> signalDefinition;

    Direction(List<String> signalDefinition) {
        this.signalDefinition = signalDefinition;
    }

    public static Direction getDirection(String value) {
        return Stream.of(Direction.values())
                .filter(item -> item.signalDefinition.contains(value))
                .findFirst()
                .orElseThrow(() -> new IllegalCallerException("Unknown value " + value));
    }
}

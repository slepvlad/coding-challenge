package org.decta;

import java.util.List;

public interface BpmnParser {

    List<Signal> parse(String path);

    void getMatchedSignals(List<Signal> signals);
}

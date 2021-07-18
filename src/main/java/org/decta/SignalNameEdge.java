package org.decta;

import org.jgrapht.graph.DefaultEdge;

public class SignalNameEdge extends DefaultEdge {

    private final String signalName;

    public SignalNameEdge(String signalName) {
        this.signalName = signalName;
    }

    public String getSignalName() {
        return signalName;
    }

    @Override
    public String toString() {
        return "(" +  signalName + ")";
    }
}

package org.decta.bpmnparser;

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
    protected Object getSource() {
        return super.getSource();
    }

    @Override
    protected Object getTarget() {
        return super.getTarget();
    }

    @Override
    public String toString() {
        return "(" +  signalName + ")";
    }
}

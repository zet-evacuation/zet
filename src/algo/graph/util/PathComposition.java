/*
 * PathComposition.java
 *
 */
package algo.graph.util;

import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.Edge;
import ds.graph.flow.EdgeBasedFlowOverTime;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;

/**
 *
 * @author Martin Groß
 */
public class PathComposition {

    private Network network;
    private EdgeBasedFlowOverTime edgeFlows;
    private PathBasedFlowOverTime pathFlows;
    private IdentifiableIntegerMapping<Edge> transitTimes;
    private int maxFlowRate;

    public PathComposition(Network network, IdentifiableIntegerMapping<Edge> transitTimes, PathBasedFlowOverTime pathFlows) {
        this.network = network;
        this.edgeFlows = new EdgeBasedFlowOverTime(network);
        this.pathFlows = pathFlows;
        this.transitTimes = transitTimes;
        this.maxFlowRate = 0;
    }

    public void run() {
        for (FlowOverTimePath pathFlow : pathFlows) {
            addPathFlow(pathFlow);
        }
    }

    private void addPathFlow(FlowOverTimePath pathFlow) {
        Edge edge = pathFlow.getDynamicPath().first();
        int time = pathFlow.delay(edge);
        edgeFlows.get(edge).increase(time, time + pathFlow.getAmount() / pathFlow.getRate(), pathFlow.getRate());
        time += transitTime(edge);
        boolean first = true;
        for (Edge e : pathFlow.getDynamicPath()) {
            if (first) {
                first = false;
                continue;
            }
            time += pathFlow.delay(e);
            //System.out.println(e + " " + edgeFlows.get(e));
            edgeFlows.get(e).increase(time, time + pathFlow.getAmount() / pathFlow.getRate(), pathFlow.getRate());
            if (edgeFlows.get(e).get(time) > maxFlowRate){
            	maxFlowRate = edgeFlows.get(e).get(time);
            }
            time += transitTimes.get(e);
        }
    }

    public int getMaxFlowRate(){
    	return maxFlowRate;
    }
    
    public EdgeBasedFlowOverTime getEdgeFlows() {
        return edgeFlows;
    }

    private int transitTime(Edge edge) {
        return transitTimes.get(edge);
    }
}

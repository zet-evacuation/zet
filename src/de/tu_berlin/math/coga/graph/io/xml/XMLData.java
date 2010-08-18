/**
 * XMLData.java
 * Created: 30.06.2010 15:17:29
 */
package de.tu_berlin.math.coga.graph.io.xml;

import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A class that stores the data found in the XML file that is read. Thus we can
 * read files with problem specifications and solutions, containing both,
 * a graph and a layout or only a graph and so on.
 * @author Jan-Philipp Kappmeier
 */
public class XMLData {
	IdentifiableIntegerMapping<Edge> edgeCapacities;
	IdentifiableIntegerMapping<Node> nodeCapacities;
	IdentifiableIntegerMapping<Edge> transitTimes;
	IdentifiableIntegerMapping<Node> supplies;
	List<Node> sources;
	ArrayList<Node> sinks;
	LinkedHashMap<String, Node> nodes = new LinkedHashMap<String, Node>();
	LinkedHashMap<String, Edge> edges = new LinkedHashMap<String, Edge>();
	Network network;
	NodePositionMapping nodePositionMapping = new NodePositionMapping();
	boolean doubleEdges = false;
	double scaleVal;
	boolean containsSuperSink;
	GraphView graphView;

	public IdentifiableIntegerMapping<Edge> getEdgeCapacities() {
		return edgeCapacities;
	}

	public LinkedHashMap<String, Edge> getEdges() {
		return edges;
	}

	public IdentifiableIntegerMapping<Node> getNodeCapacities() {
		return nodeCapacities;
	}

	public LinkedHashMap<String, Node> getNodes() {
		return nodes;
	}

	public ArrayList<Node> getSinks() {
		return sinks;
	}

	public List<Node> getSources() {
		return sources;
	}

	public IdentifiableIntegerMapping<Node> getSupplies() {
		return supplies;
	}

	public IdentifiableIntegerMapping<Edge> getTransitTimes() {
		return transitTimes;
	}

	public Network getNetwork() {
		return network;
	}

	public boolean containsSupplies() {
		return supplies != null;
	}

	boolean containsEdgeCapacities() {
		return edgeCapacities != null;
	}

	boolean containsNodeCapacities() {
		return nodeCapacities != null;
	}

	boolean containsTransitTimes() {
		return transitTimes != null;
	}

	public boolean isContainsSuperSink() {
		return containsSuperSink;
	}

	public boolean isDoubleEdges() {
		return doubleEdges;
	}

	public NodePositionMapping getNodePositionMapping() {
		return nodePositionMapping;
	}

	public double getScaleVal() {
		return scaleVal;
	}

	public GraphView generateGraphView() {
		GraphView graphView = new GraphView( getNetwork(), getNodePositionMapping(), getEdgeCapacities(), getNodeCapacities(), getTransitTimes(), getSupplies(), getSources(), sinks );
		graphView.setScale( scaleVal );
		graphView.containsSuperSink = containsSuperSink;
		return graphView;
	}

	public GraphView getGraphView() {
		return graphView;
	}

	

}

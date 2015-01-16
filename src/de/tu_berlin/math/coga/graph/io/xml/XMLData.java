/**
 * XMLData.java
 * Created: 30.06.2010 15:17:29
 */
package de.tu_berlin.math.coga.graph.io.xml;

import de.tu_berlin.math.coga.graph.io.xml.visualization.GraphVisualization;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.container.mapping.IdentifiableDoubleMapping;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DirectedGraph;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A class that stores the data found in the XML file that is read. Thus we can
 * read files with problem specifications and solutions, containing both,
 * a graph and a layout or only a graph and so on.
 * @author Jan-Philipp Kappmeier
 */
public class XMLData {
	IdentifiableIntegerMapping<Edge> edgeCapacitiesIntegral;
	IdentifiableIntegerMapping<Node> nodeCapacitiesIntegral;
	IdentifiableIntegerMapping<Edge> transitTimesIntegral;
	IdentifiableIntegerMapping<Node> suppliesIntegral;
	IdentifiableDoubleMapping<Edge> edgeCapacities;
	IdentifiableDoubleMapping<Node> nodeCapacities;
	IdentifiableDoubleMapping<Edge> transitTimes;
	IdentifiableDoubleMapping<Node> supplies;
	List<Node> sources;
	List<Node> sinks;
	LinkedHashMap<String, Node> nodes = new LinkedHashMap<>();
	LinkedHashMap<String, Edge> edges = new LinkedHashMap<>();
	DirectedGraph network;
	NodePositionMapping nodePositionMapping = new NodePositionMapping();
	boolean doubleEdges = false;
	double scaleVal;
	boolean containsSuperSink;
	GraphVisualization graphView;

	public IdentifiableDoubleMapping<Edge> getEdgeCapacities() {
		return edgeCapacities;
	}

	public IdentifiableIntegerMapping<Edge> getEdgeCapacitiesIntegral() {
		if( edgeCapacitiesIntegral == null )
			edgeCapacitiesIntegral = new IdentifiableIntegerMapping<>( edgeCapacities );
		return edgeCapacitiesIntegral;
	}

	public LinkedHashMap<String, Edge> getEdges() {
		return edges;
	}

	public IdentifiableDoubleMapping<Node> getNodeCapacities() {
		return nodeCapacities;
	}

	public IdentifiableIntegerMapping<Node> getNodeCapacitiesIntegral() {
		if( nodeCapacitiesIntegral == null )
			nodeCapacitiesIntegral = new IdentifiableIntegerMapping<Node>( nodeCapacities );
		return nodeCapacitiesIntegral;
	}

	public LinkedHashMap<String, Node> getNodes() {
		return nodes;
	}

	public List<Node> getSinks() {
		return sinks;
	}

	public List<Node> getSources() {
		return sources;
	}

	public IdentifiableDoubleMapping<Node> getSupplies() {
		return supplies;
	}

	public IdentifiableIntegerMapping<Node> getSuppliesIntegral() {
		if( suppliesIntegral == null )
			suppliesIntegral = new IdentifiableIntegerMapping<Node>( supplies );
		return suppliesIntegral;
	}

	public IdentifiableDoubleMapping<Edge> getTransitTimes() {
		return transitTimes;
	}

	public IdentifiableIntegerMapping<Edge> getTransitTimesIntegral() {
		if( transitTimesIntegral == null )
			transitTimesIntegral = new IdentifiableIntegerMapping<Edge>( transitTimes );
		return transitTimesIntegral;
	}

	public DirectedGraph getGraph() {
		return network;
	}

	public boolean containsSupplies() {
		return suppliesIntegral != null;
	}

	boolean containsEdgeCapacities() {
		return edgeCapacitiesIntegral != null;
	}

	boolean containsNodeCapacities() {
		return nodeCapacitiesIntegral != null;
	}

	boolean containsTransitTimes() {
		return transitTimesIntegral != null;
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

	public GraphVisualization generateGraphView() {
		GraphVisualization createdGraphView = new GraphVisualization( getGraph(), getNodePositionMapping(), getEdgeCapacitiesIntegral(), getNodeCapacitiesIntegral(), getTransitTimesIntegral(), getSuppliesIntegral(), getSources(), sinks );
		createdGraphView.setScale( scaleVal );
		createdGraphView.setContainsSuperSink( containsSuperSink );
		return createdGraphView;
	}

	public GraphVisualization getGraphView() {
		return graphView;
	}
}

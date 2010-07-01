///**
// * EarliestArrivalFlowProblemConverter.java
// * Created: 30.06.2010 14:54:08
// */
//package de.tu_berlin.math.coga.graph.io.xml;
//
//import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
//import com.thoughtworks.xstream.converters.Converter;
//import com.thoughtworks.xstream.converters.MarshallingContext;
//import com.thoughtworks.xstream.converters.UnmarshallingContext;
//import com.thoughtworks.xstream.io.HierarchicalStreamReader;
//import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
//import ds.graph.Edge;
//import ds.graph.IdentifiableIntegerMapping;
//import ds.graph.Network;
//import ds.graph.Node;
//import java.util.List;
//
//
///**
// *
// * @author Jan-Philipp Kappmeier
// */
//public class EarliestArrivalFlowProblemConverter implements Converter {
//	private XMLData
//	public boolean canConvert( Class type ) {
//		return type.equals( EarliestArrivalFlowProblem.class );
//	}
//
//	public void marshal( Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2 ) {
//		throw new UnsupportedOperationException( "Not supported yet." );
//	}
//
//	public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
//		if( !reader.getNodeName().equals( "graph" ) )
//			throw new InvalidFileFormatException( "EarliestArrivalFlowProblem is a graph." );
//
//		// Convert the graph first
//		GraphConverter graphConverter = new GraphConverter( xmlData );
//		Network graph = (Network)graphConverter.unmarshal( reader, context );
//
//		if( graphConverter.getSinks().size() != 1 ) {
//			throw new InvalidFileFormatException( "Earliest arrival flow problem contains exactly 1 sink. Given: " + graphConverter.getSinks().size() );
//		}
//
//		Node sink = graphConverter.getSinks().get( 0 );
//		List<Node> sources = graphConverter.getSources();
//		IdentifiableIntegerMapping<Edge> edgeCapacities = graphConverter.getEdgeCapacities();
//		IdentifiableIntegerMapping<Node> nodeCapacities = graphConverter.getNodeCapacities();
//		IdentifiableIntegerMapping<Edge> transitTimes = graphConverter.getTransitTimes();
//		IdentifiableIntegerMapping<Node> supplies = graphConverter.getSupplies();
//
//		EarliestArrivalFlowProblem eafp = new EarliestArrivalFlowProblem( edgeCapacities, graph, nodeCapacities, sink, sources, -1, transitTimes, supplies );
//
////		reader.moveUp();
//		return eafp;
//
//	}
//
//}

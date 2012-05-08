/**
 * GraphConverterAlgorithms.java
 * Created: 31.08.2011, 11:22:01
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import de.tu_berlin.math.coga.zet.converter.graph.BaseZToGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToDijkstraSpannerConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGreedySpannerConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridClusterConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridDijkstraConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridGreedyConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridSpanTreeConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridSteinerTreeConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridAPSPGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridClusterConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridShortestPathGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridSteinerTreeConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToSpanTreeConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZtoGridShortestPathGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridShortestPathsConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToThinNetworkConverter;
/**
 * An enumeration of the different converter algorithms for graphs. They provide
 * methods to retrieve the converting algorithm objects.
 * @author Jan-Philipp Kappmeier
 */
public enum GraphConverterAlgorithms {
	GridGraph, NonGridGraph, MinSpanningTreeGrid, MinSpanningTreeNonGrid, GreedyTSpannerGrid, GreedyTSpannerNonGrid, DijkstraNonGrid, DijkstraGrid, SteinerTreeNonGrid, SteinerTreeGrid, ClusterGrid, ClusterNonGrid, ShortestPathGraphNonGrid, ShortestPathGraphGrid, APSPGraphNonGrid, RepeatedShortestPaths, ThinNetwork;

	public BaseZToGraphConverter converter() {
		switch( this ) {
			case GridGraph:
				return new ZToGridGraphConverter();
			case NonGridGraph:
				return new ZToNonGridGraphConverter();
			case MinSpanningTreeGrid:
				return new ZToGridSpanTreeConverter();
			case MinSpanningTreeNonGrid:
				return new ZToSpanTreeConverter();
			case GreedyTSpannerNonGrid:
				return new ZToGreedySpannerConverter();
			case GreedyTSpannerGrid:
				return new ZToGridGreedyConverter();
			case DijkstraNonGrid:
				return new ZToDijkstraSpannerConverter();
			case DijkstraGrid:
				return new ZToGridDijkstraConverter();
                        case SteinerTreeNonGrid:
                            return new ZToNonGridSteinerTreeConverter();
                        case SteinerTreeGrid:
                            return new ZToGridSteinerTreeConverter();
                        case ClusterNonGrid:
                            return new ZToNonGridClusterConverter();
                        case ClusterGrid:
                            return new ZToGridClusterConverter();
                        case ShortestPathGraphNonGrid:
                            return new ZToNonGridShortestPathGraphConverter();
                        case ShortestPathGraphGrid:
                            return new ZtoGridShortestPathGraphConverter();
                        case APSPGraphNonGrid:
                            return new ZToNonGridAPSPGraphConverter();
                        case RepeatedShortestPaths:
                            return new ZToNonGridShortestPathsConverter();
                        case ThinNetwork:
                            return new ZToThinNetworkConverter();
			default:
				throw new IllegalStateException( "Error! Unsupported Graph converter algorithm in enumeration: " + this.toString() );
		}
	}	
}
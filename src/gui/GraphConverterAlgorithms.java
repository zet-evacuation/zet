/**
 * GraphConverterAlgorithms.java
 * Created: 31.08.2011, 11:22:01
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.GraphShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.GreedySpannerShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.SpanningTreeShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.SteinerTreeShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.ZToDijkstraSpannerConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridClusterConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridDijkstraConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridShortestPathGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridAPSPGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridClusterConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridShortestPathGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridShortestPathsConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToThinNetworkConverter;
import ds.z.BuildingPlan;
/**
 * An enumeration of the different converter algorithms for graphs. They provide
 * methods to retrieve the converting algorithm objects.
 * @author Jan-Philipp Kappmeier
 */
public enum GraphConverterAlgorithms {
	GridGraph, NonGridGraph, MinSpanningTreeGrid, MinSpanningTreeNonGrid, GreedyTSpannerGrid, GreedyTSpannerNonGrid, DijkstraNonGrid, DijkstraGrid, SteinerTreeNonGrid, SteinerTreeGrid, ClusterGrid, ClusterNonGrid, ShortestPathGraphNonGrid, ShortestPathGraphGrid, APSPGraphNonGrid, RepeatedShortestPaths, ThinNetwork;

	public Algorithm<BuildingPlan,NetworkFlowModel> converter() {
		switch( this ) {
			case GridGraph:
				return new ZToGridGraphConverter();
			case NonGridGraph:
				return new ZToNonGridGraphConverter();
			case MinSpanningTreeNonGrid:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new SpanningTreeShrinker() );
			case MinSpanningTreeGrid:
				return new GraphShrinker( new ZToGridGraphConverter(), new SpanningTreeShrinker() );
			case GreedyTSpannerNonGrid:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new GreedySpannerShrinker() );
			case GreedyTSpannerGrid:
				return new GraphShrinker( new ZToGridGraphConverter(), new GreedySpannerShrinker() );
			case DijkstraNonGrid:
				return new ZToDijkstraSpannerConverter();
			case DijkstraGrid:
				return new ZToGridDijkstraConverter();
			case SteinerTreeNonGrid:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new SteinerTreeShrinker() );
				//return new SteinerTreeShrinker();
			case SteinerTreeGrid:
				return new GraphShrinker( new ZToGridGraphConverter(), new SteinerTreeShrinker() );
//				return new ZToGridSteinerTreeConverter();
			case ClusterNonGrid:
				return new ZToNonGridClusterConverter();
			case ClusterGrid:
				return new ZToGridClusterConverter();
			case ShortestPathGraphNonGrid:
				return new ZToNonGridShortestPathGraphConverter();
			case ShortestPathGraphGrid:
				return new ZToGridShortestPathGraphConverter();
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

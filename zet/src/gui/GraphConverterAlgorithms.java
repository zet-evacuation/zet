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
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.APSPGraphShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.ClusterShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.DijkstraSpannerShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.GraphShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.GreedySpannerShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.RepeatedShortestPathsShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.ShortestPathGraphShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.SpanningTreeShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.SteinerTreeShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridGraphConverter;
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
			case MinSpanningTreeGrid:
				return new GraphShrinker( new ZToGridGraphConverter(), new SpanningTreeShrinker() );
			case MinSpanningTreeNonGrid:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new SpanningTreeShrinker() );
			case GreedyTSpannerGrid:
				return new GraphShrinker( new ZToGridGraphConverter(), new GreedySpannerShrinker() );
			case GreedyTSpannerNonGrid:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new GreedySpannerShrinker() );
			case DijkstraGrid:
				return new GraphShrinker( new ZToGridGraphConverter(), new DijkstraSpannerShrinker() );
			case DijkstraNonGrid:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new DijkstraSpannerShrinker() );
			case SteinerTreeGrid:
				return new GraphShrinker( new ZToGridGraphConverter(), new SteinerTreeShrinker() );
			case SteinerTreeNonGrid:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new SteinerTreeShrinker() );
			case ClusterGrid:
				return new GraphShrinker( new ZToGridGraphConverter(), new ClusterShrinker() );
			case ClusterNonGrid:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new ClusterShrinker() );
			case ShortestPathGraphGrid:
				return new GraphShrinker( new ZToGridGraphConverter(), new ShortestPathGraphShrinker() );
			case ShortestPathGraphNonGrid:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new ShortestPathGraphShrinker() );
			case APSPGraphNonGrid:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new APSPGraphShrinker() );
			case RepeatedShortestPaths:
				return new GraphShrinker( new ZToNonGridGraphConverter(), new RepeatedShortestPathsShrinker() );
			case ThinNetwork:
				return new ZToThinNetworkConverter();
			default:
				throw new IllegalStateException( "Error! Unsupported Graph converter algorithm in enumeration: " + this.toString() );
		}
	}
}

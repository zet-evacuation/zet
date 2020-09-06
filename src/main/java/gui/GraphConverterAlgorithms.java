/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui;

import org.zetool.common.algorithm.AbstractAlgorithm;
import de.tu_berlin.math.coga.zet.converter.graph.APSPGraphShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.ClusterShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.GraphConverterAndShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.GreedySpannerShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.GridGraphConverter;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.RectangleConverter;
import de.tu_berlin.math.coga.zet.converter.graph.RepeatedShortestPathsShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.ShortestPathGraphShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.ShortestPathTreeShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.SpanningTreeShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.SteinerTreeShrinker;
import de.tu_berlin.math.coga.zet.converter.graph.ThinNetworkConverter;
import de.zet_evakuierung.model.BuildingPlan;

/**
 * An enumeration of the different converter algorithms for graphs. They provide
 * methods to retrieve the converting algorithm objects.
 * @author Jan-Philipp Kappmeier
 */
public enum GraphConverterAlgorithms {
	GridGraph, NonGridGraph, MinSpanningTreeGrid, MinSpanningTreeNonGrid, GreedyTSpannerGrid, GreedyTSpannerNonGrid, DijkstraNonGrid, DijkstraGrid, SteinerTreeNonGrid, SteinerTreeGrid, ClusterGrid, ClusterNonGrid, ShortestPathGraphNonGrid, ShortestPathGraphGrid, APSPGraphNonGrid, RepeatedShortestPaths, ThinNetwork;

	public AbstractAlgorithm<BuildingPlan,NetworkFlowModel> converter() {
		switch( this ) {
			case GridGraph:
				return new GridGraphConverter();
			case NonGridGraph:
				return new RectangleConverter();
			case MinSpanningTreeGrid:
				return new GraphConverterAndShrinker( new GridGraphConverter(), new SpanningTreeShrinker() );
			case MinSpanningTreeNonGrid:
				return new GraphConverterAndShrinker( new RectangleConverter(), new SpanningTreeShrinker() );
			case GreedyTSpannerGrid:
				return new GraphConverterAndShrinker( new GridGraphConverter(), new GreedySpannerShrinker() );
			case GreedyTSpannerNonGrid:
				return new GraphConverterAndShrinker( new RectangleConverter(), new GreedySpannerShrinker() );
			case DijkstraGrid:
				return new GraphConverterAndShrinker( new GridGraphConverter(), new ShortestPathTreeShrinker() );
			case DijkstraNonGrid:
				return new GraphConverterAndShrinker( new RectangleConverter(), new ShortestPathTreeShrinker() );
			case SteinerTreeGrid:
				return new GraphConverterAndShrinker( new GridGraphConverter(), new SteinerTreeShrinker() );
			case SteinerTreeNonGrid:
				return new GraphConverterAndShrinker( new RectangleConverter(), new SteinerTreeShrinker() );
			case ClusterGrid:
				return new GraphConverterAndShrinker( new GridGraphConverter(), new ClusterShrinker() );
			case ClusterNonGrid:
				return new GraphConverterAndShrinker( new RectangleConverter(), new ClusterShrinker() );
			case ShortestPathGraphGrid:
				return new GraphConverterAndShrinker( new GridGraphConverter(), new ShortestPathGraphShrinker() );
			case ShortestPathGraphNonGrid:
				return new GraphConverterAndShrinker( new RectangleConverter(), new ShortestPathGraphShrinker() );
			case APSPGraphNonGrid:
				return new GraphConverterAndShrinker( new RectangleConverter(), new APSPGraphShrinker() );
			case RepeatedShortestPaths:
				return new GraphConverterAndShrinker( new RectangleConverter(), new RepeatedShortestPathsShrinker() );
			case ThinNetwork:
				return new ThinNetworkConverter();
			default:
				throw new IllegalStateException( "Error! Unsupported Graph converter algorithm in enumeration: " + this.toString() );
		}
	}
}

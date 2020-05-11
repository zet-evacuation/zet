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
package de.tu_berlin.math.coga.zet.converter.graph;

import org.zetool.common.algorithm.AbstractAlgorithm;
import de.zet_evakuierung.model.BuildingPlan;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphConverterAndShrinker extends AbstractAlgorithm<BuildingPlan, NetworkFlowModel> {
	private final AbstractAlgorithm<BuildingPlan, NetworkFlowModel> converter;
	private final AbstractAlgorithm<NetworkFlowModel, NetworkFlowModel> shrinker;

	public GraphConverterAndShrinker( AbstractAlgorithm<BuildingPlan,NetworkFlowModel> converter, AbstractAlgorithm<NetworkFlowModel,NetworkFlowModel> shrinker ) {
		this.converter = converter;
		this.shrinker = shrinker;
	}

	@Override
	protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		converter.setProblem( problem );
		converter.run();
		shrinker.setProblem( converter.getSolution() );
		shrinker.run();
		return shrinker.getSolution();
	}
}

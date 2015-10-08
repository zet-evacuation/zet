/* zet evacuation tool copyright © 2007-15 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.batch.operations;

import org.zetool.components.batch.operations.AtomicOperation;
import org.zetool.components.batch.operations.AbstractOperation;
import org.zetool.netflow.ds.flow.MaximumFlow;
import org.zetool.components.batch.input.reader.InputFileReader;
import ds.graph.problem.RawMaximumFlowProblem;
import org.zetool.common.algorithm.Algorithm;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class MaximumFlowComputation extends AbstractOperation<RawMaximumFlowProblem,MaximumFlow> {
	InputFileReader<RawMaximumFlowProblem> input;
	AtomicOperation<RawMaximumFlowProblem, MaximumFlow> maxFlowAlgorithm;
  MaximumFlow mf;

  public MaximumFlowComputation() {
		// First, we go from zet to network flow model
		maxFlowAlgorithm = new AtomicOperation<>( "Max Flow Computation", RawMaximumFlowProblem.class, MaximumFlow.class );
		this.addOperation( maxFlowAlgorithm );
  }

	@Override
	@SuppressWarnings( "unchecked" )
	public boolean consume( InputFileReader<?> o ) {

		if( o.getTypeClass() == RawMaximumFlowProblem.class ) {
			input = (InputFileReader<RawMaximumFlowProblem>)o;
			return true;
		}
		return false;
	}

  @Override
  public Class<MaximumFlow> produces() {
    return MaximumFlow.class;
  }

  @Override
  public MaximumFlow getProduced() {
    return this.mf;
  }

	@Override
	public String toString() {
		return "Maximum Flow Computation";
	}

  @Override
  public void run() {
    System.out.println( "Load instance..." );
    input.run();
		RawMaximumFlowProblem flowInstance = input.getSolution();

		//System.out.println( flowInstance );

		if( maxFlowAlgorithm.getSelectedAlgorithm() == null ) {
			System.out.println( "No algorithm selected!");
			return;
		}
		System.out.println( "Selected algorithm: " + maxFlowAlgorithm.getSelectedAlgorithm() );

		final Algorithm<RawMaximumFlowProblem,MaximumFlow> maxFlowAlgo = maxFlowAlgorithm.getSelectedAlgorithm();
		maxFlowAlgo.setProblem( flowInstance );
		maxFlowAlgo.run();

    mf = maxFlowAlgo.getSolution();
  }


}

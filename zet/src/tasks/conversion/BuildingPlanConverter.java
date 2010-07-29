/**
 * BuildingPlanConverter.java
 * Created: Jul 28, 2010,4:41:30 PM
 */
package tasks.conversion;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.z.BuildingPlan;
import io.visualization.BuildingResults;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BuildingPlanConverter extends Algorithm<BuildingPlan, BuildingResults> {

	@Override
	protected BuildingResults runAlgorithm( BuildingPlan problem ) {
		return new BuildingResults( getProblem() );
	}

}

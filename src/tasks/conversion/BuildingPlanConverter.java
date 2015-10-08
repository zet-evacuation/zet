/**
 * BuildingPlanConverter.java
 * Created: Jul 28, 2010,4:41:30 PM
 */
package tasks.conversion;

import org.zetool.common.algorithm.AbstractAlgorithm;
import de.zet_evakuierung.model.BuildingPlan;
import io.visualization.BuildingResults;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BuildingPlanConverter extends AbstractAlgorithm<BuildingPlan, BuildingResults> {

	@Override
	protected BuildingResults runAlgorithm( BuildingPlan problem ) {
		return new BuildingResults( getProblem() );
	}

}

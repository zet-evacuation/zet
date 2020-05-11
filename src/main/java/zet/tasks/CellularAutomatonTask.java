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
package zet.tasks;

import org.zet.cellularautomaton.algorithm.EvacuationCellularAutomatonAlgorithm;
import org.zetool.common.algorithm.AbstractAlgorithm;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import org.zet.cellularautomaton.results.VisualResultsRecorder;
import io.visualization.EvacuationSimulationResults;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.statistic.CAStatistic;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonTask extends AbstractAlgorithm<EvacuationSimulationProblem, EvacuationSimulationResults> {

    EvacuationCellularAutomatonAlgorithm cellularAutomatonAlgorithm;
    MultiFloorEvacuationCellularAutomaton ca;
    ZToCAMapping mapping;
    ZToCARasterContainer container;

    public void setCaAlgo(EvacuationCellularAutomatonAlgorithm caAlgo) {
        this.cellularAutomatonAlgorithm = caAlgo;
    }

    public EvacuationCellularAutomatonAlgorithm getCellularAutomatonAlgorithm() {
        return cellularAutomatonAlgorithm;
    }

    @Override
    protected EvacuationSimulationResults runAlgorithm(EvacuationSimulationProblem esp) {
        
        log.info("Step 3: Run simulation...");
        // Step 3: Create the Algorithm
        //EvacuationCellularAutomatonAlgorithm caAlgorithm = CellularAutomatonAlgorithms.RandomOrder.getAlgorithm();
        cellularAutomatonAlgorithm.setProblem(esp);

        VisualResultsRecorder recorder = new VisualResultsRecorder(esp.getInitialConfiguration(), cellularAutomatonAlgorithm);
        
        Object solution = cellularAutomatonAlgorithm.call();
        log.info("... done step 3. (" + solution + ")");

        log.info("Collected " + recorder.getRecordedCount() + " actions during " + cellularAutomatonAlgorithm.getStep() + " steps.");
        //for( Action a : allActions) {
            //log.info(a.toString());
        //}
//        // convert cellular automaton
//        final ZToCAConverter conv = new ZToCAConverter();
//        conv.setProblem(project.getBuildingPlan());
//        conv.run();
//        ca = conv.getCellularAutomaton();
//        mapping = conv.getMapping();
//        container = conv.getContainer();
//        final ConvertedCellularAutomaton cca = new ConvertedCellularAutomaton(ca, mapping, container);
//
//        // create and convert concrete assignment
//        log.warning("Ignore setting assignment type!");
//        //for (AssignmentType at : project.getCurrentAssignment().getAssignmentTypes()) {
//        //    ca.setAssignmentType(at.getName(), at.getUid());
//        //}
//        ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment(project.getCurrentAssignment(), 400);
//        final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
//        cac.setProblem(new AssignmentApplicationInstance(cca, concreteAssignment));
//        cac.run();
//
//        // set up simulation algorithm and compute
//        EvacuationCellularAutomatonAlgorithm caAlgo = cellularAutomatonAlgorithm;
//
//        // We need:
//        //EvacuationCellularAutomaton ca = null;
//        List<Individual> individuals = null;
//        Map<Individual, EvacCellInterface> individualStartPositions = null;
//
//        EvacuationSimulationProblemImpl esp = new EvacuationSimulationProblemImpl(ca, individuals, individualStartPositions);
//        int caMaxTime = (int) Math.round(PropertyContainer.getGlobal().getAsDouble("algo.ca.maxTime"));
//        esp.setEvacuationTimeLimit(caMaxTime);
//        caAlgo.setProblem(esp);
//        //ca.startRecording();
//        // Recording is now done through a listener?
//        caAlgo.run();
//        //ca.stopRecording();
//
//        CellularAutomatonVisualizationResults caVisResults = new CellularAutomatonVisualizationResults(mapping, ca);
        EvacuationState es = cellularAutomatonAlgorithm.getEvacuationState();
        EvacuationSimulationSpeed ess = cellularAutomatonAlgorithm.getEvacuationSimulationSpeed();
        EvacuationSimulationResults evacResults = new EvacuationSimulationResults(es, ess, recorder.getRecording());

        // No statistic available
        evacResults.statistic = new CAStatistic(cellularAutomatonAlgorithm.getStatisticResults());

        return evacResults;
    }
    

    public MultiFloorEvacuationCellularAutomaton getCa() {
        return ca;
    }

    public ZToCARasterContainer getContainer() {
        return container;
    }

    public ZToCAMapping getMapping() {
        return mapping;
    }
}

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
package de.tu_berlin.math.coga.batch.operations;

import org.zetool.components.batch.operations.AtomicOperation;
import org.zetool.components.batch.operations.AbstractOperation;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationResult;
import org.zet.cellularautomaton.algorithm.SwapCellularAutomaton;
import algo.graph.exitassignment.ExitAssignment;
import org.zetool.common.algorithm.AbstractAlgorithm;
import de.zet_evakuierung.model.AssignmentType;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Project;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import org.zetool.components.batch.input.reader.InputFileReader;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.RectangleConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import ds.PropertyContainer;
import org.zet.cellularautomaton.results.VisualResultsRecorder;
import evacuationplan.BidirectionalNodeCellMapping;
import exitdistributions.GraphBasedIndividualToExitMapping;
import io.visualization.EvacuationSimulationResults;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.InitialConfiguration;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblemImpl;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zetool.common.algorithm.Algorithm;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ExitAssignmentOperation extends AbstractOperation<Project, EvacuationSimulationResults> {

    InputFileReader<Project> input;
    EvacuationSimulationResults visResults;

    // Currently unused, we use default converter and algorithms
    AtomicOperation<NetworkFlowModel, ExitAssignment> assignmentOperation;

    public ExitAssignmentOperation() {
        assignmentOperation = new AtomicOperation<>("Assignment computation", NetworkFlowModel.class, ExitAssignment.class);
        this.addOperation(assignmentOperation);
    }

    @Override
    public boolean consume(InputFileReader<?> o) {
        if (o.getTypeClass() == Project.class) {
            input = (InputFileReader<Project>) o;
            return true;
        }
        return false;
    }

    @Override
    public Class<EvacuationSimulationResults> produces() {
        return EvacuationSimulationResults.class;
    }

    @Override
    public EvacuationSimulationResults getProduced() {
        return visResults;
    }

    @Override
    public void run() {
        Project project = input.getSolution();
        //todo
        // step 1: compute an exit assignment
        // here we use plugins to select type of exit assignment

        final AbstractAlgorithm<BuildingPlan, NetworkFlowModel> conv = new RectangleConverter();
        conv.setProblem(project.getBuildingPlan());
        conv.run();
        NetworkFlowModel networkFlowModel = conv.getSolution();

        // convert and create the concrete assignment
        ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment(project.getCurrentAssignment(), 400);

        GraphAssignmentConverter cav = new GraphAssignmentConverter(networkFlowModel);
        cav.setProblem(concreteAssignment);
        cav.run();
        networkFlowModel = cav.getSolution();

        if (assignmentOperation.getSelectedAlgorithm() == null) {
            System.out.println("No algorithm selected!");
            return;
        }
        final Algorithm<NetworkFlowModel, ExitAssignment> assignmentAlgorithm = assignmentOperation.getSelectedAlgorithm();
        System.out.println("Selected algorithm: " + assignmentAlgorithm);

        assignmentAlgorithm.setProblem(networkFlowModel);
        assignmentAlgorithm.run();
        ExitAssignment exitAssignment = assignmentAlgorithm.getSolution();

        System.out.println("Exit Assignment: ");
        System.out.println(exitAssignment);

        // step 2: put the exit assingment into a cellular automaton
        // We now have to create a cellular automaton, assign the concrete assignment and have to map the exits to the
        // chosen exits in our above exit computation
        MultiFloorEvacuationCellularAutomaton ca;
        ZToCAMapping mapping;
        ZToCARasterContainer container;

        final ZToCAConverter caConv = new ZToCAConverter();
        //final ExitDistributionZToCAConverter caConv = new ExitDistributionZToCAConverter();
        caConv.setProblem(project.getBuildingPlan());
        caConv.run();

        ca = caConv.getCellularAutomaton();

        System.out.println("The converted CA:\n" + ca);

        mapping = caConv.getMapping();
        container = caConv.getContainer();
        ConvertedCellularAutomaton cca = new ConvertedCellularAutomaton(ca, mapping, container);

        // here we have to assign the concrete assignment
        // create and convert concrete assignment
        System.out.println("Creating concrete Assignment.");
        for (AssignmentType at : project.getCurrentAssignment().getAssignmentTypes()) {
            //ca.setAssignmentType(at.getName(), at.getUid());
        }
        //ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
        final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
        cac.setProblem(new AssignmentApplicationInstance(cca, concreteAssignment));
        cac.run();

        ZToGraphRasterContainer graphRaster = networkFlowModel.getZToGraphMapping().getRaster();

        cca = caConv.getSolution();

        BidirectionalNodeCellMapping.CAPartOfMapping caPartOfMapping = caConv.getLatestCAPartOfNodeCellMapping();

        BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);

        GraphBasedIndividualToExitMapping graphBasedIndividualToExitMaping;
        graphBasedIndividualToExitMaping = new GraphBasedIndividualToExitMapping(
                ca, nodeCellMapping, exitAssignment);
        graphBasedIndividualToExitMaping.calculate();
        System.out.println("Graph based individual to exit mapping:");
        System.out.println(graphBasedIndividualToExitMaping);
        // TODO exit computation
        //ca.setIndividualToExitMapping(graphBasedIndividualToExitMaping);

        // step 3: simulate
        AbstractAlgorithm<EvacuationSimulationProblem, EvacuationSimulationResult> caAlgo;

        SwapCellularAutomaton swapAlgo = new SwapCellularAutomaton();

        caAlgo = swapAlgo;

        List<Individual> individuals = Collections.emptyList();
        Map<Individual, EvacCellInterface> individualStartPositions = Collections.emptyMap();

        InitialConfiguration ic = new InitialConfiguration(ca, individuals, individualStartPositions);
        EvacuationSimulationProblemImpl esp = new EvacuationSimulationProblemImpl(ic);
        double caMaxTime = PropertyContainer.getGlobal().getAsDouble("algo.ca.maxTime");
        esp.setEvacuationTimeLimit((int)caMaxTime);
        caAlgo.setProblem(esp);

        caAlgo.run();

        System.out.println("Recording stopped.");

        VisualResultsRecorder recorder = new VisualResultsRecorder(null, null);
        EvacuationState es = null; //caAlgo.getSolution().getEvacuationState();
        EvacuationSimulationSpeed ess = null; //caAlgo.getEvacuationSimulationSpeed();
        visResults = new EvacuationSimulationResults(es, ess, recorder.getRecording());
    }

    @Override
    public String toString() {
        return "Exit Assignment";
    }
}

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

import org.zet.cellularautomaton.algorithm.EvacuationCellularAutomatonAlgorithm;
import algo.graph.exitassignment.Assignable;
import algo.graph.exitassignment.EarliestArrivalTransshipmentExitAssignment;
import algo.graph.exitassignment.ExitAssignment;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.util.Formatter;
import org.zetool.common.util.units.Quantity;
import org.zetool.common.util.units.TimeUnits;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import ds.CompareVisualizationResults;
import ds.GraphVisualizationResults;
import ds.PropertyContainer;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.results.VisualResultsRecorder;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Project;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import evacuationplan.BidirectionalNodeCellMapping;
import evacuationplan.BidirectionalNodeCellMapping.CAPartOfMapping;
import exitdistributions.GraphBasedIndividualToExitMapping;
import io.visualization.BuildingResults;
import io.visualization.CellularAutomatonVisualizationResults;
import io.visualization.EvacuationSimulationResults;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.InitialConfiguration;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblemImpl;
import org.zet.cellularautomaton.statistic.CAStatistic;
import tasks.conversion.BuildingPlanConverter;
import zet.tasks.CellularAutomatonAlgorithms;
import zet.tasks.CompareTask;
import zet.tasks.GraphAlgorithmEnumeration;
import zet.tasks.GraphAlgorithmTask;
import zet.tasks.SerialTask;

/**
 * A class that starts, stops and pauses the algorithms that can be used in ZET.
 *
 * @author Jan-Philipp Kappmeier
 */
public class AlgorithmControl implements PropertyChangeListener {

    /** The logger of the main class. */
    private static final Logger log = Logger.getGlobal();

    private AlgorithmControlCellularAutomaton caControl = new AlgorithmControlCellularAutomaton();

    private BuildingResults buildingResults;
    private Project project;
    private NetworkFlowModel networkFlowModel;
    private GraphVisualizationResults graphVisResults;
    private CompareVisualizationResults compVisResults;
    //private boolean createdValid = false;
    private RuntimeException error;

    public AlgorithmControl(Project project) {
        this.project = project;
    }

    public boolean isError() {
        return error != null;
    }

    public RuntimeException getError() {
        return error;
    }

    public void throwError() {
        throw error;
    }

    void setProject(Project project) {
        this.project = project;
    }

    public void convertBuildingPlan() {
        convertBuildingPlan(null);
    }
    Quantity<TimeUnits> conversionTime;

    public void convertBuildingPlan(PropertyChangeListener pcl) {
        final BuildingPlanConverter bpc = new BuildingPlanConverter();
        bpc.setProblem(project.getBuildingPlan());

        final SerialTask st = new SerialTask();
        st.add(bpc);
        st.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (st.isDone()) {
                    buildingResults = bpc.getSolution();
                    conversionTime = bpc.getRuntime();
                }
            }
        });
        if (pcl != null) {
            st.addPropertyChangeListener(pcl);
        }
        st.execute();
        //bpc.run();
        //buildingResults = bpc.getSolution();
    }

    public Quantity<TimeUnits> getConversionRuntime() {
        return conversionTime;
    }

    public BuildingResults getBuildingResults() {
        return buildingResults;
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        System.out.println(pce.getPropertyName());
    }

    public void convertGraph() {
        convertGraph(null, GraphConverterAlgorithms.NonGridGraph);
    }

    GraphConverterAlgorithms last = GraphConverterAlgorithms.NonGridGraph;

    public RunnableFuture<Void> convertGraph(PropertyChangeListener propertyChangeListener, GraphConverterAlgorithms Algo) {
        if (project.getBuildingPlan().isRastered() == false) {
            System.out.print("Building is not rasterized. Rastering... ");
            project.getBuildingPlan().rasterize();
            System.out.println(" done.");
        }
        final AbstractAlgorithm<BuildingPlan, NetworkFlowModel> conv = Algo.converter();
        last = Algo;
        conv.setProblem(project.getBuildingPlan());
        final SerialTask st = new SerialTask(conv);
        st.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (st.isDone()) {
                    if (st.isError()) {
                        System.err.println("An error occured:");
                        st.getError().printStackTrace(System.out);
                    } else {
                        System.out.println(st);
                        networkFlowModel = conv.getSolution();
                        System.out.println("Nodes: " + networkFlowModel.numberOfNodes());
                        System.out.println("Edges: " + networkFlowModel.numberOfEdges());
                    }
                }
            }
        });
        if (propertyChangeListener != null) {
            st.addPropertyChangeListener(propertyChangeListener);
        }
        st.execute();
        return st;
    }

    public NetworkFlowModel getNetworkFlowModel() {
        return networkFlowModel;
    }

    public void performOptimization(GUIControl control) {
        performOptimization(null, control);
    }

    public void performOptimization(PropertyChangeListener propertyChangeListener, AlgorithmListener control) {
        if (!project.getBuildingPlan().isRastered()) {
            System.out.print("Building is not rasterized. Rastering... ");
            project.getBuildingPlan().rasterize();
            System.out.println(" done.");
        }

        final GraphAlgorithmTask gat = new GraphAlgorithmTask(GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimized);
        gat.setProblem(project);

        gat.addAlgorithmListener(control);

        gat.setNetworkFlowModel(networkFlowModel);
        gat.setConv(last.converter());

        final SerialTask st = new SerialTask(gat);
        st.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (st.isDone()) {
                    if (st.isError()) {
                        System.err.print("Error occured.");
                        st.getError().printStackTrace(System.err);
                    } else {
                        networkFlowModel = gat.getNetworkFlowModel();
                        graphVisResults = gat.getSolution();
                    }
                }
            }
        });
        if (propertyChangeListener != null) {
            st.addPropertyChangeListener(propertyChangeListener);
        }
        st.execute();
    }

    public void performExitAssignmentEAT(PropertyChangeListener propertyChangeListener, AlgorithmListener control) {

        if (networkFlowModel == null) {
            log.severe("No model created.");
            return;
        }

        EarliestArrivalTransshipmentExitAssignment eatAssignment;
        eatAssignment = new EarliestArrivalTransshipmentExitAssignment();
        //ZToGraphConverter.convertConcreteAssignment( concreteAssignments[runNumber], res.getNetworkFlowModel() );

        log.info("Compute concrete assignment...");
        ConcreteAssignment concreteAssignment;

        concreteAssignment = AssignmentConcrete.createConcreteAssignment(project.getCurrentAssignment(), 400);
        GraphAssignmentConverter cav = new GraphAssignmentConverter(networkFlowModel);
        cav.setProblem(concreteAssignment);
        cav.run();
        networkFlowModel = cav.getSolution();
        log.log(Level.INFO, "Persons: {0}", concreteAssignment.getPersons().size());
        log.info("done.");

        eatAssignment.setProblem(networkFlowModel);
        log.info("Compute exit assignment...");
        eatAssignment.run();
        log.info("done.");
        Assignable exitAssignmenta = eatAssignment;

        ExitAssignment exitAssignment = eatAssignment.getExitAssignment();

        log.info("Computed ExitAssignment: ");
        log.info(exitAssignment.toString());

        log.info("Create Cellular Automaton according to the exit assignment...");

        // convert
        final ZToCAConverter conv = new ZToCAConverter();
        conv.setProblem(project.getBuildingPlan());
        conv.run();
        MultiFloorEvacuationCellularAutomaton ca = conv.getCellularAutomaton();
        ZToCARasterContainer container;
        EvacuationSimulationResults caVisResults;
        ZToCAMapping mapping;
        mapping = conv.getMapping();
        container = conv.getContainer();
        final ConvertedCellularAutomaton cca = new ConvertedCellularAutomaton(ca, mapping, container);

        CAPartOfMapping caPartOfMapping = conv.getLatestCAPartOfNodeCellMapping();//this.getLatestCAPartOfNodeCellMapping();

        final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();

        cac.setProblem(new AssignmentApplicationInstance(cca, concreteAssignment));
        cac.run();
        ZToGraphRasterContainer graphRaster = getNetworkFlowModel().getZToGraphMapping().getRaster();
        BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);
        //GraphBasedExitToCapacityMapping graphBasedExitToCapacityMapping = null;
        GraphBasedIndividualToExitMapping graphBasedIndividualToExitMapping;
        graphBasedIndividualToExitMapping = new GraphBasedIndividualToExitMapping(ca, nodeCellMapping, exitAssignment);
        graphBasedIndividualToExitMapping.calculate();
        // TODO
        //ca.setIndividualToExitMapping( graphBasedIndividualToExitMapping );

        // Now, we have a CA
        //		EvacuationCellularAutomaton ca = super.convert(buildingPlan);
//		CAPartOfMapping caPartOfMapping = this.getLatestCAPartOfNodeCellMapping();
//		applyConcreteAssignment(concreteAssignment);
//		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);
//		graphBasedIndividualToExitMaping = new GraphBasedIndividualToExitMapping(ca, nodeCellMapping, exitAssignment);
//		graphBasedIndividualToExitMaping.calculate();
//		ca.setIndividualToExitMapping( graphBasedIndividualToExitMaping );
//		return ca;
        log.info("done.");

        // Perform CA simulation
        log.info("Performing Simulation...");

        EvacuationCellularAutomatonAlgorithm caAlgo = CellularAutomatonAlgorithms.InOrder.getAlgorithm();

        List<Individual> individuals = Collections.emptyList();
        Map<Individual, EvacCellInterface> individualStartPositions = Collections.emptyMap();

        InitialConfiguration ic = new InitialConfiguration(ca, individuals, individualStartPositions);
        EvacuationSimulationProblemImpl esp = new EvacuationSimulationProblemImpl(ic);
        double caMaxTime = PropertyContainer.getGlobal().getAsDouble("algo.ca.maxTime");
        esp.setEvacuationTimeLimit((int) caMaxTime);
        caAlgo.setProblem(esp);
        //ca.startRecording ();

        //caAlgo.addAlgorithmListener( this );
        caAlgo.run();	// hier wird initialisiert
        //ca.stopRecording();

        // create results
        //CAVisualizationResults visResults = new EvacuationSimulationResults( mapping, ca.getPotentialManager() );
        
        EvacuationSimulationResults evacResults = new EvacuationSimulationResults(caAlgo.getEvacuationState(),
                caAlgo.getEvacuationSimulationSpeed(), null);
        CellularAutomatonVisualizationResults visResults = new CellularAutomatonVisualizationResults(mapping, ca);

        //caAlgo.getProblem().caStatisticWriter.getStoredCAStatisticResults().
        evacResults.statistic = new CAStatistic(caAlgo.getStatisticResults());

        EvacuationCellularAutomaton cellularAutomaton = ca;
        //mapping = mapping;
        //container = cca;
        container = conv.getContainer();
        caVisResults = evacResults;

        double secondsPerStep = 1; //cellularAutomaton.getSecondsPerStep();

        caControl.tempSetParametersFromEx(caVisResults, ca);
        log.log(Level.INFO, "Egress time: {0}", Formatter.formatUnit(caAlgo.getSolution().getSteps() * secondsPerStep, TimeUnits.SECOND));
        log.info("done.");
    }

    public void performOptimizationCompare(PropertyChangeListener propertyChangeListener) {
        if (!project.getBuildingPlan().isRastered()) {
            System.out.print("Building is not rasterized. Rastering... ");
            project.getBuildingPlan().rasterize();
            System.out.println(" done.");
        }

        final CompareTask ct = new CompareTask(GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimizedCompare);
        ct.setProblem(project);

        //values for original network
        GraphConverterAlgorithms ConvOrig = GraphConverterAlgorithms.NonGridGraph;
        ct.setConvOriginal(ConvOrig.converter());

        //values for thin network
        ct.setConvThinNet(last.converter());
        ct.setThinNetwork(networkFlowModel);

        final SerialTask st = new SerialTask(ct);
        st.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent pce) {
                if (st.isDone()) {
                    networkFlowModel = ct.getOriginal();
                    compVisResults = ct.getSolution();
                }
            }
        });
        if (propertyChangeListener != null) {
            st.addPropertyChangeListener(propertyChangeListener);
        }
        System.out.println("done");
        st.execute();
        System.out.println("done");
    }

    public GraphVisualizationResults getGraphVisResults() {
        return graphVisResults;
    }

    public CompareVisualizationResults getCompVisResults() {
        return compVisResults;
    }

    void performSimulation(PropertyChangeListener propertyChangeListener, AlgorithmListener listener) {
        caControl.performSimulation(project, propertyChangeListener, listener);
    }

    void pauseSimulation() {
        caControl.pauseStepByStep();
    }

    void performOneStep(AlgorithmListener listener) {
        caControl.performOneStep(project, listener);
    }

    void performSimulationQuick(AlgorithmListener listener) {
        caControl.performSimulationQuick(project, listener);
    }

    public MultiFloorEvacuationCellularAutomaton getCellularAutomaton() {
        return caControl.getCellularAutomaton();
    }

    public ZToCARasterContainer getContainer() {
        return caControl.getContainer();
    }

    public ZToCAMapping getMapping() {
        return caControl.getMapping();
    }

    void convertCellularAutomaton(PropertyChangeListener propertyChangeListener) {
        caControl.convertCellularAutomaton(project.getBuildingPlan(), propertyChangeListener);
    }

    EvacuationSimulationResults getCaVisResults() {
        return caControl.getCaVisResults();
    }

    void setSimulationAlgorithm(CellularAutomatonAlgorithms cellularAutomaton) {
        caControl.setSimulationAlgorithm(cellularAutomaton);
    }

    CellularAutomatonAlgorithms getSimulationAlgorithm() {
        return caControl.getSimulationAlgorithm();
    }

}

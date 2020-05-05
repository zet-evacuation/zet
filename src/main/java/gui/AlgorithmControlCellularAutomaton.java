/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.common.algorithm.AbstractAlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.algorithm.AlgorithmStartedEvent;
import org.zetool.common.util.Formatter;
import org.zetool.common.util.units.TimeUnits;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import ds.PropertyContainer;
import de.zet_evakuierung.model.AssignmentType;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Project;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.zet_evakuierung.model.Assignment;
import io.visualization.EvacuationSimulationResults;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.InitialConfiguration;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblemImpl;
import org.zet.cellularautomaton.algorithm.StepByStepAutomaton;
import org.zetool.common.algorithm.AlgorithmTerminatedEvent;
import org.zetool.common.datastructure.SimpleTuple;
import zet.tasks.CellularAutomatonAlgorithms;
import zet.tasks.CellularAutomatonTask;
import zet.tasks.SerialTask;
import zet.tasks.SerialTask.Lubricant;
import zet.tasks.SerialTask.SimpleLubricant;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AlgorithmControlCellularAutomaton {

    /**
     * The logger of the main class.
     */
    private static final Logger log = Logger.getGlobal();
    //private EvacuationCellularAutomatonAlgorithm simulationAlgorithm;
    private CellularAutomatonAlgorithms simulationAlgorithm = CellularAutomatonAlgorithms.RandomOrder;
    private ZToCAMapping mapping;
    private ZToCARasterContainer container;
    private EvacuationSimulationResults evacuationSimulationResults;
    private MultiFloorEvacuationCellularAutomaton cellularAutomaton;
    private CellularAutomatonTask cat = new CellularAutomatonTask();
    private ConcreteAssignment concreteAssignment;

    public CellularAutomatonAlgorithms getSimulationAlgorithm() {
        return simulationAlgorithm;
    }

    /**
     * Sets the simulation algorithm that will be used for the next simulation runs.
     *
     * @param simulationAlgorithm
     */
    public void setSimulationAlgorithm(CellularAutomatonAlgorithms simulationAlgorithm) {
        this.simulationAlgorithm = simulationAlgorithm;
    }

    void convertCellularAutomaton(BuildingPlan buildingPlan, PropertyChangeListener propertyChangeListener) {
        final ZToCAConverter conv = new ZToCAConverter();

        conv.setProblem(buildingPlan);

        final SerialTask st = new SerialTask(conv);
        st.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (st.isDone()) {
                    if (st.isError()) {
                        //error = st.getError();
                    } else {
                        cellularAutomaton = conv.getCellularAutomaton();
                        mapping = conv.getMapping();
                        container = conv.getContainer();
                        //caInitialized = true;
                    }
                }
            }
        });
        if (propertyChangeListener != null) {
            st.addPropertyChangeListener(propertyChangeListener);
        }

        st.execute();
    }

    public void invalidateConvertedCellularAutomaton() {
        //caInitialized = false;
    }

    public MultiFloorEvacuationCellularAutomaton getCellularAutomaton() {
        return cellularAutomaton;
    }

    public ZToCARasterContainer getContainer() {
        return container;
    }

    public ZToCAMapping getMapping() {
        return mapping;
    }

    void performSimulation(Project project, PropertyChangeListener propertyChangeListener, AlgorithmListener listener) {
        // Step 1: Convert a cellular automaton
        log.info("Setting up the converter");
        final ZToCAConverter conv = new ZToCAConverter();
        conv.setProblem(project.getBuildingPlan());

        log.info("Setting up the ca simulation task");
        cat = new CellularAutomatonTask();
        cat.setCaAlgo(CellularAutomatonAlgorithms.RandomOrder.getAlgorithm());
        cat.addAlgorithmListener(listener);

        // Intermediate step. Move the solution from conv to cat
        log.info("Setting up intermediate step");
        
        final AbstractAlgorithm<SimpleTuple<Assignment, ConvertedCellularAutomaton>, EvacuationSimulationProblem> concreteAssignmentConverter
                = new AbstractAlgorithm< SimpleTuple<Assignment, ConvertedCellularAutomaton> , EvacuationSimulationProblem>() {

            @Override
            protected EvacuationSimulationProblem runAlgorithm(SimpleTuple<Assignment, ConvertedCellularAutomaton> problem) {
                log.info("Step 2: Convert concrete assignment...");
                log.warning("Ignore setting assignment type!");
                //for (AssignmentType at : project.getCurrentAssignment().getAssignmentTypes()) {
                //    ca.setAssignmentType(at.getName(), at.getUid());
                //}
                log.info("Converting an assignment to a cellular automaton");
                ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment(project.getCurrentAssignment(), 400);
                final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
                cac.setProblem(new AssignmentApplicationInstance(conv.getSolution(), concreteAssignment));
                cac.run();   
                List<Individual> individuals = new ArrayList<>(cac.getSolution().getU().keySet());

                for (Entry<Individual, EvacCellInterface> e : cac.getSolution().getU().entrySet()) {
                    log.info("i " + e.getKey() + ": " + e.getValue() );
                }
                
                InitialConfiguration initialConfiguration = new InitialConfiguration(
                        (MultiFloorEvacuationCellularAutomaton)problem.getV().getCellularAutomaton(),
                        individuals, cac.getSolution().getU());
                return new EvacuationSimulationProblemImpl(initialConfiguration);
            }  
        };

        SimpleLubricant<EvacuationSimulationProblem> moveCaToAlgorithm
                = new SimpleLubricant<>(concreteAssignmentConverter, cat);
        
        Lubricant<ConvertedCellularAutomaton, SimpleTuple<Assignment, ConvertedCellularAutomaton>> moveCaToConcreteConverter
                = new Lubricant<>(conv, concreteAssignmentConverter, cca -> new SimpleTuple<>(project.getCurrentAssignment(), cca));
        
        conv.addAlgorithmListener(new AlgorithmListener() {
            @Override
            public void eventOccurred(AbstractAlgorithmEvent event) {
                if (event instanceof AlgorithmTerminatedEvent) {
                    log.info("conv is terminated. Storing the results.");
                    cellularAutomaton = conv.getCellularAutomaton();
                    mapping = conv.getMapping();
                    container = conv.getContainer();
                }
            }
        });

        log.info("Starting the task execution");
        final SerialTask st = new SerialTask();
        st.add(conv);
        st.add(moveCaToConcreteConverter);        
        st.add(concreteAssignmentConverter);
        st.add(moveCaToAlgorithm);
        st.add(cat);
        //final SerialTask st = new SerialTask(cat);
        st.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (st.isDone()) {
                    if (st.isError()) {
                        //error = st.getError();
                    } else {
                        log.info("Simulation complete. Sending data to visualization.");
                        evacuationSimulationResults = cat.getSolution();
                        //cellularAutomaton = cat.getCa();
                        //mapping = cat.getMapping();
                        //container = cat.getContainer();
                        //evacuationSimulationResults = cat.getSolution();
                        //EventServer.getInstance().dispatchEvent( new MessageEvent<>( this, MessageType.Status, "Simulation finished" ) );
                        double secondsPerStep = 1; // cellularAutomaton.getSecondsPerStep()
                        
                        log.log(Level.INFO, "Egress time: {0}", Formatter.formatUnit(cat.getCellularAutomatonAlgorithm().getSolution().getSteps()
                                * secondsPerStep, TimeUnits.SECOND));
                    }
                }
            }
        });
        if (propertyChangeListener != null) {
            st.addPropertyChangeListener(propertyChangeListener);
        }
        st.execute();
    }

    void performSimulationQuick(Project project, AlgorithmListener listener) {
        initStepByStep(project, listener, false);
    }

    public EvacuationSimulationResults getCaVisResults() {
        return evacuationSimulationResults;
    }

    void createConcreteAssignment(Project project) throws IllegalArgumentException, ZToCAConverter.ConversionNotSupportedException {
        log.warning("Skipping setting of assignment type!");
        for (AssignmentType at : project.getCurrentAssignment().getAssignmentTypes()) {
            //cellularAutomaton.setAssignmentType(at.getName(), at.getUid());
        }
        concreteAssignment = AssignmentConcrete.createConcreteAssignment(project.getCurrentAssignment(), 400);
        final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
        cac.setProblem(new AssignmentApplicationInstance(new ConvertedCellularAutomaton(cellularAutomaton, mapping, container), concreteAssignment));
        cac.run();
    }

    void setUpSimulationAlgorithm() {
        List<Individual> individuals = Collections.emptyList();
        Map<Individual, EvacCellInterface> individualStartPositions = Collections.emptyMap();
        EvacuationCellularAutomatonAlgorithm cellularAutomatonAlgorithm = simulationAlgorithm.getAlgorithm();
        InitialConfiguration initialConfiguration = new InitialConfiguration(cellularAutomaton, individuals, individualStartPositions);
        EvacuationSimulationProblemImpl esp = new EvacuationSimulationProblemImpl(initialConfiguration);
        double caMaxTime = PropertyContainer.getGlobal().getAsDouble("algo.ca.maxTime");
        esp.setEvacuationTimeLimit((int)caMaxTime);
        cellularAutomatonAlgorithm.setProblem(esp);
    }

    void pauseStepByStep() {
        if (ecasbs != null && ecasbs.isRunning()) {
            ecasbs.setPaused(true);
        }
    }

    void performOneStep(Project project, AlgorithmListener listener) {
        initStepByStep(project, listener, true);
    }

    EvacuationCellularAutomatonAlgorithm eca = null;
    EvacuationCellularAutomatonAlgorithm ecasbs = null;

    class BackgroundTask<S> extends SwingWorker<S, AbstractAlgorithmEvent> {

        AbstractAlgorithm<?, S> algo;

        private BackgroundTask(AbstractAlgorithm<?, S> algorithm) {
            this.algo = algorithm;
        }

        @Override
        protected S doInBackground() throws Exception {
            algo.run();
            S result = algo.getSolution();
            return result;
        }
    }

    private void initStepByStep(Project project, AlgorithmListener listener, boolean stopMode) {
        if (ecasbs == null || !ecasbs.isRunning()) {
            cat = new CellularAutomatonTask();
            eca = CellularAutomatonAlgorithms.RandomOrder.getAlgorithm();
            if (stopMode) {
                log.info("Initializing the algorithm for step-by-step execution...");
                ecasbs = StepByStepAutomaton.getStepByStepAlgorithm(eca);
            } else {
                log.info("Initializing the algorithm for slow execution...");
                ecasbs = StepByStepAutomaton.getSlowAlgorithm(eca);
            }

            cat.setCaAlgo(ecasbs);
            InitialConfiguration i = new InitialConfiguration(cellularAutomaton, null /* individuals */, null /*individualStartPositions*/ );
            cat.setProblem(new EvacuationSimulationProblemImpl(i));

            /**
             * Listens to the actual simulation algorithm and sets the data structures for the view accordingly. These
             * datastructures are the cellular automaton, the container and the mapping. They can afterwards be accessed
             * from the GUI to visualize the temporal simulation status.
             */
            final AlgorithmListener al = new AlgorithmListener() {
                @Override
                public void eventOccurred(AbstractAlgorithmEvent event) {
                    if (event instanceof AlgorithmStartedEvent) {
                        cellularAutomaton = cat.getCa();
                        mapping = cat.getMapping();
                        container = cat.getContainer();
                        assert cellularAutomaton != null;
                    }
                }
            };

            ecasbs.addAlgorithmListener(al);
            ecasbs.addAlgorithmListener(listener);

            new BackgroundTask<>(cat).execute(); // execute the algorithm in a new thread
        } else {
            if (ecasbs.isRunning()) {
                log.info("Continuing the algorithm...");
                ecasbs.setPaused(false);
            } else {
                log.info("Ignoring, algorithm has finished.");
            }
        }
    }

    void tempSetParametersFromEx(EvacuationSimulationResults caVis, MultiFloorEvacuationCellularAutomaton ca) {
        this.evacuationSimulationResults = caVis;
        this.cellularAutomaton = ca;
    }
}

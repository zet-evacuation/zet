/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA	02110-1301, USA.
 */
package de.tu_berlin.math.coga.batch.operations;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Project;
import ds.GraphVisualizationResults;
import ds.PropertyContainer;
import org.zetool.algorithm.shortestpath.Dijkstra;
import org.zetool.algorithm.shortestpath.IntegralSingleSourceShortestPathProblem;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.components.batch.input.reader.InputFileReader;
import org.zetool.components.batch.operations.AbstractOperation;
import org.zetool.components.batch.operations.AtomicOperation;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DefaultDirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.ds.network.ExtendedGraph;
import org.zetool.netflow.dynamic.LongestShortestPathTimeHorizonEstimator;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import zet.tasks.GraphAlgorithmEnumeration;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BasicOptimization extends AbstractOperation<Project, GraphVisualizationResults> {

    private static final Logger log = Logger.getGlobal();
    InputFileReader<Project> input;
    AtomicOperation<BuildingPlan, NetworkFlowModel> transformationOperation;
    AtomicOperation<EarliestArrivalFlowProblem, PathBasedFlowOverTime> eafAlgorithm;
    GraphVisualizationResults gvr;

    public BasicOptimization() {
		// First, we go from zet to network flow model
        // then, we go from nfm to path based flow
        // therefore, we need two algorithms

        transformationOperation = new AtomicOperation<>("Transformation", BuildingPlan.class, NetworkFlowModel.class);

        this.addOperation(transformationOperation);

        eafAlgorithm = new AtomicOperation<>("Flow Computation", EarliestArrivalFlowProblem.class, PathBasedFlowOverTime.class);

        this.addOperation(eafAlgorithm);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean consume(InputFileReader<?> o) {
        if (o.getTypeClass() == Project.class) {
            input = (InputFileReader<Project>) o;
            return true;
        }
        return false;
    }

    @Override
    public Class<GraphVisualizationResults> produces() {
        return GraphVisualizationResults.class;
    }

    @Override
    public GraphVisualizationResults getProduced() {
        return gvr;
    }

    @Override
    public String toString() {
        return "Basic Optimization";
    }

    @Override
    public void run() {
        Project project = input.getSolution();

        System.out.println(project);

        if (!project.getBuildingPlan().isRastered()) {
            System.out.print("Building is not rasterized. Rastering... ");
            project.getBuildingPlan().rasterize();
            System.out.println(" done.");
        }

        if (transformationOperation.getSelectedAlgorithm() == null) {
            System.out.println("No algorithm selected!");
            return;
        }
        System.out.println("Selected algorithm: " + transformationOperation.getSelectedAlgorithm());

        // Convert
        final Algorithm<BuildingPlan, NetworkFlowModel> conv = transformationOperation.getSelectedAlgorithm();
        conv.setProblem(project.getBuildingPlan());
        conv.run();
        NetworkFlowModel networkFlowModel;

        networkFlowModel = conv.getSolution();

        // convert and create the concrete assignment
        ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment(project.getCurrentAssignment(), 400);

        GraphAssignmentConverter cav = new GraphAssignmentConverter(networkFlowModel);

        cav.setProblem(concreteAssignment);
        cav.run();
        networkFlowModel = cav.getSolution();

        // call the graph algorithm
        Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime> gt = eafAlgorithm.getSelectedAlgorithm();

        EarliestArrivalFlowProblem eafp = networkFlowModel.getEAFP();

        System.out.println("Transforming transit times.");
        EarliestArrivalFlowProblem oldEafp = eafp;
        eafp = transformTransitTimes(eafp);
        IdentifiableIntegerMapping<Edge> newTransitTimes = eafp.getTransitTimes();

        System.out.println("Earliest arrival transshipment calculation starts");
        LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(eafp);
        estimator.run();
        System.out.println("Geschätzte Lösung:" + estimator.getSolution());
//		eafp = networkFlowModel.getEAFP( estimator.getSolution().getUpperBound() );
        eafp = networkFlowModel.getEAFP(estimator.getSolution().getLowerBound() + 1);

        // The latest call to getEAFP takes the old transit times again! Set them again
        eafp = new EarliestArrivalFlowProblem(eafp.getEdgeCapacities(), eafp.getNetwork(), eafp.getNodeCapacities(), eafp.getSink(), eafp.getSources(), eafp.getTimeHorizon(), newTransitTimes, eafp.getSupplies());

        gt.setProblem(eafp);
        int maxTime = (int) PropertyContainer.getGlobal().getAsDouble("algo.ca.maxTime");
        //Algorithm<NetworkFlowModel, PathBasedFlowOverTime> gt;
        GraphAlgorithmEnumeration graphAlgorithm = GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimized;

		//gt = graphAlgorithm.createTask( cav.getSolution(), maxTime );
        //gt.setProblem( cav.getSolution() );
        //gt.addAlgorithmListener( this );
        gt.run();

        // create graph vis result
        gvr = new GraphVisualizationResults(cav.getSolution(), gt.getSolution());
    }

    private EarliestArrivalFlowProblem transformTransitTimes(EarliestArrivalFlowProblem eafp) {

		// transform the transit times
        // compute shortest paths
        DefaultDirectedGraph n = (DefaultDirectedGraph) eafp.getNetwork();

        IdentifiableIntegerMapping<Edge> transitTimes;

        transitTimes = eafp.getTransitTimes();

        ExtendedGraph ex = new ExtendedGraph(n, 1, eafp.getSources().size());
        Node superNode = ex.getFirstNewNode();

        transitTimes.setDomainSize(ex.edgeCount()); // reserve space

        for (Node source : eafp.getSources()) {
            Edge newEdge = ex.createAndSetEdge(superNode, source);
            transitTimes.set(newEdge, 0);
        }

        Dijkstra dijkstra = new Dijkstra();
        dijkstra.setProblem(new IntegralSingleSourceShortestPathProblem(ex, eafp.getTransitTimes(), superNode));
        dijkstra.run();

        log.log(Level.INFO, "Solution: {0}", dijkstra.getSolution().getForest());

        transitTimes = eafp.getTransitTimes();
        IdentifiableIntegerMapping<Edge> newTransitTimes = new IdentifiableIntegerMapping<>(transitTimes);

        for (Edge e : eafp.getNetwork().edges()) {
            // We have a value of Integer.MAX_VALUE (= infinity) at e.start()) if the node is not reachable.
            int newTransit = transitTimes.get(e) + dijkstra.getSolution().getDistance(e.start()) - dijkstra.getSolution().getDistance(e.end());
            if (dijkstra.getSolution().getDistance(e.start()) == Integer.MAX_VALUE) {
                //if( newTransit == 2147483318 ) {
                newTransit = 0; // the transit time does not matter, the start node is not reachable anyway
            }
            log.log(Level.FINEST, "t = {0} + {1} - {2} = {3}", new Object[]{transitTimes.get(e), dijkstra.getSolution().getDistance(e.start()), dijkstra.getSolution().getDistance(e.end()), newTransit});
            newTransitTimes.set(e, newTransit);
        }

        log.log(Level.INFO, "Old transit: {0}", transitTimes);
        log.log(Level.INFO, "new transit: {0}", newTransitTimes);

        EarliestArrivalFlowProblem neweafp = new EarliestArrivalFlowProblem(eafp.getEdgeCapacities(), eafp.getNetwork(), eafp.getNodeCapacities(), eafp.getSink(), eafp.getSources(), eafp.getTimeHorizon(), newTransitTimes, eafp.getSupplies());

        return neweafp;
    }
}

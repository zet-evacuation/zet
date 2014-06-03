/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.tasks;

import de.tu_berlin.coga.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import de.tu_berlin.coga.netflow.dynamic.earliestarrival.LongestShortestPathTimeHorizonEstimator;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.BaseZToGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import ds.CompareVisualizationResults;
import de.tu_berlin.coga.netflow.ds.network.AbstractNetwork;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;
import de.tu_berlin.coga.zet.model.Project;

/**
 *
 * @author schwengf
 */
public class CompareTask extends Algorithm<Project, CompareVisualizationResults>{
    
    NetworkFlowModel OrigNetwork;
    NetworkFlowModel ThinNetwork;
    GraphAlgorithmEnumeration algo;
    Algorithm<BuildingPlan,NetworkFlowModel> convOrig;
    Algorithm<BuildingPlan,NetworkFlowModel> convThinNet;
    int TimeHorizonGlobal;
    
    public CompareTask (NetworkFlowModel OriginNetwork, NetworkFlowModel ThinNet, GraphAlgorithmEnumeration graphalgo, BaseZToGraphConverter ConvOrig, BaseZToGraphConverter ConvThin)
    {
        OrigNetwork = OriginNetwork;
        ThinNetwork = ThinNet;
        algo = graphalgo;
        convOrig = ConvOrig;
        convThinNet = ConvThin;
    }
    public CompareTask(GraphAlgorithmEnumeration graphalgo)
    {
        algo = graphalgo;
    }
    
    @Override
    protected CompareVisualizationResults runAlgorithm( Project project ) {
    
        //setting the originalnetworkflowmodel
        if (OrigNetwork == null)
        {
            convOrig.setProblem(project.getBuildingPlan());
            convOrig.run();
            OrigNetwork = convOrig.getSolution(); 
        }
        
        // convert and create the concrete assignment for the original network
	ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );	
	GraphAssignmentConverter cav = new GraphAssignmentConverter( OrigNetwork );		
	cav.setProblem( concreteAssignment );
	cav.run();
	OrigNetwork = cav.getSolution();
        
        // convert and create the concrete assignment for the thin network
        ConcreteAssignment concreteAssignment2 = project.getCurrentAssignment().createConcreteAssignment( 400 );	
	GraphAssignmentConverter cav2 = new GraphAssignmentConverter( ThinNetwork );		
	cav2.setProblem( concreteAssignment2 );
	cav2.run();
	ThinNetwork = cav2.getSolution();
        
        //gets TimeHorizon for the Original Network
        LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(OrigNetwork.edgeCapacities(), (AbstractNetwork)OrigNetwork.graph(), OrigNetwork.nodeCapacities() , OrigNetwork.getSupersink(), OrigNetwork.getSources(), 100, OrigNetwork.transitTimes(), OrigNetwork.currentAssignment());
        estimator.setProblem(problem);
        estimator.run();
        System.out.println("Geschaetzte Loesung Original: " + estimator.getSolution());
        
        //gets TimeHorizon for the Thin Network
        LongestShortestPathTimeHorizonEstimator estimator2 = new LongestShortestPathTimeHorizonEstimator();
        EarliestArrivalFlowProblem problem2 = new EarliestArrivalFlowProblem(ThinNetwork.edgeCapacities(), (AbstractNetwork)ThinNetwork.graph(), ThinNetwork.nodeCapacities() , ThinNetwork.getSupersink(), ThinNetwork.getSources(), 100, ThinNetwork.transitTimes(), ThinNetwork.currentAssignment());
        estimator2.setProblem(problem2);
        estimator2.run();
        System.out.println("Geschaetzte Loesung Kleineres Netzwerk: " + estimator2.getSolution());
        
        if (estimator2.getSolution().getUpperBound() > estimator.getSolution().getUpperBound())
        {
            TimeHorizonGlobal = estimator2.getSolution().getUpperBound();
        }
        else
        {
            TimeHorizonGlobal = estimator.getSolution().getUpperBound();
        }
        
        //calling the graph algorithm for the OriginalNetwork
	/*Algorithm<NetworkFlowModel, PathBasedFlowOverTime> gt = null;
	gt = algo.createTask( OrigNetwork, TimeHorizonGlobal );       
	gt.setProblem(OrigNetwork);
	gt.run();
        gt.*/
        SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask gt = new SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask(TimeHorizonGlobal);
        gt.setProblem(OrigNetwork);
        gt.run();
        int[] FlowTime = gt.getTimeFlowPair();
        for (int t=0; t< TimeHorizonGlobal+1; t++)
        {
            System.out.println("Zeit: " + t + "Flow: " + FlowTime[t]);
        }
        
        //calling the graph algorithm for the Thin Network
	SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask gt2 = new SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask(TimeHorizonGlobal);
        gt2.setProblem(ThinNetwork);
        gt2.run();
        int[] FlowTime2 = gt2.getTimeFlowPair();
        for (int t=0; t< TimeHorizonGlobal+1; t++)
        {
            System.out.println("Zeit: " + t + "Flow: " + FlowTime2[t]);
        }
        
        
        
        //Todo: draw the flowvlaues as a function of time
        return new CompareVisualizationResults(FlowTime, FlowTime2);
    }   
    
    public void setOriginal(NetworkFlowModel Origin)
    {
        this.OrigNetwork = Origin;
    }
    public void setThinNetwork (NetworkFlowModel Thin)
    {
        this.ThinNetwork = Thin;
    }
    public void setConvOriginal(Algorithm<BuildingPlan,NetworkFlowModel> Orig)
    {
        this.convOrig = Orig;
    }
    public void setConvThinNet(Algorithm<BuildingPlan,NetworkFlowModel> Thin)
    {
        this.convThinNet = Thin;
    }
    public NetworkFlowModel getOriginal()
    {
        return OrigNetwork;
    }
            
}

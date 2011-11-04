/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.tasks;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.BaseZToGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import ds.CompareVisualizationResults;
import ds.GraphVisualizationResults;
import ds.PropertyContainer;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.z.ConcreteAssignment;
import ds.z.Project;

/**
 *
 * @author schwengf
 */
public class CompareTask extends Algorithm<Project, CompareVisualizationResults>{
    
    NetworkFlowModel OrigNetwork;
    NetworkFlowModel ThinNetwork;
    GraphAlgorithmEnumeration algo;
    BaseZToGraphConverter convOrig;
    BaseZToGraphConverter convThinNet;
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
        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(OrigNetwork.getEdgeCapacities(), OrigNetwork.getNetwork(), OrigNetwork.getNodeCapacities() , OrigNetwork.getSupersink(), OrigNetwork.getSources(), 100, OrigNetwork.getTransitTimes(), OrigNetwork.getCurrentAssignment());
        estimator.setProblem(problem);
        estimator.run();
        System.out.println("Geschaetzte Loesung Original: " + estimator.getSolution());
        
        //gets TimeHorizon for the Thin Network
        LongestShortestPathTimeHorizonEstimator estimator2 = new LongestShortestPathTimeHorizonEstimator();
        EarliestArrivalFlowProblem problem2 = new EarliestArrivalFlowProblem(ThinNetwork.getEdgeCapacities(), ThinNetwork.getNetwork(), ThinNetwork.getNodeCapacities() , ThinNetwork.getSupersink(), ThinNetwork.getSources(), 100, ThinNetwork.getTransitTimes(), ThinNetwork.getCurrentAssignment());
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
    public void setConvOriginal(BaseZToGraphConverter Orig)
    {
        this.convOrig = Orig;
    }
    public void setConvThinNet(BaseZToGraphConverter Thin)
    {
        this.convThinNet = Thin;
    }
    public NetworkFlowModel getOriginal()
    {
        return OrigNetwork;
    }
            
}

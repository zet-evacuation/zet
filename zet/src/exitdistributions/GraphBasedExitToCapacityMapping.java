package exitdistributions;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.StaticPotential;
import ds.ca.TargetCell;
import ds.graph.IdentifiableCollection;
import ds.graph.NetworkFlowModel;
import ds.graph.Node;
import evacuationplan.BidirectionalNodeCellMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import util.ExitCapacityEstimator;

/**

 */
public class GraphBasedExitToCapacityMapping{
    
    private HashMap<StaticPotential, Double> exitToCapacityMapping; 
    private boolean isInitialized;     
    private NetworkFlowModel model;
    BidirectionalNodeCellMapping nodeCellMapping;    
    CellularAutomaton ca;
    
    public GraphBasedExitToCapacityMapping(CellularAutomaton ca, BidirectionalNodeCellMapping nodeCellMapping, NetworkFlowModel model){
        exitToCapacityMapping = new HashMap<StaticPotential, Double>();
        isInitialized = false;
        this.model = model;
        this.nodeCellMapping = nodeCellMapping;       
        this.ca = ca;
    }
    
    public HashMap<StaticPotential, Double> getExitCapacity(){        
        if(!isInitialized){
            initialize();
        }		        
        return exitToCapacityMapping;
    }
    
    public void calculate(){
        initialize();
    }
    
    private void initialize(){        
        calculateExitToCapacityMapping();
        isInitialized = true;		
    }    

    private void calculateExitToCapacityMapping(){
               
        IdentifiableCollection<Node> sinks = model.getNetwork().predecessorNodes(model.getSupersink());                
        ArrayList<StaticPotential> potentials = new ArrayList<StaticPotential>();
        potentials.addAll(ca.getPotentialManager().getStaticPotentials());
        ExitCapacityEstimator estimator = new ExitCapacityEstimator();        
        for (Node sink : sinks){        	
                ArrayList<Cell> sinkCells = nodeCellMapping.getCells(sink);
                TargetCell cell = findATargetCell(sinkCells);                                
                for (StaticPotential potential : potentials) {                                                                        				
                    if (potential.getAssociatedExitCells().contains(cell)) {
                                double value = estimator.estimateCapacityByMaximumFlow(model, sink);                                    
                                double previousValue = 0;
                                if (exitToCapacityMapping.get(potential) != null){                                    
                                    previousValue = exitToCapacityMapping.get(potential).doubleValue();
                                }                                    
                                Double newValue = Double.valueOf(value + previousValue);
                                exitToCapacityMapping.put(potential, newValue);
                        }
                }                                                             
        }
    }
    
    private TargetCell findATargetCell(Iterable<Cell> cellList){
        Cell targetCell = null;
        boolean targetCellFound = false;
        Iterator<Cell> it = cellList.iterator();
        while(!targetCellFound && it.hasNext()){
                Cell possibleTargetCell = it.next();
                if(possibleTargetCell instanceof TargetCell){
                        targetCellFound = true;
                        targetCell = possibleTargetCell;
                }
        }

        if(targetCellFound){
                return (TargetCell)targetCell;
        } else {
                return null;
        }
    }
    
}
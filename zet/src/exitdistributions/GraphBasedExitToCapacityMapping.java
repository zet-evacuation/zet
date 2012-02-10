/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package exitdistributions;

import ds.ca.evac.Cell;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.evac.StaticPotential;
import ds.ca.evac.TargetCell;
import ds.graph.IdentifiableCollection;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.Node;
import evacuationplan.BidirectionalNodeCellMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import algo.graph.exitassignment.ExitCapacityEstimator;

/**

 */
public class GraphBasedExitToCapacityMapping{
    
    private HashMap<StaticPotential, Double> exitToCapacityMapping; 
    private boolean isInitialized;     
    private NetworkFlowModel model;
    BidirectionalNodeCellMapping nodeCellMapping;    
    EvacuationCellularAutomaton ca;
    
    public GraphBasedExitToCapacityMapping(EvacuationCellularAutomaton ca, BidirectionalNodeCellMapping nodeCellMapping, NetworkFlowModel model){
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package exitdistributions;

import converter.ZToCAConverter;
import converter.ZToGraphRasterContainer;
import ds.ca.CellularAutomaton;
import ds.graph.NetworkFlowModel;
import ds.z.BuildingPlan;
import ds.z.ConcreteAssignment;
import evacuationplan.BidirectionalNodeCellMapping;
import evacuationplan.BidirectionalNodeCellMapping.CAPartOfMapping;

/**
 *
 * @author Joscha
 */
public class ExitCapacityBasedCAFactory  extends ZToCAConverter{
    
    private static ExitCapacityBasedCAFactory instance=null;
    
    private static GraphBasedExitToCapacityMapping graphBasedExitToCapacityMapping = null;
    
    protected ExitCapacityBasedCAFactory(){        
    }
    
    public CellularAutomaton convertAndApplyConcreteAssignment( BuildingPlan buildingPlan, NetworkFlowModel model, ConcreteAssignment concreteAssignment, ZToGraphRasterContainer graphRaster ) throws converter.ZToCAConverter.ConversionNotSupportedException {        
        CellularAutomaton ca = super.convert(buildingPlan);
        CAPartOfMapping caPartOfMapping = this.getLatestCAPartOfNodeCellMapping();
        applyConcreteAssignment(concreteAssignment);
        BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);                              
        graphBasedExitToCapacityMapping = new GraphBasedExitToCapacityMapping(ca, nodeCellMapping, model);
        graphBasedExitToCapacityMapping.calculate();
        ca.setExitToCapacityMapping( graphBasedExitToCapacityMapping.getExitCapacity() );
        return ca;
    }
    
    public static  ExitCapacityBasedCAFactory getInstance(){
        if (instance==null){
                instance = new ExitCapacityBasedCAFactory();
        }
        return instance;
    }

}

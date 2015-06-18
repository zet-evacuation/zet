/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds;

import org.zetool.math.vectormath.Vector2;
import org.zetool.opengl.framework.abs.VisualizationResult;

/**
 *
 * @author schwengf
 */
public class CompareVisualizationResults implements VisualizationResult{
    
    //needs only pairs of time and flow 
    int[] TimeFlowPairOrigNetwork;
    int[] TimeFlowPairThinNetwork;
    Vector2 xAxis;
    Vector2 yaxis;
    
    public CompareVisualizationResults(int[] values1, int[] values2)
    {
        this.TimeFlowPairOrigNetwork = values1;
        this.TimeFlowPairThinNetwork = values2;
        createAxes();
        createPoints();
    }
    
    private void createAxes()
    {
        this.xAxis = new Vector2(0,0);
        this.yaxis = new Vector2(0,2);
    }
    
    private void createPoints()
    {
        
    }
    
    public int getSize()
    {
        return TimeFlowPairOrigNetwork.length;
    }
}

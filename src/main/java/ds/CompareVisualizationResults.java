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
package ds;

import org.zetool.math.vectormath.Vector2;
import org.zetool.opengl.framework.abs.VisualizationResult;

/**
 *
 * @author Marlen Schwengfelder
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

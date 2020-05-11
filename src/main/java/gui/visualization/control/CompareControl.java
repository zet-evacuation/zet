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
package gui.visualization.control;

import org.zetool.math.vectormath.Vector3;
import ds.CompareVisualizationResults;
import gui.visualization.draw.graph.GLCompare;
import org.zetool.opengl.framework.abs.AbstractControl;
import org.zetool.opengl.framework.abs.DrawableControlable;

/**
 *
 * @author Marlen Schwengfelder
 */
public class CompareControl extends AbstractControl<CompareControl, GLCompare> {
   
   public Vector3 startPointxAxis;
   public Vector3 endPointxAxis;
   public Vector3 startPointyAxis;
   public Vector3 endPointyAxis; 
    

   public CompareControl(CompareVisualizationResults compVisRes)
   {
       super();
       
   }
   
   protected void setView() {
		System.out.println( "Axis were print" );
		setView(new GLCompare(this));
	}
   
   public void build(CompareVisualizationResults compVisRes)
   {
       int TimeCount = compVisRes.getSize();
       System.out.println("Size: " + TimeCount);
       GLCompare comp  = new GLCompare(this);    
       this.setView( comp );
       view.update();
      // comp.performDrawing(null);
       
   }
   
   public void delete() {
		view.delete();
	}
   
   
}

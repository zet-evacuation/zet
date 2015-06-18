/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.visualization.control;

import org.zetool.math.vectormath.Vector3;
import ds.CompareVisualizationResults;
import gui.visualization.draw.graph.GLCompare;
import org.zetool.opengl.framework.abs.AbstractControl;
import org.zetool.opengl.framework.abs.DrawableControlable;


/**
 *
 * @author schwengf
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

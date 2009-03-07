/**
 * Class JDynamicPotentialCell
 * Erstellt 05.05.2008, 18:16:07
 */

package gui.ca;

import gui.components.AbstractFloor;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JDynamicPotentialCell extends JPotentialCell {

	public JDynamicPotentialCell( AbstractFloor floor, int potential, int maxPotential ) {
		super( floor, potential, maxPotential );
		Color fillColor = potential > 0 ? new Color( 1.0f, 1-(float)potential/maxPotential, 1-(float)potential/maxPotential ) : Color.white;
		this.setFillColor( fillColor );
	}
	
	public JDynamicPotentialCell( AbstractFloor floor, Color lineColor, int potential, int maxPotential ) {
		super( floor, lineColor, potential, maxPotential );
		Color fillColor = potential > 0 ? new Color( 1.0f, 1-(float)potential/maxPotential, 1-(float)potential/maxPotential ) : Color.white;
		this.setFillColor( fillColor );
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
	}
}

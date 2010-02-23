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
/**
 * Class JPotentialCell
 * Erstellt 23.04.2008, 17:01:37
 */

package gui.ca;

import ds.PropertyContainer;
import gui.components.AbstractFloor;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JPotentialCell extends JCellPolygon {
	private int potential;
	private int maxPotential;
	private float colorValue = 0f;
	private boolean showPotentialValue;

	public JPotentialCell( AbstractFloor floor, int potential, int maxPotential ) {
		super( floor, potential > 0 ? new Color( 1-(float)potential/maxPotential, 1-(float)potential/maxPotential, 1.0f ) : Color.white );
		this.potential = potential;
		this.maxPotential = maxPotential;
		setToolTipText();
		showPotentialValue = PropertyContainer.getInstance().getAsBoolean( "editor.options.cavis.showIndividualNames" );	}

	/**
	 * 
	 * @param floor
	 * @param lineColor
	 * @param potential
	 * @param maxPotential
	 */
	public JPotentialCell( AbstractFloor floor, Color lineColor, int potential, int maxPotential ) {
		super( floor, potential > 0 ? new Color( 1-(float)potential/maxPotential, 1-(float)potential/maxPotential, 1.0f ) : Color.white, lineColor);
		colorValue = 1-(float)potential/maxPotential;
		this.potential = potential;
		this.maxPotential = maxPotential;
		setToolTipText();
		showPotentialValue = PropertyContainer.getInstance().getAsBoolean( "editor.options.cavis.showPotentialValue" );
	}
	
	@Override
	protected void setToolTipText() {
		String s = "<html>";
		s += "Potenzial: " + Integer.toString( potential );
		s += "</html>";
		setToolTipText( s );
	}

	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		if( !showPotentialValue )
			return;
		Graphics2D g2 = (Graphics2D) g;
		if( colorValue <= 0.5)
			drawName( Integer.toString( potential ), g2, Color.white );
		else
			drawName( Integer.toString( potential ), g2, Color.black );
	}

//	public boolean isShowPotentialValue() {
//		return showPotentialValue;
//	}
//
//	public void setShowPotentialValue( boolean showPotentialValue ) {
//		this.showPotentialValue = showPotentialValue;
//	}
//	
//	public void setMaxPotential( int maxPotential ) {
//		this.maxPotential = maxPotential;
//		setToolTipText();
//	}
//
//	public void setPotential( int potential ) {
//		this.potential = potential;
//		setToolTipText();
//	}
//
//	protected void setColorValue( float colorValue ) {
//		this.colorValue = colorValue;
//	}
//	
//	protected float getColorValue( ) {
//		return colorValue;
//	}
}

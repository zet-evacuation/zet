/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

/*
 * JCorner.java
 * Created on 14.12.2007, 16:05
 */
package de.tu_berlin.math.coga.components;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

/**
 * A simple component that fills itself with white color. Can be used to fill
 * unused corners of scroll panes.
 * @author Jan-Philipp Kappmeier
 */
public class JCorner extends JComponent {

	/** The color which is used to fill the corner. */
	Color color;

	/**
	 * Initializes a white corner.
	 */
	public JCorner() {
		this( Color.white );
	}

	/**
	 * Initializes a corner with arbitrary color.
	 * @param color the color of the corner
	 */
	public JCorner( Color color ) {
		this.color = color;
	}

	/**
	 * Draws the corner and fills the complete area with the specified color.
	 * @param g the graphics context
	 */
	@Override
	protected void paintComponent( Graphics g ) {
		g.setColor( color );
		g.fillRect( 0, 0, getWidth(), getHeight() );
	}
}

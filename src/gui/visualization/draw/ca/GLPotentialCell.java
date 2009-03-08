/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package gui.visualization.draw.ca;

import gui.visualization.control.ca.GLCellControl;

public class GLPotentialCell extends GLCell {
	// Vorlaeufige Konstanten bis Verwaltungsklasse fertig
	private static double REDCOLOR_COMPONENT = 0.5;
	private static double GREENCOLOR_COMPONENT = 0.5;
	private static double BLUECOLOR_COMPONENT = 0.5;

	public GLPotentialCell( GLCellControl control ) {
		super( control );
	//this.colorRed = GLPotentialCell.REDCOLOR_COMPONENT;
	//this.colorGreen = GLPotentialCell.GREENCOLOR_COMPONENT;
	//this.colorBlue = GLPotentialCell.BLUECOLOR_COMPONENT;
	}
}


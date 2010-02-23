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

/*
 * JLogView.java
 * Created 29.10.2009, 15:57:37
 */

package gui.editor;

import gui.components.JLogField;
import info.clearthought.layout.TableLayout;
import javax.swing.JPanel;

/**
 * The class <code>JLogView</code> ...
 * @author Jan-Philipp Kappmeier
 */
public class JLogView extends JPanel {

	/**
	 * Creates a new instance of <code>JLogView</code>.
	 */
	public JLogView() {
		double size[][] = // Columns
		{
			{ TableLayout.FILL },
			//Rows
			{ TableLayout.FILL }
		};

		setLayout( new TableLayout( size ) );

		JLogField logField = new JLogField();
		add( logField, "0,0" );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "JLogView";
	}
}

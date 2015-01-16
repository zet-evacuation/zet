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

package zet.gui.main.tabs.base;

import org.zetool.common.localization.Localized;
import de.tu_berlin.math.coga.components.JCorner;
import de.tu_berlin.math.coga.components.JRuler;
import gui.editor.CoordinateTools;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import zet.gui.GUILocalization;
import zet.gui.components.JZoomableRuler;

/**
 *
 * @param <T>
 * @author Jan-Philipp Kappmeier
 */
public class JFloorScrollPane<T extends AbstractFloor> extends JScrollPane implements Localized {
		/** The floor that is drawn. */
	private T floorPanel;
	/** The upper ruler. */
	private JZoomableRuler topRuler;
	/** The left ruler. */
	private JZoomableRuler leftRuler;
	/** A button used to change the units of the rulers. */
	private JButton unitButton;
	/** The localization string for the unit button. */
	private String locString = "gui.EditPanel.Unit.Meter";

	public JFloorScrollPane( T floorPanel ) {
		super( floorPanel );
		this.floorPanel = floorPanel;
		topRuler = new JZoomableRuler( JRuler.RulerOrientation.Horizontal, JRuler.RulerDisplayUnits.Meter );
		topRuler.setBigScaleStep( 2 );
		topRuler.setSmallScaleStep( 1 );
		topRuler.setZoomFactor( 0.1 );
		topRuler.setPreferredWidth( 2400 );
		leftRuler = new JZoomableRuler( JRuler.RulerOrientation.Vertical, JRuler.RulerDisplayUnits.Meter );
		leftRuler.setBigScaleStep( 2 );
		leftRuler.setSmallScaleStep( 1 );
		leftRuler.setZoomFactor( 0.1 );
		leftRuler.setPreferredHeight( 600 );
		setColumnHeaderView( topRuler );
		setRowHeaderView( leftRuler );

		JPanel buttonCorner = new JPanel();
		buttonCorner.setBackground( Color.white );
		unitButton = new JButton( "m" );
		unitButton.setFont( new Font( "SansSerif", Font.PLAIN, 11 ) );
		unitButton.setMargin( new Insets( 2, 2, 2, 2 ) );
		unitButton.setActionCommand( "unit" );
		unitButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				if( e.getActionCommand().equals( "unit" ) ) {
					JRuler.RulerDisplayUnits nextUnit = JRuler.RulerDisplayUnits.Centimeter;
					int bigStep = 40;
					int smallStep = 10;
					switch( topRuler.getDisplayUnit() ) {
						case Centimeter:
							nextUnit = JRuler.RulerDisplayUnits.Decimeter;
							bigStep = 4;
							smallStep = 1;
							locString = "gui.EditPanel.Unit.Decimeter";
							break;
						case Decimeter:
							nextUnit = JRuler.RulerDisplayUnits.Meter;
							bigStep = 2;
							smallStep = 1;
							locString = "gui.EditPanel.Unit.Meter";
							break;
						case Meter:
							nextUnit = JRuler.RulerDisplayUnits.Inch;
							bigStep = 10;
							smallStep = 5;
							locString = "gui.EditPanel.Unit.Inch";
							break;
						case Inch:
							nextUnit = JRuler.RulerDisplayUnits.Foot;
							bigStep = 5;
							smallStep = 1;
							locString = "gui.EditPanel.Unit.Foot";
							break;
						case Foot:
							nextUnit = JRuler.RulerDisplayUnits.Yard;
							bigStep = 2;
							smallStep = 1;
							locString = "gui.EditPanel.Unit.Yard";
							break;
						case Yard:
							nextUnit = JRuler.RulerDisplayUnits.Centimeter;
							bigStep = 40;
							smallStep = 10;
							locString = "gui.EditPanel.Unit.Centimeter";
							break;
					}
					topRuler.setDisplayUnit( nextUnit );
					topRuler.setBigScaleStep( bigStep );
					topRuler.setSmallScaleStep( smallStep );
					leftRuler.setDisplayUnit( nextUnit );
					leftRuler.setBigScaleStep( bigStep );
					leftRuler.setSmallScaleStep( smallStep );
					topRuler.repaint();
					leftRuler.repaint();
					unitButton.setText( nextUnit.toString() );
					unitButton.setToolTipText( GUILocalization.loc.getStringWithoutPrefix( locString ) );
				}
			}
		});
		buttonCorner.add( unitButton );
		unitButton.setToolTipText( GUILocalization.loc.getStringWithoutPrefix( locString ) );

		setCorner( JScrollPane.UPPER_LEFT_CORNER, buttonCorner );
		setCorner( JScrollPane.UPPER_RIGHT_CORNER, new JCorner() );
		setCorner( JScrollPane.LOWER_RIGHT_CORNER, new JCorner() );
		setCorner( JScrollPane.LOWER_RIGHT_CORNER, new JCorner() );
	}

	public final void setMainComponent( T floorPanel) {
		setViewportView( floorPanel );
		this.floorPanel = floorPanel;
	}

	public final T getMainComponent() {
		return floorPanel;
	}

	public JZoomableRuler getLeftRuler() {
		return leftRuler;
	}

	public JZoomableRuler getTopRuler() {
		return topRuler;
	}

	@Override
	public void localize() {
		unitButton.setToolTipText( GUILocalization.loc.getStringWithoutPrefix( locString ) );
	}

	/**
	 * @param zoomFactor A double in the range [0;1]
	 */
	public void setZoomFactor( double zoomFactor ) {
		CoordinateTools.setZoomFactor( zoomFactor );
		topRuler.setZoomFactor( zoomFactor );
		topRuler.repaint();
		leftRuler.setZoomFactor( zoomFactor );
		leftRuler.repaint();
	}
}

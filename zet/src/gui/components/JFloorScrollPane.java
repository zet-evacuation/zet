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
 * Class JFloorScrollPane
 * Erstellt 29.04.2008, 22:36:38
 */

package gui.components;

import gui.editor.CoordinateTools;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.localization.Localized;

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

	public JFloorScrollPane( T panel ) {
		super( panel );
		this.floorPanel = panel;
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
			public void actionPerformed( ActionEvent e ) {
				if( e.getActionCommand().equals( "unit" ) ) {
					JRuler.RulerDisplayUnits t = JRuler.RulerDisplayUnits.Centimeter;
					int bs = 40;
					int ss = 10;
					String m = "cm";
					String tt = Localization.getInstance().getString( "gui.editor.JEditorPanel.unitCentimeter" );
					switch( topRuler.getDisplayUnit() ) {
						case Centimeter:
							t = JRuler.RulerDisplayUnits.Decimeter;
							bs = 4;
							ss = 1;
							tt = Localization.getInstance().getString( "gui.editor.JEditorPanel.unitDecimeter" );
							break;
						case Decimeter:
							t = JRuler.RulerDisplayUnits.Meter;
							bs = 2;
							ss = 1;
							tt = Localization.getInstance().getString( "gui.editor.JEditorPanel.unitMeter" );
							break;
						case Meter:
							t = JRuler.RulerDisplayUnits.Inch;
							bs = 10;
							ss = 5;
							tt = Localization.getInstance().getString( "gui.editor.JEditorPanel.unitInch" );
							break;
						case Inch:
							t = JRuler.RulerDisplayUnits.Foot;
							bs = 5;
							ss = 1;
							tt = Localization.getInstance().getString( "gui.editor.JEditorPanel.unitFoot" );
							break;
						case Foot:
							t = JRuler.RulerDisplayUnits.Yard;
							bs = 2;
							ss = 1;
							tt = Localization.getInstance().getString( "gui.editor.JEditorPanel.unitYard" );
							break;
						case Yard:
							t = JRuler.RulerDisplayUnits.Centimeter;
							bs = 40;
							ss = 10;
							tt = Localization.getInstance().getString( "gui.editor.JEditorPanel.unitCentimeter" );
							break;
					}
					topRuler.setDisplayUnit( t );
					topRuler.setBigScaleStep( bs );
					topRuler.setSmallScaleStep( ss );
					leftRuler.setDisplayUnit( t );
					leftRuler.setBigScaleStep( bs );
					leftRuler.setSmallScaleStep( ss );
					topRuler.repaint();
					leftRuler.repaint();
					unitButton.setText( t.toString() );
					unitButton.setToolTipText( tt );
				}
			}
		});
		buttonCorner.add( unitButton );
		Localization.getInstance().setPrefix( "" );
		unitButton.setToolTipText( Localization.getInstance().getString( "gui.editor.JEditorPanel.unitMeter" ) );

		setCorner( JScrollPane.UPPER_LEFT_CORNER, buttonCorner );
		setCorner( JScrollPane.UPPER_RIGHT_CORNER, new JCorner() );
		setCorner( JScrollPane.LOWER_RIGHT_CORNER, new JCorner() );
		setCorner( JScrollPane.LOWER_RIGHT_CORNER, new JCorner() );
	}
	
	public T getMainComponent() {
		return floorPanel;
	}

	public JZoomableRuler getLeftRuler() {
		return leftRuler;
	}

	public JZoomableRuler getTopRuler() {
		return topRuler;
	}

	public void localize() {
		unitButton.setToolTipText( Localization.getInstance().getString( "gui.editor.JEditorPanel.unitMeter" ) );
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

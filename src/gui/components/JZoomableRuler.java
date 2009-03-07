/*
 * JZoomableRuler.java
 * Created on 14. Dezember 2007, 00:47
 */
package gui.components;

import gui.editor.CoordinateTools;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

/**
 * Displays a {@link JRuler} that is capable to perform zooming.
 * @author Jan-Philipp Kappmeier
 */
public class JZoomableRuler extends JRuler implements ChangeListener {
	/** Creates a new instance of JZoomableRuler
	 * @param orientation
	 * @param unit 
	 */
	public JZoomableRuler( RulerOrientation orientation, RulerDisplayUnits unit ) {
		super( orientation, unit );
	}

	/**
	 * Handles Swing events sent to the floor. Components adding some of the displaying styles of the
	 * floor can send their events to the <code>JFloor</code> class directly.
	 * <p>The possibility to handle events sent by slider events is implemented. The slider needs to
	 * have positive values (0 is not allowed, though). A value of 100 implies a zoom factor of 1, that
	 * is displayed as 1mm for 1 pixel.</p>
	 */
	public void stateChanged( javax.swing.event.ChangeEvent e ) {
		Object s = e.getSource();
		if( s instanceof JSlider ) {
			JSlider source = (JSlider)e.getSource();
			if( !source.getValueIsAdjusting() ) {
				CoordinateTools.setZoomFactor( source.getValue() * 0.01 );
				setZoomFactor( source.getValue() * 0.01 );
				repaint();
			}
		}

	}
}

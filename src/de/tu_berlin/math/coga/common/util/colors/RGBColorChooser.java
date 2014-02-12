/**
 * RGBColorChooser.java
 * Created: 12.02.2014, 13:10:21
 */
package de.tu_berlin.math.coga.common.util.colors;

import java.awt.Color;
import javax.swing.JColorChooser;
import javax.swing.colorchooser.AbstractColorChooserPanel;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RGBColorChooser extends JColorChooser {

	public RGBColorChooser( Color initialColor ) {
		super( initialColor );
		for( AbstractColorChooserPanel p : getChooserPanels() )
			if( !p.getDisplayName().equals( "RGB" ) )
				removeChooserPanel( p );
	}

}

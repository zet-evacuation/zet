/**
 * ColorProperty.java Created: 12.02.2014, 13:37:37
 */
package gui.propertysheet.types;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import gui.propertysheet.BasicProperty;
import java.awt.Color;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "colorNode" )
@XStreamConverter( ColorPropertyConverter.class )
@SuppressWarnings( "serial" )
public class ColorProperty extends BasicProperty<Color> {
	public ColorProperty() {
		super();
		setPropertyValue( Color.black );
		setType( Color.class );
	}

}

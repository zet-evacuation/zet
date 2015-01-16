/**
 * ColorPropertyConverter.java
 * Created: 12.02.2014, 13:37:46
 */
package gui.propertysheet.types;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import org.zetool.common.util.Formatter;
import gui.propertysheet.abs.AbstractPropertyConverter;
import java.awt.Color;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ColorPropertyConverter extends AbstractPropertyConverter<ColorProperty, Color> {
	public boolean canConvert( Class type ) {
		return type.equals( ColorProperty.class );
	}

	public String getNodeName() {
		return "colorNode";
	}

	public void createNewProp() {
		prop = new ColorProperty();
	}

	public void writeValue( MarshallingContext context ) {
		Color c = prop.getValue();
		context.convertAnother( Formatter.colorToHex( c ) );
	}

	/**
	 * Reads a hex string (e.g. #FFFFFF for white) and converts it to the
	 * corresponding color object.
	 * @param context
	 */
	public void readValue( UnmarshallingContext context ) {
		String bool = (String)context.convertAnother( prop, String.class );
		prop.setValue( Color.decode( bool ) );
	}
}

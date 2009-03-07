/**
 * Class BoolNodeConverter
 * Erstellt 09.04.2008, 22:08:24
 */

package gui.editor.properties.converter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import gui.editor.properties.framework.AbstractPropertyConverter;
import gui.editor.properties.types.BooleanProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BooleanPropertyConverter extends AbstractPropertyConverter<BooleanProperty, Boolean> {

	public boolean canConvert( Class type ) {
		return type.equals( BooleanProperty.class );
	}

	public String getNodeName() {
		return "boolNode";
	}
	
	public void createNewProp() {
		prop = new BooleanProperty();
	}
	
	public void writeValue( MarshallingContext context ) {
		context.convertAnother( new Boolean( prop.getValue() ) );
	}

	public void readValue( UnmarshallingContext context ) {
		Boolean bool = (Boolean)context.convertAnother( prop, Boolean.class );
		prop.setValue( bool );
	}
}

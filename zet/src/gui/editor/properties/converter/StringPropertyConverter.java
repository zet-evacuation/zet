/**
 * Class StringPropertyConverter
 * Erstellt 14.04.2008, 14:02:24
 */

package gui.editor.properties.converter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import gui.editor.properties.framework.AbstractPropertyConverter;
import gui.editor.properties.types.StringProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class StringPropertyConverter extends AbstractPropertyConverter<StringProperty, String> {

	public boolean canConvert( Class type ) {
		return type.equals( StringProperty.class );
	}

	@Override
	public String getNodeName() {
		return "stringNode";
	}

	@Override
	public void createNewProp() {
		prop = new StringProperty();
	}

	@Override
	public void writeValue( MarshallingContext context ) {
		context.convertAnother( prop.getValue() );
	}

	@Override
	public void readValue( UnmarshallingContext context ) {
		String string = (String)context.convertAnother( prop, String.class );
		prop.setValue( string );
	}
}

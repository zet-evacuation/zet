/**
 * Class StringListPropertyConverter
 * Erstellt 14.04.2008, 20:42:46
 */

package gui.editor.properties.converter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import gui.editor.properties.framework.AbstractPropertyConverter;
import gui.editor.properties.types.StringListProperty;
import java.util.ArrayList;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class StringListPropertyConverter extends AbstractPropertyConverter<StringListProperty, ArrayList<String>>  {

	public boolean canConvert( Class type ) {
		return type.equals( StringListProperty.class );
	}

	@Override
	public String getNodeName() {
		return "stringListNode";
	}

	@Override
	public void createNewProp() {
		prop = new StringListProperty();
	}

	@Override
	public void writeValue( MarshallingContext context ) {
		context.convertAnother( prop.getValue() );
	}

	@Override
	public void readValue( UnmarshallingContext context ) {
		ArrayList<String> string = (ArrayList<String>)context.convertAnother( prop, ArrayList.class );
		prop.setValue( string );
	}

}

/**
 * Class IntegerPropertyConverter
 * Erstellt 11.04.2008, 00:04:44
 */

package gui.editor.properties.converter;

import gui.editor.properties.framework.AbstractPropertyConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import gui.editor.properties.types.IntegerProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class IntegerPropertyConverter extends AbstractPropertyConverter<IntegerProperty, Integer> {
	public boolean canConvert( Class type ) {
		return type.equals( IntegerProperty.class );
	}

	/**
	 * 
	 * @return
	 */
	public String getNodeName() {
		return "intNode";
	}
	
	@Override
	public void createNewProp() {
		prop = new IntegerProperty();
	}
	
	@Override
	public void writeValue( MarshallingContext context ) {
		context.convertAnother( new Integer( prop.getValue() ) );
	}

	@Override
	public void readValue( UnmarshallingContext context ) {
		Integer intV = (Integer)context.convertAnother( prop, Integer.class );
		prop.setValue( intV );
	}
}

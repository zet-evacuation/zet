/**
 * Class DoublePropertyConverter
 * Erstellt 11.04.2008, 18:28:27
 */

package gui.editor.properties.converter;

import gui.editor.properties.framework.AbstractPropertyConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import gui.editor.properties.types.DoubleProperty;

/**
 *
 * @author Kapman
 */
public class DoublePropertyConverter extends AbstractPropertyConverter<DoubleProperty, Double>{

	public boolean canConvert( Class type ) {
		return type.equals( DoubleProperty.class );
	}

	@Override
	public String getNodeName() {
		return "doubleNode";
	}

	@Override
	public void createNewProp() {
		prop = new DoubleProperty();
	}

	@Override
	public void writeValue( MarshallingContext context ) {
		context.convertAnother( new Double( prop.getValue() ) );
	}

	@Override
	public void readValue( UnmarshallingContext context ) {
		Double doubleV = (Double)context.convertAnother( prop, Double.class );
		prop.setValue( doubleV );
	}

}

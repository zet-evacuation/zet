/**
 * Class IntegerRangePropertyConverter
 * Erstellt 11.04.2008, 17:58:57
 */

package gui.editor.properties.converter;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import gui.editor.properties.types.IntegerRangeProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class IntegerRangePropertyConverter extends IntegerPropertyConverter {

	@Override
	public boolean canConvert( Class type ) {
		return type.equals( IntegerRangeProperty.class );
	}

	@Override
	public String getNodeName() {
		return "intRangeNode";
	}
	
	@Override
	public void createNewProp() {
		prop = new IntegerRangeProperty();
	}
	
	@Override
	public void readAttributes( HierarchicalStreamReader reader ) {
		super.readAttributes( reader );
		((IntegerRangeProperty)prop).setMinValue( Integer.parseInt( reader.getAttribute( "minValue" ) ) );
		((IntegerRangeProperty)prop).setMaxValue( Integer.parseInt( reader.getAttribute( "maxValue" ) ) );
		((IntegerRangeProperty)prop).setMinorTick( Integer.parseInt( reader.getAttribute( "minorTick" ) ) );
		((IntegerRangeProperty)prop).setMajorTick( Integer.parseInt( reader.getAttribute( "majorTick" ) ) );
	}

	@Override
	public void writeAttributes( HierarchicalStreamWriter writer ) {
		super.writeAttributes( writer );
		writer.addAttribute( "minValue", Integer.toString( ((IntegerRangeProperty)prop).getMinValue() ) );
		writer.addAttribute( "maxValue", Integer.toString( ((IntegerRangeProperty)prop).getMaxValue() ) );
		writer.addAttribute( "minorTick", Integer.toString( ((IntegerRangeProperty)prop).getMinorTick() ) );
		writer.addAttribute( "majorTick", Integer.toString( ((IntegerRangeProperty)prop).getMajorTick() ) );
	}
}

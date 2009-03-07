/**
 * Class AbstractPropertyConverter
 * Erstellt 11.04.2008, 17:27:12
 */

package gui.editor.properties.framework;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * 
 * @param T die Propertyselector-Klasse
 * @param U die Klasse der Property selbst
 * @author Kapman
 */
public abstract class AbstractPropertyConverter<T extends AbstractPropertyValue<U>, U extends Object> implements Converter {
	protected T prop;
	
	public abstract String getNodeName();
	
	public abstract void createNewProp();

	public abstract void writeValue( MarshallingContext context );

	public abstract void readValue( UnmarshallingContext context );
	
	public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
    prop = (T)source;
    writer.startNode( getNodeName() );
		writeAttributes( writer );
		writeValue( context );
    writer.endNode();
	}
	
	public void readAttributes( HierarchicalStreamReader reader ) {
    String name = reader.getAttribute( "name" );
		prop.setName( name );
    prop.useAsLocString( (reader.getAttribute( "useAsLocString" ).equals("true")?true:false ) );
		prop.setDescription( reader.getAttribute( "description" ) );
		prop.setInformation( reader.getAttribute( "information" ) );
		prop.setPropertyName( reader.getAttribute( "parameter" ) );
	}
	
	public void writeAttributes( HierarchicalStreamWriter writer ) {
    writer.addAttribute( "name", prop.getNameTag() );
    writer.addAttribute( "useAsLocString", Boolean.toString( prop.isUsedAsLocString() ) );
		writer.addAttribute( "description", prop.getDescriptionTag() );
		writer.addAttribute( "information", prop.getInformationTag() );
		writer.addAttribute( "parameter", prop.getPropertyName() );
	}

	public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
    //prop = new BooleanProperty();
		createNewProp();
		readAttributes( reader );
		readValue( context );
    return prop;
	}

}

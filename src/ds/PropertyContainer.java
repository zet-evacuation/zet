/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * PropertyContainer.java
 *
 */
package ds;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import gui.editor.properties.PropertyLoadException;
import gui.editor.properties.PropertyTreeModel;
import gui.editor.properties.types.BooleanProperty;
import gui.editor.properties.PropertyTreeNode;
import gui.editor.properties.framework.AbstractPropertyValue;
import gui.editor.properties.types.IntegerProperty;
import gui.editor.properties.types.DoubleProperty;
import gui.editor.properties.types.QualitySettingProperty;
import gui.editor.properties.types.StringProperty;
import gui.editor.properties.types.StringListProperty;
import gui.visualization.QualityPreset;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tu_berlin.math.coga.common.localization.Localization;

/**
 * Stores properties of arbitrary type accessible via a string.
 */
public class PropertyContainer {
	/** The instance of the singleton <code>PropertyContainer</code>.*/
	private static PropertyContainer instance;

	/**
	 * Returns the instance of the singleton <code>PropertyContainer</code>.
	 * @return the instance of the singleton <code>PropertyContainer</code>
	 */
	public static PropertyContainer getInstance() {
		if( instance == null )
			instance = new PropertyContainer();
		return instance;
	}
	protected Map<String, Object> properties;
	protected Map<String, Class<?>> propertyTypes;

	private PropertyContainer() {
		properties = new HashMap<String, Object>();
		propertyTypes = new HashMap<String, Class<?>>();
	}

	public <T> void define( String key, Class<T> type, T defaultValue ) {
		if( propertyTypes.containsKey( key ) )
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.PropertyAlreadyDefinedException" + key ) );
		else {
			properties.put( key, defaultValue );
			propertyTypes.put( key, type );
		}
	}

	public Object get( String key ) {
		if( !propertyTypes.containsKey( key ) )
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.PropertyNotDefinedException" + key ) );
		return properties.get( key );
	}

	public <T> T getAs( String key, Class<T> type ) {
		if( !propertyTypes.containsKey( key ) )
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.PropertyNotDefinedException" + key ) );
		else
			if( !type.isAssignableFrom( propertyTypes.get( key ) ) )
				throw new IllegalArgumentException( Localization.getInstance().getString( "ds.PropertyTypeCastException" + key + ", " + propertyTypes.get( key ) + ", " + type ) );
			else
				return type.cast( properties.get( key ) );
	}

	public boolean getAsBoolean( String key ) {
		return getAs( key, Boolean.class );
	}

	public Color getAsColor( String key ) {
		return getAs( key, Color.class );
	}

	public double getAsDouble( String key ) {
		return getAs( key, Double.class );
	}

	public int getAsInt( String key ) {
		return getAs( key, Integer.class );
	}

	public Font getAsFont( String key ) {
		return getAs( key, Font.class );
	}

	public String getAsString( String key ) {
		return getAs( key, String.class );
	}

	public ArrayList<String> getAsStringList( String key ) {
		return (ArrayList<String>)getAs( key, ArrayList.class );
	}

	public void set( String key, Object value ) {
		if( !propertyTypes.containsKey( key ) )
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.PropertyNotDefinedException" + key ) );
		else
			if( !propertyTypes.get( key ).isInstance( value ) )
				throw new IllegalArgumentException( Localization.getInstance().getString( "ds.PropertyValueException" + key + ", " + propertyTypes.get( key ) + ", " + value ) );
			else
				properties.put( key, value );
	}

	public boolean isDefined( String key ) {
		return propertyTypes.containsKey( key );
	}

	public Class<?> getType( String key ) {
		return propertyTypes.get( key );
	}

	/**
	 * Loads a file containing the configuration in XML-Format.
	 * @param file the property file
	 * @return the tree model containing the properties
	 * @throws PropertyLoadException if an xstream error occured when the file is read
	 */
	public static PropertyTreeModel loadConfigFile( File file ) throws PropertyLoadException {
		PropertyTreeModel propertyTreeModel = null;
		try {
			XStream xstream = new XStream();
			Annotations.configureAliases( xstream, PropertyTreeModel.class );
			Annotations.configureAliases( xstream, PropertyTreeNode.class );
			propertyTreeModel = (PropertyTreeModel)xstream.fromXML( new FileReader( file ) );
		} catch( Exception ex ) {
			throw new PropertyLoadException( file );
		}
		return propertyTreeModel;
	}

	/**
	 * Saves a file containing the configuration given in a
	 * {@link PropertyTreeModel} in XML-Format.
	 * @param propertyTreeModel the model that should be written to the file
	 * @param file the file
	 * @throws java.io.IOException if an error during writing occurs
	 */
	public static void saveConfigFile( PropertyTreeModel propertyTreeModel, File file ) throws IOException {
		XStream xstream = new XStream();
		Annotations.configureAliases( xstream, PropertyTreeModel.class );
		Annotations.configureAliases( xstream, PropertyTreeNode.class );
		PropertyTreeNode root = propertyTreeModel.getRoot();
		List<AbstractPropertyValue> props = root.getProperties();
		if( props.size() > 0 ) {
			StringProperty name = (StringProperty)props.get( 0 );
			propertyTreeModel.setPropertyName( name.getValue() );
		}
		xstream.toXML( propertyTreeModel, new FileWriter( file ) );
	}

	/**
	 * Loads properties from an XML-file into the {@link PropertyContainer} and
	 * returns a {@link PropertyTreeModel} of the properties. This can be used to
	 * store the same (maybe changed) data later.
	 * @param file the property XML-file
	 * @return a model of the loaded properties
	 * @throws PropertyLoadException if an error occurs during loading of the specified file
	 */
	public PropertyTreeModel applyParameters( File file ) throws PropertyLoadException {
		final PropertyTreeModel ptm = loadConfigFile( file );
		applyParameters( ptm );
		return ptm;
	}

	/**
	 * Loads properties from an {@link PropertyTreeModel} into the
	 * {@link PropertyContainer}.
	 * @param propertyTreeModel the tree model containing the properties
	 */
	public void applyParameters( PropertyTreeModel propertyTreeModel ) {
		applyParameters( propertyTreeModel.getRoot() );
	}

	/**
	 * Loads properties stored in an node of an {@link PropertyTreeModel} into the
	 * {@link PropertyContainer}.
	 * @param node the node at which recursiv loading starts.
	 */
	protected void applyParameters( PropertyTreeNode node ) {
		for( int i = 0; i < node.getChildCount(); i++ )
			applyParameters( (PropertyTreeNode)node.getChildAt( i ) );
		PropertyContainer pc = PropertyContainer.getInstance();
		for( gui.editor.properties.framework.AbstractPropertyValue property : node.getProperties() )
			if( property instanceof BooleanProperty )
				if( !pc.isDefined( property.getPropertyName() ) )
					pc.define( property.getPropertyName(), Boolean.class, (Boolean)property.getValue() );
				else
					pc.set( property.getPropertyName(), (Boolean)property.getValue() );
			else if( property instanceof IntegerProperty )
				if( !pc.isDefined( property.getPropertyName() ) )
					pc.define( property.getPropertyName(), Integer.class, (Integer)property.getValue() );
				else
					pc.set( property.getPropertyName(), (Integer)property.getValue() );
			else if( property instanceof DoubleProperty )
				if( !pc.isDefined( property.getPropertyName() ) )
					pc.define( property.getPropertyName(), Double.class, (Double)property.getValue() );
				else
					pc.set( property.getPropertyName(), (Double)property.getValue() );
			else if( property instanceof StringProperty )
				if( !pc.isDefined( property.getPropertyName() ) )
					pc.define( property.getPropertyName(), String.class, (String)property.getValue() );
				else
					pc.set( property.getPropertyName(), (String)property.getValue() );
			else if( property instanceof StringListProperty )
				if( !pc.isDefined( property.getPropertyName() ) )
					pc.define( property.getPropertyName(), ArrayList.class, (ArrayList)property.getValue() );
				else
					pc.set( property.getPropertyName(), (ArrayList)property.getValue() );
			else if( property instanceof QualitySettingProperty ) {
				if( !pc.isDefined( property.getPropertyName() ) )
					pc.define( property.getPropertyName(), QualityPreset.class, (QualityPreset)property.getValue() );
				else
					pc.set( property.getPropertyName(), (QualityPreset)property.getValue() );
			}
	}
}

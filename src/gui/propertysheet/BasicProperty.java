
package gui.propertysheet;

import com.l2fprod.common.propertysheet.DefaultProperty;
import de.tu_berlin.math.coga.zet.ZETLocalization2;
import ds.PropertyContainer;
import gui.propertysheet.abs.PropertyElement;
import gui.propertysheet.abs.PropertyValue;

/**
 *
 * @param <T> 
 * @author Jan-Philipp Kappmeier
 */
public class BasicProperty<T> extends DefaultProperty implements PropertyValue<T>, PropertyElement {
	private static final long serialVersionUID = 1L;
	boolean useAsLocString = false;
	
	public BasicProperty( ) {
		super();
	}

	public BasicProperty( String name, String displayName ) {
		super();
		setName( name );
		setDisplayName( displayName );
	}

	public void store() {
		try {
			if( getName() != null && PropertyContainer.getInstance().isDefined( getName() ) )
				PropertyContainer.getInstance().set( getName(), getValue() );
			else
				System.out.println( "NOT DEFINED: " + getName() );
			
		} catch( Exception e ) {
			System.out.println( "ERROR STORING THIS" );
		}
	}

	public void reloadFromPropertyContainer() {
		setPropertyValue( (T)PropertyContainer.getInstance().get( getName() ) );
	}

	/**
	 * Returns the detailed description for the property.
	 * @return the detailed description for the property
	 */
	@Override
	public String getDisplayName() {
		return isUsedAsLocString() ? ZETLocalization2.loc.getString( super.getDisplayName() ) : super.getDisplayName();
	}

	@Override
	public String getDisplayNameTag() {
		return super.getDisplayName();
	}

	/**
	 * Returns the value for the property.
	 * @return the value for the property
	 */
	@Override
	public T getValue() {
		return (T)super.getValue();
	}

	@Override
	public String getShortDescription() {
		return isUsedAsLocString() ? ZETLocalization2.loc.getString( super.getShortDescription() ) : super.getShortDescription();
	}

	@Override
	public String getShortDescriptionTag() {
		return super.getShortDescription();
	}

	/**
	 * Sets the description for the property. Note that you cannot change the
	 * description if it shall be used as a tag for localized string. In that
	 * case, you can only change the tag.
	 * @param text the description
	 */
	
	public void setShortDescription( String text ) {
		super.setShortDescription( text );
	}

	/**
	 * Returns the name of the property. That is the name by which it can be
	 * accessed in the {@link PropertyContainer}.
	 * @return the name of the property
	 */
//	@Override
//	public String getPropertyName() {
//		return property;
//	}

	/**
	 * Sets the name of the property. That is the name by which it can be accessed
	 * using the {@link PropertyContainer}.
	 * @param property the name of the property
	 * @return  
	 */
//	@Override
//	public void setPropertyName( String property ) {
//		this.property = property;
//	}

	@Override
	public boolean isUsedAsLocString() {
		return useAsLocString;
	}

	@Override
	public void useAsLocString( boolean useAsLocString ) {
		this.useAsLocString = useAsLocString;
	}

//	@Override
//	public String getNameTag() {
//		return getName();
//	}	

	@Override
	public void setPropertyValue( T defaultValue ) {
		setValue( defaultValue );
	}

}

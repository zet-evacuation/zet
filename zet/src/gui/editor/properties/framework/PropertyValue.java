/*
 * Interface PropertyValue
 * Erstellt 09.04.2008, 21:48:35
 */

package gui.editor.properties.framework;

/**
 *
 * @param T 
 * @author Jan-Philipp Kappmeier
 */
public interface PropertyValue<T> {

	public String getDescription();

	public String getDescriptionTag();
	
	public void setDescription( String text );
	
	public Object getValue();
	
	public void setValue( T defaultValue);
	
	public String getInformation();
	
	public String getInformationTag();
	
	public void setInformation( String text);
	
	public String getPropertyName();

	public void setPropertyName( String property );
}

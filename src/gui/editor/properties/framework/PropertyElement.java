/*
 * Interface PropertyElement
 * Erstellt 09.04.2008, 21:29:41
 */
package gui.editor.properties.framework;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface PropertyElement {
	
	public boolean isUsedAsLocString();
	
	public void useAsLocString( boolean useAsLocString );
	
	public String getName();
	
	public String getNameTag();
	
	public void setName( String name );
}

/**
 * TemplateTypes.java
 * Created: 21.09.2012, 10:48:18
 */
package de.tu_berlin.math.coga.zet.template;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public enum TemplateTypes {
	Door( "door" ),
	Delay( "delay" ),
	Stair( "stair" ),
	Properties( "properties" );
	private String xmlCode;

	private TemplateTypes( String xmlCode ) {
		this.xmlCode = xmlCode;
	}

	public String getXmlCode() {
		return xmlCode;
	}
	
	
}

/**
 * TemplateTypes.java
 * Created: 21.09.2012, 10:48:18
 */
package ds.z.template;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public enum TemplateTypes {
	Door( "door" ),
	ExitDoor( "exitDoor" ),
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

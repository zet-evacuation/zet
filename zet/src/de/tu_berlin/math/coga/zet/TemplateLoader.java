/**
 * TemplateLoader.java Created: 20.09.2012, 16:59:15
 */
package de.tu_berlin.math.coga.zet;

import de.tu_berlin.math.coga.common.debug.Debug;
import de.tu_berlin.math.coga.zet.template.Door;
import de.tu_berlin.math.coga.zet.template.TemplateTypes;
import de.tu_berlin.math.coga.zet.template.Templates;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TemplateLoader extends DefaultHandler {
	/** The logger for the template loader. */
	private final static Logger log = Debug.globalLogger;
	private SAXParser saxParser;
	private TemplateTypes type = null;
	private boolean start = false;
	private Templates<Door> doors = new Templates<>( "doors" );
//	private Templates<Delay> delays;
//	private Templates<Stair> stairs;
//	private Templates<Properties> properties;

	public TemplateLoader() throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		saxParser = factory.newSAXParser();
	}

	public void parse( File f ) throws SAXException, IOException {
		saxParser.parse( f, this );
	}

	@Override
	public void startElement( String namespaceURI, String localName, String qName, Attributes atts ) throws SAXException {
		if( start == false && qName.equals( "tmpl" ) ) { // we have not started yet. check if the type is correct.
			for( int i = 0; i < atts.getLength() && start == false; i++ )
				if( atts.getQName( i ).equals( "type" ) )
					for( TemplateTypes t : TemplateTypes.values() )
						if( atts.getValue( i ).equals( t.getXmlCode() ) ) {
							type = t;
							start = true;
							String name = atts.getValue( "name" );
							switch( t ) {
								case Door:
									doors = new Templates<>( name == null ? "doors" : name );
							}
							break;
						}
		} else if( start == true && qName.equals( "entry" ) )
			readTemplateEntry( atts );
	}

	private void readTemplateEntry( Attributes atts ) {
		switch( type ) {
			case Door:
				String name = atts.getValue( "name") == null ? "door" : atts.getValue( "name" );
				double priority = Double.parseDouble( atts.getValue( "priority" ) == null ? "1" : atts.getValue( "priority" ) );
				int size = Integer.parseInt( atts.getValue( "size" ) == null ? "800" : atts.getValue( "size" ) );
				doors.add( new Door( name == null ? "door" : name, size, priority ) );
				log.fine( "Door '" + name + "' found with priority '" + priority + "' and size '" + size + "'" );
		}
	}

	@Override
	public void endElement( String namespaceURI, String localName, String qName ) {
		if( start == true && qName.equals( "tmpl" ) ) {
			start = false;
			switch( type ) {
				case Door:
					log.config( "Door templates '" + doors.getName() + "' loaded." );
			}
		}
	}

	/**
	 * <p>Returns a list of {@link Door} templates. If no doors have been read so
	 * far, an empty list is returned. If several template files containing
	 * {@link Door} templates have been read, the last set of templates is
	 * returned.</p>
	 * <p>The returned list is the actual list, and the list can be modified.
	 * After a file has been loaded completely, the list is never changed from
	 * inside this class.</p>
	 * @return a list of {@link Door} templates that may be empty
	 */
	public Templates<Door> getDoors() {
		return doors;
	}
	
	public static void main( String args[] ) throws ParserConfigurationException, SAXException, IOException {
		String file = "./templates/door_default.xml";

		Debug.setDefaultLogLevel( Level.ALL );
		Debug.setUpLogging();


		TemplateLoader sax = new TemplateLoader();
		sax.parse( new File( file ) );
	}
}

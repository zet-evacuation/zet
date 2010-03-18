/*
 * XMLReader.java
 *
 */
package zet.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Martin Groß
 */
public class XMLReader {

	private File file;
	private XStream xstream;

	public XMLReader( String filename ) throws IOException {
		this( new File( filename ) );
	}

	public XMLReader( File file ) throws IOException {
		this.file = file;
		xstream = new XStream( new DomDriver() );
	}

	public Object read() throws IOException {
		BufferedReader reader = null;
		Object result = null;
		try {
			reader = new BufferedReader( new FileReader( file ) );
			xstream.alias( "flowVisualization", FlowVisualization.class );
			xstream.setMode( XStream.NO_REFERENCES );
			xstream.registerConverter( new ColorConverter() );
			xstream.registerConverter( new FontConverter() );
			xstream.registerConverter( new GraphViewConverter() );
			xstream.registerConverter(new FlowVisualisationConverter());
			//xstream.registerConverter(new NodeShapeConverter());
			result = xstream.fromXML( reader );
		} finally {
			if( reader != null )
				reader.close();
		}
		return result;
	}
}

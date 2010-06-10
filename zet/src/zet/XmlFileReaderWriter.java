/**
 * XmlFileReaderWriter.java
 * Created: 18.03.2010, 10:53:48
 */
package zet;

import java.io.IOException;
import zet.xml.FlowVisualization;
import zet.xml.XMLReader;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class XmlFileReaderWriter {

	public static void main( String[] argumengs ) throws IOException {
		XMLReader reader = new XMLReader( "./testinstanz/test.xml" );

		FlowVisualization fv = (FlowVisualization)reader.read();

	}
}

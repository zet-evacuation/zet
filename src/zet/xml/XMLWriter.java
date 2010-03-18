/*
 * XMLWriter.java
 *
 */

package zet.xml;

import com.thoughtworks.xstream.XStream;
//import fv.gui.view.FlowVisualisation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Martin Groß
 */
public class XMLWriter {
    
    private File file;
    private XStream xstream;
    
    public XMLWriter(String filename) throws IOException {
        this(new File(filename));
    }
    
    public XMLWriter(File file) throws IOException {
        this.file = file;
        xstream = new XStream();
    }
    
    public void write(Object object) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            //xstream.alias("flowVisualisation",FlowVisualisation.class);
            //xstream.setMode(XStream.NO_REFERENCES);
            //xstream.registerConverter(new ColorConverter());
            //xstream.registerConverter(new FontConverter());
            //xstream.registerConverter(new GraphViewConverter());
            //xstream.registerConverter(new FlowVisualisationConverter());
            //xstream.registerConverter(new NodeShapeConverter());
            xstream.toXML(object,writer);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
    
}

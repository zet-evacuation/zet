/*
 * FontConverter.java
 *
 */

package zet.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.awt.Font;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author Martin Groß
 */
public class FontConverter implements Converter {
    
    public FontConverter() {
    }
    
    public boolean canConvert(Class type) {
        return type.equals(Font.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Font font = (Font) source;
        writer.startNode("name");
        writer.setValue(font.getName());
        writer.endNode();
        writer.startNode("style");
        if (font.getStyle() == Font.PLAIN) {
            writer.setValue("plain");
        } else if (font.getStyle() == Font.BOLD) {
            writer.setValue("bold");
        } else if (font.getStyle() == Font.ITALIC) {
            writer.setValue("italic");
        } else if (font.getStyle() == Font.BOLD + Font.ITALIC) {
            writer.setValue("bold, italic");
        } else {
            System.err.println("FontConverter: Unexpected Font Style: "+font.getStyle());
        }
        writer.endNode();
        writer.startNode("size");
        writer.setValue(String.valueOf(font.getSize()));
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String name = "";
        int style = 0;
        int size = 12;        
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("name")) {
                name = reader.getValue();
            } else if (reader.getNodeName().equals("style")) {
                String str = reader.getValue().trim().toLowerCase();
                String[] s = str.split("\\s*,\\s*");
                for (int i=0; i<s.length; i++) {
                    if (s[i].equals("bold")) {
                        style += Font.BOLD;
                    } else if (s[i].equals("italic")) {
                        style += Font.ITALIC;
                    } else if (s[i].equals("plain")) {
                        style += Font.PLAIN;
                    } else {
                        System.err.println("Unexpected Font Style: "+s[i]);
                    }
                }
            } else if (reader.getNodeName().equals("size")) {
                size = Integer.parseInt(reader.getValue());
            } 
            reader.moveUp();
        }
        Font font = new Font(name,style,size);
        if (context.getRequiredType() == FontUIResource.class) {
            return new FontUIResource(font);
        } else {
            return font;
        }        
    }
    
}

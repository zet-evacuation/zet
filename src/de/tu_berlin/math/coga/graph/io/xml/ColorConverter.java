/*
 * ColorConverter.java
 *
 */

package de.tu_berlin.math.coga.graph.io.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.awt.Color;

/**
 *
 * @author Martin Groß
 */
public class ColorConverter implements Converter {
    
    public boolean canConvert(Class type) {
        return (type != null) && type.getName().equals("java.awt.Color");
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Color color = (Color) source;
        writer.setValue(String.format("(%1$s,%2$s,%3$s,%4$s)",color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha()));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String value = reader.getValue().trim();
        value = value.substring(1,value.length()-1).trim();
        String[] colors = value.split("\\s*,\\s*");
        return new Color(Integer.parseInt(colors[0]),
                    Integer.parseInt(colors[1]),
                Integer.parseInt(colors[2]),
                Integer.parseInt(colors[3]));
    }
    
    public static Color convert(String value) {
        value = value.substring(1,value.length()-1).trim();
        String[] colors = value.split("\\s*,\\s*");
        return new Color(Integer.parseInt(colors[0]),
                    Integer.parseInt(colors[1]),
                Integer.parseInt(colors[2]),
                Integer.parseInt(colors[3]));
    }
    
}

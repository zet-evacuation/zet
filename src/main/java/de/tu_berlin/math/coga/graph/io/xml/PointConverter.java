/*
 * PointConverter.java
 *
 */

package de.tu_berlin.math.coga.graph.io.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.awt.geom.Point2D;

/**
 *
 * @author Martin Groß
 */
public class PointConverter implements Converter {
    
    public PointConverter() {
    }
    
    public boolean canConvert(Class type) {
        return type.equals(Point2D.Double.class) || type.equals(Point2D.Float.class);
    }    

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Point2D point = (Point2D) source;
        writer.setValue(toString(point));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return fromString(reader.getValue());
    }
    
    public static Point2D fromString(String string) {
        string = string.trim();
        string = string.substring(1,string.length()-1).trim();
        String[] c = string.split("\\s*,\\s*");
        return new Point2D.Double(Double.parseDouble(c[0]),Double.parseDouble(c[1]));
    }
    
    public static String toString(Point2D point) {
        return "("+point.getX()+","+point.getY()+")";
    }

    public static String toString(Point2D[] points) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Point2D point : points) {
            builder.append(toString(point));
            builder.append(",");
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append("]");
        return builder.toString();
    }    
    
}

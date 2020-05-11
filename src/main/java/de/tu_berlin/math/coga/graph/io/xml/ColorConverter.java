/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
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

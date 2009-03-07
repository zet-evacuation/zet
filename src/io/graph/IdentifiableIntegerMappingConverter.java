/*
 * IdentifiableMappingConverter.java
 *
 */
package io.graph;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import ds.graph.Identifiable;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;

/**
 *
 */
public class IdentifiableIntegerMappingConverter implements Converter {

    protected int defaultValue;
    
    protected IdentifiableCollection<? extends Identifiable> domain;

    public boolean canConvert(Class type) {
        return type.equals(IdentifiableIntegerMapping.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        IdentifiableIntegerMapping mapping = (IdentifiableIntegerMapping) source;
        writer.addAttribute("number", String.valueOf(mapping.getDomainSize()));
        /*
        TreeMap<Integer, LinkedList<Identifiable>> sortedMap = sort(mapping);
        for (Integer integer : sortedMap.keySet()) {
        XStreamAlias alias = domain.first().getClass().getAnnotation(XStreamAlias.class);
        writer.startNode(alias.value());
        writer.addAttribute("id", compress(sortedMap.get(integer)));
        writer.setValue(String.valueOf(mapping.get(sortedMap.get(integer).getFirst())));
        writer.endNode();
        }*/        
        for (Identifiable identifiable : domain) {            
            if (mapping.get(identifiable) == defaultValue) {
                continue;
            }
            System.out.println(mapping.get(identifiable) + " " + defaultValue + " " + (mapping.get(identifiable) == defaultValue));
            XStreamAlias alias = identifiable.getClass().getAnnotation(XStreamAlias.class);
			if (alias != null) {
	            writer.startNode (alias.value ());
			} else {
	            writer.startNode (identifiable.getClass().getCanonicalName ());
			}
            writer.addAttribute("id", String.valueOf(identifiable.id()));
            writer.setValue(String.valueOf(mapping.get(identifiable)));
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        int size = Integer.parseInt(reader.getAttribute("number"));
        IdentifiableIntegerMapping mapping = new IdentifiableIntegerMapping(size);
        for (Identifiable identifiable : domain) {
            mapping.set(identifiable, defaultValue);
        }
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String line = reader.getAttribute("id");
            int value = Integer.parseInt(reader.getValue());
            String[] idStrings = split(line);
            for (String idString : idStrings) {
                if (isRange(idString)) {
                    for (int id = first(idString); id <= last(idString); id++) {
                        mapping.set(domain.get(id), value);
                    }
                } else {
                    int id = Integer.parseInt(idString);
                    mapping.set(domain.get(id), value);
                }
            }
            reader.moveUp();
        }
        return mapping;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public IdentifiableCollection<? extends Identifiable> getDomain() {
        return domain;
    }

    public void setDomain(IdentifiableCollection<? extends Identifiable> domain) {
        this.domain = domain;
    }

    protected static String[] split(String s) {
        return s.replaceAll("\\s", "").split(",");
    }

    protected static boolean isRange(String s) {
        return s.contains("-");
    }

    protected static int first(String s) {
        return Integer.parseInt(s.substring(0, s.indexOf("-")));
    }

    protected static int last(String s) {
        return Integer.parseInt(s.substring(s.indexOf("-") + 1));
    }
    /*
    protected TreeMap<Integer, LinkedList<Identifiable>> sort(IdentifiableIntegerMapping mapping) {
    TreeMap<Integer, LinkedList<Identifiable>> result = new TreeMap<Integer, LinkedList<Identifiable>>();
    for (Identifiable identifiable : domain) {
    int value = mapping.get(identifiable);
    if (!result.containsKey(value)) {
    result.put(value, new LinkedList<Identifiable>());
    }
    result.get(value).add(identifiable);
    }
    return result;
    }
    protected String compress(LinkedList<Identifiable> list) {
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    int start = -1;
    int startValue = -1;
    for (Identifiable identifiable : list) {
    if (start == -1) {
    start = identifiable.id();
    startValue 
    }
    current = identifiable.id();
    if (identifiable.id() > start + 1) {
    if (!first) {
    builder.append(", ");
    }
    if (start + 1 < current) {
    builder.append(String.format("%1$s-%2$s", start, current));
    } else if (start + 1 == current) {
    builder.append(String.format("%1$s, %2$s", start, current));
    } else if (start == current) {
    builder.append(String.format("%1$s", start));
    }
    start = -1;
    first = false;
    }
    }
    if (start != -1) {
    if (start + 1 < current) {
    builder.append(String.format("%1$s-%2$s", start, current));
    } else if (start + 1 == current) {
    builder.append(String.format("%1$s, %2$s", start, current));
    } else if (start == current) {
    builder.append(String.format("%1$s", start));
    }
    }
    return builder.toString();
    }*/
}

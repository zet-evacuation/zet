///*
// * NodeShapeConverter.java
// *
// */
//
//package zet.xml;
//
//import com.thoughtworks.xstream.converters.Converter;
//import com.thoughtworks.xstream.converters.MarshallingContext;
//import com.thoughtworks.xstream.converters.UnmarshallingContext;
//import com.thoughtworks.xstream.io.HierarchicalStreamReader;
//import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
////import fv.gui.view.NodeShape;
//
///**
// *
// * @author Martin Gro�
// */
//public class NodeShapeConverter implements Converter {
//
//    public boolean canConvert(Class type) {
//        return type.equals(NodeShape.class);
//    }
//
//    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
//        NodeShape shape = (NodeShape) source;
//        writer.setValue(shape.getKey());
//    }
//
//    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
//        return NodeShape.getShape(reader.getValue().trim().toLowerCase());
//    }
//
//}

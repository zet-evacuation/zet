/*
 * FlowVisualisationConverter.java
 *
 */
package zet.xml;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
//import fv.gui.view.FlowVisualisation;
//import fv.gui.view.GraphView;
//import fv.gui.view.FlowAttributes;
//import fv.gui.view.FlowView;
//import fv.model.DelayedPath;
//import fv.model.Edge;
//import fv.model.PathFlow;
import ds.graph.Edge;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.IntegerIntegerMapping;
import java.util.Iterator;
import ds.graph.flow.EdgeBasedFlowOverTime;

/**
 *
 * @author Martin Groß
 */
public class FlowVisualisationConverter implements Converter {

//    protected LinkedHashMap<String, PathFlow> flows = new LinkedHashMap<String,PathFlow>();
//    protected LinkedHashMap<String, FlowAttributes> flowAttributes = new LinkedHashMap<String,FlowAttributes>();
	public FlowVisualisationConverter() {
	}

	public boolean canConvert( Class type ) {
		return type.equals( FlowVisualization.class );
	}

	public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
//        FlowVisualization fv = (FlowVisualization) source;
//        writer.startNode("graphLayout");
//        context.convertAnother(fv.graphView());
//        writer.endNode();
//        writer.startNode("flows");
//        for (int f=0; f<fv.flowViews().size(); f++) {
//            PathFlow flow = fv.flowViews().get(f).getFlow();
//            writer.startNode("flow");
//            writer.addAttribute("id",fv.flowViews().get(f).getAttributes().getID());
//            if (flow.getUnitSize() != 1.0) writer.addAttribute("amount",String.valueOf(flow.getUnitSize()));
//            if (flow.getRate() != 1.0) writer.addAttribute("rate",String.valueOf(flow.getRate()));
//            StringBuilder path = new StringBuilder();
//            for (Edge edge : flow.edges()) {
//                if (flow.delay(edge) > 0) path.append(String.valueOf(flow.delay(edge))+",");
//                String id = fv.graphView().getEdgeAttributes(edge).getID();
//                path.append(id);
//                path.append(",");
//            }
//            if (flow.edges().size() > 0) path.deleteCharAt(path.length()-1);
//            /*
//            for (int e=0; e<flow.getEdges().length; e++) {
//                if (flow.getDelays()[e] > 0) path.append(String.valueOf(flow.getDelays()[e])+",");
//                Edge edge = flow.getEdges()[e];
//                String id = fv.graphView().getEdgeAttributes(edge).getID();
//                path.append(id);
//                if (e < flow.getEdges().length-1) path.append(",");
//            }*/
//            writer.addAttribute("path",path.toString());
//            writer.endNode();
//        }
//        writer.endNode();
//        writer.startNode("flowLayouts");
//        for (int i=0; i<fv.flowViews().size(); i++) {
//            writer.startNode("flowLayout");
//            writer.addAttribute("id",fv.flowViews().get(i).getAttributes().getID());
//            Set keys = fv.flowViews().get(i).getAttributes().keySet();
//            for (Object key : keys) {
//                if (key.toString().equals("id")) continue;
//                writer.startNode(key.toString());
//                context.convertAnother(fv.flowViews().get(i).getAttributes().get(key));
//                writer.endNode();
//            }
//            writer.endNode();
//        }
//        writer.endNode();
	}

	public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
		if( !reader.hasMoreChildren() ) {
			throw new ConversionException( "Flow visualization root has no children." );
		}

		// move to the first child, must be 'graphLayout'. then convert the layout.
		reader.moveDown();
		if( !reader.getNodeName().equals( "graphLayout" ) ) {
			throw new ConversionException( "First node has to be graphlayout" );
		}

		String scale = reader.getAttribute( "scale" );
		double scaleVal = 1;
		if( scale != null ) {
			scaleVal = Double.parseDouble( scale );
		}

		GraphViewConverter gvc = new GraphViewConverter();
		GraphView graphView = (GraphView) gvc.unmarshal( reader, context );
		reader.moveUp();	// close graphLayout reading
		graphView.setScale( scaleVal );

		System.out.println( "converted network: " );
		System.out.println( graphView.getNetwork().toString() );

		IdentifiableObjectMapping<Edge,IntegerIntegerMapping> map = null;

		// read the flow if exists
		if( reader.hasMoreChildren() ) {
			// must be flow
			reader.moveDown();
			if( !reader.getNodeName().equals( "flows" ) )
				throw new ConversionException( "Second part has to be the flow." );

			map = new IdentifiableObjectMapping<Edge, IntegerIntegerMapping>( gvc.edges.size(), IntegerIntegerMapping.class );
			for( Edge edge : graphView.getNetwork().edges() )
				map.set( edge, new IntegerIntegerMapping() );

			while( reader.hasMoreChildren() ) {
				reader.moveDown();
				if( reader.getNodeName().equals( "flow" ) ) {
					String path = reader.getAttribute( "path" );
					String sp[] = path.split( "," );

					int current = 0; // the current time
					// parsing the path

					for( int i = 0; i < sp.length; ++i ) {
						int amount = 1;
						int rate = 1;

						final Iterator iter = reader.getAttributeNames();
						while(iter.hasNext()) {
							Object name = iter.next();
							if( name.equals( "rate" ) )
								rate = (int)Double.parseDouble( reader.getAttribute( "rate" ) );
							else if( name.equals( "amount" ) )
								amount = (int)Double.parseDouble( reader.getAttribute( "amount" ) );
						}


						if( sp[i].contains( "." ) ) {
							current += (int)Double.parseDouble( sp[i] );
						} else {
							Edge edge = gvc.edges.get( sp[i] );
							// Zeitpunkt ist current:
							IntegerIntegerMapping iim = map.get( edge );
							iim.set( current, rate );
							current += gvc.transitTimes.get( edge );
						}
					}

				}
				reader.moveUp();
			}
			reader.moveUp();
		}


//		if( reader.hasMoreChildren() ) {
//			reader.moveDown();
		// ignore children
			while(reader.hasMoreChildren()) {
				reader.moveDown();
				reader.moveUp();
			}
//                PathFlow flow = new PathFlow();
//                flow.setUnitSize(1.0);
//                //flow.setRate(1.0);
//                String id = null;
//                reader.moveDown();
//                for (Iterator i = reader.getAttributeNames(); i.hasNext();) {
//                    Object name = i.next();
//                    if (name.equals("amount")) {
//                        flow.setUnitSize(Double.parseDouble(reader.getAttribute("amount")));
//                    } else if (name.equals("rate")) {
//                        flow.setRate(Double.parseDouble(reader.getAttribute("rate")));
//                    } else if (name.equals("id")) {
//                        id = reader.getAttribute("id");
//                    } else if (name.equals("path")) {
//                        String path = reader.getAttribute("path");
//                        String[] strs = path.split("\\s*,\\s*");
//                        int index = 0;
//                        ArrayList<Double> delays = new ArrayList<Double>();
//                        ArrayList<Edge> edges = new ArrayList<Edge>();
//                        while (index < strs.length) {
//                            if (strs[index].matches("[0-9]+\\.[0-9]+")) {
//                                delays.add(Double.parseDouble(strs[index++]));
//                                if (graphView.getEdge(strs[index]) == null) System.out.println(strs[index]);
//                                edges.add(graphView.getEdge(strs[index++]));
//                            } else {
//                                delays.add(new Double(0.0));
//                                if (graphView.getEdge(strs[index]) == null) System.out.println(strs[index]);
//                                edges.add(graphView.getEdge(strs[index++]));
//                            }
//                        }
//                        DelayedPath delayedPath = new DelayedPath();
//                        for (int j=0; j<edges.size(); j++) {
//                            delayedPath.add(edges.get(j),delays.get(j));
//                        }
//                        flow.setPath(delayedPath);
//                    }
//                }
//                //flows.add(flow);
//                this.flows.put(id,flow);
//				reader.moveUp();
//			}
//			reader.moveUp();
// }
//            FlowAttributes fa = null;
//            reader.moveDown();
//            String sizePerUnit = reader.getAttribute("sizePerUnit");
//
//            String texture = reader.getAttribute("texture");
//            if (sizePerUnit != null) { FlowAttributes.setDefaultSizePerUnit(Double.parseDouble(sizePerUnit)); }
//            if (texture != null) {
//                FlowAttributes.setTexturePath(texture);
//                FlowAttributes.setUpdateTexture(true);
//            }
//            while (reader.hasMoreChildren()) {
//                reader.moveDown();
//                fa = new FlowAttributes();
//                String id = reader.getAttribute("id");
//                while (reader.hasMoreChildren()) {
//                    reader.moveDown();
//                    fa.put(reader.getNodeName(),context.convertAnother(context.currentObject(),requiredTypes.get(reader.getNodeName())));
//                    reader.moveUp();
//                }
//                reader.moveUp();
//                flowAttributes.put(id,fa);
//            }
//            reader.moveUp();
//        }
//        LinkedList<FlowView> flowViews = new LinkedList<FlowView>();
//        for (String key : this.flows.keySet()) {
//            flowViews.add(new FlowView(graphView,this.flows.get(key),flowAttributes.get(key)));
//        }
		FlowVisualization fv = new FlowVisualization( graphView );//),flowViews);
		EdgeBasedFlowOverTime flow = new EdgeBasedFlowOverTime( map );
		fv.setFlow( flow );
		return fv;
	}
}
//    static HashMap<String,Class> requiredTypes = new HashMap<String,Class>();
//    static {
//        requiredTypes.put("color",Color.class);
//        requiredTypes.put("sizePerUnit",Double.class);
//        requiredTypes.put("visibleAfter",Boolean.class);
//        requiredTypes.put("visibleBefore",Boolean.class);
//    }


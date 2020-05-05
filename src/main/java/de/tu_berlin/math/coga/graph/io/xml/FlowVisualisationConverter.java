/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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

import de.tu_berlin.math.coga.graph.io.xml.visualization.GraphVisualization;
import de.tu_berlin.math.coga.graph.io.xml.visualization.FlowVisualization;
import org.zetool.netflow.classic.PathComposition;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.zetool.graph.Edge;
import org.zetool.netflow.ds.structure.FlowOverTimeEdge;
import org.zetool.netflow.ds.structure.FlowOverTimeEdgeSequence;
import org.zetool.netflow.ds.structure.FlowOverTimePath;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import java.util.Iterator;

/**
 * A converter that can convert a flow visualization datastracture out of an XML
 * file using {@code XStream}.
 * @author Martin Groß, Jan-Philipp Kappmeier
 */
public class FlowVisualisationConverter implements Converter {

	private XMLData xmlData;

//    protected LinkedHashMap<String, PathFlow> flows = new LinkedHashMap<String,PathFlow>();
//    protected LinkedHashMap<String, FlowAttributes> flowAttributes = new LinkedHashMap<String,FlowAttributes>();
	public FlowVisualisationConverter( XMLData xmlData ) {
		this.xmlData = xmlData;
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
		if( !reader.hasMoreChildren() )
			throw new InvalidFileFormatException( "Flow visualization root has no children." );

		// move to the first child, must be 'graphLayout'. then convert the layout.
		reader.moveDown();
		if( !reader.getNodeName().equals( "graphLayout" ) )
			throw new InvalidFileFormatException( "First node has to be graphlayout" );

		// TODO the scale and subersink things are moved to GraphViewConverter (as they should!)
		
		GraphViewConverter gvc = new GraphViewConverter( xmlData );
		GraphVisualization graphView2 = (GraphVisualization) gvc.unmarshal( reader, context );
		FlowVisualization graphView = new FlowVisualization( graphView2 );
		reader.moveUp();	// close graphLayout reading

		System.out.println( "converted network: " );
		System.out.println( graphView.getNetwork().toString() );


		PathBasedFlowOverTime dynamicFlow = new PathBasedFlowOverTime();
		int maxFlowRate = 0;

		// read the flow if exists
		if( reader.hasMoreChildren() ) {
			// must be flow
			reader.moveDown();
			if( !reader.getNodeName().equals( "flows" ) )
				throw new InvalidFileFormatException( "Second part has to be the flow." );

			while(reader.hasMoreChildren()) {
				reader.moveDown();
				if( reader.getNodeName().equals( "flow" ) ) {

					String path = "";
					String sp[] = null;
					int amount = 1;
					int rate = 1;

					Iterator iter = reader.getAttributeNames();
					while(iter.hasNext()) {
						Object name = iter.next();
						if( name.equals( "rate" ) )
							rate = (int) Double.parseDouble( reader.getAttribute( "rate" ) );
						else if( name.equals( "amount" ) )
							amount = (int) Double.parseDouble( reader.getAttribute( "amount" ) );
						else if( name.equals( "path" ) ) {
							path = reader.getAttribute( "path" );
							sp = path.split( "," );
						}
					}

					maxFlowRate = Math.max( maxFlowRate, rate );

					if( sp == null )
						throw new InvalidFileFormatException( "No path defined." );

					// parsing the path
					FlowOverTimeEdgeSequence es = new FlowOverTimeEdgeSequence();
					for( int i = 0; i < sp.length; ++i ) {
						// Baue den Pfad zusammen
						final int delay = sp[i].contains( "." ) ? (int) Double.parseDouble( sp[i++] ) : 0;
						es.addLast( new FlowOverTimeEdge( xmlData.getEdges().get( sp[i] ), delay ) );
					}
					// füge den Pfad hinzu
					final FlowOverTimePath p = new FlowOverTimePath( es );
					p.setRate( rate );
					p.setAmount( amount );
					dynamicFlow.addPathFlow( p );
				}

				reader.moveUp();
			}
			reader.moveUp();
		}


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
		//FlowVisualization fv = new FlowVisualization( graphView );

		graphView.setEdgesDoubled( xmlData.doubleEdges );
		

		PathComposition pathComposition = new PathComposition( graphView.getNetwork() , graphView.getTransitTimes(), dynamicFlow );
		pathComposition.run();

		graphView.setFlow( pathComposition.getEdgeFlows(), maxFlowRate );

		int maxTimeHorizon = 0;
		for( Edge edge : xmlData.getEdges().values() )
			maxTimeHorizon = Math.max( maxTimeHorizon, pathComposition.getEdgeFlows().get( edge ).getLastTimeWithNonZeroValue() + xmlData.getTransitTimesIntegral().get( edge ) );

		graphView.setTimeHorizon( maxTimeHorizon );
		return graphView;
	}
}

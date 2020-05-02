package de.tu_berlin.math.coga.graph.io.xml;

///*
// * GWReader.java
// *
// */
//
//package zet.xml;
//
////import fv.gui.view.EdgeAttributes;
////import fv.gui.view.EdgeView;
////import fv.gui.view.FlowVisualisation;
////import fv.gui.view.GraphView;
////import fv.gui.view.NodeAttributes;
////import fv.gui.view.NodeView;
////import fv.gui.view.FlowAttributes;
////import fv.gui.view.FlowView;
////import fv.model.DynamicGraph;
////import fv.model.Edge;
////import fv.model.Node;
////import fv.model.PathFlow;
//import java.awt.geom.Point2D;
//import java.io.BufferedReader;
//import java.io.EOFException;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.text.ParseException;
//import java.util.Arrays;
//import java.util.LinkedHashMap;
//import java.util.LinkedList;
//
///**
// *
// * @author Martin Gro�
// */
//public class GWReader {
//
//    protected static final String GRAPH_HEADER_STRING = "LEDA.GRAPH";
//    protected static final String GRAPHWIN_HEADER_STRING = "GraphWin";
//    protected static final String USERDATA_BEGIN = "|{";
//    protected static final String USERDATA_END = "}|";
//
//    protected String currentLine;
//    protected int lineNumber;
//    protected BufferedReader reader;
//    protected boolean useFlowObjects;
//    protected boolean useRGBColors;
//
//    protected LinkedHashMap<String, PathFlow> flows = new LinkedHashMap<String,PathFlow>();
//    protected LinkedHashMap<String, FlowAttributes> flowAttributes = new LinkedHashMap<String,FlowAttributes>();
//
//    protected FlowAttributes flowDefaults;
//
//    protected FlowVisualisation flowVisualisation;
//
//    public GWReader(String string) throws InvalidFileFormatException {
//        this(new File(string));
//    }
//
//    public GWReader(File file) throws InvalidFileFormatException {
//        init(file);
//    }
//
//    public FlowVisualisation getFlowVisualisation() {
//        return flowVisualisation;
//    }
//
//    protected GWAdapter adapter;
//
//    protected void init(File file) throws InvalidFileFormatException {
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            adapter = new GWAdapter();
//            flowDefaults = new FlowAttributes();//
//            adapter.startGraphSection();
//            readHeader();
//            readGraphNodeSection();
//            readEdgeSection();
//            adapter.endGraphSection();
//            adapter.startGraphLayoutSection();
//            readGlobalParameters();
//            readNodeAttributes();
//            readEdgeAttributes();
//            adapter.endGraphLayoutSection();
//
//            DynamicGraph graph = adapter.getGraph();
//            graph.removeLoops();
//            GraphView graphView = adapter.getGraphView();
//
//            for (int i=0; i<graph.numberOfEdges(); i++) {
//                Edge edge = graph.getEdge(i);
//                boolean visible = graphView.getNodeAttributes(edge.start()).isVisible()
//                && graphView.getNodeAttributes(edge.end()).isVisible();
//                graphView.getEdgeAttributes(edge).setVisible(visible);
//            }
//            graphView.extractDefaultProfiles();
//            for (String p : flows.keySet()) {
//                //adapter.writePath(flows.get(p),graph);
//                adapter.writePath(p ,graph);
//            }
//            LinkedList<FlowView> flowViews = new LinkedList<FlowView>();
//            for (Object key : flows.keySet()) {
//                flowViews.add(new FlowView(graphView, flows.get(key), flowAttributes.get(key)));
//            }
//
//            flowVisualisation = new FlowVisualisation(graphView,flowViews);
//
//            String newFilename = file.getParentFile() + file.separator + file.getName().substring(0,file.getName().length()-3) + ".xml";
//            File newFile = new File(newFilename);
//            if (!newFile.exists()) {
//                XMLWriter xmlWriter = new XMLWriter(newFile);
//                xmlWriter.write(flowVisualisation);
//                //XMLReader xmlReader = new XMLReader("c:\\text.xml");
//                //flowVisualisation = (FlowVisualisation) xmlReader.read();*/
//            }
//        } catch (Exception ex) {
//            throw new InvalidFileFormatException("Fehler beim Parsen von Zeile "+lineNumber+": "+currentLine,ex);
//        } finally {
//            try {
//                if (reader != null) reader.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    protected void readHeader() throws InvalidFileFormatException {
//        if (!readLine().equals(GRAPH_HEADER_STRING)) throw new InvalidFileFormatException();
//        adapter.setNodeUserDataType(readLine());
//        adapter.setEdgeUserDataType(readLine());
//        adapter.setDirected(Integer.parseInt(readLine()));
//    }
//
//    protected void readGraphNodeSection() throws InvalidFileFormatException {
//        adapter.startNodeSection(Integer.parseInt(readLine()));
//        for (int i=0; i<adapter.getNumberOfNodes(); i++) {
//            String line = readLine();
//            if (line.startsWith(USERDATA_BEGIN) && line.endsWith(USERDATA_END)) {
//                line = line.substring(USERDATA_BEGIN.length(),line.length()-USERDATA_END.length());
//                adapter.setNodeUserData(i,line);
//            } else {
//                throw new InvalidFileFormatException();
//            }
//        }
//    }
//
//    protected void readEdgeSection() throws InvalidFileFormatException {
//        adapter.startEdgeSection(Integer.parseInt(readLine()));
//        for (int i=0; i<adapter.getNumberOfEdges(); i++) {
//            String[] tokens = readTokens(4);
//            if (tokens.length < 4) throw new InvalidFileFormatException();
//            if (tokens[3].startsWith(USERDATA_BEGIN) && tokens[3].endsWith(USERDATA_END)) {
//                tokens[3] = tokens[3].substring(USERDATA_BEGIN.length(),tokens[3].length()-USERDATA_END.length());
//                adapter.setEdgeUserData(i,tokens[3]);
//            } else {
//                throw new InvalidFileFormatException();
//            }
//            adapter.setSource(i,Integer.parseInt(tokens[0]));
//            adapter.setTarget(i,Integer.parseInt(tokens[1]));
//            adapter.setReversalEdge(i,Integer.parseInt(tokens[2]));
//        }
//    }
//
//    protected void readGlobalParameters() throws InvalidFileFormatException {
//        String[] tokens = readTokens();
//        if (tokens.length != 2) throw new InvalidFileFormatException();
//        if (!tokens[0].equals(GRAPHWIN_HEADER_STRING)) throw new InvalidFileFormatException();
//        adapter.setVersion(Double.parseDouble(tokens[1]));
//        if (new Double(Double.parseDouble(tokens[1])).equals(1.32)) {
//            useRGBColors = false;
//        } else if (new Double(Double.parseDouble(tokens[1])).equals(1.4)) {
//            useRGBColors = true;
//        } else {
//            throw new InvalidFileFormatException();
//        }
//        tokens = readTokens();
//        if (tokens.length != 5) throw new InvalidFileFormatException();
//        adapter.setScaling(Double.parseDouble(tokens[0]));
//        adapter.setXMin(Double.parseDouble(tokens[1]));
//        adapter.setYMin(Double.parseDouble(tokens[2]));
//        adapter.setXMax(Double.parseDouble(tokens[3]));
//        adapter.setYMax(Double.parseDouble(tokens[4]));
//        tokens = readTokens();
//        if (tokens.length != 2) throw new InvalidFileFormatException();
//        adapter.setNodeFont(Double.parseDouble(tokens[1]),Integer.parseInt(tokens[0]));
//        tokens = readTokens();
//        if (tokens.length != 2) throw new InvalidFileFormatException();
//        adapter.setEdgeFont(Double.parseDouble(tokens[1]),Integer.parseInt(tokens[0]));
//        adapter.setNodeLabelFormat(readLine());
//        adapter.setEdgeLabelFormat(readLine());
//        adapter.setParallelEdgeDistance(Double.parseDouble(readLine()));
//    }
//
//    protected int nodeI, edgeI;
//
//    protected void readNodeAttributes() throws InvalidFileFormatException {
//        DynamicGraph graph = adapter.getGraph();
//        GraphView graphView = adapter.getGraphView();
//        for (int i=0; i<adapter.getNumberOfNodes(); i++) {
//            String[] tokens = readTokens();
//            NodeAttributes a = new NodeAttributes(); //graphView.getNodeAttributes(graph.getNode(i));
//            NodeAttributes p = new NodeAttributes(graphView.getAttributes().getNodeDefaults());
//            a.setID(String.valueOf(nodeI++));
//            int index = 0;
//            adapter.setNodeAttributes(graph.getNode(i),a);
//            double x = 0.0, y = 0.0;
//            adapter.setX(x = Double.parseDouble(tokens[index++]));
//            adapter.setY(y = Double.parseDouble(tokens[index++]));
//
//            adapter.setNodeAttributes(p);
//            adapter.setNodeShape(Integer.parseInt(tokens[index++]));
//            if (useRGBColors) {
//                adapter.setBorderColor(Integer.parseInt(tokens[index++]),
//                        Integer.parseInt(tokens[index++]),
//                        Integer.parseInt(tokens[index++])
//                        );
//            } else {
//                adapter.setBorderColor(Integer.parseInt(tokens[index++]));
//            }
//            adapter.setBorderWidth(Math.round(Double.parseDouble(tokens[index++])*10.0)/10.0);
//            double w = 0.0, h = 0.0;
//            adapter.setRadius1(w = Math.round(Double.parseDouble(tokens[index++])*10.0)/10.0);
//            adapter.setRadius2(h = Math.round(Double.parseDouble(tokens[index++])*10.0)/10.0);
//
//            //a.setX(x-w);
//            //a.setY(y-h);
//
//            if (useRGBColors) {
//                adapter.setNodeColor(Integer.parseInt(tokens[index++]),Integer.parseInt(tokens[index++]),Integer.parseInt(tokens[index++]));
//            } else {
//                adapter.setNodeColor(Integer.parseInt(tokens[index++]));
//            }
//            adapter.setNodeLabelType(Integer.parseInt(tokens[index++]));
//            if (useRGBColors) {
//                adapter.setNodeLabelColor(Integer.parseInt(tokens[index++]),Integer.parseInt(tokens[index++]),Integer.parseInt(tokens[index++]));
//            } else {
//                adapter.setNodeLabelColor(Integer.parseInt(tokens[index++]));
//            }
//            adapter.setNodeLabelPosition(Integer.parseInt(tokens[index++]));
//            StringBuilder userLabel = new StringBuilder();
//            for (int j=index; j<tokens.length; j++) {
//                if (j > index) userLabel.append(" ");
//                userLabel.append(tokens[j]);
//            }
//            if (!readFlow(graph.getNode(i),userLabel.toString())) {
//                adapter.setNodeAttributes(a);
//                adapter.setNodeUserLabel(userLabel.toString());
//            }
//
//            a.setDefaults(graphView.getLayoutProfile(p));
//            a.setType("nodelayout"+graphView.getIndexOf(p));
//            graphView.getAttributes().setNodeAttributes(graph.getNode(i),a);
//        }
//    }
//
//    protected void readEdgeAttributes() throws IOException, ParseException, InvalidFileFormatException {
//        DynamicGraph graph = adapter.getGraph();
//        GraphView graphView = adapter.getGraphView();
//        for (int i=0; i<adapter.getNumberOfEdges(); i++) {
//            String[] tokens = readTokens();
//
//            EdgeAttributes a = new EdgeAttributes();
//            EdgeAttributes p = new EdgeAttributes(graphView.getAttributes().getEdgeDefaults());
//
//            a.setID(String.valueOf(edgeI++));
//
//            int index = 0;
//            adapter.setEdgeAttributes(graph.getEdge(i),p);
//            adapter.setEdgeWidth(Math.round(Double.parseDouble(tokens[index++])*10.0)/10.0);
//            if (useRGBColors) {
//                adapter.setEdgeColor(Integer.parseInt(tokens[index++]),Integer.parseInt(tokens[index++]),Integer.parseInt(tokens[index++]));
//            } else {
//                adapter.setEdgeColor(Integer.parseInt(tokens[index++]));
//            }
//            adapter.setEdgeShape(Integer.parseInt(tokens[index++]));
//            adapter.setEdgeStyle(Integer.parseInt(tokens[index++]));
//            adapter.setDirection(Integer.parseInt(tokens[index++]));
//            adapter.setEdgeLabelType(Integer.parseInt(tokens[index++]));
//            if (useRGBColors) {
//                adapter.setEdgeLabelColor(Integer.parseInt(tokens[index++]),Integer.parseInt(tokens[index++]),Integer.parseInt(tokens[index++]));
//            } else {
//                adapter.setEdgeLabelColor(Integer.parseInt(tokens[index++]));
//            }
//            adapter.setEdgeLabelPosition(Integer.parseInt(tokens[index++]));
//            adapter.setSourceAnchor(parsePoint(tokens[index++]));
//            adapter.setTargetAnchor(parsePoint(tokens[index++]));
//            int numberOfPoints = Integer.parseInt(tokens[index++]);
//            Point2D[] points = new Point2D.Double[numberOfPoints];
//            for (int j=index; j<index+numberOfPoints; j++) {
//                points[j-index] = parsePoint(tokens[j]);
//            }
//
//            adapter.setEdgeAttributes(a);
//            adapter.setPoints(points);
//            StringBuilder userLabel = new StringBuilder();
//            for (int j=index+numberOfPoints; j<tokens.length; j++) {
//                if (j > index+numberOfPoints) userLabel.append(" ");
//                userLabel.append(tokens[j]);
//            }
//            adapter.setEdgeUserLabel(userLabel.toString());
//
//            a.setDefaults(graphView.getLayoutProfile(p));
//            a.setType("edgelayout"+graphView.getIndexOf(p));
//            graphView.getGraphAttributes().setEdgeAttributes(graph.getEdge(i),a);
//        }
//    }
//
//    protected String readLine() throws InvalidFileFormatException {
//        try {
//        do {
//            lineNumber++;
//            currentLine = reader.readLine();
//            if (currentLine != null) {
//                currentLine = currentLine.trim();
//            }
//        } while (currentLine != null && (currentLine.startsWith("#") || currentLine.equals("")));
//        if (currentLine == null) throw new EOFException();
//        } catch (IOException ex) {
//            throw new InvalidFileFormatException("",ex);
//        }
//        return currentLine;
//    }
//
//    protected String[] readTokens() throws InvalidFileFormatException {
//        return readLine().split("\\s+");
//    }
//
//    protected String[] readTokens(int limit) throws InvalidFileFormatException {
//        return readLine().split("\\s+",limit);
//    }
//
//    protected boolean readFlow(Node node, String str) throws InvalidFileFormatException {
//        if (str.equals("")) return false;
//        String[] tokens = str.split("\\s+");
//        for (int i=0; i<tokens.length; i++) {
//            if (tokens[i].endsWith("|")) tokens[i] = tokens[i].substring(0,tokens[i].length()-1);
//            String[] params = tokens[i].split("\\|");
//            String id = "";
//            if (params[0].matches("s[0-9].*")) {
//                id = params[0].substring(1,params[0].length());
//            } else if (params[0].matches("s[^0-9].*")) {
//                id = params[0].replaceAll("[0-9]","").substring(1,tokens[i].length());
//            } else if (params[0].matches("i[0-9].*")) {
//                id = params[0].substring(1,params[0].length());
//            } else if (params[0].matches("i[^0-9].*")) {
//                id = params[0].replaceAll("[0-9]","").substring(1,tokens[i].length());
//            } else if (params[0].matches("t[0-9].*")) {
//                id = params[0].substring(1,params[0].length());
//            } else if (params[0].matches("t[^0-9].*")) {
//                id = params[0].replaceAll("[0-9]","").substring(1,tokens[i].length());
//            } else {
//                return false;
//            }
//            PathFlow p;
//            adapter.flows = this.flows;
//            if (!flows.containsKey(id)) {
//                p = new PathFlow();
//                flows.put(id,p);
//            } else {
//                p = (PathFlow) flows.get(id);
//            }
//            FlowAttributes a;
//            if (!flowAttributes.containsKey(id)) {
//                a = new FlowAttributes(flowDefaults);
//                a.setID(id);
//                flowAttributes.put(id,a);
//            } else {
//                a = (FlowAttributes) flowAttributes.get(id);
//            }
//            if (params[0].matches("s[0-9].*")) {
//                adapter.setFlowAmount(id, Double.parseDouble(params[1]));
//                adapter.setFlowRate(id, Double.parseDouble(params[2]));
//                adapter.setFlowStartDelay(id, Double.parseDouble(params[3]));
//                adapter.setFlowSlowdown(id, Double.parseDouble(params[4]));
//                adapter.setFlowColor(a,params[5]);
//                adapter.setFlowSource(id, node);
//            } else if (params[0].matches("s[^0-9].*")) {
//                String demand = params[0].replaceAll("[^0-9]","");
//                adapter.setFlowAmount(id, Double.parseDouble(demand));
//                adapter.setFlowRate(id, Double.parseDouble(params[1]));
//                adapter.setFlowStartDelay(id, Double.parseDouble(params[2]));
//                adapter.setFlowSlowdown(id, Double.parseDouble(params[3]));
//                adapter.setFlowColor(null,params[5]);
//                adapter.setFlowSource(id, node);
//            } else if (params[0].matches("i[0-9].*")) {
//                adapter.setFlowStation(id, Integer.parseInt(params[1]),node);
//            } else if (params[0].matches("i[^0-9].*")) {
//                adapter.setFlowStation(id, Integer.parseInt(params[1]),node);
//            } else if (params[0].matches("t[0-9].*")) {
//                adapter.setFlowSink(id, node);
//            } else if (params[0].matches("t[^0-9].*")) {
//                adapter.setFlowSink(id, node);
//            } else {
//                return false;
//            }
//
//            /*
//            if (params[0].matches("s[0-9].*")) {
//                adapter.setFlowAmount(p,Double.parseDouble(params[1]));
//                adapter.setFlowRate(p,Double.parseDouble(params[2]));
//                adapter.setFlowStartDelay(p,Double.parseDouble(params[3]));
//                adapter.setFlowSlowdown(p,Double.parseDouble(params[4]));
//                adapter.setFlowColor(a,params[5]);
//                adapter.setFlowSource(p,node);
//            } else if (params[0].matches("s[^0-9].*")) {
//                String demand = params[0].replaceAll("[^0-9]","");
//                adapter.setFlowAmount(p,Double.parseDouble(demand));
//                adapter.setFlowRate(p,Double.parseDouble(params[1]));
//                adapter.setFlowStartDelay(p,Double.parseDouble(params[2]));
//                adapter.setFlowSlowdown(p,Double.parseDouble(params[3]));
//                adapter.setFlowColor(null,params[5]);
//                adapter.setFlowSource(p,node);
//            } else if (params[0].matches("i[0-9].*")) {
//                adapter.setFlowStation(p,Integer.parseInt(params[1]),node);
//            } else if (params[0].matches("i[^0-9].*")) {
//                adapter.setFlowStation(p,Integer.parseInt(params[1]),node);
//            } else if (params[0].matches("t[0-9].*")) {
//                adapter.setFlowSink(p,node);
//            } else if (params[0].matches("t[^0-9].*")) {
//                adapter.setFlowSink(p,node);
//            } else {
//                return false;
//            }
//             */
//        }
//        return true;
//    }
//
//    protected Point2D parsePoint(String str) {
//        String[] d = str.substring(1,str.length()-1).split(",");
//        return new Point2D.Double(Double.parseDouble(d[0]),Double.parseDouble(d[1]));
//    }
//
//}

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

//import fv.gui.view.EdgeAttributes;
//import fv.gui.view.EdgeDirection;
//import fv.gui.view.GraphView;
//import fv.gui.view.NodeAttributes;
//import fv.gui.view.NodeShape;
//import fv.gui.view.FlowAttributes;
//import fv.model.DelayedPath;
//import fv.model.DoubleMap;
//import fv.model.DynamicGraph;
//import fv.model.Edge;
//import fv.model.Node;
//import fv.model.PathFlow;
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.geom.Point2D;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author Martin Gro�
// */
//public class GWAdapter {
//
//    private static final Logger LOGGER = Logger.getLogger("fv.io.GWAdapter");
//    static {
//        LOGGER.setLevel(Level.SEVERE);
//    }
//
//    private void logAttributeNotUsed(String attributeName) {
//        LOGGER.warning("Using the attribute " + attributeName + " has currently no effect.");
//    }
//
//    private void logAttributeValueNotUsed(String attributeName, String value) {
//        LOGGER.warning("Using the value " + value + " for the attribute " + attributeName + " has currently no effect.");
//    }
//
//    /**
//     * Constants for the possible graph types.
//     */
//    public static final int DIRECTED = -1;
//    public static final int UNDIRECTED = -2;
//
//    /**
//     * Constants for the possible values of the global font settings used for
//     * node and edge labels.
//     */
//    public static final int ROMAN_FONT = 0;
//    public static final int BOLD_FONT = 1;
//    public static final int ITALIC_FONT = 2;
//    public static final int FIXED_FONT = 3;
//
//    /**
//     * Returns a Java Font instance corresponding to the given font size and the
//     * specified LEDA font type.
//     * @param size the size of the font.
//     * @param type the LEDA font type.
//     * @return the Java Font object corresponding to the given values.
//     * @throws IllegalArgumentException if the given values are not valid.
//     */
//    private static Font createFont(double size, int type) {
//        String name;
//        int style;
//        switch (type) {
//            case ROMAN_FONT:
//                name = "SansSerif";
//                style = Font.PLAIN;
//                break;
//            case BOLD_FONT:
//                name = "SansSerif";
//                style = Font.BOLD;
//                break;
//            case ITALIC_FONT:
//                name = "SansSerif";
//                style = Font.ITALIC;
//                break;
//            case FIXED_FONT:
//                name = "Monospaced";
//                style = Font.PLAIN;
//                break;
//            default:
//                throw new IllegalArgumentException("type="+type);
//        }
//        return new Font(name,style,(int)Math.round(size));
//    }
//
//    /**
//     * Constants for the possible values of the node shape attribute.
//     */
//    public static final int CIRCLE_NODE = 0;
//    public static final int ELLIPSE_NODE = 1;
//    public static final int SQUARE_NODE = 2;
//    public static final int RECTANGLE_NODE = 3;
//
//    /**
//     * Mappings for the LEDA Color values.
//     */
//    public static final Color[] COLOR = {
//        Color.BLACK,
//        Color.WHITE,
//        Color.RED,
//        Color.GREEN,
//        Color.BLUE,
//        Color.YELLOW,
//        new Color(128,0,128),
//        Color.ORANGE,
//        Color.CYAN,
//        new Color(128,64,0),
//        Color.PINK,
//        new Color(0,128,0),
//        new Color(0,0,128),
//        Color.DARK_GRAY,
//        Color.GRAY,
//        Color.LIGHT_GRAY,
//        new Color(255,255,160)
//    };
//
//    /**
//     * Returns a Java Color instance corresponding to the given LEDA color
//     * value.
//     * @param color the LEDA color value.
//     * @return the Java color object corresponding to the given value.
//     * @throws IllegalArgumentException if the given value is no valid LEDA
//     * color value.
//     */
//    private static Color gwColorToColor(int color) {
//        if (0 <= color && color < COLOR.length) {
//            return COLOR[color];
//        } else if (color == -1) {
//            return new Color(0,0,0,0);
//        } else {
//            throw new IllegalArgumentException("color="+color);
//        }
//    }
//
//    /**
//     * Constants for the possible values of the label type attribute.
//     */
//    public static final int NO_LABEL = 0;
//    public static final int USER_LABEL = 1;
//    public static final int DATA_LABEL = 2;
//    public static final int INDEX_LABEL = 3;
//
//    /**
//     * Constants for the possible values of the label position attribute.
//     */
//    public static final int CENTRAL_POS = 0;
//    public static final int NORTHWEST_POS = 1;
//    public static final int NORTH_POS = 2;
//    public static final int NORTHEAST_POS = 3;
//    public static final int EAST_POS = 4;
//    public static final int SOUTHEAST_POS = 5;
//    public static final int SOUTH_POS = 6;
//    public static final int SOUTHWEST_POS = 7;
//    public static final int WEST_POS = 8;
//
//    /**
//     * Constants for the possible values of the edge shape attribute.
//     */
//    public static final int POLY_EDGE = 0;
//    public static final int CIRCLE_EDGE = 1;
//    public static final int BEZIER_EDGE = 2;
//    public static final int SPLINE_EDGE = 3;
//
//    /**
//     * Constants for the possible values of the edge style attribute.
//     */
//    public static final int SOLID = 0;
//    public static final int DASHED = 1;
//    public static final int DOTTED = 2;
//    public static final int DASHED_DOTTED = 3;
//
//    /**
//     * Constants for the possible values of the edge direction attribute.
//     */
//    public static final int UNDIRECTED_EDGE = 0;
//    public static final int DIRECTED_EDGE = 1;
//    public static final int REDIRECTED_EDGE = 2;
//    public static final int BIDIRECTED_EDGE = 3;
//
//    public GWAdapter() {
//
//    }
//
//    public DynamicGraph getGraph() {
//        return graph;
//    }
//
//    public GraphView getGraphView() {
//        return graphView;
//    }
//
//    /**************************************************************************/
//    /*                             Graph Section                              */
//    /**************************************************************************/
//
//    private DynamicGraph graph;
//    private DoubleMap<Node> balances;
//    private DoubleMap<Edge> capacities;
//    private DoubleMap<Edge> transitTimes;
//    private boolean isDirected;
//
//    public void startGraphSection() {
//        graph = new DynamicGraph();
//    }
//
//    /* Header */
//
//    private transient String nodeUserDataType;
//    private transient String edgeUserDataType;
//    private transient int directed;
//
//    public String getNodeUserDataType() {
//        return nodeUserDataType;
//    }
//
//    public void setNodeUserDataType(String value) {
//        nodeUserDataType = value;
//    }
//
//    public String getEdgeUserDataType() {
//        return edgeUserDataType;
//    }
//
//    public void setEdgeUserDataType(String value) {
//        edgeUserDataType = value;
//    }
//
//    public int getDirected() {
//        return directed;
//    }
//
//    public boolean isDirected() {
//        return isDirected;
//    }
//
//    public void setDirected(int value) {
//        if (value == DIRECTED) {
//            isDirected = true;
//        } else if (value == UNDIRECTED) {
//            isDirected = false;
//        } else {
//            throw new IllegalArgumentException("value="+value);
//        }
//        directed = value;
//    }
//
//    /* End Header */
//
//    /* Node Section */
//
//    private transient int numberOfNodes;
//    private transient String[] nodeUserData;
//
//    public void startNodeSection(int numberOfNodes) {
//        this.numberOfNodes = numberOfNodes;
//        nodeUserData = new String[numberOfNodes];
//    }
//
//    public int getNumberOfNodes() {
//        return numberOfNodes;
//    }
//
//    public void setNodeUserData(int nodeId, String userData) {
//        nodeUserData[nodeId] = userData;
//    }
//
//    /* End Node Section */
//
//    /* Edge Section */
//
//    private transient int numberOfEdges;
//    private transient int[] starts;
//    private transient int[] ends;
//    private transient int[] reversalEdges;
//    private transient String[] edgeUserData;
//
//    public void startEdgeSection(int numberOfEdges) {
//        this.numberOfEdges = numberOfEdges;
//        starts = new int[numberOfEdges];
//        ends = new int[numberOfEdges];
//        reversalEdges = new int[numberOfEdges];
//        edgeUserData = new String[numberOfEdges];
//    }
//
//    public int getNumberOfEdges() {
//        return numberOfEdges;
//    }
//
//    public int getReversalEdge(int edgeId) {
//        return reversalEdges[edgeId];
//    }
//
//    public void setReversalEdge(int edgeId, int reversalEdge) {
//        reversalEdges[edgeId] = reversalEdge;
//    }
//
//    public int getSource(int edgeId) {
//        return starts[edgeId];
//    }
//
//    public void setSource(int edgeId, int source) {
//        starts[edgeId] = source;
//    }
//
//    public int getTarget(int edgeId) {
//        return ends[edgeId];
//    }
//
//    public void setTarget(int edgeId, int target) {
//        ends[edgeId] = target;
//    }
//
//    public String getEdgeUserData(int edgeId) {
//        return edgeUserData[edgeId];
//    }
//
//    public void setEdgeUserData(int edgeId, String userData) {
//        edgeUserData[edgeId] = userData;
//    }
//
//    public void endEdgeSection() {
//    }
//
//    /* End Edge Section */
//
//    public void endGraphSection() {
//        Node[] nodes = new Node[numberOfNodes];
//        for (int i=0; i<numberOfNodes; i++) {
//            nodes[i] = new Node(i);
//            graph.addNode(nodes[i]);
//        }
//        Edge[] edges = new Edge[numberOfEdges];
//        for (int i=0; i<numberOfEdges; i++) {
//            edges[i] = new Edge(i,nodes[starts[i]-1],nodes[ends[i]-1]);
//            graph.addEdge(edges[i]);
//        }
//        balances = new DoubleMap<Node>(graph.nodes());
//        capacities = new DoubleMap<Edge>(graph.edges());
//        transitTimes = new DoubleMap<Edge>(graph.edges());
//        for (int i=0; i<numberOfNodes; i++) {
//            if (nodeUserData[i].matches("\\-?[0-9]+\\.?[0-9]*")) {
//                balances.set(nodes[i],Double.parseDouble(nodeUserData[i]));
//            } else {
//                balances.set(nodes[i],0.0);
//            }
//        }
//        for (int i=0; i<numberOfEdges; i++) {
//            double[] data = convertEdgeUserData(edgeUserData[i]);
//            capacities.set(edges[i],data[0]);
//            transitTimes.set(edges[i],data[1]);
//        }
//    }
//
//    private double[] convertEdgeUserData(String str) {
//        double[] result = { 1.0, 1.0 };
//        if (str.startsWith("(") && str.endsWith(")") && str.length() > 2) {
//            String[] tokens = str.substring(1,str.length()-1).split(",");
//            if (tokens.length != 3)  return result;
//            result[0] = Double.parseDouble(tokens[2]);
//            result[1] = Double.parseDouble(tokens[1]);
//        }
//        return result;
//    }
//
//    /**************************************************************************/
//    /*                       Graph Layout Section                             */
//    /**************************************************************************/
//
//    private GraphView graphView;
//
//    public void startGraphLayoutSection() {
//        graphView = new GraphView(graph);
//        graphView.getAttributes().setEdgeDefaults(new EdgeAttributes(true));
//        graphView.getDefaultEdgeLayout().setVisible(true);
//        graphView.getAttributes().setNodeDefaults(new NodeAttributes());
//        graphView.getDefaultNodeLayout().setVisible(true);
//    }
//
//    /* Global Parameters */
//
//    protected double version;
//    protected double scaling;
//    protected double xmin;
//    protected double ymin;
//    protected double xmax;
//    protected double ymax;
//    protected String nodeLabelFormat;
//    protected String edgeLabelFormat;
//    protected double parallelEdgeDistance;
//
//    public void setVersion(double value) {
//        version = value;
//    }
//
//    public void setScaling(double value) {
//        scaling = value;
//        logAttributeNotUsed("Scaling");
//    }
//
//    public void setXMin(double value) {
//        xmin = value;
//        logAttributeNotUsed("Xmin");
//    }
//
//    public void setYMin(double value) {
//        ymin = value;
//        logAttributeNotUsed("Ymin");
//    }
//
//    public void setXMax(double value) {
//        xmax = value;
//        logAttributeNotUsed("Xmax");
//    }
//
//    public void setYMax(double value) {
//        ymax = value;
//        logAttributeNotUsed("Ymax");
//    }
//
//    public void setNodeFont(double size, int type) {
//        graphView.getDefaultNodeLayout().setFont(createFont(size,type));
//        logAttributeNotUsed("NodeFont");
//    }
//
//    public void setEdgeFont(double size, int type) {
//        graphView.getDefaultEdgeLayout().setFont(createFont(size,type));
//        logAttributeNotUsed("EdgeFont");
//    }
//
//    public void setNodeLabelFormat(String value) {
//        nodeLabelFormat = value;
//        logAttributeNotUsed("NodeLabelFormat");
//    }
//
//    public void setEdgeLabelFormat(String value) {
//        edgeLabelFormat = value;
//        logAttributeNotUsed("EdgeLabelFormat");
//    }
//
//    public void setParallelEdgeDistance(double value) {
//        parallelEdgeDistance = value;
//        logAttributeNotUsed("ParallelEdgeDistance");
//    }
//
//    /* End Global Parameters */
//
//    /* Start Node Layout Section */
//
//    private Node node;
//    private NodeAttributes na;
//
//    public void setNodeAttributes(NodeAttributes attributes) {
//        na = attributes;
//    }
//
//    public void setNodeAttributes(Node node, NodeAttributes attributes) {
//        graphView.getGraphAttributes().setNodeAttributes(node,attributes);
//        this.node = node;
//        setNodeAttributes(attributes);
//    }
//
//    public void setX(double value) {
//        na.setX(value);
//    }
//
//    public void setY(double value) {
//        na.setY(value);
//    }
//
//    public void setBorderColor(int r, int g, int b) {
//        logAttributeNotUsed("BorderColor");
//    }
//
//    public void setBorderColor(int value) {
//        logAttributeNotUsed("BorderColor");
//    }
//
//    public void setBorderWidth(double value) {
//        logAttributeNotUsed("BorderWidth");
//    }
//
//    public void setNodeShape(int value) {
//        switch (value) {
//            case CIRCLE_NODE:
//                na.setShape(NodeShape.getEllipseShape());
//                break;
//            case ELLIPSE_NODE:
//                na.setShape(NodeShape.getEllipseShape());
//                break;
//            case SQUARE_NODE:
//                na.setShape(NodeShape.getRectangleShape());
//                break;
//            case RECTANGLE_NODE:
//                na.setShape(NodeShape.getRectangleShape());
//                break;
//            default:
//                new IllegalArgumentException("value="+value);
//        }
//    }
//
//    public void setRadius1(double value) {
//        if (value < 0) {
//            throw new IllegalArgumentException("value="+value);
//        }
//        na.setWidth(value*2);
//    }
//
//    public void setRadius2(double value) {
//        if (value < 0) {
//            throw new IllegalArgumentException("value="+value);
//        }
//        na.setHeight(value*2);
//    }
//
//    public void setNodeColor(int r, int g, int b) {
//        if (r < 0 || g < 0 || b < 0 || r > 255 || g > 255 || b > 255) {
//            throw new IllegalArgumentException("r="+r+",g="+g+",b="+b);
//        } else {
//            na.setBackground(new Color(r,g,b));
//        }
//    }
//
//    public void setNodeColor(int value) {
//        na.setBackground(gwColorToColor(value));
//    }
//
//    public void setNodeLabelType(int value) {
//        logAttributeNotUsed("NodeLabelType");
//    }
//
//    public void setNodeLabelColor(int value) {
//        logAttributeNotUsed("NodeLabelColor");
//    }
//
//    public void setNodeLabelColor(int r, int g, int b) {
//        logAttributeNotUsed("NodeLabelColor");
//    }
//
//    public void setNodeLabelPosition(int value) {
//        logAttributeNotUsed("NodeLabelPosition");
//    }
//
//    public void setNodeUserLabel(String value) {
//        logAttributeNotUsed("NodeUserLabel");
//    }
//
//    /* End Node Layout Section */
//
//    /* Start Edge Layout Section */
//
//    private Edge edge;
//    private EdgeAttributes ea;
//
//    public void setEdgeAttributes(EdgeAttributes attributes) {
//        ea = attributes;
//    }
//
//    public void setEdgeAttributes(Edge edge, EdgeAttributes attributes) {
//        graphView.getGraphAttributes().setEdgeAttributes(edge,attributes);
//        this.edge = edge;
//        setEdgeAttributes(attributes);
//    }
//
//    public void setEdgeWidth(double value) {
//        if (value < 0) {
//            throw new IllegalArgumentException("value="+value);
//        }
//        ea.setLineWidth(value);
//    }
//
//    public void setEdgeColor(int r, int g, int b) {
//        if (r < 0 || g < 0 || b < 0 || r > 255 || g > 255 || b > 255) {
//            throw new IllegalArgumentException("r="+r+",g="+g+",b="+b);
//        } else {
//            ea.setColor(new Color(r,g,b));
//        }
//    }
//
//    public void setEdgeColor(int value) {
//        ea.setColor(gwColorToColor(value));
//    }
//
//    public void setEdgeShape(int value) {
//        logAttributeNotUsed("EdgeShape");
//    }
//
//    public void setEdgeStyle(int value) {
//        logAttributeNotUsed("EdgeStyle");
//    }
//
//    public void setDirection(int value) {
//        switch (value) {
//            case UNDIRECTED_EDGE:
//                ea.setDirection(EdgeDirection.UNDIRECTED);
//                break;
//            case DIRECTED_EDGE:
//                ea.setDirection(EdgeDirection.DIRECTED);
//                break;
//            case REDIRECTED_EDGE:
//                logAttributeValueNotUsed("EdgeDirection","RedirectedEdge");
//                break;
//            case BIDIRECTED_EDGE:
//                logAttributeValueNotUsed("EdgeDirection","BidirectedEdge");
//                break;
//            default:
//                throw new IllegalArgumentException("value="+value);
//        }
//    }
//
//    public void setEdgeLabelType(int value) {
//        logAttributeNotUsed("EdgeLabelType");
//    }
//
//    public void setEdgeLabelColor(int value) {
//        logAttributeNotUsed("EdgeLabelColor");
//    }
//
//    public void setEdgeLabelColor(int r, int g, int b) {
//        logAttributeNotUsed("EdgeLabelColor");
//    }
//
//    public void setEdgeLabelPosition(int value) {
//        logAttributeNotUsed("EdgeLabelPosition");
//    }
//
//    public void setSourceAnchor(Point2D value) {
//        logAttributeNotUsed("SourceAnchor");
//    }
//
//    public void setTargetAnchor(Point2D value) {
//        logAttributeNotUsed("TargetAnchor");
//    }
//
//    public void setPoints(Point2D[] value) {
//        Point2D[] points = new Point2D[value.length-2];
//        System.arraycopy(value,1,points,0,points.length);
//        Point2D[] points2 = new Point2D[points.length+2];
//        System.arraycopy(points,0,points2,1,points.length);
//        points2[0] = new Point2D.Double(
//                graphView.getGraphAttributes().getNodeAttributes(edge.start()).getBounds().getX(),
//                graphView.getGraphAttributes().getNodeAttributes(edge.start()).getBounds().getY());
//        points2[points2.length-1] = new Point2D.Double(
//                graphView.getGraphAttributes().getNodeAttributes(edge.end()).getBounds().getX(),
//                graphView.getGraphAttributes().getNodeAttributes(edge.end()).getBounds().getY());
//        Point2D[] points3 = new Point2D[points2.length+2];
//        points3[0] = points2[0];
//        System.arraycopy(points2,1,points3,2,points.length);
//        points3[points3.length-1] = points2[points2.length-1];
//        points3[1] = NodeShape.circleNode(
//                points2[0],
//                graphView.getGraphAttributes().getNodeAttributes(edge.start()).getBounds().getWidth()/2,
//                points2[1]
//                );
//        points3[points3.length-2] = NodeShape.circleNode(
//                points2[points2.length-1],
//                graphView.getGraphAttributes().getNodeAttributes(edge.end()).getBounds().getWidth()/2,
//                points2[points2.length-2]
//                );
//        ea.setPoints(points3);
//    }
//
//    public void setEdgeUserLabel(String value) {
//        logAttributeNotUsed("EdgeUserLabel");
//    }
//
//    /* Start Flow Data */
//
//    protected HashMap<String, PathFlow> flows = new HashMap<String, PathFlow>();
//    protected HashMap<String, ArrayList<Node>> paths = new HashMap<String,ArrayList<Node>>();
//    protected HashMap<String, Node> sinks = new HashMap<String,Node>();
//    protected HashMap<String, Node> sources = new HashMap<String,Node>();
//    protected HashMap<String, Double> startDelays = new HashMap<String,Double>();
//    /*
//    protected HashMap<PathFlow, ArrayList<Node>> paths = new HashMap<PathFlow,ArrayList<Node>>();
//    protected HashMap<PathFlow, Node> sinks = new HashMap<PathFlow,Node>();
//    protected HashMap<PathFlow, Node> sources = new HashMap<PathFlow,Node>();
//    protected HashMap<PathFlow, Double> startDelays = new HashMap<PathFlow,Double>();
//     */
//
//    public void setFlowColor(FlowAttributes a, String value) {
//        if (value.equals("r")) {
//            a.setColor(Color.RED);
//        } else if (value.equals("l")) {
//            a.setColor(Color.BLUE.brighter());
//        } else if (value.equals("g")) {
//            a.setColor(Color.GREEN);
//        } else if (value.equals("C")) {
//            a.setColor(Color.CYAN.darker());
//        } else if (value.equals("y")) {
//            a.setColor(Color.YELLOW);
//        } else if (value.equals("m")) {
//            a.setColor(Color.MAGENTA);
//        } else if (value.equals("n")) {
//            a.setColor(Color.CYAN);
//        } else if (value.equals("R")) {
//            a.setColor(Color.RED.darker());
//        } else if (value.equals("G")) {
//            a.setColor(Color.GREEN.darker());
//        } else if (value.equals("b")) {
//            a.setColor(Color.BLUE);
//        } else if (value.equals("B")) {
//            a.setColor(Color.BLUE.darker());
//        } else if (value.equals("M")) {
//            a.setColor(Color.MAGENTA.darker());
//        } else if (value.equals("a")) {
//            a.setColor(Color.RED.brighter());
//        } else if (value.equals("k")) {
//            a.setColor(Color.GREEN.brighter());
//        } else if (value.equals("q")) {
//            a.setColor(Color.MAGENTA.brighter());
//        } else if (value.equals("c")) {
//            a.setColor(Color.CYAN.brighter());
//        } else {
//            throw new IllegalArgumentException("value="+value);
//        }
//    }
//
//    public void setFlowAmount(String id, double value) {
//        flows.get(id).setUnitSize(value);
//    }
//
//    public void setFlowRate(String id, double value) {
//        //p.setRate(value);
//    }
//
//    public void setFlowStation(String id, int number, Node value) {
//        if (!paths.containsKey(id)) {
//            paths.put(id,new ArrayList<Node>());
//        }
//        ArrayList<Node> l = paths.get(id);
//        while (number-1 >= l.size()) {
//            l.add(null);
//        }
//        l.remove(number-1);
//        l.add(number-1,value);
//    }
//
//    public void setFlowSink(String id, Node value) {
//        sinks.put(id,value);
//    }
//
//    public void setFlowSlowdown(String id, double value) {
//    }
//
//    public void setFlowSource(String id, Node value) {
//        sources.put(id,value);
//    }
//
//    public void setFlowStartDelay(String id, double value) {
//        startDelays.put(id,value);
//    }
//    /*
//    public void setFlowAmount(PathFlow p, double value) {
//        p.setUnitSize(value);
//    }
//
//    public void setFlowRate(PathFlow p, double value) {
//        //p.setRate(value);
//    }
//
//    public void setFlowStation(PathFlow p, int number, Node value) {
//        if (!paths.containsKey(p)) {
//            paths.put(p,new ArrayList<Node>());
//        }
//        ArrayList<Node> l = paths.get(p);
//        while (number-1 >= l.size()) {
//            l.add(null);
//        }
//        l.remove(number-1);
//        l.add(number-1,value);
//    }
//
//    public void setFlowSink(PathFlow p, Node value) {
//        sinks.put(p,value);
//    }
//
//    public void setFlowSlowdown(PathFlow p, double value) {
//    }
//
//    public void setFlowSource(PathFlow p, Node value) {
//        sources.put(p,value);
//    }
//
//    public void setFlowStartDelay(PathFlow p, double value) {
//        startDelays.put(p,value);
//    }
//    */
//
//    public void writePath(String id, DynamicGraph g) {
//        PathFlow flow = flows.get(id);
//        double[] delays;
//        Edge[] edges;
//        //System.out.println(id + ": " + startDelays.get(id));
//        if (!paths.containsKey(id) || ((ArrayList)paths.get(id)).size() == 0) {
//            delays = new double[1];
//            delays[0] = startDelays.get(id);
//            edges = new Edge[1];
//            edges[0] = g.getEdge(sources.get(id),sinks.get(id));
//        } else {
//            ArrayList<Node> list = paths.get(id);
//            ArrayList<Double> delayList = new ArrayList<Double>();
//            Object node = null;
//            int index = 0;
//            while (index<list.size()) {
//                if (list.get(index) != node) {
//                    delayList.add(new Double(0.0));
//                    node = list.get(index);
//                    index++;
//                } else {
//                    double d = delayList.get(delayList.size()-1).doubleValue();
//                    delayList.remove(delayList.size()-1);
//                    delayList.add(d+1.0);
//                    list.remove(index);
//                }
//            }
//            delays = new double[delayList.size()+1];
//            delays[0] = startDelays.get(id);
//            for (int i=1; i<delayList.size()+1; i++) {
//                delays[i] = delayList.get(i-1);
//            }
//            edges = new Edge[list.size()+1];
//            edges[0] = g.getEdge((Node)sources.get(id),(Node)list.get(0));
//            for (int i=0; i<list.size()-1; i++) {
//                edges[i+1] = g.getEdge((Node)list.get(i),(Node)list.get(i+1));
//            }
//            edges[edges.length-1] = g.getEdge((Node)list.get(list.size()-1),(Node)sinks.get(id));
//        }
//
//        //System.out.println("GWAdapter");
//        //System.out.println(Arrays.deepToString(edges));
//        //for (double delay : delays) {
//       //     System.out.println(delay);
//       // }
//        ////System.out.println(Arrays.deepToString(delays));
//        DelayedPath delayedPath = new DelayedPath();
//        for (int j=0; j<edges.length; j++) {
//            //System.out.println(edges[j] + " " + delays[j]);
//            delayedPath.add(edges[j],delays[j]);
//        }
//        flow.setPath(delayedPath);
//        //System.out.println(delayedPath);
//    }
//
//    /*
//    public void writePath(PathFlow flow, DynamicGraph g) {
//        double[] delays;
//        Edge[] edges;
//        //System.out.println(flow. s tartDelays);
//        if (!paths.containsKey(flow) || ((ArrayList)paths.get(flow)).size() == 0) {
//            delays = new double[1];
//            delays[0] = startDelays.get(flow);
//            edges = new Edge[1];
//            edges[0] = g.getEdge(sources.get(flow),sinks.get(flow));
//        } else {
//            ArrayList<Node> list = paths.get(flow);
//            ArrayList<Double> delayList = new ArrayList<Double>();
//            Object node = null;
//            int index = 0;
//            while (index<list.size()) {
//                if (list.get(index) != node) {
//                    delayList.add(new Double(0.0));
//                    node = list.get(index);
//                    index++;
//                } else {
//                    double d = delayList.get(delayList.size()-1).doubleValue();
//                    delayList.remove(delayList.size()-1);
//                    delayList.add(d+1.0);
//                    list.remove(index);
//                }
//            }
//            delays = new double[delayList.size()+1];
//            delays[0] = startDelays.get(flow);
//            for (int i=1; i<delayList.size()+1; i++) {
//                delays[i] = delayList.get(i-1);
//            }
//            edges = new Edge[list.size()+1];
//            edges[0] = g.getEdge((Node)sources.get(flow),(Node)list.get(0));
//            for (int i=0; i<list.size()-1; i++) {
//                edges[i+1] = g.getEdge((Node)list.get(i),(Node)list.get(i+1));
//            }
//            edges[edges.length-1] = g.getEdge((Node)list.get(list.size()-1),(Node)sinks.get(flow));
//        }
//        //System.out.println("GWAdapter");
//        //System.out.println(Arrays.deepToString(edges));
//        //for (double delay : delays) {
//       //     System.out.println(delay);
//       // }
//        ////System.out.println(Arrays.deepToString(delays));
//                        DelayedPath delayedPath = new DelayedPath();
//                        for (int j=0; j<edges.length; j++) {
//                            delayedPath.add(edges[j],delays[j]);
//                        }
//        flow.setPath(delayedPath);
//    }*/
//
//    /* End Flow Data */
//
//    /* End Edge Layout Section */
//
//    public void endGraphLayoutSection() {
//        for (Node node : graph.nodes()) {
//            graphView.getNodeAttributes(node).setBalance(balances.get(node));
//        }
//        for (Edge edge : graph.edges()) {
//            graphView.getEdgeAttributes(edge).setCapacity(capacities.get(edge));
//            graphView.getEdgeAttributes(edge).setTransitTime(transitTimes.get(edge));
//        }
//    }
//
//    /* End Graph Layout Section */
//
//}

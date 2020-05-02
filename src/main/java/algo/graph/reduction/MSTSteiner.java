package algo.graph.reduction;

import algo.graph.spanningtree.PrimForNetwork;
import org.zetool.algorithm.shortestpath.Dijkstra;
import org.zetool.common.algorithm.AbstractAlgorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphMapping;
import org.zetool.container.collection.ListSequence;
import org.zetool.graph.Edge;
import org.zetool.graph.structure.Forest;
import org.zetool.graph.Graph;
import org.zetool.container.collection.IdentifiableCollection;
import ds.graph.MinSteinerTree;
import algo.graph.spanningtree.NetworkMST;
import org.zetool.graph.Node;
import org.zetool.graph.structure.Path;
import algo.graph.spanningtree.MinSpanningTreeProblem;
import algo.graph.spanningtree.NetworkMSTProblem;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DynamicNetwork;

/**
 *
 * @author Marlen Schwengfelder
 */
public class MSTSteiner extends AbstractAlgorithm<MinSpanningTreeProblem, MinSteinerTree> {

    int overalldist = 0;
    NetworkFlowModel originNetwork;
    Graph OriginGraph;
    Node steiner;
    Edge edge;
    Edge supersinkedge;
    int NumNode = 1;
    int Nodenum = 1;
    int NumEdges = 0;
    int Num = 0;
    int count;
    IdentifiableCollection<Node> solNodes = new ListSequence();
    IdentifiableCollection<Node> solutionNodes = new ListSequence();
    IdentifiableCollection<Node> steinerNodes = new ListSequence();
    IdentifiableCollection<Node> EvacuationNodes = new ListSequence();
    IdentifiableCollection<Edge> solEdges = new ListSequence();
    IdentifiableCollection<Edge> solutionEdges = new ListSequence();

    Path[][] shortestPaths;
    IdentifiableIntegerMapping<Edge> shortestpathDist;
    NetworkMSTProblem networkprob;
    PrimForNetwork prim;
    Dijkstra dijkstra;

    @Override
    public MinSteinerTree runAlgorithm(MinSpanningTreeProblem minspan) {

        try {
            originNetwork = minspan.getNetworkFlowModel();
            Node supersink = minspan.getNetworkFlowModel().getSupersink();
            OriginGraph = originNetwork.graph();
            int numNodes = OriginGraph.nodeCount();
            ZToGraphMapping mapping = originNetwork.getZToGraphMapping();
            IdentifiableIntegerMapping<Edge> TransitForEdge = originNetwork.transitTimes();
            //saves the current considered network for different iterations
            DynamicNetwork firstnet = new DynamicNetwork();

            for (Node node : OriginGraph.nodes()) {
                if (node.id() != 0) {
                    //if (mapping.getIsEvacuationNode(node)== true /*|| mapping.getIsSourceNode(node) == true */ )
                    boolean isSource = originNetwork.getSources().contains(node);
                    boolean isSink = originNetwork.getSinks().contains(node);
                    if (isSource || isSink) // TODO: chick if works  
                    {

                        firstnet.addNode(node);
                        steinerNodes.add(node);

                    }
                }
            }
            System.out.println("Number of steinernodes: " + steinerNodes.size());
            TransitForEdge = originNetwork.transitTimes();
            shortestPaths = new Path[numNodes][numNodes];
            shortestpathDist = new IdentifiableIntegerMapping<>(originNetwork.numberOfEdges());

            //gives a network connecting the source and evacutaion nodes with shortest path edges...
            while (!steinerNodes.isEmpty()) {
                Node node = steinerNodes.first();
                dijkstra = new Dijkstra(originNetwork.graph(), TransitForEdge, node, true);
                dijkstra.run();

                steinerNodes.remove(steinerNodes.first());
                for (Node restnode : steinerNodes) {
                    int dist = dijkstra.getDistance(restnode);

                    edge = new Edge(NumEdges++, node, restnode);
                    firstnet.addEdge(edge);
                    //weight of edge is shortest distance (using Dijkstra)
                    shortestpathDist.set(edge, dist);
                    solEdges.add(edge);
                    Forest spt = dijkstra.getShortestPathTree();
                    //stores the shortest path from root to certain vertex
                    shortestPaths[node.id()][restnode.id()] = spt.getPathToRoot(restnode);
                }

            }
            System.out.println("Dijkstra done");

            /*for (Node node: firstnet.nodes())
        {
            System.out.println("Evakuierungsknoten: " + node);
        }
        for (Edge edge: firstnet.edges())
        {
            System.out.println("Kante zwischen Evakuierungsknoten:" + edge);
            System.out.println("Distance: " + shortestpathDist.get(edge));
        }*/
            //creates problem to find a min spanning tree for given network in 1. iteration
            networkprob = new NetworkMSTProblem(firstnet, shortestpathDist);
            prim = new PrimForNetwork();
            prim.setProblem(networkprob);
            prim.run();
            NetworkMST solv = prim.getSolution();
            IdentifiableCollection<Edge> MSTEdges = solv.getEdges();

            for (Edge mst : MSTEdges) {
                count = 0;
                //get shortest Path in Original Network
                Path path = shortestPaths[mst.start().id()][mst.end().id()];
                //gets edges of shortest path
                IdentifiableCollection<Edge> PathEdges = path.getEdges();
                IdentifiableCollection<Node> PathNodes = new ListSequence();
                //gets nodes of shortest path
                for (Edge sptedge : PathEdges) {
                    if (!PathNodes.contains(sptedge.end())) {
                        PathNodes.add(sptedge.end());
                    }
                    if (!PathNodes.contains(sptedge.start())) {
                        PathNodes.add(sptedge.start());
                    }
                }

                for (Node currNode : solNodes) {
                    if (PathNodes.contains(currNode)) {
                        count++;
                    }
                }

                if (count < 2) {
                    for (Edge sptedge : PathEdges) {
                        Edge insert = new Edge(Num++, sptedge.start(), sptedge.end());

                        solutionEdges.add(insert);
                        //solutionEdges.add(sptedge);
                        if (!solNodes.contains(sptedge.start())) {
                            solNodes.add(sptedge.start());
                        }
                        if (!solNodes.contains(sptedge.end())) {
                            solNodes.add(sptedge.end());
                        }
                    }
                } else {
                    for (Edge sptedge : PathEdges) {
                        if (!solutionEdges.contains(edge)) {
                            for (Edge e : solutionEdges) {
                                if (e.start().equals(sptedge.start()) && e.end().equals(sptedge.end())) {
                                    //System.out.println( "We are to insert an edge twice (in the second loop)" );
                                } else {
                                    Edge insert = new Edge(Num++, sptedge.start(), sptedge.end());
                                    solutionEdges.add(insert);
                                    if (!solNodes.contains(sptedge.start())) {
                                        solNodes.add(sptedge.start());
                                    }
                                    if (!solNodes.contains(sptedge.end())) {
                                        solNodes.add(sptedge.start());
                                    }

                                }
                            }

                        }
                    }
                }

            }

            IdentifiableCollection<Edge> addEdges = originNetwork.graph().incidentEdges(supersink);
            for (Edge sinkedge : addEdges) {
                supersinkedge = new Edge(Num++, sinkedge.start(), sinkedge.end());
                solutionEdges.add(supersinkedge);
            }

        } catch (Exception e) {
            System.out.println("Fehler in Steiner-MST " + e.toString());
        }
        return new MinSteinerTree(minspan, shortestpathDist, solutionEdges, solNodes, overalldist);

    }
}

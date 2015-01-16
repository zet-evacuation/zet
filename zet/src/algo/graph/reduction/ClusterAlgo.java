/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.reduction;

import org.zetool.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.graph.Graph;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.container.collection.ListSequence;
import algo.graph.spanningtree.UndirectedTree;
import de.tu_berlin.coga.graph.Node;
import algo.graph.spanningtree.MinSpanningTreeProblem;
import java.util.Random;

/**
 *
 * @author schwengf
 */
public class ClusterAlgo extends Algorithm<MinSpanningTreeProblem, UndirectedTree>{
    
    IdentifiableCollection<Edge> solEdges = new ListSequence();
    IdentifiableCollection<Node> centerNodes = new ListSequence();
    IdentifiableCollection<Edge> centerEdges;
    IdentifiableIntegerMapping<Edge> TransitForCenterEdges;
    IdentifiableCollection<Node> noncenterNodes = new ListSequence();
    IdentifiableCollection<Edge> incidentEdges = new ListSequence();
    IdentifiableCollection<Edge> currentEdges = new ListSequence();
    //int overalldist = 0;
    int NumNodes;
    double prob;
    int i,j;
    int[] init;
    int [] Cluster;
    int[][] used;
    boolean[][] connected;
    int NumEdges = 0;
    Edge MinEdge;
    int NumCluster;
    
	@Override
    public UndirectedTree runAlgorithm(MinSpanningTreeProblem minspan)
    {
        try{
            NetworkFlowModel OriginNetwork = minspan.getNetworkFlowModel(); 
            Graph OriginGraph = minspan.getNetworkFlowModel().graph();
            NumNodes = minspan.getNetworkFlowModel().numberOfNodes();
            //initial edges
            currentEdges = OriginGraph.edges();
            prob = 1/Math.sqrt(NumNodes);
            init = new int[NumNodes];
            used = new int [NumNodes][NumNodes];
            Cluster = new int[NumNodes];
            for (int i=0; i< NumNodes; i++)
            {
                for (int j=0; j< NumNodes; j++)
                {
                    used[i][j] = 0;
                }
            }
            
            /*long seed = r.nextLong();
            seed = 5706550742198787144l; // this one creates a chain decomposition error in 3-storey 4-rooms.
            System.out.println( "Spanning Tree Seed: " + seed );
            r.setSeed( seed );*/
           
            //use node i as a cluster center with probability 1/sqrt(n)
            for (i=1; i< NumNodes  ; i++)
            {
                Random r = new Random();
                double num = 0 + Math.abs(r.nextDouble()) % 1;
                //System.out.println("Random: " + num);
                if (num <= prob)
                {
                    init[i] = 1; 
                    Node node = OriginGraph.getNode(i);
                    centerNodes.add(node);
                    NumCluster++;
                    //node is initial cluster
                    Cluster[i] = NumCluster;
                }
                else 
                {
                    init[i] = 0;
                    Node node = OriginGraph.getNode(i);
                    noncenterNodes.add(node);
                }
            }
           
            //stores if different clusters are connected via an edge
            connected = new boolean [NumCluster+1][NumCluster+1];
            for (int i=0; i< NumCluster; i++)
            {
                for (int j=0; j< NumCluster; j++)
                {
                    connected[i][j] = false;
                }
            }
            
            
            //1.Phase of Algorithm
            for (Node v: noncenterNodes)
            {
                int transitMin = 100000;
                TransitForCenterEdges = new IdentifiableIntegerMapping<>(OriginGraph.edgeCount());
                incidentEdges = OriginGraph.incidentEdges(v);
                int count = 0;
                for (Edge edge: incidentEdges)
                {
                    for (Node centernode: centerNodes)
                    {
                       
                        if (edge.opposite(v) == centernode)
                        {
                            count++;
                            /*TransitForCenterEdges.add(edge, OriginNetwork.getTransitTime(edge));
                            Edge orig = new Edge(NumEdges++, edge.start(), edge.end());
                            solEdges.add(edge);*/
                            if (OriginNetwork.getTransitTime(edge) < transitMin)
                            {
                                transitMin = OriginNetwork.getTransitTime(edge);
                                MinEdge = edge;
                            }
                        }
                    }                   
                }
                //node not incident to a centernode, add all the edges to spanner
                if (count == 0)
                {
                    for (Edge edge: incidentEdges)
                    {
                        if ((used[edge.start().id()][edge.end().id()]) == 0)
                        {
                            Edge solv = new Edge(NumEdges++, edge.start(), edge.end());
                            solEdges.add(solv);
                            currentEdges.remove(solv);
                            used[edge.start().id()][edge.end().id()] = 1;
                            used[edge.end().id()][edge.start().id()] = 1;
                        }
                    }
                }
                //node incident to one or more centernodes, add only min weight edge and 
                //those with lower weight than weigth of Min Edge 
                else if (count > 0 )
                {
                    if (used[MinEdge.start().id()][MinEdge.end().id()] == 0)
                    {
                        Edge min = new Edge(NumEdges++, MinEdge.start(), MinEdge.end());
                        solEdges.add(min);
                        currentEdges.remove(min);
                        used[MinEdge.start().id()][MinEdge.end().id()] = 1;
                        used[MinEdge.end().id()][MinEdge.start().id()] = 1;
                        Cluster[v.id()] = Cluster[MinEdge.opposite(v).id()];
                        
                    }
 
                    for (Edge vw : incidentEdges)
                    {
                       if ((used[vw.start().id()][vw.end().id()]) == 0) 
                       {
                            if (OriginNetwork.getTransitTime(vw) <= transitMin)
                            {
                                Edge solu = new Edge(NumEdges++, vw.start(), vw.end());
                                solEdges.add(solu);
                                currentEdges.remove(solu);
                                used[vw.start().id()][vw.end().id()] = 1;
                                used[vw.end().id()][vw.start().id()] = 1;
                                //edge connects different clusters
                                if (Cluster[vw.start().id()] != Cluster[vw.end().id()])
                                {
                                    connected[Cluster[vw.start().id()]][Cluster[vw.end().id()]] = true;
                                    connected[Cluster[vw.end().id()]][Cluster[vw.start().id()]] = true;
                                }
                            }
                       }
                    }                   
            }
            
            
            }  //end of 1. phase
            
        //2. phase of algorithm - prove if different clusters are connected
        for (i=1; i< NumCluster-1 ; i++)
        {
            for (j=i+1; j< NumCluster ; j++)
            {    
                for (Edge edge: currentEdges)
                {
                    if (!connected[i][j]) 
                    {
                        
                        if ((Cluster[edge.start().id()]==i && Cluster[edge.end().id()]==j) || (Cluster[edge.end().id()]==i && Cluster[edge.start().id()]==j))
                        {
                            if (used[edge.start().id()][edge.end().id()] == 0)
                            {
                                Edge conn = new Edge(NumEdges++, edge.start(),edge.end());
                                solEdges.add(conn);
                                connected[i][j]= true;
                                connected[j][i] = true;
                                used[edge.start().id()][edge.end().id()] = 1;
                                used[edge.end().id()][edge.start().id()] = 1;
                            }
                        }
                    }
                }
            }
        }
            
        //ads the edges adjacent to supersink    
        IdentifiableCollection<Edge> addEdges = OriginNetwork.graph().incidentEdges(OriginNetwork.getSupersink());
        for (Edge edge: addEdges)
        {
            Edge supersinkedge = new Edge(NumEdges++, edge.start(), edge.end());
            solEdges.add(supersinkedge);
        }
        }
        
         catch(Exception e) {
             System.out.println("Fehler in ClusterAlgo " + e.toString() + e.getLocalizedMessage());
         }
        
         return new UndirectedTree( solEdges );
    }
    
   
    
}

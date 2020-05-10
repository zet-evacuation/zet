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
package algo.graph.thinflow;

import ds.graph.problem.ThinFlowProblem;
import ds.graph.flow.ThinFlow;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.container.collection.ListSequence;
import org.zetool.container.mapping.IdentifiableDoubleMapping;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.graph.DynamicNetwork;

public class ThinFlowAlgo extends AbstractAlgorithm<ThinFlowProblem,ThinFlow> {


     private static final double EPSILON = 0.001;
     private static final double ZEROLIMIT = 0.000001;


     private HashMap<Edge,Edge> newedgeToOrigedgeMap;

     private HashSet<Edge> usedEdges;

     private void initializeDatastructures() {
         newedgeToOrigedgeMap = new HashMap<Edge,Edge>();
         usedEdges = new HashSet<Edge>();
     }


     public HashSet<Edge> getUsedEdges() {
         return usedEdges;
     }

     private void addEdgeToUsedEdges(Edge e) {
         usedEdges.add(e);
     }

     private int getNewedgeToOrigedgeMapSize() {
         return newedgeToOrigedgeMap.size();
     }


     private Edge getOrigEdge(Edge edge) {
         Edge oldedge;
         if((oldedge=newedgeToOrigedgeMap.get(edge)) != null)
             return oldedge;
         else
             return edge;

     }

     @Override
     public ThinFlow runAlgorithm(ThinFlowProblem thinflowprob) {
         initializeDatastructures();
         runThinFlow();
         return new ThinFlow(thinflowprob,getProblem().getEdgeFlowValues(),getProblem().getNodeLabels());
     }


     /*function returns a set containing the exiting edges of given nodeset*/
     private HashSet<Edge> exitingEdges(DynamicNetwork wgraph, Set<Node> nodeset) {
         HashSet<Edge> outedges = new HashSet<>();
         HashSet<Edge> inedges = new HashSet<>();
         for(Node n : nodeset) {
             for(Edge oe : wgraph.outgoingEdges(n)) {
                 outedges.add(oe);
            }

             for(Edge ie : wgraph.incomingEdges(n)) {
                 inedges.add(ie);
            }
         }
         for(Edge e : inedges) {
             outedges.remove(e);
         }

         return outedges;
     }

     /*function returns set containing edges that are behind sparsest cut and for which algorithm does not
     explicitely set flow values*/
     private HashSet<Edge> behindEdges(DynamicNetwork wgraph, Set<Node> nodeset) {
         HashSet<Edge> behindEdgeSet = new HashSet();
         for(Edge e: wgraph.edges()) {
             if(!nodeset.contains(e.start()) )
             behindEdgeSet.add(e);
         }
         return behindEdgeSet;
     }


     public void runThinFlow() {

        DynamicNetwork workingGraph = getProblem().getGraph();
        IdentifiableDoubleMapping<Node> workingDemands = new IdentifiableDoubleMapping(getProblem().getNodeDemands());
        IdentifiableDoubleMapping<Node> nodelabels = new IdentifiableDoubleMapping(getProblem().getGraph().nodeCount());
        IdentifiableDoubleMapping<Edge> flowvalues = new IdentifiableDoubleMapping(getProblem().getGraph().edgeCount()+getNewedgeToOrigedgeMapSize());
        Double q; //congestion
        Set<Node> spcutSet;
        Set<Edge> exitEdges;
        Set<Edge> edgesBehindSparsestCut;
        Set<Edge> internalEdges = new HashSet<>();
        workingGraph.edges().forEach(internalEdges::add);

        SparsestCut sparsestcut;
        Node source = getProblem().getSource();

        boolean sourceDemandIsZero = false;

        while(true) {

           sparsestcut = new SparsestCut(workingGraph,getProblem().getCapacities(),workingDemands,source);

           q = sparsestcut.getCongestion();

           spcutSet = sparsestcut.getMinCut();

           exitEdges = exitingEdges(workingGraph,spcutSet);
           edgesBehindSparsestCut = behindEdges(workingGraph,spcutSet);

           HashSet<Edge> edgesBehind = new HashSet<Edge>();
           HashSet<Edge> edgesExit = new HashSet<Edge>();

           /*set flowvalues of edges behind sparsest cut*/
           Edge origEdge;
           double value;
           for(Edge be: edgesBehindSparsestCut) {
              value = q*sparsestcut.getMinCutEdgeFlowValue(be);
              flowvalues.set(be,value);
              if((origEdge=getOrigEdge(be)) != be)
                  flowvalues.set(origEdge,value);
              if(value > ZEROLIMIT) {
                  addEdgeToUsedEdges(be);
                  edgesBehind.add(getOrigEdge(be));
              }
              internalEdges.remove(be);

              }

           /*set flowvalues of exiting edges of sparsest cut*/
           for(Edge e: exitEdges) {
               value = q * getProblem().getCapacities().get(e);  //getEdgeCapacity(e);
               flowvalues.set(e,value);
               if((origEdge=getOrigEdge(e)) != e)
                   flowvalues.set(origEdge,value);
               if(value > ZEROLIMIT) {
                   addEdgeToUsedEdges(e);
                   edgesExit.add(getOrigEdge(e));
               }
               internalEdges.remove(e);
           }


           /*set nodelabels of nodes in workingGraph /setminus sparsestCutSet to congestion*/
           for(Node n: workingGraph.nodes()) {
               if(!spcutSet.contains(n)) {
                   nodelabels.set(n,q);
               }
           }

           if(spcutSet.size() == 1) {
               for(Node n: spcutSet) {
                  nodelabels.set(n, 0.0);
               }
               for(Edge e: internalEdges) {
                 flowvalues.set(e,0.0);
               }
              break;
           }

           /*set new demands of set in sparsest cut*/
           for(Node v: spcutSet) {

                ListSequence<Edge> outedges = workingGraph.outgoingEdges(v);
                HashSet<Edge> outEdges = new HashSet();
                for(Edge e: outedges) {
                   outEdges.add(e);
                }

                outEdges.retainAll(exitEdges);

                double newdemands = workingDemands.get(v);



                for(Edge edge: outEdges)
                    newdemands -= flowvalues.get(edge);

                workingDemands.set(v,newdemands);

            //check if demand is fulfilled
            if(v==source && newdemands <= EPSILON) {
                sourceDemandIsZero = true; //the demand of the source is fulfilled
                for(Node n: spcutSet) {
                    nodelabels.set(n, 0.0);
                }

                for(Edge e: internalEdges) {
                   flowvalues.set(e,0.0);
                }

                break; //jump out of for-loop
            }
           }

           //if the demand of the source is fulfilled, then jump out of while-loop
           if(sourceDemandIsZero) {
               break;
           }


            //update workingGraph
            DynamicNetwork newworkingGraph = new DynamicNetwork(workingGraph);
            for(Node n: workingGraph.nodes()) {
                if(!spcutSet.contains(n)) {
                    newworkingGraph.removeNode(n);
                }
            }
            workingGraph = newworkingGraph;


           } //end while

        System.out.println("nodelabels = " + nodelabels.toString());
        System.out.println("flowvalues = " + flowvalues.toString());

        //set NodeLabels
        getProblem().setNodeLabels(nodelabels);
        //set EdgeFlowValues
        getProblem().setEdgeFlowValues(flowvalues);

       }

     }



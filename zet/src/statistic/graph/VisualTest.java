/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * VisualTest.java
 *
 */
package statistic.graph;


import org.zetool.graph.Edge;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.Node;
import org.zetool.graph.DefaultDirectedGraph;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JComboBox;
import javax.swing.JFrame;

/**
 *
 * @author Martin Groß
 */
public class VisualTest extends JFrame {

    public static void main(String[] args) {
        DefaultDirectedGraph network = new DefaultDirectedGraph(9, 10);
        Node source1 = network.getNode(0);
        Node source2 = network.getNode(1);
        Node source3 = network.getNode(4);
        Node sink1 = network.getNode(5);
        Node sink2 = network.getNode(6);
        Node sink3 = network.getNode(8);
        Node a = network.getNode(2);
        Node b = network.getNode(3);
        Node c = network.getNode(7);
        Edge e = new Edge(0, source1, a);
        Edge f = new Edge(1, source1, b);
        Edge g = new Edge(2, source2, a);
        Edge h = new Edge(3, source2, source3);
        Edge i = new Edge(4, a, b);
        Edge j = new Edge(5, a, source3);
        Edge k = new Edge(6, b, sink1);
        Edge l = new Edge(7, b, sink2);
        Edge m = new Edge(8, source3, c);
        Edge n = new Edge(9, sink2, sink3);
        network.setEdge(e);
        network.setEdge(f);
        network.setEdge(g);
        network.setEdge(h);
        network.setEdge(i);
        network.setEdge(j);
        network.setEdge(k);
        network.setEdge(l);
        network.setEdge(m);
        network.setEdge(n);
        IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<Edge>(network.edgeCount());
        capacities.set(e, 1);
        capacities.set(f, 1);
        capacities.set(g, 2);
        capacities.set(h, 2);
        capacities.set(i, 3);
        capacities.set(j, 3);
        capacities.set(k, 1);
        capacities.set(l, 2);
        capacities.set(m, 1);
        capacities.set(n, 1);
        IdentifiableIntegerMapping<Edge> transitTimes = new IdentifiableIntegerMapping<Edge>(network.edgeCount());
        transitTimes.set(e, 1);
        transitTimes.set(f, 1);
        transitTimes.set(g, 2);
        transitTimes.set(h, 2);
        transitTimes.set(i, 3);
        transitTimes.set(j, 3);
        transitTimes.set(k, 1);
        transitTimes.set(l, 2);
        transitTimes.set(m, 1);
        transitTimes.set(n, 1);
        LinkedList<Node> sources = new LinkedList<Node>();
        sources.add(source1);
        sources.add(source2);
        sources.add(source3);
        LinkedList<Node> sinks = new LinkedList<Node>();
        sinks.add(sink1);
        sinks.add(sink2);
        sinks.add(sink3);
        IdentifiableIntegerMapping<Node> supplies = new IdentifiableIntegerMapping<Node>(network.nodeCount());
        supplies.set(source1, 2);
        supplies.set(source2, 1);
        supplies.set(source3, 0);
        supplies.set(sink1, -1);
        supplies.set(sink2, -1);
        supplies.set(sink3, -1);
       //TimeExpandedMaximumFlowOverTime algo = new TimeExpandedMaximumFlowOverTime(network, capacities, transitTimes, sources, sinks, 10);
        //algo.run();
        //MinimumMeanCycleCancelling algo2 = new MinimumMeanCycleCancelling(network, capacities, transitTimes, supplies);
        //algo2.run();
        //PathComposition fc = new PathComposition(network, transitTimes, algo.getSolution());
        //fc.run();
        //NetworkFlowModel nfm = new NetworkFlowModel();
        //nfm.setNetwork(network);
        //nfm.setEdgeCapacities(capacities);
        //nfm.setTransitTimes(transitTimes);
        //nfm.setCurrentAssignment(supplies);
        //GraphData data = new GraphData(network, capacities, supplies, transitTimes, supplies, sinks, algo.getSolution());
        //final Statistics<GraphData> stats = new Statistics<GraphData>(data);
        //final FlowStatisticsCalculator fs = new FlowStatisticsCalculator(nfm, algo.getDynamicFlow());

        VisualTest test = new VisualTest();
        test.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        final JComboBox box = new JComboBox();
        final JChartPanel charts = new JChartPanel();
        final Edge edge = i;
        final Node node = sink1;
        charts.clear();
        //charts.add("Flussrate", stats.get(FLOW_RATE, edge));
        //charts.add("Flussmenge", stats.get(FLOW_AMOUNT, edge));
        box.addItem("Kante: Flussrate & -menge");
        box.addItem("Kante: Flussrate & Auslastung");
        box.addItem("Senke: Eingehende Flussrate & -menge");
        box.addItem("Senke: Ausgehende Flussrate & -menge");
        box.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                charts.clear();
                /*
                switch (box.getSelectedIndex()) {
                    case 0:
                        charts.add("Flussrate", stats.get(FLOW_RATE, edge));
                        charts.add("Flussmenge", stats.get(FLOW_AMOUNT, edge));
                        break;
                    case 1:
                        charts.add("Flussrate", stats.get(FLOW_RATE, edge));
                        charts.add("Auslastung", stats.get(DynamicEdgeStatistic.LOAD, edge));
                        charts.add("Kapazität", stats.getData().getCapacity(edge));
                        break;
                    case 2:
                        charts.add("Eingehende Flussrate", stats.get(INCOMING_FLOW_RATE, node));
                        charts.add("Eingegangene Flussmenge", stats.get(INCOMING_FLOW_AMOUNT, node));
                        break;
                    case 3:
                        charts.add("Ausgehende Flussrate", stats.get(OUTGOING_FLOW_RATE, node));
                        charts.add("Ausgegangene Flussmenge", stats.get(OUTGOING_FLOW_AMOUNT, node));
                        break;
                }*/
            }
        });
        test.add(box, BorderLayout.NORTH);
        test.add(charts, BorderLayout.CENTER);
        test.pack();
        test.setVisible(true);
    }
}

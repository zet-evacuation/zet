/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * JGraphStatisticPanel.java
 *
 */
package gui.statistic;

import algo.graph.util.PathComposition;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.converters.ConversionException;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.Edge;
import ds.mapping.IdentifiableIntegerMapping;
import ds.graph.network.AbstractNetwork;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.Node;
import ds.graph.network.Network;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import statistic.graph.ComplexStatistic;
import statistic.graph.Controller;
import statistic.graph.GraphData;
import statistic.graph.Statistics;
import statistic.graph.StatisticsCollection;
import statistic.graph.gui.DiagramPanel;
import statistic.graph.gui.DiagramData;
import statistic.graph.gui.DisplayProfile;
import statistic.graph.gui.DisplayableStatistic;
import statistic.graph.gui.ProfilePanel;
import statistic.graph.gui.StatisticPanel;

/**
 *
 * @author Martin Groß
 */
public class JGraphStatisticPanel extends JPanel {
    
    private static final Logger LOGGER = Logger.getLogger("gui.statistic.JGraphStatisticPanel");

    private List<Edge> selectedEdges;
    private List<FlowOverTimePath> selectedFlows;
    private List<Node> selectedNodes;
    private StatisticsCollection runs;
    private ProfilePanel profilePanel;

    public JGraphStatisticPanel() {
        super();
        //createTestInstance();
        createComponents();
        loadSettings();
    }

    private void createTestInstance() {
        runs = new StatisticsCollection();
        for (int index = 0; index < 5; index++) {
            AbstractNetwork network = new Network(10, 14) {};
            Node source1 = network.getNode(0);
            Node source2 = network.getNode(1);
            Node source3 = network.getNode(4);
            Node sink1 = network.getNode(5);
            Node sink2 = network.getNode(6);
            Node sink3 = network.getNode(8);
            Node a = network.getNode(2);
            Node b = network.getNode(3);
            Node c = network.getNode(7);
            Node supersink = network.getNode(9);
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
            Edge o = new Edge(10, c, sink3);
            Edge p = new Edge(11, sink1, supersink);
            Edge q = new Edge(12, sink2, supersink);
            Edge r = new Edge(13, sink3, supersink);
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
            network.setEdge(o);
            network.setEdge(p);
            network.setEdge(q);
            network.setEdge(r);
            IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<>(network.numberOfEdges());
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
            capacities.set(o, 1);
            capacities.set(p, 1000);
            capacities.set(q, 1000);
            capacities.set(r, 1000);
            IdentifiableIntegerMapping<Edge> transitTimes = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges());
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
            transitTimes.set(r, 1);
            transitTimes.set(p, 0);
            transitTimes.set(q, 0);
            transitTimes.set(r, 0);
            LinkedList<Node> sources = new LinkedList<Node>();
            sources.add(source1);
            sources.add(source2);
            sources.add(source3);
            LinkedList<Node> sinks = new LinkedList<Node>();
            sinks.add(sink1);
            sinks.add(sink2);
            sinks.add(sink3);
            IdentifiableIntegerMapping<Node> supplies = new IdentifiableIntegerMapping<Node>(network.numberOfNodes());
            Random rng = new Random();
            int[] sup = new int[3];
            sup[0] = index;
            sup[1] = 5;
            sup[2] = 5 - index;
            supplies.set(source1, sup[0]);
            supplies.set(source2, sup[1]);
            supplies.set(source3, sup[2]);
            supplies.set(sink3, -sup[0] - sup[1] - sup[2]);
            //EATransshipment ea = new EATransshipment();
            PathBasedFlowOverTime flow = new PathBasedFlowOverTime(); //ea.compute(network, transitTimes, capacities, supplies);            
            PathComposition fc = new PathComposition(network, transitTimes, flow);
            fc.run();
            NetworkFlowModel nfm = new NetworkFlowModel();
            nfm.setNetwork(network);
            nfm.setEdgeCapacities(capacities);
            nfm.setTransitTimes(transitTimes);
            nfm.setCurrentAssignment(supplies);
            GraphData data = new GraphData(network, capacities, supplies, transitTimes, supplies, sinks, flow);
            final Statistics stats = new Statistics(data);
            runs.add(stats);
            selectedNodes = sinks;
            selectedEdges = new LinkedList<Edge>();
            selectedEdges.add(n);
            selectedEdges.add(o);
            selectedFlows = new LinkedList<FlowOverTimePath>();
            selectedFlows.add(flow.iterator().next());
        }
    }

    public void createComponents() {
        Controller controller = Controller.getInstance();
        controller.setEdges(selectedEdges);
        controller.setNodes(selectedNodes);
        controller.setFlows(selectedFlows);
        controller.setRuns(runs);

        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JPanel west = new JPanel(new BorderLayout());
        JPanel content = new JPanel();
        JScrollPane contentS = new JScrollPane(content);
        west.add(contentS, BorderLayout.CENTER);
        JPanel east = new JPanel(new BorderLayout());
        JPanel eastContent = new JPanel(new BorderLayout());
        JScrollPane eastScrollPane = new JScrollPane(eastContent);
        profilePanel = new ProfilePanel();
        DiagramPanel diagramPanel = new DiagramPanel();
        StatisticPanel statisticPanel = new StatisticPanel();
        eastContent.add(profilePanel, BorderLayout.NORTH);
        eastContent.add(diagramPanel, BorderLayout.CENTER);
        eastContent.add(statisticPanel, BorderLayout.SOUTH);
        controller.setContentPane(content);
        controller.setScrollPane(contentS);
        east.add(eastScrollPane);
        east.setMinimumSize(new Dimension((int) eastContent.getMinimumSize().getWidth() + 22, (int) eastContent.getMinimumSize().getHeight()));
        east.setPreferredSize(east.getMinimumSize());
        east.setMaximumSize(east.getMinimumSize());

        pane.setLeftComponent(west);
        pane.setRightComponent(east);
        pane.setResizeWeight(1);

        diagramPanel.addDiagramListener(controller);        
        profilePanel.addProfileListener(diagramPanel);
        profilePanel.addProfileListener(statisticPanel);
        profilePanel.addProfileListener(controller);
        statisticPanel.addStatisticListener(controller);
        diagramPanel.addDiagramListener(statisticPanel);

        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
    }
    
    public void loadSettings() {
        File file = new File("statistic_profiles.xml");
        if (file.exists()) {
            try {
                XStream xstream = new XStream();
                Annotations.configureAliases(xstream, ComplexStatistic.class);
                Annotations.configureAliases(xstream, DiagramData.class);
                Annotations.configureAliases(xstream, DisplayProfile.class);
                Annotations.configureAliases(xstream, DisplayableStatistic.class);
                List<DisplayProfile> profiles = (List<DisplayProfile>) xstream.fromXML(new BufferedReader(new FileReader(file)));
                profilePanel.setProfiles(profiles);
            } catch (ConversionException e) {
                LOGGER.info(String.format("Die Datei %1$s mit den Einstellungen des Standard-Profils für die Graph-Statistik besteht aus einem unbekannten Format. Möglicherweise ist die Datei veraltet.",file.getAbsolutePath()));
            } catch (FileNotFoundException ex) {
                LOGGER.info(String.format("Die Datei %1$s mit den Einstellungen des Standard-Profils für die Graph-Statistik wurde nicht gefunden.",file.getAbsolutePath()));
            }
        }        
    }

    public void saveSettings() {
        BufferedWriter writer = null;
        try {
            XStream xstream = new XStream();
            File file = new File("statistic_profiles.xml");
            writer = new BufferedWriter(new FileWriter(file));
            Annotations.configureAliases(xstream, ComplexStatistic.class);
            Annotations.configureAliases(xstream, DiagramData.class);
            Annotations.configureAliases(xstream, DisplayProfile.class);
            Annotations.configureAliases(xstream, DisplayableStatistic.class);
            xstream.toXML(profilePanel.getProfiles(), writer);
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

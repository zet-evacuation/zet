/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
package statistic.graph.gui;

import org.zetool.netflow.classic.PathComposition;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.graph.Edge;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.Node;
import org.zetool.graph.DefaultDirectedGraph;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import statistic.graph.ComplexStatistic;
import statistic.graph.Controller;
import statistic.graph.GraphData;
import org.zetool.statistic.Statistics;
import statistic.graph.StatisticsCollection;

/**
 *
 * @author Martin Groß
 */
public class Test {
    
    public static void main(String[] args) {
        Handler handler = new ConsoleHandler();
        Logger.getLogger("statistic.graph").setLevel(Level.ALL);
        Logger.getLogger("statistic.graph").addHandler(handler);
        //handler.setLevel(Level.ALL);
        try {
            Controller controller = Controller.getInstance();            
            StatisticsCollection runs = new StatisticsCollection();
            for (int index = 0; index < 5; index++) {
                DefaultDirectedGraph network = new DefaultDirectedGraph(10, 14);
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
                List<Edge> edges = new LinkedList<Edge>();
                edges.add(o);
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
                capacities.set(o, 1);
                capacities.set(p, 1000);
                capacities.set(q, 1000);
                capacities.set(r, 1000);
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
                IdentifiableIntegerMapping<Node> supplies = new IdentifiableIntegerMapping<Node>(network.nodeCount());
                Random rng = new Random();
                int[] sup = new int[3];
                sup[0] = index;
                sup[1] = 5;
                sup[2] = 5-index;
                supplies.set(source1, sup[0]);
                supplies.set(source2, sup[1]);
                supplies.set(source3, sup[2]);
                //supplies.set(sink1, -1);
                //supplies.set(sink2, -1);
                supplies.set(sink3, -sup[0]-sup[1]-sup[2]);
                //TimeExpandedMaximumFlowOverTime algo = new TimeExpandedMaximumFlowOverTime(network, capacities, transitTimes, sources, sinks, 10);
          //      EATransshipment ea = new EATransshipment();
                PathBasedFlowOverTime flow = new PathBasedFlowOverTime(); //ea.compute(network, transitTimes, capacities, supplies);
                //algo.run();
                //PathComposition fc = new PathComposition(network, transitTimes, algo.getDynamicFlow());
                PathComposition fc = new PathComposition(network, transitTimes, flow);
                fc.run();
                //NetworkFlowModel nfm = new NetworkFlowModel();
                //nfm.setNetwork(network);
                //nfm.setEdgeCapacities(capacities);
                //nfm.setTransitTimes(transitTimes);
                //nfm.setCurrentAssignment(supplies);
                //Data data = new Data(network, capacities, supplies, transitTimes, supplies, sinks, algo.getDynamicFlow());
                GraphData data = new GraphData(network, capacities, supplies, transitTimes, supplies, sinks, flow);
                final Statistics<GraphData> stats = new Statistics<>(data);            
                runs.add(stats);     
                controller.setEdges(edges);
                controller.setNodes(sinks);
            }
            controller.setRuns(runs);

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame test = new JFrame("Test");



            JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

            JPanel west = new JPanel(new BorderLayout());

            JPanel content = new JPanel();
            JScrollPane contentS = new JScrollPane(content);
            west.add(contentS, BorderLayout.CENTER);
            JPanel east = new JPanel(new BorderLayout());
            JPanel eastContent = new JPanel(new BorderLayout());
            JScrollPane eastScrollPane = new JScrollPane(eastContent);
            final ProfilePanel profile = new ProfilePanel();
            DiagramPanel diagramPanel = new DiagramPanel();
            StatisticPanel statisticPanel = new StatisticPanel();
            //controller.dcp = dcp;
            diagramPanel.addDiagramListener(controller);
            profile.addProfileListener(diagramPanel);
            //controller.p = contentS;
            
            //controller.set = set;
            eastContent.add(profile, BorderLayout.NORTH);
            eastContent.add(diagramPanel, BorderLayout.CENTER);
            eastContent.add(statisticPanel, BorderLayout.SOUTH);
            //west.add(new JTextField(), BorderLayout.CENTER);
            controller.setContentPane(content);
            controller.setScrollPane(contentS);
            //content.add(new ChartPanel(ChartFactory.createLineChart("T", "X", "Y", null, PlotOrientation.VERTICAL, true, true, false)));
            //west.setPreferredSize(west.getMaximumSize());
            //pane2.setPreferredSize(pane2.getMaximumSize());
            east.add(eastScrollPane);
            east.setMinimumSize(new Dimension((int) eastContent.getMinimumSize().getWidth()+ 22, (int) eastContent.getMinimumSize().getHeight()));
            east.setPreferredSize(east.getMinimumSize());
            east.setMaximumSize(east.getMinimumSize());

            pane.setLeftComponent(west);
            pane.setRightComponent(east);
            pane.setResizeWeight(1);
            //pane.resetToPreferredSizes();

/*
XYSeriesCollection dataset = new XYSeriesCollection();
XYSeries series = new XYSeries("Test");
series.add(Integer.MIN_VALUE, 0);
series.add(0, 0);
series.add(1, 1);
series.add(2, 2);
series.add(3, 3);
series.add(Integer.MAX_VALUE, 3);
dataset.addSeries(series);
JFreeChart chart = null;
            content.add(new ChartPanel(chart = ChartFactory.createXYAreaChart("Title", "X", "Y", dataset, PlotOrientation.VERTICAL, true, true, false)));
chart.getXYPlot().setDomainAxis(new NumberAxis("X"));*/
//chart.getXYPlot().getDomainAxis().setRange(-1, 20);

            profile.addProfileListener(statisticPanel);
            profile.addProfileListener(controller);
            statisticPanel.addStatisticListener(controller);
            diagramPanel.addDiagramListener(statisticPanel);

            File file = new File("statistic_profiles.xml");
            if (file.exists()) {
                try {
                    XStream xstream = new XStream();
                    Annotations.configureAliases(xstream, ComplexStatistic.class);                    
                    Annotations.configureAliases(xstream, DiagramData.class);
                    Annotations.configureAliases(xstream, DisplayProfile.class);                    
                    Annotations.configureAliases(xstream, DisplayableStatistic.class);                    
                    List<DisplayProfile> profiles = (List<DisplayProfile>) xstream.fromXML(new BufferedReader(new FileReader(file)));
                    profile.setProfiles(profiles);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            test.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            test.setContentPane(pane);
            test.pack();
            test.setExtendedState(JFrame.MAXIMIZED_BOTH);
            pane.resetToPreferredSizes();
            test.setLocationRelativeTo(null);
            test.setVisible(true);
            test.addWindowListener(new WindowListener() {

                public void windowOpened(WindowEvent e) {
                }

                public void windowClosing(WindowEvent e) {
                    BufferedWriter writer = null;
                    try {
                        XStream xstream = new XStream();
                        File file = new File("statistic_profiles.xml");
                        writer = new BufferedWriter(new FileWriter(file));
                        Annotations.configureAliases(xstream, ComplexStatistic.class);                    
                        Annotations.configureAliases(xstream, DiagramData.class);
                        Annotations.configureAliases(xstream, DisplayProfile.class);
                        Annotations.configureAliases(xstream, DisplayableStatistic.class);                    
                        xstream.toXML(profile.getProfiles(), writer);
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

                public void windowClosed(WindowEvent e) {
                }

                public void windowIconified(WindowEvent e) {
                }

                public void windowDeiconified(WindowEvent e) {
                }

                public void windowActivated(WindowEvent e) {
                }

                public void windowDeactivated(WindowEvent e) {
                }
            });
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StatisticPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(StatisticPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StatisticPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(StatisticPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}

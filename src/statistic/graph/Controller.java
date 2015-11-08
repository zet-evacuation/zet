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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package statistic.graph;

import org.zetool.statistic.Statistics;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.ds.structure.FlowOverTimePath;
import org.zetool.graph.Edge;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import org.zetool.graph.Node;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import statistic.graph.dataset.PieChartCreator;
import statistic.graph.dataset.TableCreator;
import statistic.graph.dataset.XYChartCreator;
import statistic.graph.gui.DiagramData;
import statistic.graph.gui.event.DiagramEvent;
import statistic.graph.gui.event.DiagramListener;
import statistic.graph.gui.DiagramType;
import statistic.graph.gui.DisplayProfile;
import statistic.graph.gui.DisplayableStatistic;
import statistic.graph.gui.event.DiagramAddedEvent;
import statistic.graph.gui.event.DiagramChangedEvent;
import statistic.graph.gui.event.DiagramRemovedEvent;
import statistic.graph.gui.event.DiagramSelectionChangedEvent;
import statistic.graph.gui.event.DiagramSequenceChangedEvent;
import statistic.graph.gui.event.DiagramTitleChangedEvent;
import statistic.graph.gui.event.DiagramTypeChangedEvent;
import statistic.graph.gui.event.DiagramXAxisLabelChangedEvent;
import statistic.graph.gui.event.DiagramYAxisLabelChangedEvent;
import statistic.graph.gui.event.ProfileEvent;
import statistic.graph.gui.event.ProfileListener;
import statistic.graph.gui.event.StatisticAddedEvent;
import statistic.graph.gui.event.StatisticBaseValueChangedEvent;
import statistic.graph.gui.event.StatisticChangedEvent;
import statistic.graph.gui.event.StatisticColorChangedEvent;
import statistic.graph.gui.event.StatisticDiagramChangedEvent;
import statistic.graph.gui.event.StatisticEvent;
import statistic.graph.gui.event.StatisticListener;
import statistic.graph.gui.event.StatisticObjectOperationChangedEvent;
import statistic.graph.gui.event.StatisticObjectParameterChangedEvent;
import statistic.graph.gui.event.StatisticRemovedEvent;
import statistic.graph.gui.event.StatisticRunOperationChangedEvent;
import statistic.graph.gui.event.StatisticRunParameterChangedEvent;

/**
 *
 * @author Martin Groß
 */
public class Controller implements DiagramListener, ProfileListener, StatisticListener {

    private static final Logger LOGGER = Logger.getLogger("statistic.graph.Controller");
    private static Controller instance;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }
    private JScrollPane scrollPane;
    private Container contentPane;
    private Map<DiagramData, ChartPanel> chartPanels;
    private Map<DiagramData, List<DisplayableStatistic>> statistics;
    private Map<DiagramData, JScrollPane> tableScrollPanes;
    private DisplayProfile profile;
    private StatisticsCollection runs;
    private List<Edge> edges;
    private List<FlowOverTimePath> flows;
    private List<Node> nodes;

    private Controller() {
        chartPanels = new HashMap<>();
        statistics = new HashMap<>();
        tableScrollPanes = new HashMap<>();
        edges = new LinkedList<>();
        flows = new LinkedList<>();
        nodes = new LinkedList<>();
    }

    public void setFlow(NetworkFlowModel model, PathBasedFlowOverTime flow) {
        // runs.clear();  // This led to a null pointer exception - Replaced by Timon
		// edges.clear();  // This led to a null pointer exception - Replaced by Timon
        // flows.clear();  // This led to a null pointer exception - Replaced by Timon
        // nodes.clear();    // This led to a null pointer exception - Replaced by Timon
		runs = new StatisticsCollection ();
        edges = new LinkedList<>();
        flows = new LinkedList<>();
        nodes = new LinkedList<>();
		
        if (model != null) {
            GraphData data = new GraphData(model, flow);
            Statistics run = new Statistics(data);
            runs.add(run);
            for (Node node : model.graph().predecessorNodes(model.getSupersink())) {
                nodes.add(node);
            }
			if (model.graph().edgeCount() > 0) {
				for (int i = 0; i < 3; i++) {
					Random rng = new Random();
					int r = rng.nextInt(model.graph().edgeCount());
					if (model.graph().getEdge(r) != null) {
						edges.add(model.graph().getEdge(r));
					}
				}
			}
            for (FlowOverTimePath f : flow) {
                flows.add(f);
            }   
        }
    }

    public Container getContentPane() {
        return contentPane;
    }

    public void setContentPane(Container contentPane) {
        this.contentPane = contentPane;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public List<FlowOverTimePath> getFlows() {
        return flows;
    }

    public void setFlows(List<FlowOverTimePath> flows) {
        this.flows = flows;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public StatisticsCollection getRuns() {
        return runs;
    }

    public void setRuns(StatisticsCollection runs) {
        this.runs = runs;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public void notify(DiagramEvent event) {
        LOGGER.entering("Controller", "notify", event);
        DiagramData data = event.getDiagram();
        if (data == null) {
            return;
        }
        if (event instanceof DiagramChangedEvent) {
            ChartPanel chartPanel = chartPanels.get(data);
            if (chartPanel != null) {
                if (event instanceof DiagramTitleChangedEvent) {
                    chartPanel.getChart().setTitle(((DiagramTitleChangedEvent) event).getNewTitle());
                } else if (event instanceof DiagramXAxisLabelChangedEvent) {
                    chartPanel.getChart().getXYPlot().getDomainAxis().setLabel(((DiagramXAxisLabelChangedEvent) event).getNewXAxisLabel());
                } else if (event instanceof DiagramYAxisLabelChangedEvent) {
                    chartPanel.getChart().getXYPlot().getRangeAxis().setLabel(((DiagramYAxisLabelChangedEvent) event).getNewYAxisLabel());
                } else {
                    throw new AssertionError("This should not happen.");
                }
                chartPanel.chartChanged(new ChartChangeEvent(event, chartPanel.getChart(), ChartChangeEventType.GENERAL));
            }
        } else if (event instanceof DiagramSelectionChangedEvent) {
        } else {
            if (event instanceof DiagramAddedEvent) {
                if (data.getType() == DiagramType.TABLE) {
                    JTable table = createTable(data);
                    JScrollPane pane = new JScrollPane(table);
                    pane.setPreferredSize(new Dimension(scrollPane.getWidth() / 2 - 14, 300));
                    tableScrollPanes.put(data, pane);
                    contentPane.add(pane);
                } else {
                    ChartPanel chartPanel = createChartPanel(data);
                    //chartPanel.setPreferredSize(new Dimension(scrollPane.getWidth() / 2 - 14, 300));
                    chartPanels.put(data, chartPanel);
                    contentPane.add(chartPanel);
                }
            } else if (event instanceof DiagramRemovedEvent) {
                if (data.getType() == DiagramType.TABLE) {
                    JScrollPane pane = tableScrollPanes.remove(data);
                    contentPane.remove(pane);
                } else {
                    ChartPanel chartPanel = chartPanels.remove(data);
                    contentPane.remove(chartPanel);
                }
            } else if (event instanceof DiagramSequenceChangedEvent) {
                DiagramData data2 = ((DiagramSequenceChangedEvent) event).getDiagram2();
                Component component1 = getComponent(data);
                LOGGER.finest("1. Komponente: " + component1);
                Component component2 = getComponent(data2);
                LOGGER.finest("2. Komponente: " + component2);
                int index = 0;
                for (Component component : contentPane.getComponents()) {
                    if (component == component1) {
                        break;
                    }
                    index++;
                }
                LOGGER.finest("1. Komponente befindet sich an Index: " + index);
                LOGGER.finest("Entferne 2. Komponente");
                contentPane.remove(component2);
                LOGGER.finest("Füge 2. Komponent an Index " + index + " wieder ein.");
                contentPane.add(component2, index);
            } else if (event instanceof DiagramTypeChangedEvent) {
                Component component = null;
                if (chartPanels.containsKey(data)) {
                    component = chartPanels.get(data);
                } else if (tableScrollPanes.containsKey(data)) {
                    component = tableScrollPanes.get(data);
                }
                int index = 0;
                for (Component c : contentPane.getComponents()) {
                    if (c == component) {
                        break;
                    }
                    index++;
                }
                contentPane.remove(component);
                if (component instanceof ChartPanel) {
                    chartPanels.remove(data);
                } else if (component instanceof JScrollPane) {
                    tableScrollPanes.remove(data);
                }
                if (data.getType() == DiagramType.TABLE) {
                    JTable table = createTable(data);
                    JScrollPane pane = new JScrollPane(table);
                    pane.setPreferredSize(new Dimension(scrollPane.getWidth() / 2 - 14, 300));
                    component = pane;
                    tableScrollPanes.put(data, pane);
                } else {
                    ChartPanel panel = createChartPanel(data);
                    component = panel;
                    chartPanels.put(data, panel);
                }
                contentPane.add(component, index);
            } else {
                throw new AssertionError("This should not happen.");
            }
            scrollPane.validate();
            scrollPane.repaint();
        }
    }

    public void notify(ProfileEvent event) {
        LOGGER.entering("statistic.graph.Controller", "notify", event);
        contentPane.removeAll();
        chartPanels.clear();
        statistics.clear();
        tableScrollPanes.clear();
        profile = event.getProfile();
        if (profile == null) {
            return;
        }
        for (DisplayableStatistic statistic : profile.getStatistics()) {
            if (!statistics.containsKey(statistic.getAttributes().getDiagram())) {
                statistics.put(statistic.getAttributes().getDiagram(), new LinkedList<DisplayableStatistic>());
            }
            statistic.getStatistic().setStatisticsCollection(runs);
            statistics.get(statistic.getAttributes().getDiagram()).add(statistic);
        }
        createChartsAndTables();
    }

    public void notify(StatisticEvent event) {
        LOGGER.entering("statistic.graph.Controller", "notify", event);
        LOGGER.fine("Controller.StatisticEvent: " + event);
        DisplayableStatistic statistic = event.getStatistic();
        if (event instanceof StatisticAddedEvent) {
            if (!statistics.containsKey(statistic.getAttributes().getDiagram())) {
                statistics.put(statistic.getAttributes().getDiagram(), new LinkedList<DisplayableStatistic>());
            }
            statistic.getStatistic().setStatisticsCollection(runs);
            statistics.get(statistic.getAttributes().getDiagram()).add(statistic);
            recreateChartOrTable(statistic.getAttributes().getDiagram());
        } else if (event instanceof StatisticChangedEvent) {
            if (event instanceof StatisticBaseValueChangedEvent
                    || event instanceof StatisticColorChangedEvent                    
                    || event instanceof StatisticObjectOperationChangedEvent
                    || event instanceof StatisticObjectParameterChangedEvent
                    || event instanceof StatisticRunOperationChangedEvent
                    || event instanceof StatisticRunParameterChangedEvent                    
                    ) {
                recreateChartOrTable(statistic.getAttributes().getDiagram());
            }
        } else if (event instanceof StatisticDiagramChangedEvent) {
            if (!statistics.containsKey(statistic.getAttributes().getDiagram())) {
                statistics.put(((StatisticDiagramChangedEvent) event).getOldDiagram(), new LinkedList<DisplayableStatistic>());
            }
            if (!statistics.containsKey(statistic.getAttributes().getDiagram())) {
                statistics.put(statistic.getAttributes().getDiagram(), new LinkedList<DisplayableStatistic>());
            }
            statistics.get(((StatisticDiagramChangedEvent) event).getOldDiagram()).remove(statistic);
            statistics.get(statistic.getAttributes().getDiagram()).add(statistic);
            recreateChartOrTable(((StatisticDiagramChangedEvent) event).getOldDiagram());
            recreateChartOrTable(statistic.getAttributes().getDiagram());
        } else if (event instanceof StatisticRemovedEvent) {
            if (!statistics.containsKey(statistic.getAttributes().getDiagram())) {
                statistics.put(statistic.getAttributes().getDiagram(), new LinkedList<DisplayableStatistic>());
            }
            statistics.get(statistic.getAttributes().getDiagram()).remove(statistic);
            recreateChartOrTable(statistic.getAttributes().getDiagram());
        }
    }

    private void createChartsAndTables() {
        for (DiagramData diagram : profile.getDiagrams()) {
            createChartOrTable(diagram);
        }
        contentPane.setLayout(new GridLayout(0, 2, 8, 8));
        scrollPane.validate();
        scrollPane.repaint();
    }

    private void createChartOrTable(DiagramData diagram) {
        if (diagram.getType() == DiagramType.TABLE) {
            JTable table = createTable(diagram);
            JScrollPane pane = new JScrollPane(table);
            pane.setPreferredSize(new Dimension(scrollPane.getWidth() / 2 - 14, 300));
            tableScrollPanes.put(diagram, pane);
            contentPane.add(pane);
        } else {
            ChartPanel chartPanel = createChartPanel(diagram);
            chartPanels.put(diagram, chartPanel);
            contentPane.add(chartPanel);
        }
    }    
    
    private void createChartOrTable(DiagramData diagram, int index) {
        if (diagram.getType() == DiagramType.TABLE) {
            JTable table = createTable(diagram);
            JScrollPane pane = new JScrollPane(table);
            pane.setPreferredSize(new Dimension(scrollPane.getWidth() / 2 - 14, 300));
            tableScrollPanes.put(diagram, pane);
            contentPane.add(pane, index);
        } else {
            ChartPanel chartPanel = createChartPanel(diagram);
            chartPanels.put(diagram, chartPanel);
            contentPane.add(chartPanel, index);
        }
    }

    private ChartPanel createChartPanel(DiagramData newDiagram) {
        LOGGER.entering("statistic.graph.Controller", "createChartPanel", new Object[]{newDiagram, newDiagram.getType()});
        DiagramData diagram = newDiagram;
        JFreeChart chart;
        switch (diagram.getType()) {
            case AREA_CHART:
            case BAR_CHART:
            case LINE_CHART:
            case STEP_CHART:
            case STEP_AREA_CHART:
                if (statistics.get(diagram) == null) {
                    chart = new XYChartCreator(diagram, null, getObjects(), null).getChart();
                } else {
                    chart = new XYChartCreator(diagram, statistics.get(diagram), getObjects(), null).getChart();
                }
                break;
            case PIE_CHART:
            case PIE_CHART_3D:
            case RING_CHART:
                if (statistics.get(diagram) == null || statistics.get(diagram).isEmpty()) {
                    chart = new PieChartCreator(diagram, null, getObjects(), null).getChart();
                } else {
                    chart = new PieChartCreator(diagram, statistics.get(diagram).get(0), getObjects(), null).getChart();
                }
                break;
            default: {
                throw new AssertionError("This should not happen.");
            }
        }
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(scrollPane.getWidth() / 2 - 14, 300));
        return chartPanel;
    }

    private JTable createTable(DiagramData diagram) {
        JTable table;
        if (statistics.get(diagram) == null) {
            table = new TableCreator(diagram, null, getObjects(), null).getTable();
        } else {
            table = new TableCreator(diagram, statistics.get(diagram), getObjects(), null).getTable();
        }
        table.setPreferredSize(new Dimension(scrollPane.getWidth() / 2 - 24, 300 - 30));
        return table;
    }

    private void recreateChartOrTable(DiagramData diagram) {
        LOGGER.entering("statistic.graph.Controller", "recreateChartOrTable", diagram);
        if (diagram == null) {
            return; 
        }
        Component component = getComponent(diagram);
        int index = getComponentIndex(diagram);
        contentPane.remove(component);        
        createChartOrTable(diagram, index);
        scrollPane.validate();
        scrollPane.repaint();
    }

    private Component getComponent(DiagramData data) {
        LOGGER.entering("statistic.graph.Controller", "getComponent", data);
        if (data.getType() == DiagramType.TABLE) {
            LOGGER.finest("Komponente ist Tabelle");
            LOGGER.finest("Bekannte Tabellen: " + tableScrollPanes);
            return tableScrollPanes.get(data);
        } else {
            return chartPanels.get(data);
        }
    }

    private int getComponentIndex(DiagramData data) {
        Component component1 = getComponent(data);
        int index = 0;
        for (Component component : contentPane.getComponents()) {
            if (component == component1) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private List getObjects() {
        switch (profile.getType()) {
            case EDGE:
                return edges;
            case FLOW:
                return flows;
            case GLOBAL:
                return new LinkedList();
            case NODE:
                return nodes;
            default:
                throw new AssertionError("This should not happen.");
        }
    }
}

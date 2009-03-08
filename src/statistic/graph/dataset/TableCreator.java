/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * TableCreator.java
 *
 */

package statistic.graph.dataset;

import ds.graph.Edge;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import statistic.graph.Operation;
import statistic.graph.gui.DiagramData;
import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class TableCreator {

    private DiagramData diagram;
    private Vector firstColumnValues;
    private DefaultTableModel model;
    private JTable table;
    private List<Object> selectedObjects;
    private List<DisplayableStatistic> statistics;
    
    public TableCreator(DiagramData diagram, List<DisplayableStatistic> statistics, List<Object> selectedObjects, Vector firstColumnValues) {
        this.diagram = diagram;
        this.firstColumnValues = firstColumnValues;
        this.selectedObjects = selectedObjects;
        this.statistics = statistics;
        validateStatistics();
        validateObjects();
        validateFirstColumnValues();
        createModel();
        createTable();
    }

    public JTable getTable() {
        return table;
    }
    
    private void validateStatistics() {
        if (statistics == null) {
            statistics = new LinkedList<DisplayableStatistic>();
        }        
        LinkedList<DisplayableStatistic> incomplete = new LinkedList<DisplayableStatistic>();
        for (DisplayableStatistic statistic : statistics) {
            if (!statistic.isInitialized() || statistic.getStatistic().getStatisticsCollection() == null || statistic.getStatistic().getStatisticsCollection().isEmpty()) {
                incomplete.add(statistic);
            }            
        }
        if (!incomplete.isEmpty()) {
            statistics = new LinkedList<DisplayableStatistic>(statistics);
            for (DisplayableStatistic statistic : incomplete) {
                statistics.remove(statistic);
            }
        }
    }
    
    private void validateFirstColumnValues() {        
    }    

    private void validateObjects() {
        if (selectedObjects == null) {
            selectedObjects = new LinkedList<Object>();
        }
    }
    
    private void createModel() { 
        model = new DefaultTableModel();
        for (DisplayableStatistic statistic : statistics) {
            addStatistic(statistic);
        }            
    }
    
    private void addStatistic(DisplayableStatistic statistic) {
        Operation objOp = statistic.getStatistic().getObjectOperation();
        Operation runOp = statistic.getStatistic().getRunOperation();
        String name = statistic.getAttributes().getName();
        if (selectedObjects.isEmpty()) {
            if (runOp == null || runOp.isComparing()) {
                List values = statistic.getStatistic().getListPerRun(selectedObjects);
                Vector runs = new Vector();
                for (int i = 0; i < values.size(); i++) {
                    runs.add("Durchlauf " + (i+1));
                }
                model.addColumn("Durchläufe", runs);
                model.addColumn(name, new Vector(values));
            } else {
                Object value = statistic.getStatistic().get(new Object());
                model.addColumn("Statistik");
                model.addColumn("Wert");                
                model.addRow(new Object[]{name, value});
            }
        } else {
            if (objOp == null || objOp.isComparing()) {
                if (runOp == null || runOp.isComparing()) {
                    List<List> values = statistic.getStatistic().getListOfLists(selectedObjects);
                    model.addColumn(name);
                    for (Object object : selectedObjects) {
                        model.addColumn(object);
                    }
                    int index = 0;
                    for (List list : values) {
                        Vector items = new Vector();
                        items.add("Durchlauf " + (index+1));
                        items.addAll(list);
                        model.addRow(items);
                        index++;
                    }
                } else {
                    List values = statistic.getStatistic().getListPerObject(selectedObjects);
                    model.addColumn(getObjectType(), new Vector(selectedObjects));
                    model.addColumn(name, new Vector(values));
                }
            } else {
                if (runOp == null || runOp.isComparing()) {
                    List values = statistic.getStatistic().getListPerRun(selectedObjects);
                    Vector runs = new Vector();
                    for (int i = 0; i < values.size(); i++) {
                        runs.add("Durchlauf " + (i+1));
                    }                    
                    model.addColumn("Durchläufe", runs);
                    model.addColumn(name, new Vector(values));
                } else {
                    Object value = statistic.getStatistic().get(selectedObjects);
                    model.addColumn("Statistik");
                    model.addColumn("Wert");                                    
                    model.addRow(new Object[]{name, value});
                }
            }
        }
    }
    
    private String getObjectType() {
        if (selectedObjects.isEmpty()) {
            return "Netzwerk";
        } else {
            if (selectedObjects.get(0) instanceof Edge) {
                return "Kante";
            } else if (selectedObjects.get(0) instanceof Node) {
                return "Knoten";
            } else {
                return "Fluss";
            }
        }
    }    
    
    private void createTable() {
        table = new JTable(model);
    }
}

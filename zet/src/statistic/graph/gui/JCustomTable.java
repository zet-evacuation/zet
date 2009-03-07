/*
 * JCustomTable.java
 *
 */

package statistic.graph.gui;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 *
 * @author Martin Groß
 */
public class JCustomTable extends JScrollPane {

    private JTable table;
    
    public JCustomTable(TableModel dm) {
        super(new JTable(dm));
    }   
}

/*
 * CustomListModel.java
 *
 */
package statistic.graph.gui;

import javax.swing.DefaultListModel;

/**
 *
 * @author Martin Groß
 */
public class CustomListModel extends DefaultListModel {

    @Override
    public void fireContentsChanged(Object source, int index0, int index1) {
        super.fireContentsChanged(source, index0, index1);
    }
}

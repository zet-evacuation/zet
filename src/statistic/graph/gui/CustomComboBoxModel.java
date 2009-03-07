/*
 * CustomComboBoxModel.java
 *
 */

package statistic.graph.gui;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Martin Groß
 */
public class CustomComboBoxModel extends DefaultComboBoxModel {

    public CustomComboBoxModel(Object[] items) {
        super(items);
    }

    public CustomComboBoxModel(Vector items) {
        super(items);
    }

    @Override
    public void fireContentsChanged(Object source, int index0, int index1) {
        super.fireContentsChanged(source, index0, index1);
    }
}

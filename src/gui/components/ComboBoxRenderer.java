/*
 * ComboBoxRenderer.java
 *
 * Created on 15. Dezember 2007, 16:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gui.components;

import java.awt.*;
import javax.swing.*;

/** A generic ComboBox renderer.
 *
 * @author Timon Kelter
 */
public class ComboBoxRenderer extends JLabel implements ListCellRenderer {
	public ComboBoxRenderer () {
		setOpaque (true);
		setHorizontalAlignment (CENTER);
		setVerticalAlignment (CENTER);
	}
	
	/* This method finds the image and text corresponding
	 * to the selected value and returns the label, set up
	 * to display the text and image. */
	@Override
	public Component getListCellRendererComponent (JList list, Object value,
		int index, boolean isSelected, boolean cellHasFocus) {
		
		if (isSelected) {
			setBackground (list.getSelectionBackground ());
			setForeground (list.getSelectionForeground ());
		} else {
			setBackground (list.getBackground ());
			setForeground (list.getForeground ());
		}

		setHorizontalAlignment( LEFT );
					
		return this;
	}
}
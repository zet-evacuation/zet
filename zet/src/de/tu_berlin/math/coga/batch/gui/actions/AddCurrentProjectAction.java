/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.actions;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import java.awt.event.ActionEvent;

/**
 *
 * @author gross
 */
public class AddCurrentProjectAction extends BatchAction {

    public AddCurrentProjectAction(JBatch batch) {
        super(batch, "Add current project", "box_24.png");
    }   
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}

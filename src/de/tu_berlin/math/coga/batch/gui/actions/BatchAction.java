/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.actions;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 *
 * @author gross
 */
public abstract class BatchAction extends AbstractAction {

    protected JBatch batch;
    
    public BatchAction(JBatch batch, String text, String icon) {
        super(text, new ImageIcon("./icons/"+icon));
        this.batch = batch;
    }
}

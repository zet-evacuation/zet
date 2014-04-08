/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import java.awt.event.ActionEvent;

/**
 *
 * @author gross
 */
public class AddAlgorithmAction extends BatchAction {

    private Class<? extends Algorithm> algorithm;
    private String title;

    public AddAlgorithmAction(JBatch batch, Class<? extends Algorithm> algorithm, String title) {
        super(batch, title, "algo_24.png");
			System.out.println( "Creating algorithm action '" + algorithm + "' with title '" + title + "'" );
        setEnabled(false);
        this.algorithm = algorithm;
        this.title = title;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
			System.out.println( "Adding algorithm '" + algorithm + "' with title '" + title + "'" );
        batch.addAlgorithm(algorithm, title);
    }
}

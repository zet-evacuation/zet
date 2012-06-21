/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch;

import java.util.LinkedList;

/**
 *
 * @author gross
 */
public class ComputationList extends LinkedList<Computation> {
    
    public String generateGenericComputationTitle() {
        int counter = 0;        
        boolean existing;
        String title;
        do {
            title = "Computation " + ++counter;
            existing = false;
            for (Computation computation : this) {
                if (computation.getTitle().equals(title)) {
                    existing = true;
                    break;
                }
            }
        } while (existing);
        return title;
    }
    
}
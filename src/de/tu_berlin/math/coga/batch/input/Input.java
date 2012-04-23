/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

/**
 *
 * @author gross
 */
public abstract class Input<T> {
    
    public abstract T getInput();
    
    public abstract String[] getPropertyNames();

    public abstract String[] getProperties();  
    
    public abstract String getTooltip();
}

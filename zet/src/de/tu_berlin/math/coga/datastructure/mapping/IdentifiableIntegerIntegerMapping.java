/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.datastructure.mapping;

import ds.mapping.Identifiable;
import ds.mapping.IdentifiableObjectMapping;
import ds.mapping.IntegerIntegerMapping;

/**
 *
 * @author Martin Groß
 */
public class IdentifiableIntegerIntegerMapping<T extends Identifiable> {
    
    private final IdentifiableObjectMapping<T, IntegerIntegerMapping> mapping;

    public IdentifiableIntegerIntegerMapping(IdentifiableObjectMapping<T, IntegerIntegerMapping> mapping) {
        this.mapping = mapping;
    }

    public int minimum(T edge, int first, int last) {
        return mapping.get(edge).minimum(first, last);
    }    
    
    public void decrease(T edge, int first, int last, int amount) {
        mapping.get(edge).decrease(first, last, amount);
    }

}

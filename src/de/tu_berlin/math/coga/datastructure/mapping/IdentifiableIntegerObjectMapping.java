/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.datastructure.mapping;

import ds.mapping.Identifiable;
import ds.mapping.IdentifiableObjectMapping;
import ds.mapping.IntegerObjectMapping;

/**
 *
 * @author Martin Groß
 */
public class IdentifiableIntegerObjectMapping<D extends Identifiable, R> {

    private IdentifiableObjectMapping<D, IntegerObjectMapping<R>> mapping;
    private Class<R> type;

    public IdentifiableIntegerObjectMapping(int domainSize, Class<R> type) {
        this.type = type;
    }

    public R get(D node, int time) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void set(D w, int i, R edge) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

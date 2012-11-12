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

    private IdentifiableObjectMapping<D, IntegerObjectMapping> mapping;
    private Class<R> type;

    public IdentifiableIntegerObjectMapping(int domainSize, Class<R> type) {
        this.mapping = new IdentifiableObjectMapping<>(domainSize, IntegerObjectMapping.class);
        this.type = type;
    }

    public R get(D identifiableObject, int time) {
        return mapping.isDefinedFor(identifiableObject)? type.cast(mapping.get(identifiableObject).get(time)) : null;
    }

    public void set(D identifiableObject, int time, R value) {
        if (!mapping.isDefinedFor(identifiableObject)) {
            mapping.set(identifiableObject, new IntegerObjectMapping());
        }
        mapping.get(identifiableObject).set(time, value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mapping.toString());
        return builder.toString();
    }
}

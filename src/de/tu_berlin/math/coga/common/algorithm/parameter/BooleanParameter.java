/*
 * BooleanParameter.java
 * 
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

/**
 * A class representing algorithmic parameters that take Boolean values.
 * 
 * @author Martin Groß
 */
public class BooleanParameter extends Parameter<Boolean> {

    /**
     * Creates a new BooleanParameter with the given name and description, that
     * belongs to the specified parameter set and has the given default value.
     * @param parent the parameter set this parameter belongs to.
     * @param name the name of this parameter.
     * @param description the description of this parameter.
     * @param value the default value for this parameter.
     */
    public BooleanParameter(ParameterSet parent, String name, String description, Boolean value) {
        super(parent, name, description, value);
    }
}

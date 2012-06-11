/*
 * ParameterSet.java
 * 
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

import de.tu_berlin.math.coga.common.algorithm.parameter.Parameter.ValidationResult;

/**
 *
 * @author Martin Groß
 */
public class ParameterSet {

    protected <T> ValidationResult validate(Parameter<T> parameter, T oldValue, T newValue) {
        return ValidationResult.SUCCESS;
    }

    protected <T> void valueChanged(Parameter<T> parameter, T oldValue, T newValue) {
    }
}

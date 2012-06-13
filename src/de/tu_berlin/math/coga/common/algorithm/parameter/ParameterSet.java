/*
 * ParameterSet.java
 * 
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

import de.tu_berlin.math.coga.common.algorithm.parameter.Parameter.ValidationResult;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * A class representing a set of parameters that belong together or interact
 * with each other. It provides support or notifying listeners when changes in
 * its parameters occur as well of vetoing support to ensure that all parameters
 * have feasible values.
 *
 * @author Martin Groß
 */
public class ParameterSet extends LinkedHashSet<Parameter> {

    /**
     * Stores the listeners of this set.
     */
    private HashSet<ParameterChangedListener> listeners;

    /**
     * Creates a new ParameterSet.
     */
    public ParameterSet() {
        this.listeners = new HashSet<>();
    }

    /**
     * A shortcut for adding new parameters to this set. Supports Strings,
     * Integers, Enumerations, Booleans, Doubles and Objects.
     * @param name the name of the new parameter.
     * @param description the description of the new parameter.
     * @param value the default value of this parameter.
     * @param params additional parameters that the constructors of the specific
     * parameter classes support. See their description for more information.
     */
    public <T> Parameter addParameter(String name, String description, T value, Object... params) {
        Class<?> type = value.getClass();
        Parameter<T> parameter;
        if (type.equals(String.class)) {
            if (params.length == 0 || !(params[0] instanceof String)) {
                parameter = (Parameter<T>) new StringParameter(this, name, description, (String) value);
            } else {
                parameter = (Parameter<T>) new StringParameter(this, name, description, (String) value, (String) params[0]);
            }
        } else if (type.equals(Integer.class)) {
            if (params.length != 2 || !(params[0] instanceof Integer)  || !(params[1] instanceof Integer)) {
                parameter = (Parameter<T>) new IntegerParameter(this, name, description, (Integer) value);
            } else {
                parameter = (Parameter<T>) new IntegerParameter(this, name, description, (Integer) value, (Integer) params[0], (Integer) params[1]);
            }
        } else if (type.equals(Boolean.class)) {
            parameter = (Parameter<T>) new BooleanParameter(this, name, description, (Boolean) value);
        } else if (type.equals(Double.class)) {
            if (params.length != 2 || !(params[0] instanceof Double)  || !(params[1] instanceof Double)) {
                parameter = (Parameter<T>) new DoubleParameter(this, name, description, (Double) value);
            } else {
                parameter = (Parameter<T>) new DoubleParameter(this, name, description, (Double) value, (Double) params[0], (Double) params[1]);
            }
        } else if (Enum.class.isAssignableFrom(type)) {
            parameter = new EnumParameter(this, name, description, (Class<? extends Enum>) type, (Enum) value);
        } else {
            parameter = new Parameter(this, name, description, value);
        }
        add(parameter);
        return parameter;
    }

    /**
     * Adds a new parameter changed listener that will receive notification when
     * a parameter in this set changes.
     * @param listener the listener to be added.
     */
    public void addParameterChangedListener(ParameterChangedListener listener) {
        listeners.add(listener);
    }

    /**
     * Informs all listeners that a parameter in this set has been changed by
     * calling the method described in their interface.
     * @param <T> the type of the parameter's value.
     * @param parameter the parameter that was changed.
     * @param oldValue the old value of the parameter.
     * @param newValue the new value of the parameter.
     */
    protected <T> void fireParameterChanged(Parameter<T> parameter, T oldValue, T newValue) {
        for (ParameterChangedListener listener : listeners) {
            listener.parameterChanged(this, parameter, oldValue, newValue);
        }
    }
    
    /**
     * Removes the specified listener.
     * @param listener the listener to be removed.
     */
    public void removeParameterChangedListener(ParameterChangedListener listener) {
        listeners.remove(listener);
    }

    /**
     * This method is called when one of the parameters in this set is about to
     * change. This method can be overwritten by subclasses to implement complex
     * checks to ensure the integrity of the parameter sets values.
     * @param <T> the type of the parameter's value.
     * @param parameter the parameter that was changed.
     * @param oldValue the old value of the parameter.
     * @param newValue the new value of the parameter.
     * @return the result of the validation, consisting of a Boolean flag and an
     * error message, if applicable.
     */
    protected <T> ValidationResult validate(Parameter<T> parameter, T oldValue, T newValue) {
        return ValidationResult.SUCCESS;
    }

    /**
     * This method is called, when one of the parameters in this set has been
     * successfully changed. This is done only after it has passed the
     * validation done by the call for validate. By default, this method
     * notifies listeners of this change.
     * @param <T> the type of the parameter's value.
     * @param parameter the parameter that was changed.
     * @param oldValue the old value of the parameter.
     * @param newValue the new value of the parameter.
     */
    protected <T> void valueChanged(Parameter<T> parameter, T oldValue, T newValue) {
        fireParameterChanged(parameter, oldValue, newValue);
    }
}

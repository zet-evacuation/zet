/*
 * ParameterChangedListener.java
 * 
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

/**
 * An interface for classes that want to receive parameter changed events.
 *
 * @author Martin Groß
 */
public interface ParameterChangedListener {

    /**
     * This is called by parameter sets which the listener has subscribed to,
     * when the value of one of its parameters changes.
     * @param <T> the type of the value of the changing parameter.
     * @param set the parameter set the parameter belongs to.
     * @param parameter the parameter that has changed.
     * @param oldValue the old value of the parameter.
     * @param newValue the new value of the parameter.
     */
    <T> void parameterChanged(ParameterSet set, Parameter<T> parameter, T oldValue, T newValue);

}

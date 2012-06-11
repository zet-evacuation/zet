/*
 * IntegerParameter.java
 *
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

/**
 * A class representing algorithmic parameters that take integer values and is
 * capable of checking whether new values are in a specified interval. The
 * interval bounds are considered to be included in the interval.
 * 
 * @author Martin Groß
 */
public class IntegerParameter extends Parameter<Integer> {

    /**
     * The lower bound (inclusive) for the range check.
     */
    private int lowerBound;
    /**
     * The upper bound (inclusive) for the range check.
     */
    private int upperBound;

    /**
     * Creates a new IntegerParameter with the given name and description, that
     * belongs to the specified parameter set and has the given default value. 
     * The interval of allowed values is initialized to 
     * <code>Integer.MIN_VALUE</code> and <code>Integer.MAX_VALUE</code>.    
     * @param parent the parameter set this parameter belongs to.
     * @param name the name of this parameter.
     * @param description the description of this parameter.
     * @param value the default value for this parameter.
     */
    public IntegerParameter(ParameterSet parent, String name, String description, Integer value) {
        this(parent, name, description, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Creates a new IntegerParameter with the given name and description, that
     * belongs to the specified parameter set and has the given default value.
     * Furthermore, it specifies an interval, which new values are checked
     * with.
     * @param parent the parameter set this parameter belongs to.
     * @param name the name of this parameter.
     * @param description the description of this parameter.
     * @param value the default value for this parameter.
     * @param regularExpression the regularExpression all values have to match.
     *  If this is <code>null</code>, no checks are being performed.
     */
    public IntegerParameter(ParameterSet parent, String name, String description, Integer value, int lowerBound, int upperBound) {
        super(parent, name, description, value);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Returns the lower bound of the range check.
     * @return the lower bound of the range check.
     */
    public int getLowerBound() {
        return lowerBound;
    }

    /**
     * Sets the upper bound of the range check.
     * @param upperBound the upper bound of the range check.
     */
    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * Returns the upper bound of the range check.
     * @return the upper bound of the range check.
     */
    public int getUpperBound() {
        return upperBound;
    }

    /**
     * Sets the upper bound of the range check.
     * @param upperBound the upper bound of the range check.
     */
    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    /**
     * Checks whether the <code>lower bound <= value <= upper bound</code>
     * holds.
     * @param value the value which is being validated.
     * @return the result of the validation.
     */
    @Override
    protected ValidationResult validate(Integer value) {
        if (value < lowerBound) {
            return new ValidationResult(false, value + " is smaller than " + lowerBound + ".");
        }
        if (value > upperBound) {
            return new ValidationResult(false, value + " is greater than " + upperBound + ".");
        }
        return ValidationResult.SUCCESS;
    }
}

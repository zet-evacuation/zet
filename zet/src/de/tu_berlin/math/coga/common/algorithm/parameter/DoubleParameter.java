/*
 * DoubleParameter.java
 *
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

/**
 * A class representing algorithmic parameters that take double values and is
 * capable of checking whether new values are in a specified interval. The
 * interval bounds are considered to be included in the interval.
 *
 * @author Martin Groß
 */
public class DoubleParameter extends Parameter<Double> {

    /**
     * The lower bound (inclusive) for the range check.
     */
    private double lowerBound;
    /**
     * The upper bound (inclusive) for the range check.
     */
    private double upperBound;

    /**
     * Creates a new DoubleParameter with the given name and description, that
     * belongs to the specified parameter set and has the given default value.
     * The interval of allowed values is initialized to
     * <code>Double.NEGATIVE_INFINITY</code> and
     * <code>Double.POSITIVE_INFINITY</code>.
     *
     * @param parent the parameter set this parameter belongs to.
     * @param name the name of this parameter.
     * @param description the description of this parameter.
     * @param value the default value for this parameter.
     */
    public DoubleParameter(ParameterSet parent, String name, String description, Double value) {
        this(parent, name, description, value, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * Creates a new DoublerParameter with the given name and description, that
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
    public DoubleParameter(ParameterSet parent, String name, String description, Double value, double lowerBound, double upperBound) {
        super(parent, name, description, value);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Returns the lower bound of the range check.
     * @return the lower bound of the range check.
     */
    public double getLowerBound() {
        return lowerBound;
    }

    /**
     * Sets the upper bound of the range check.
     * @param upperBound the upper bound of the range check.
     */
    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * Returns the upper bound of the range check.
     * @return the upper bound of the range check.
     */
    public double getUpperBound() {
        return upperBound;
    }

    /**
     * Sets the upper bound of the range check.
     * @param upperBound the upper bound of the range check.
     */
    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    /**
     * Checks whether the <code>lower bound <= value <= upper bound</code>
     * holds.
     * @param value the value which is being validated.
     * @return the result of the validation.
     */
    @Override
    protected Parameter.ValidationResult validate(Double value) {
        if (value < lowerBound) {
            return new Parameter.ValidationResult(false, value + " is smaller than " + lowerBound + ".");
        }
        if (value > upperBound) {
            return new Parameter.ValidationResult(false, value + " is greater than " + upperBound + ".");
        }
        return Parameter.ValidationResult.SUCCESS;
    }
}

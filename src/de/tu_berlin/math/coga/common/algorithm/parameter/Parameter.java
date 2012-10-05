/*
 * Parameter.java
 * 
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

import java.util.Collection;
import java.util.Iterator;

/**
 * This is the base class for algorithmic parameters. A parameter has a name and
 * a description that is used by the UI. The description should explain to the
 * user what the parameter affects and which values are allowed by it.
 *
 * A parameter can belong to a <code>ParameterSet</code>, this is usually the
 * set of all parameters an algorithm has.
 *
 * Each attempted change of a parameter's value is first validated by the
 * parameter's own validate method and then by the the validate of its
 * ParameterSet (if it has one). This allows to check for dependencies between
 * different parameters in a ParameterSet. After a successful value change, the
 * ParameterSet is notified of the change.
 *
 * @param T the type of this parameter's values.
 * @author Martin Groß
 */
public class Parameter<T> {

    /**
     * The description of this parameter.
     */
    private String description;
    /**
     * An iterator to the possible values of this parameter.
     */
    private Iterator<T> iterator;
    /**
     * The name of this parameter.
     */
    private String name;
    /**
     * The parameter set this parameter belongs to.
     */
    private ParameterSet parent;
    /**
     * The current value of this parameter.
     */
    private T value;
    /**
     * A sequence of values for this parameter.
     */
    private Iterable<T> values;

    /**
     * Creates a new Parameter with the given name and description, that
     * belongs to the specified parameter set and has the given default value.     *
     * @param parent the parameter set this parameter belongs to.
     * @param name the name of this parameter.
     * @param desc the description of this parameter.
     * @param value the default value for this parameter.
     */
    public Parameter(ParameterSet parent, String name, String desc, T value) {
        this.description = desc;
        this.name = name;
        this.parent = parent;
        this.value = value;
    }

    /**
     * Checks whether this parameter has a potential next value (which might
     * not be valid).
     * @return <code>true</code> if a candidate for a next value exists,
     * <code>false</code> otherwise.
     */
    public boolean hasPotentialNextValue() {
        return iterator != null && iterator.hasNext();
    }

    /**
     * Returns the number of values this parameter can take. Notice that this
     * does only count the number of potential values it can take, not all of
     * them might be valid with regard to the whole parameter set. If the values
     * are not given by a <code>Collection</code> or <code>Sequence</code> all
     * values are iterated to count the number of values.
     * @return the number of values this parameter can take.
     */
    public int numberOfValues() {
        if (values == null) {
            return 1;
        } else if (values instanceof Collection) {
            return ((Collection) values).size();
        } else if (values instanceof Sequence) {
            return ((Sequence) values).size();
        } else {
            int count = 0;
            Iterator<T> countingIterator = values.iterator();
            while (countingIterator.hasNext()) {
                countingIterator.next();
                ++count;
            }
            return count;
        }
    }

    /**
     * Sets this parameter to the first valid value and returns
     * <code>true</code> if it finds one and <code>false</code> if it finds
     * none. This also updates the internal iterator to this position. If the
     * parameter has only one value, nothing happens, which is counted as a
     * success by the method.
     * @return a <code>ValidationResult</code> specifying whether the operation
     * was a success or a failure.
     */
    public ValidationResult setToFirstValue() {
        if (values == null) {
            return ValidationResult.SUCCESS;
        }
        Iterator<T> newIterator = values.iterator();
        ValidationResult result = ValidationResult.FAILURE;
        while (newIterator.hasNext() && !result.isSuccessful()) {
            T newValue = newIterator.next();
            result = setValue(newValue);
        }
        if (result.isSuccessful()) {
            iterator = newIterator;
        }
        return result;
    }

    /**
     * If this parameter has a sequence of values, this sets the current value
     * to the next value in the sequence which is valid and returns
     * <code>true</code> if a valid next value was found. Otherwise, 
     * <code>false</code> is returned. If the parameter just has one value, the
     * current value remains the same and <code>false</code> is returned.
     * @return a <code>ValidationResult</code> specifying whether this a success
     * or a failure.
     */
    public ValidationResult setToNextValue() {
        if (iterator == null) {
            return new ValidationResult(false, "This parameter does not have "
                    + "multiple values.");
        } else if (!iterator.hasNext()) {
            return new ValidationResult(false, "This parameter has reached it "
                    + "last possible value.");
        } else {
            ValidationResult result;
            do {
                T nextValue = iterator.next();
                result = setValue(nextValue);
            } while (!result.isSuccessful() && iterator.hasNext());
            return result;
        }
    }

    /**
     * This method should be overwritten by subclasses. The default
     * implementation returns always true.
     * @param value the value that is to be validated.
     * @return the result of the validation.
     */
    protected ValidationResult validate(T value) {
        return ValidationResult.SUCCESS;
    }

    /**
     * Returns the description of this parameter.
     * @return the description of this parameter.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this algorithm. This should be a description of
     * what this parameter affects and what values are valid for it.
     * @param description the new description of this parameter.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the name of this parameter.
     * @return the name of this parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this parameter to the specified value. This should be
     * something human-readable.
     * @param name the new name of this parameter.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the parameter set this parameter belongs to. This might be <code>
     * null</code>.
     * @return the parameter set this parameter belongs to.
     */
    public ParameterSet getParent() {
        return parent;
    }

    /**
     * Returns the current value of this parameter.
     * @return the current value of this parameter.
     */
    public T getValue() {
        return value;
    }

    /**
     * Changes the value of this parameter to the specified one. Before the
     * value is changed, it is first checked by {@link validate} and then by
     * the parameter set, if this parameter belongs to one. If both checks are
     * passed, the value is changed and the parent parameter set is notified.
     * @param value the new value of this parameter.
     * @return a <code>ValidationResult</code> specifying whether the operation
     * was a success or a failure.
     */
    public ValidationResult setValue(T value) {
        if (value != this.value) {
            T oldValue = this.value;
            ValidationResult result = validate(value);
            if (parent != null) {
                result.combine(parent.validate(this, this.value, value));
            }
            if (result.isSuccessful()) {
                this.value = value;
                if (parent != null) {
                    parent.valueChanged(this, oldValue, this.value);
                }
            }
            return result;
        }
        return ValidationResult.SUCCESS;
    }

    /**
     * Returns the <code>Iterable</code> providing the iterator for the values
     * that this parameter can take.
     * @return the <code>Iterable</code> providing the iterator for the values
     * that this parameter can take.
     */
    public Iterable<T> getValues() {
        return values;
    }

    /**
     * Changes the values that this parameter can take to the specified
     * <code>Iterable</code>. The current value is then set to the first valid
     * value of this <code>Iterable</code>, provided that one exists. If it does
     * not, nothing happens and a validation failure is returned.
     * @param values an <code>Iterable</code> providing the values that this
     * parameter can take.
     * @return a <code>ValidationResult</code> specifying whether the operation
     * was a success or a failure.
     */
    public ValidationResult setValues(Iterable<T> values)  {
        Iterable<T> oldValues = values;
        this.values = values;
        ValidationResult result = setToFirstValue();
        if (!result.isSuccessful()) {
            this.values = oldValues;
        }
        return result;
    }

    /**
     * A wrapper class the contains the results of a validation. This consists
     * of a Boolean value whether a new value has been accepted and a String
     * storing an error message in case something went wrong.
     */
    public static class ValidationResult {

        /**
         * A constant representing a successful validation.
         */
        public static final ValidationResult SUCCESS = new ValidationResult(true, "");
        /**
         * A constant representing a failed validation.
         */
        public static final ValidationResult FAILURE = new ValidationResult(false, "");

        /**
         * The error message of the validation.
         */
        private String message;
        /**
         * Whether the validation was successful.
         */
        private boolean successful;

        /**
         * Creates a new ValidationResult with the specified error message and
         * success flag.
         * @param successful whether the validation was successful.
         * @param message the error message of this validation result.
         */
        public ValidationResult(boolean successful, String message) {
            this.message = message;
            this.successful = successful;
        }

        /**
         * Combines this result with the given one. This concatenates the
         * messages and performs a logical AND of their success flags. The
         * result is then stored in this object.
         * @param result the result this result is to be combined with.
         */
        public void combine(ValidationResult result) {
            message = (message + " " + result.getMessage()).trim();
            successful = successful && result.isSuccessful();
        }

        /**
         * Returns the error message of this validation result. This is an empty
         * string if the validation was successful.         *
         * @return the error message of this validation result.
         */
        public String getMessage() {
            return message;
        }

        /**
         * Sets the error message of this validation result. Should be empty if
         * and only if the validation was a success.
         * @param message the error message of this validation result.
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * Returns whether the validation was successful.
         * @return whether the validation was successful.
         */
        public boolean isSuccessful() {
            return successful;
        }

        /**
         * Sets whether the validation was successful.
         * @param successful specifies whether the validation was successful.
         */
        public void setSuccessful(boolean successful) {
            this.successful = successful;
        }
    }
}

/*
 * StringParameter.java
 * 
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

/**
 * A class representing algorithmic parameters that take string values and is
 * capable of checking new values against a regular expression.
 *
 * @author Martin Groß
 */
public class StringParameter extends Parameter<String> {

    /**
     * A regular expression with which new values have to adhere to. If this is
     * null, checks are omitted.
     */
    private String regularExpression;

    /**
     * Creates a new StringParameter with the given name and description, that
     * belongs to the specified parameter set and has the given default value.
     * No regular expression checks are done during value changes.
     * @param parent the parameter set this parameter belongs to.
     * @param name the name of this parameter.
     * @param description the description of this parameter.
     * @param value the default value for this parameter.
     */
    public StringParameter(ParameterSet parent, String name, String description, String value) {
        this(parent, name, description, value, null);
    }

    /**
     * Creates a new StringParameter with the given name and description, that
     * belongs to the specified parameter set and has the given default value.
     * Furthermore, it sets a regular expression, which new values are checked
     * with. If <code>null</code> is the regular expression, checks are omitted.
     * @param parent the parameter set this parameter belongs to.
     * @param name the name of this parameter.
     * @param description the description of this parameter.
     * @param value the default value for this parameter.
     * @param regularExpression the regularExpression all values have to match.
     *  If this is <code>null</code>, no checks are being performed.
     */
    public StringParameter(ParameterSet parent, String name, String description, String value, String regularExpression) {
        super(parent, name, description, value);
        this.regularExpression = regularExpression;
    }

    /**
     * Returns the regular expression used for validity checks of new values.
     * Can be <code>null</code>, which means that checks are currently disabled.
     * @return the currently used regular expression.
     */
    public String getRegularExpression() {
        return regularExpression;
    }

    /**
     * Specifies the RegEx that is used to check new values. If it is set to
     * <code>null</code>, the check is omitted.     *
     * @param regularExpression the regular expression used for the check.
     */
    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    /**
     * Checks whether the specified value matches the regular expression this
     * parameter is using (unless it is <code>null</code>, in which case all
     * values are accepted).
     * @param value the value which is being validated.
     * @return the result of the validation.
     */
    @Override
    protected ValidationResult validate(String value) {
        if (regularExpression != null && !value.matches(regularExpression)) {
            return new ValidationResult(false, "'" + value + "' does not match the RegEx '" + regularExpression + "'.");
        } else {
            return ValidationResult.SUCCESS;
        }
    }
}

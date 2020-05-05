/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package statistic.graph;

import org.zetool.statistic.Operation;
import org.zetool.statistic.Parameter;
import org.zetool.container.mapping.IntegerDoubleMapping;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.zetool.container.mapping.TimeValuePair;

/**
 * 
 * @author Martin Groß
 */
public enum DynamicOperation implements Operation<IntegerDoubleMapping> {

    COMPARISON("Vergleich") {
        
        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            throw new UnsupportedOperationException("This needs to be implemented.");
        }
        
        @Override
        public boolean isComparing() {
            return true;
        }
    },
    CONFIDENCE_INTERVAL_LOWER("Konfidenzintervall: Untere Grenze") {

        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else if (parameters.length != 1 || !(parameters[0] instanceof Number)) {
                throw new IllegalArgumentException("Error: Exactly one number as parameter required, but " + parameters.length + " parameters recieved.");
            } else {
                Double p = ((Number) parameters[0]).doubleValue();
                IntegerDoubleMapping result = new IntegerDoubleMapping(values.get(0).isPiecewiseLinear());
                SortedSet<Integer> timeList = new TreeSet<Integer>();
                for (IntegerDoubleMapping value : values) {
                    for (TimeValuePair tip : value) {
                        timeList.add(tip.time());
                    }
                }
                for (Integer time : timeList) {
                    List<Double> buffer = new LinkedList<Double>();
                    for (IntegerDoubleMapping value : values) {
                        buffer.add(value.get(time));
                    }
                    result.set(time, DoubleOperation.CONFIDENCE_INTERVAL_LOWER.execute(buffer, parameters[0]));
                }
                return result;
            }
        }

        @Override
        public Parameter[] parameters() {
            Parameter[] parameters = new Parameter[1];
            parameters[0] = new Parameter("p", 0.0, 1.0, 0.95);
            return parameters;
        }
    },    
    CONFIDENCE_INTERVAL_UPPER("Konfidenzintervall: Obere Grenze") {

        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else if (parameters.length != 1 || !(parameters[0] instanceof Number)) {
                throw new IllegalArgumentException("Error: Exactly one number as parameter required, but " + parameters.length + " parameters recieved.");
            } else {
                Double p = ((Number) parameters[0]).doubleValue();
                IntegerDoubleMapping result = new IntegerDoubleMapping(values.get(0).isPiecewiseLinear());
                SortedSet<Integer> timeList = new TreeSet<Integer>();
                for (IntegerDoubleMapping value : values) {
                    for (TimeValuePair tip : value) {
                        timeList.add(tip.time());
                    }
                }
                for (Integer time : timeList) {
                    List<Double> buffer = new LinkedList<Double>();
                    for (IntegerDoubleMapping value : values) {
                        buffer.add(value.get(time));
                    }
                    result.set(time, DoubleOperation.CONFIDENCE_INTERVAL_UPPER.execute(buffer, parameters));
                }
                return result;
            }
        }

        @Override
        public Parameter[] parameters() {
            Parameter[] parameters = new Parameter[1];
            parameters[0] = new Parameter("p", 0.0, 1.0, 0.95);
            return parameters;
        }
    },    
    DEVIATION("Standardabweichung") {

        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else if (parameters.length != 1 || !(parameters[0] instanceof Number)) {
                throw new IllegalArgumentException("Error: Exactly one number as parameter required, but " + parameters.length + " parameters recieved.");
            } else {
                Double p = ((Number) parameters[0]).doubleValue();
                IntegerDoubleMapping result = new IntegerDoubleMapping(values.get(0).isPiecewiseLinear());
                SortedSet<Integer> timeList = new TreeSet<Integer>();
                for (IntegerDoubleMapping value : values) {
                    for (TimeValuePair tip : value) {
                        timeList.add(tip.time());
                    }
                }
                for (Integer time : timeList) {
                    List<Double> buffer = new LinkedList<Double>();
                    for (IntegerDoubleMapping value : values) {
                        buffer.add(value.get(time));
                    }
                    result.set(time, DoubleOperation.DEVIATION.execute(buffer, parameters));
                }
                return result;
            }
        }
    },        
    MAXIMUM("Maximum") {

        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else {
                IntegerDoubleMapping result = new IntegerDoubleMapping(values.get(0).isPiecewiseLinear());
                SortedSet<Integer> timeList = new TreeSet<Integer>();
                for (IntegerDoubleMapping value : values) {
                    for (TimeValuePair tip : value) {
                        timeList.add(tip.time());
                    }
                }
                for (Integer time : timeList) {
                    double maximum = Double.NEGATIVE_INFINITY;
                    for (IntegerDoubleMapping value : values) {
                        if (value.get(time) > maximum) {
                            maximum = value.get(time);
                        }
                    }
                    result.set(time, maximum);
                }
                return result;
            }
        }
    },
    MEAN("Arithmetrisches Mittel") {

        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("At least one object is required for calculation of a mean.");
            } else {
                IntegerDoubleMapping result = new IntegerDoubleMapping(values.get(0).isPiecewiseLinear());
                for (IntegerDoubleMapping value : values) {
                    result = result.add(value);
                }
                result = result.divide(values.size());
                return result;
            }
        }
    },
    MEDIAN("Median") {

        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else {
                IntegerDoubleMapping result = new IntegerDoubleMapping(values.get(0).isPiecewiseLinear());
                SortedSet<Integer> timeList = new TreeSet<Integer>();
                for (IntegerDoubleMapping value : values) {
                    for (TimeValuePair tip : value) {
                        timeList.add(tip.time());
                    }
                }
                for (Integer time : timeList) {
                    List<Double> buffer = new LinkedList<Double>();
                    for (IntegerDoubleMapping value : values) {
                        buffer.add(value.get(time));
                    }
                    Collections.sort(buffer);
                    result.set(time, (buffer.get((int) Math.floor(buffer.size() / 2.0)) + buffer.get((int) Math.ceil(buffer.size() / 2.0))) / 2.0);
                }
                return result;
            }
        }
    },
    MINIMUM("Minimum") {

        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else {
                IntegerDoubleMapping result = new IntegerDoubleMapping(values.get(0).isPiecewiseLinear());
                SortedSet<Integer> timeList = new TreeSet<Integer>();
                for (IntegerDoubleMapping value : values) {
                    for (TimeValuePair tip : value) {
                        timeList.add(tip.time());
                    }
                }
                for (Integer time : timeList) {
                    double minimum = Double.POSITIVE_INFINITY;
                    for (IntegerDoubleMapping value : values) {
                        if (value.get(time) < minimum) {
                            minimum = value.get(time);
                        }
                    }
                    result.set(time, minimum);
                }
                return result;
            }
        }
    },
    QUANTIL("p-Quantil") {

        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else if (parameters.length != 1 || !(parameters[0] instanceof Number)) {
                throw new IllegalArgumentException("Error: Exactly one number as parameter required, but " + parameters.length + " parameters recieved.");
            } else {
                Double p = ((Number) parameters[0]).doubleValue();
                IntegerDoubleMapping result = new IntegerDoubleMapping(values.get(0).isPiecewiseLinear());
                SortedSet<Integer> timeList = new TreeSet<Integer>();
                for (IntegerDoubleMapping value : values) {
                    for (TimeValuePair tip : value) {
                        timeList.add(tip.time());
                    }
                }
                for (Integer time : timeList) {
                    List<Double> buffer = new LinkedList<Double>();
                    for (IntegerDoubleMapping value : values) {
                        buffer.add(value.get(time));
                    }
                    Collections.sort(buffer);
                    result.set(time, buffer.get((int) Math.floor(p * buffer.size())));
                }
                return result;
            }
        }

        @Override
        public Parameter[] parameters() {
            Parameter[] parameters = new Parameter[1];
            parameters[0] = new Parameter("p", 0.0, 1.0, 0.25);
            return parameters;
        }
    },
    SUM("Aufsummieren") {

        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            if (values.isEmpty()) {
                return new IntegerDoubleMapping();
            } else {
                IntegerDoubleMapping result = new IntegerDoubleMapping(values.get(0).isPiecewiseLinear());
                for (IntegerDoubleMapping value : values) {
                    result = result.add(value);
                }
                return result;
            }
        }
    },
    VARIANCE("Varianz") {

        public IntegerDoubleMapping execute(List<IntegerDoubleMapping> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else if (parameters.length != 1 || !(parameters[0] instanceof Number)) {
                throw new IllegalArgumentException("Error: Exactly one number as parameter required, but " + parameters.length + " parameters recieved.");
            } else {
                Double p = ((Number) parameters[0]).doubleValue();
                IntegerDoubleMapping result = new IntegerDoubleMapping(values.get(0).isPiecewiseLinear());
                SortedSet<Integer> timeList = new TreeSet<Integer>();
                for (IntegerDoubleMapping value : values) {
                    for (TimeValuePair tip : value) {
                        timeList.add(tip.time());
                    }
                }
                for (Integer time : timeList) {
                    List<Double> buffer = new LinkedList<Double>();
                    for (IntegerDoubleMapping value : values) {
                        buffer.add(value.get(time));
                    }
                    result.set(time, DoubleOperation.VARIANCE.execute(buffer));
                }
                return result;
            }
        }
    };
    private String description;

    private DynamicOperation(String description) {
        this.description = description;
    }

    public boolean isComparing() {
        return false;
    }    
    
    public Parameter[] parameters() {
        return new Parameter[0];
    }    
    
    @Override
    public String toString() {
        return description;
    }
}

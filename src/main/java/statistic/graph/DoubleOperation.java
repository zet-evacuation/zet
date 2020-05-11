/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
import java.util.List;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.probdist.StudentDist;

/**
 * 
 * @author Martin Groß
 */
public enum DoubleOperation implements Operation<Double> {

    COMPARISON("Vergleich") {
        
        public Double execute(List<Double> values, Object... parameters) {
            throw new AssertionError("This should not happen.");
        }
        
        @Override
        public boolean isComparing() {
            return true;
        }
    },
    CONFIDENCE_INTERVAL_LOWER("Konfidenzintervall: Untere Grenze") {

        public Double execute(List<Double> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else if (parameters.length != 1 || !(parameters[0] instanceof Number)) {
                throw new IllegalArgumentException("Error: Exactly one number as parameter expected.");
            } else {
                double alpha = ((Number) parameters[0]).doubleValue();
                double mu = MEAN.execute(values);
                double s = DEVIATION.execute(values);
                int n = values.size();
                double result;
                if (n < 50) {
                    result = StudentDist.inverseF(n - 1, 1 - alpha / 2) * s / Math.sqrt(n);
                } else {
                    result = NormalDist.inverseF01(1 - alpha / 2) * s / Math.sqrt(n);
                }
                return mu - result;
            }
        }

        @Override
        public Parameter[] parameters() {
            Parameter[] parameters = new Parameter[1];
            parameters[0] = new Parameter("alpha", 0.0, 1.0, 0.95);
            return parameters;
        }
    },
    CONFIDENCE_INTERVAL_UPPER("Konfidenzintervall: Obere Grenze") {

        public Double execute(List<Double> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else if (parameters.length != 1 || !(parameters[0] instanceof Number)) {
                throw new IllegalArgumentException("Error: Exactly one number as parameter expected.");
            } else {
                double alpha = ((Number) parameters[0]).doubleValue();
                double mu = MEAN.execute(values);
                double s = DEVIATION.execute(values);
                int n = values.size();
                double result;
                if (n < 50) {
                    result = StudentDist.inverseF(n - 1, 1 - alpha / 2) * s / Math.sqrt(n);
                } else {
                    result = NormalDist.inverseF01(1 - alpha / 2) * s / Math.sqrt(n);
                }
                return mu + result;
            }
        }

        @Override
        public Parameter[] parameters() {
            Parameter[] parameters = new Parameter[1];
            parameters[0] = new Parameter("alpha", 0.0, 1.0, 0.95);
            return parameters;
        }
    },
    DEVIATION("Standardabweichung") {

        public Double execute(List<Double> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else if (values.size() == 1) {
                return 0.0;
            } else {
                return Math.sqrt(VARIANCE.execute(values));
            }
        }
    },
    MAXIMUM("Maximum") {

        public Double execute(List<Double> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else {
                Double max = Double.NEGATIVE_INFINITY;
                for (Double value : values) {
                    if (value > max) {
                        max = value;
                    }
                }
                return max;
            }
        }
    },
    MEAN("Arithmetisches Mittel") {

        public Double execute(List<Double> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else {
                return SUM.execute(values) / values.size();
            }
        }
    },
    MEDIAN("Median") {

        public Double execute(List<Double> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else {
                return (values.get((int) Math.floor(values.size() / 2.0)) + values.get((int) Math.ceil(values.size() / 2.0))) / 2.0;
            }
        }
    },
    MINIMUM("Minimum") {

        public Double execute(List<Double> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else {
                Double min = Double.POSITIVE_INFINITY;
                for (Double value : values) {
                    if (value < min) {
                        min = value;
                    }
                }
                return min;
            }
        }
    },
    QUANTIL("p-Quantil") {

        public Double execute(List<Double> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else if (parameters.length != 1 || !(parameters[0] instanceof Number)) {
                throw new IllegalArgumentException("Error: Exactly one number as parameter expected.");
            } else {
                Double p = ((Number) parameters[0]).doubleValue();
                return values.get((int) Math.floor(p * values.size()));
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

        public Double execute(List<Double> values, Object... parameters) {
            if (values.isEmpty()) {
                return 0.0;
            } else {
                double result = 0.0;
                for (Double value : values) {
                    result += value;
                }
                return result;
            }
        }
    },
    VARIANCE("Varianz") {

        public Double execute(List<Double> values, Object... parameters) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Error: The specified value set is empty.");
            } else if (values.size() == 1) {
                return 0.0;
            } else {
                double result = 0.0;
                double mean = MEAN.execute(values);
                for (Double value : values) {
                    result += (value - mean) * (value - mean);
                }
                return result / (values.size() - 1);
            }
        }
    };
    private String description;

    private DoubleOperation(String description) {
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

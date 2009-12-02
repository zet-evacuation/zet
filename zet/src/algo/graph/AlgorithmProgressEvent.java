/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * AlgorithmProgressEvent.java
 *
 */
package algo.graph;

/**
 * An algorithm event that is fired by the algorithm when progress occurs.
 *
 * @author Martin Groß
 */
public class AlgorithmProgressEvent extends AlgorithmEvent {

    /**
     * The current progress value, 0 <= progress <= 1.
     */
    private double progress;

    /**
     * Creates an <code>AlgorithmProgressEvent</code> for the specified 
     * algorithm and the current progress value.
     * @param algorithm the algorithm for which progress occurred.
     * @param progress the progress value.
     * @throws IllegalArgumentException if the progress value is not between 0
     * and 1 (inclusively).
     */
    public AlgorithmProgressEvent(Algorithm algorithm, double progress) {
        this(algorithm, System.currentTimeMillis(), progress);
    }

    /**
     * Creates an <code>AlgorithmProgressEvent</code> for the specified
     * algorithm and the current progress value.
     * @param algorithm the algorithm for which progress occurred.
     * @param progress the progress value.
     * @throws IllegalArgumentException if the progress value is not between 0
     * and 1 (inclusively).
     */
    public AlgorithmProgressEvent(Algorithm algorithm, long eventTime, double progress) {
        super(algorithm, eventTime);
        if (progress < 0.0) {
            throw new IllegalArgumentException("The progress value must not be < 0.0.");
        } else if (progress > 1.0) {
            throw new IllegalArgumentException("The progress values must not not be > 1.0.");
        }
        this.progress = progress;
    }

    /**
     * Returns the current progress value, which is zero at the beginning and 1
     * after successfully terminating.
     * @return the current progress value.
     */
    public double getProgress() {
        return progress;
    }

    /**
     * Returns the current progress value as an integer between 0 and 100. The
     * progress value is 0 at the beginning of the algorithm and 100 after
     * a successful termination of the algorithm.
     * @return the current progress value.
     */
    public int getProgressAsInteger() {
        return (int) Math.round(progress * 100);
    }
}

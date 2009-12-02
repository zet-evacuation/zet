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
 * AlgorithmDetailedProgressEvent.java
 *
 */

package de.tu_berlin.math.coga.common.algorithm;

/**
 * An algorithm event that is fired by the algorithm when progress occurs. It
 * contains detailed information about the current task.
 * 
 * @author Martin Groß
 */
public class AlgorithmDetailedProgressEvent extends AlgorithmProgressEvent {

    /**
     * A short description of the task currently performed by the algorithm.
     */
    private String message;

    public AlgorithmDetailedProgressEvent(Algorithm algorithm, double progress, String message) {
        super(algorithm, progress);
        this.message = message;
    }

    public AlgorithmDetailedProgressEvent(Algorithm algorithm, long eventTime, double progress, String message) {
        super(algorithm, eventTime, progress);
    }


    /**
     * Returns a short description of the task currently performed by the
     * algorithm.
     * @return a short description of the task currently performed by the
     * algorithm.
     */
    public String getMessage() {
        return message;
    }

}

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
 * AlgorithmEvent.java
 *
 */
package sandbox;

/**
 *
 * @author Martin Groß
 */
public abstract class AlgorithmEvent {

    private Algorithm algorithm;
    private long eventTime;
    private long startTime;

    protected AlgorithmEvent(Algorithm algorithm, long startTime, long eventTime) {
        this.algorithm = algorithm;
        this.startTime = startTime;
        this.eventTime = eventTime;
    }

    public final Algorithm getAlgorithm() {
        return algorithm;
    }

    public final long getEventTime() {
        return eventTime;
    }

    public final long getStartTime() {
        return startTime;
    }
}

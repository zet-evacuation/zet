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
 * AlgorithmRunData.java
 *
 */

package algo.graph.util.benchmark;

/**
 *
 */
public class AlgorithmRunData implements Comparable<AlgorithmRunData> {
    
    protected long runtime;
    protected long seed;

    public AlgorithmRunData(long runtime, long seed) {
        this.runtime = runtime;
        this.seed = seed;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public int compareTo(AlgorithmRunData o) {
        return (int) Math.signum(runtime - o.runtime);
    }

}

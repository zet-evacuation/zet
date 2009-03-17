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
 * Preprocessor.java
 *
 */

package algo.graph;

/**
 *
 * @author Martin Groß
 */
public abstract class Preprocessor<Problem, A extends Algorithm<Problem, Solution>, Solution> extends Algorithm<Problem, Solution> {

    private A algorithm;
    
    @Override
    protected final Solution runAlgorithm(Problem problem) {
        Problem preprocessedProblem = preprocess(problem);
        algorithm.setProblem(preprocessedProblem);
        Solution solution = postprocess(algorithm.getSolution());
        return null;
    }
    
    protected abstract Problem preprocess(Problem problem);
    
    protected abstract Solution postprocess(Solution solution);

}

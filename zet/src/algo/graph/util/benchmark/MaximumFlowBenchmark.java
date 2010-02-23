/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * MaximumFlowBenchmark.java
 *
 */

package algo.graph.util.benchmark;

/**
 *
 */
public class MaximumFlowBenchmark {

    public static void main(String[] args) {
        /*
        Network grid = GridGenerator.generateGridNetwork(50, 50);
        AlgorithmRuns<AlgorithmRunData> runs = new AlgorithmRuns<AlgorithmRunData>();
        AlgorithmRuns<AlgorithmRunData> runs2 = new AlgorithmRuns<AlgorithmRunData>();
        AlgorithmRuns<AlgorithmRunData> runs3 = new AlgorithmRuns<AlgorithmRunData>();
        AlgorithmRuns<AlgorithmRunData> runs4 = new AlgorithmRuns<AlgorithmRunData>();
        for (int i = 0; i < 100; i++) {            
            System.out.println(i);
            long seed = System.nanoTime();
            IdentifiableIntegerMapping<Edge> c = GridGenerator.generateMappingWithUniformlyDistributedFunctionValues(grid.edges(), 10, 20, seed);
            DischargingHighestLabelPreflowPushAlgorithm algo = new DischargingHighestLabelPreflowPushAlgorithm(grid, c, grid.getNode(0), grid.getNode(grid.numberOfNodes()-1));
            DischargingGlobalHighestLabelPreflowPushAlgorithm algo2 = new DischargingGlobalHighestLabelPreflowPushAlgorithm(grid, c, grid.getNode(0), grid.getNode(grid.numberOfNodes()-1));
            DischargingGlobalStartHLPreflowPush algo3 = new DischargingGlobalStartHLPreflowPush(grid, c, grid.getNode(0), grid.getNode(grid.numberOfNodes()-1));
            DischargingGlobalGapHighestLabelPreflowPushAlgorithm algo4 = new DischargingGlobalGapHighestLabelPreflowPushAlgorithm(grid, c, grid.getNode(0), grid.getNode(grid.numberOfNodes()-1));
            long start = System.nanoTime();
            algo.run();
            System.out.println(seed + " " + algo.getSolution().getFlowValue());
            long time = System.nanoTime() - start;
            runs.add(new AlgorithmRunData(time, seed));
            start = System.nanoTime();
            algo2.run();
            System.out.println(seed + " " + algo2.getValueOfMaximumFlow());
            time = System.nanoTime() - start;
            runs2.add(new AlgorithmRunData(time, seed));            
            start = System.nanoTime();
            algo3.run();
            System.out.println(seed + " " + algo3.getValueOfMaximumFlow());
            time = System.nanoTime() - start;
            runs3.add(new AlgorithmRunData(time, seed));                        
            start = System.nanoTime();
            algo4.run();
            System.out.println(seed + " " + algo4.getValueOfMaximumFlow());
            time = System.nanoTime() - start;
            runs4.add(new AlgorithmRunData(time, seed));                                
        }
        runs.analyse();
        runs2.analyse();
        runs3.analyse();
        runs4.analyse();
        System.out.println(runs.results());
        System.out.println(runs2.results());
        System.out.println(runs3.results());
        System.out.println(runs4.results());
         */
    }

}

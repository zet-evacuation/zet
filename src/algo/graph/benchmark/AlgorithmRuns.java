/*
 * AlgorithmRuns.java
 *
 */

package algo.graph.benchmark;

import java.util.Arrays;
import java.util.LinkedList;
import util.NanosecondTimeFormatter;

/**
 *
 */
public class AlgorithmRuns<T extends AlgorithmRunData> extends LinkedList<T> {    
    
    protected long deviation;
    protected long lowerQuartile;
    protected long max;
    protected long mean;
    protected long median;
    protected long min;
    protected long upperQuartile;
    protected long variance;
    
    public void analyse() {
        AlgorithmRunData[] runs = toArray(new AlgorithmRunData[0]);
        Arrays.sort(runs);
        int last = runs.length - 1;
        min = runs[0].getRuntime();
        max = runs[last].getRuntime();
        if (last % 2 == 0) {
            median = runs[last / 2].getRuntime();
        } else {
            median = (runs[last / 2].getRuntime() + runs[last / 2 + 1].getRuntime()) / 2;
        }
        long sum = 0;
        for (AlgorithmRunData data : runs) {
            sum += data.getRuntime();
        }
        mean = sum / runs.length;
        sum = 0;
        for (AlgorithmRunData data : runs) {
            sum += (data.getRuntime() / 1000 - mean) * (data.getRuntime() / 1000 - mean);
        }        
        variance = sum / runs.length * 1000000;
        deviation = Math.round(Math.sqrt(variance));
    }
    
    public String results() {
        StringBuilder builder = new StringBuilder();
        builder.append("Minimum: " + NanosecondTimeFormatter.formatTime(min) + "\n");
        builder.append("Maximum: " + NanosecondTimeFormatter.formatTime(max) + "\n");
        builder.append("Mittelwert: " + NanosecondTimeFormatter.formatTime(mean) + "\n");
        builder.append("Varianz: " + NanosecondTimeFormatter.formatTime(variance) + "\n");
        builder.append("Standardabweichung: " + NanosecondTimeFormatter.formatTime(deviation) + "\n");
        builder.append("Median: " + NanosecondTimeFormatter.formatTime(median) + "\n");
        return builder.toString();
    }

}

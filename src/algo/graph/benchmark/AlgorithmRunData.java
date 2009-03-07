/*
 * AlgorithmRunData.java
 *
 */

package algo.graph.benchmark;

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

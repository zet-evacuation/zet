/*
 * Preprocessor.java
 *
 */

package sandbox;

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

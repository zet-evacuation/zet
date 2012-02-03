/*
 * Transformation.java
 *
 */

package de.tu_berlin.math.coga.common.algorithm;

public class Transformation<OriginalProblem,TransformedProblem,TransformedSolution,OriginalSolution> extends Algorithm<OriginalProblem, OriginalSolution> implements AlgorithmListener {

    protected Algorithm<TransformedProblem, TransformedSolution> algorithm;

    public Algorithm<TransformedProblem, TransformedSolution> getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm<TransformedProblem, TransformedSolution> algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    protected OriginalSolution runAlgorithm(OriginalProblem originalProblem) {
        TransformedProblem reducedProblem = transformProblem(originalProblem);
        algorithm.setProblem(reducedProblem);
        algorithm.addAlgorithmListener( (AlgorithmListener)this );
        algorithm.run();
        algorithm.removeAlgorithmListener(this);
        TransformedSolution solutionToTheTransformedProblem = algorithm.getSolution();
        return transformSolution(solutionToTheTransformedProblem);
    }

	@Override
    public void eventOccurred(AlgorithmEvent event) {
        fireEvent(event);
    }

    protected TransformedProblem transformProblem(OriginalProblem originalProblem) {
        try {
            return (TransformedProblem) originalProblem;
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("This needs to be implemented.");
        }
    }

    protected OriginalSolution transformSolution(TransformedSolution transformedSolution) {
        try {
            return (OriginalSolution) transformedSolution;
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("This needs to be implemented.");
        }
    }
}

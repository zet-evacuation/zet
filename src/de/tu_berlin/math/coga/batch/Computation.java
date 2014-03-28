/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch;

import de.tu_berlin.math.coga.batch.algorithm.AlgorithmList;
import de.tu_berlin.math.coga.batch.input.InputList;
import de.tu_berlin.math.coga.batch.input.ProblemType;
import de.tu_berlin.math.coga.batch.operations.OperationList;

/**
 *
 * @author Martin Groß
 */
public class Computation {

    private AlgorithmList algorithms;
		private OperationList operations;
    private InputList input;
    private String title;
    private ProblemType type;

    public Computation(ProblemType problemType) {
        this.type = problemType;
        this.algorithms = new AlgorithmList();
        this.input = new InputList(this);
				this.operations = new OperationList();
        this.title = "Computation";
    }

    public Computation() {
        this(null);
    }

    public InputList getInput() {
        return input;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ProblemType getType() {
        return type;
    }

    public void setType(ProblemType type) {
        this.type = type;
    }

    public AlgorithmList getAlgorithms() {
        return algorithms;
    }

		public OperationList getOperations() {
			return operations;
		}



}

/**
 * DimacsMaximumFlowFileReader.java
 * Created: 18.07.2014, 18:25:31
 */
package de.tu_berlin.math.coga.batch.input.reader;

import ds.graph.problem.RawMaximumFlowProblem;


/**
 * A reader for maximum flow problem instances stored in the DIMACS format:
 * http://www.avglab.com/andrew/CATS/maxflow_formats.htm.
 * @author Martin Groß
 * @author Jan-Philipp Kappmeier
 */
public class DimacsMaximumFlowFileReader extends DimacsReader<RawMaximumFlowProblem> {

  

  @Override
  protected RawMaximumFlowProblem postOperation() {
    throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Class<RawMaximumFlowProblem> getTypeClass() {
    throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String[] getProperties() {
    throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
  }

}

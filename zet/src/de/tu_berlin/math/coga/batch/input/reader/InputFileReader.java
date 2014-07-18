/*
 * InputFileReader.java
 *
 */
package de.tu_berlin.math.coga.batch.input.reader;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.common.algorithm.parameter.Parameter;
import java.io.File;

/**
 * The abstract base class for algorithms that read instances out of files.
 *
 * @author Martin Groß
 */
public abstract class InputFileReader<T> extends Algorithm<File, T> {

  public abstract Class<T> getTypeClass();

  /**
   * An enumeration type that specifies whether the reader should try to optimize its runtime or its memory consumption.
   */
  public enum OptimizationHint {
    SPEED, MEMORY;
  }

  /**
   * Stores the optimization goal.
   */
  private Parameter<OptimizationHint> optimizationHint;

  /**
   * Creates a new input file reader and creates a parameter to specify an optimization goal by the user. It is up to
   * subclasses to pay heed to this, though.
   */
  protected InputFileReader() {
    super();
    optimizationHint = getParameterSet().addParameter( "Optimization", "Specifies whether the reader should try to conserve runtime or memory", OptimizationHint.SPEED );
  }

  /**
   * Mnemonic shortcut for <code>getProblem()</code>.
   * @return the input file of the reader.
   */
  public File getFile() {
    return getProblem();
  }

  /**
   * Mnemonic shortcut for <code>setProblem(File file)</code>.
   * @param file the input file for the reader.
   */
  public void setFile( File file ) {
    setProblem( file );
  }

  /**
   * Returns the optimization hint for this reader, i.e. whether memory should be preserved or runtime speed. Note that
   * this is only a hint for subclasses.
   * @return the optimization hint.
   */
  public OptimizationHint getOptimization() {
    return optimizationHint.getValue();
  }

  /**
   * Sets the optimization hint for this problem.
   * @param hint the optimization hint.
   */
  public void setOptimization( OptimizationHint hint ) {
    optimizationHint.setValue( hint );
  }

  /**
   * Abstract method that returns an array of String properties with basic information about the input file. The
   * specifics depend on the sub-classing reader and the file format it is for. Usually the properties contain
   * information about the size of the instance, and can be obtained quickly by the reader, i.e. without parsing the
   * whole file but only its header.
   * @return the properties of the input file.
   */
  public abstract String[] getProperties();
}

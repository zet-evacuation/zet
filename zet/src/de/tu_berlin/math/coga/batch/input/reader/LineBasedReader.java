/**
 * LineBasedReader.java Created: 18.07.2014, 17:49:04
 */
package de.tu_berlin.math.coga.batch.input.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * An implementation of an {@link InputFileReader} that reads line by line.
 * @author Jan-Philipp Kappmeier
 */
public abstract class LineBasedReader<T> extends InputFileReader<T> {
  private boolean stop = false;

  public boolean isStop() {
    return stop;
  }

  public void setStop( boolean stop ) {
    this.stop = stop;
  }
  
  /**
   * Reads the minimum cost flow problem from the specified file.
   * @param file the file which contains the minimum cost flow problem.
   * @return the minimum cost flow problem. Requires 8n + 12m + O(1) storage.
   */
  @Override
  protected T runAlgorithm( File file ) {
    setStop( false );
    preOperation();
    switch( getOptimization() ) {
      case SPEED:
        runAlgorithmSpeed( file, false );
        break;
      case MEMORY:
        runAlgorithmMemory( file, false );
        break;
      default:
        throw new AssertionError( "Should not occur." );
    }
    return postOperation();
  }

  /**
   * Reads the problem in a speed-optimized way. 16n + 28m + O(1) Bytes are required.
   * @param file the file which contains the minimum cost flow problem.
   * @param propertiesOnly whether only the number of nodes, edges and supply should be read. Much faster than reading
   * the whole file.
   */
  private void runAlgorithmSpeed( File file, boolean propertiesOnly ) {
    String line = null;
    int lineIndex = 0;
    try (BufferedReader reader = new BufferedReader( new FileReader( file ) )) {
      while( (line = reader.readLine()) != null && !stop ) {
        ++lineIndex;
        if( line.trim().isEmpty() ) {
          continue;
        }

        parseLine( line );
      }
    } catch( AssertionError error ) {
      System.err.println( "Error reading " + file );
      System.err.println( error.getMessage() );
      System.err.println( lineIndex + ": " + line );
    } catch( IOException ex ) {
      System.err.println( "Exception during DimacsLoader loaded file from " + file );
    }
  }

  /**
   * Reads the problem in a speed-optimized way. 12n + 8m + O(1) Bytes are required.
   * @param file the file which contains the maximum flow problem.
   * @param propertiesOnly whether only the number of nodes and edges should be read. Much faster than reading the whole
   * file.
   * @return the maximum flow problem. 4n + 8m + O(1) Bytes required for storage.
   */
  protected void runAlgorithmMemory( File file, boolean propertiesOnly ) {
    String line = null;
    int lineIndex = 1;
    try (BufferedReader reader = new BufferedReader( new FileReader( file ) )) {
      while( (line = reader.readLine()) != null ) {
        ++lineIndex;
        if( line.trim().isEmpty() ) {
          continue;
        }

        parseLine( line );
      }
    } catch( AssertionError error ) {
      System.err.println( "Error reading " + file );
      System.err.println( error.getMessage() );
      System.err.println( lineIndex + ": " + line );
    } catch( IOException ex ) {
      System.err.println( "Exception during DimacsLoader loaded file from " + file );
    }

    phaseComplete();

    lineIndex = 1;
    try (BufferedReader reader = new BufferedReader( new FileReader( file ) )) {
      while( (line = reader.readLine()) != null ) {
        ++lineIndex;
        if( line.trim().isEmpty() ) {
          continue;
        }

        parseLine( line );
      }
    } catch( AssertionError error ) {
      System.err.println( "Error reading " + file );
      System.err.println( error.getMessage() );
      System.err.println( lineIndex + ": " + line );
    } catch( IOException ex ) {
      System.err.println( "Exception during DimacsLoader loaded file from " + file );
    }
  }

  protected abstract void parseLine( String line );

  protected abstract void phaseComplete();

  protected abstract T postOperation();

  protected void preOperation() {
  }
}

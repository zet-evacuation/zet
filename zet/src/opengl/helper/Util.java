/**
 * Util.java
 * input:
 * output:
 *
 * method:
 *
 * Created: Apr 15, 2010,4:26:26 PM
 */
package opengl.helper;

import java.io.PrintStream;
import javax.media.opengl.GL;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Util {
	/**
	 * Gives out all error messages to a submitted {@link PrintStream}.
	 * @param stream
	 */
	public static void printErrors( GL gl, PrintStream stream ) {
		int ret;
		while( (ret = gl.glGetError()) != GL.GL_NO_ERROR ) {
			switch( ret ) {
				case GL.GL_INVALID_ENUM:
					//stream.println( "INVALID ENUM" );
					break;
				case GL.GL_INVALID_VALUE:
					stream.println( "INVALID VALUE" );
					break;
				case GL.GL_INVALID_OPERATION:
					stream.println( "INVALID OPERATION" );
					break;
				case GL.GL_STACK_OVERFLOW:
					stream.println( "STACK OVERFLOW" );
					break;
				case GL.GL_STACK_UNDERFLOW:
					stream.println( "STACK UNDERFLOW" );
					break;
				case GL.GL_OUT_OF_MEMORY:
					stream.println( "OUT OF MEMORY" );
					break;
			}
		}
	}

}

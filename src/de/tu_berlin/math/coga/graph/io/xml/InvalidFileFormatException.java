/*
 * InvalidFileFormatException.java
 *
 */
package de.tu_berlin.math.coga.graph.io.xml;

import com.thoughtworks.xstream.converters.ConversionException;

/**
 *
 * @author Martin Groß
 */
public class InvalidFileFormatException extends ConversionException {

	public InvalidFileFormatException( String message ) {
		super( message );
	}

	public InvalidFileFormatException( String message, Throwable cause ) {
		super( message, cause );
	}
}

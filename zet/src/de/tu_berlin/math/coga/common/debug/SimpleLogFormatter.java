/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.common.debug;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SimpleLogFormatter extends Formatter {
	@Override
	public synchronized String format( LogRecord record ) {
		String message = formatMessage( record ) + "\n";
		if( record.getThrown() != null ) {
			OutputStream os = record.getLevel().intValue()>= Level.WARNING.intValue() ? System.err : System.out;
			try (PrintWriter pw = new PrintWriter( os )) {
				pw.println();
				record.getThrown().printStackTrace( pw );
			}
		}
		return message;
	}
}

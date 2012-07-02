/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.common.debug;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SysoutHandler extends StreamHandler {
	private Level minLevel = Level.FINEST;  // by default, put out everything
	private Level errLevel = Level.WARNING;  // 
	private boolean doneHeader;
	private Writer writer;

	/**
	 * The only method we really change to check whether the message
	 * is smaller than maxlevel.
	 * We also flush here to make sure that the message is shown immediately.
	 */
	@Override
	public synchronized void publish( LogRecord record ) {
		if( record.getLevel().intValue() >= this.errLevel.intValue() ) {
			super.publish( record );
			super.flush();
		} else if( record.getLevel().intValue() >= this.minLevel.intValue() ) {
			// if we arrived here, do what we always do
			outPublish( record );
		try {
					writer.flush();
				} catch( Exception ex ) {
					// We don't want to throw an exception here, but we
					// report the exception to any registered ErrorManager.
					reportError( null, ex, ErrorManager.FLUSH_FAILURE );
				}
			flush();
		} else {
			// below min level. do nothing
		}
	}
	
	private synchronized void outPublish( LogRecord record ) {
		if( !isLoggable( record ) )
			return;
		String msg;
		try {
			msg = getFormatter().format( record );
		} catch( Exception ex ) {
			// We don't want to throw an exception here, but we
			// report the exception to any registered ErrorManager.
			reportError( null, ex, ErrorManager.FORMAT_FAILURE );
			return;
		}

		try {
			if( !doneHeader ) {
				writer.write( getFormatter().getHead( this ) );
				doneHeader = true;
			}
			writer.write( msg );
			//System.out.println( "---" + msg );
		} catch( Exception ex ) {
			// We don't want to throw an exception here, but we
			// report the exception to any registered ErrorManager.
			reportError( null, ex, ErrorManager.WRITE_FAILURE );
		}
	}

	/**
	 * getter for maxlevel
	 * @return
	 */
	public Level getMinLevel() {
		return minLevel;
	}

	/**
	 * Setter for maxlevel. 
	 * If a logging event is larger than this level, it won't be displayed
	 * @param minlevel
	 */
	public void setMinLevel( Level minlevel ) {
		this.minLevel = minlevel;
		setLevel( minlevel );
	}

	/** Constructor forwarding */
	public SysoutHandler( PrintStream out, Formatter formatter ) {
		super( out, formatter );
	}

	/** Constructor forwarding */
	public SysoutHandler() throws SecurityException {
		super();
		setOutputStream( System.err );
		if( getEncoding() == null )
			writer = new OutputStreamWriter( System.out );
		else
			try {
				writer = new OutputStreamWriter( System.out, getEncoding() );
			} catch( UnsupportedEncodingException ex ) {
				// This shouldn't happen.  The setEncoding method
				// should have validated that the encoding is OK.
				throw new Error( "Unexpected exception " + ex );
			}
		setLevel( minLevel );
	}
}

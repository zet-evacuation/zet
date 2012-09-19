/**
 * Log.java Created 03.03.2010, 15:28:52
 */
package de.tu_berlin.math.coga.common.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;
import javax.swing.JEditorPane;

/**
 * A list of log entries. Entries can have two types (error and normal messages)
 * and are stored in two ways: in an array and as a string. The error messages
 * have additional html-tags to format them for logging out.
 *
 * @author Jan-Philipp Kappmeier
 */
public class HTMLLoggerHandler extends StreamHandler {
	private Level minLevel = Level.FINEST;  // by default, put out everything
	private Level errLevel = Level.SEVERE;  // 
	JEditorPane logPane;
	/** The complete log as string. */
	StringWriter buffer = new StringWriter();

	/**
	 * Creates an instance, sets up a buffer for output strings and a formatter
	 * for proper coloring.
	 */
	public HTMLLoggerHandler() {
		super.setLevel( minLevel );
		OutputStream os = new OutputStream() {
			@Override
			public void write( int b ) throws IOException {
				buffer.write( b );
			}
		};

		setOutputStream( os );

		Formatter f = new Formatter() {
			@Override
			public String format( LogRecord record ) {
				String s = formatMessage( record );
				
				if( record.getLevel().intValue() < errLevel.intValue() ) {
					//s = record.getMessage();
					s = s.replace( "<", "&lt;" );
					s = s.replace( ">", "&gt;" ) + "<br>";
				} else {
					//s = record.getMessage();
					s = s.replace( "<", "&lt;" );
					s = s.replace( ">", "&gt;" );
					s = "<font color=\"red\">" + s + "</font><br>";
				}
				return s;
			}
		};
		setFormatter( f );

	}

	public JEditorPane getLogPane() {
		return logPane;
	}

	public void setLogPane( JEditorPane logPane ) {
		this.logPane = logPane;
		logPane.setText( buffer.toString() );
	}

	public Level getMinLevel() {
		return minLevel;
	}

	public void setMinLevel( Level minLevel ) {
		this.minLevel = minLevel;
	}

	public Level getMaxLevel() {
		return errLevel;
	}

	public void setErrLevel( Level errLevel ) {
		this.errLevel = errLevel;
	}

	/**
	 * Clears all logged data.
	 */
	public void clear() {
		buffer = new StringWriter();
	}

	/**
	 * Format and publish a <tt>LogRecord</tt>.
	 * @param record description of the log event. A null record is silently
	 * ignored and is not published
	 */
	@Override
	public synchronized void publish( LogRecord record ) {
		if( !isLoggable( record ) )
			return;
		super.publish( record );
		flush();
		if( logPane != null )
			logPane.setText( buffer.toString() );
	}

	@Override
	public boolean isLoggable( LogRecord record ) {
		return !(record.getLevel().intValue() < minLevel.intValue() || record.getLevel() == Level.OFF || record.getLevel().intValue() > errLevel.intValue());
	}
}

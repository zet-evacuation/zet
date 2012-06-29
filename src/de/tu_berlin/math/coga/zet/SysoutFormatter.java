/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import sun.util.logging.LoggingSupport;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SysoutFormatter extends Formatter {
// format string for printing the log record
	private static final String format = LoggingSupport.getSimpleFormat();
	private final Date dat = new Date();

	public synchronized String format( LogRecord record ) {
		dat.setTime( record.getMillis() );
		String source;
		if( record.getSourceClassName() != null ) {
			source = record.getSourceClassName();
			if( record.getSourceMethodName() != null )
				source += " " + record.getSourceMethodName();
		} else
			source = record.getLoggerName();
		String message = formatMessage( record ) + "\n";
		String throwable = "";
		if( record.getThrown() != null ) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			pw.println();
			record.getThrown().printStackTrace( pw );
			pw.close();
			throwable = sw.toString();
		}
		return message;
//		return String.format( format,
//													dat,
//													source,
//													record.getLoggerName(),
//													record.getLevel().getLocalizedName(),
//													message,
//													throwable );
	}
}

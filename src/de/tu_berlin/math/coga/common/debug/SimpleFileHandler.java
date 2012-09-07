/**
 * SimpleFileHandler.java
 * Created: 07.09.2012, 17:50:04
 */
package de.tu_berlin.math.coga.common.debug;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SimpleFileHandler extends StreamHandler {
	private Level minLevel = Level.FINEST;  // by default, put out everything
	private Level maxLevel = Level.SEVERE;  // 

	private boolean append;
    private String pattern;
    private String lockFileName;
    private FileOutputStream lockStream;
    private File file;
    private static java.util.HashMap<String, String> locks = new java.util.HashMap<>();
		
		private String fileName;

	public Level getMinLevel() {
		return minLevel;
	}

	public void setMinLevel( Level minLevel ) {
		this.minLevel = minLevel;
	}

	public Level getMaxLevel() {
		return maxLevel;
	}

	public void setErrLevel( Level errLevel ) {
		this.maxLevel = errLevel;
	}

    

    private void open(File fname, boolean append) throws IOException {
        FileOutputStream fout = new FileOutputStream(fname.toString(), append);
        BufferedOutputStream bout = new BufferedOutputStream(fout);
        //meter = new SimpleFileHandler.MeteredStream(bout, len);
        setOutputStream(bout);
    }

    // Private method to configure a FileHandler from LogManager
    // properties and/or default values as specified in the class
    // javadoc.
//    private void configure() {
//        LogManager manager = LogManager.getLogManager();
//
//        String cname = getClass().getName();
//
//        pattern = manager.getStringProperty(cname + ".pattern", "%h/java%u.log");
//        limit = manager.getIntProperty(cname + ".limit", 0);
//        if (limit < 0) {
//            limit = 0;
//        }
//        count = manager.getIntProperty(cname + ".count", 1);
//        if (count <= 0) {
//            count = 1;
//        }
//        append = manager.getBooleanProperty(cname + ".append", false);
//        setLevel(manager.getLevelProperty(cname + ".level", Level.ALL));
//        setFilter(manager.getFilterProperty(cname + ".filter", null));
//        setFormatter(manager.getFormatterProperty(cname + ".formatter", new XMLFormatter()));
//        try {
//            setEncoding(manager.getStringProperty(cname +".encoding", null));
//        } catch (Exception ex) {
//            try {
//                setEncoding(null);
//            } catch (Exception ex2) {
//                // doing a setEncoding with null should always work.
//                // assert false;
//            }
//        }
//    }


    /**
     * Construct a default <tt>FileHandler</tt>.  This will be configured
     * entirely from <tt>LogManager</tt> properties (or their default values).
     * <p>
     * @exception  IOException if there are IO problems opening the files.
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have <tt>LoggingPermission("control"))</tt>.
     * @exception  NullPointerException if pattern property is an empty String.
     */
    public SimpleFileHandler() throws IOException, SecurityException {
        //configure();
			super.setLevel( minLevel );
        openFile();
    }

    /**
     * Initialize a <tt>FileHandler</tt> to write to the given filename.
     * <p>
     * The <tt>FileHandler</tt> is configured based on <tt>LogManager</tt>
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to no limit, and the file count is set to one.
     * <p>
     * There is no limit on the amount of data that may be written,
     * so use this with care.
     *
     * @param pattern  the name of the output file
     * @exception  IOException if there are IO problems opening the files.
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have <tt>LoggingPermission("control")</tt>.
     * @exception  IllegalArgumentException if pattern is an empty string
     */
    public SimpleFileHandler(String pattern) throws IOException, SecurityException {
        if (pattern.length() < 1 ) {
            throw new IllegalArgumentException();
        }
				
				fileName = pattern;

				//configure();
        this.pattern = pattern;
        openFile();
    }

    /**
     * Initialize a <tt>FileHandler</tt> to write to the given filename,
     * with optional append.
     * <p>
     * The <tt>FileHandler</tt> is configured based on <tt>LogManager</tt>
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to no limit, the file count is set to one, and the append
     * mode is set to the given <tt>append</tt> argument.
     * <p>
     * There is no limit on the amount of data that may be written,
     * so use this with care.
     *
     * @param pattern  the name of the output file
     * @param append  specifies append mode
     * @exception  IOException if there are IO problems opening the files.
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have <tt>LoggingPermission("control")</tt>.
     * @exception  IllegalArgumentException if pattern is an empty string
     */
    public SimpleFileHandler(String pattern, boolean append) throws IOException, SecurityException {
        if (pattern.length() < 1 ) {
            throw new IllegalArgumentException();
        }
        //configure();
        this.pattern = pattern;
        this.append = append;
        openFile();
    }

    // Private method to open the set of output files, based on the
    // configured instance variables.
    private void openFile() throws IOException {
        LogManager manager = LogManager.getLogManager();
        manager.checkAccess();

        //setErrorManager(em);

        // Create a lock file.  This grants us exclusive access
        // to our set of output files, as long as we are alive.
        int unique = -1;
        for (;;) {
            // Generate a lock file name from the "unique" int.
            lockFileName = fileName + ".lck"; //generate(pattern, 0, unique).toString() + ".lck";
            // Now try to lock that filename.
            // Because some systems (e.g., Solaris) can only do file locks
            // between processes (and not within a process), we first check
            // if we ourself already have the file locked.
            synchronized(locks) {
                if (locks.get(lockFileName) != null) {
                    // We already own this lock, for a different FileHandler
                    // object.  Try again.
									throw new IllegalStateException( "Cannot be locked" );
                    //continue;
                }
                FileChannel fc;
                //try {
                    lockStream = new FileOutputStream(lockFileName);
                    fc = lockStream.getChannel();
                //} catch (IOException ix) {
                    // We got an IOException while trying to open the file.
                    // Try the next file.
                    //continue;
                //}
                
								boolean available;
                try {
                    available = fc.tryLock() != null;
                    // We got the lock OK.
                } catch (IOException ix) {
                    // We got an IOException while trying to get the lock.
                    // This normally indicates that locking is not supported
                    // on the target directory.  We have to proceed without
                    // getting a lock.   Drop through.
                    available = true;
                }
                if (available) {
                    // We got the lock.  Remember it.
                    locks.put(lockFileName, lockFileName);
                    break;
                }

                // We failed to get the lock.  Try next file.
                fc.close();
								throw new IllegalStateException( "Lock not possible" );
            }
        }

        file = new File( fileName );
        //for (int i = 0; i < count; i++) {
            //file = new File( fileName );//generate(pattern, i, unique);
        //}

        // Create the initial log file.
        if (append)
           open(file, true);
				else
					open( file, false);
        //} else {
        //    rotate();
        //}

        // Did we detect any exceptions during initialization?
//        Exception ex = em.lastException;
//        if (ex != null) {
//            if (ex instanceof IOException) {
//                throw (IOException) ex;
//            } else if (ex instanceof SecurityException) {
//                throw (SecurityException) ex;
//            } else {
//                throw new IOException("Exception: " + ex);
//            }
//        }
//
//        // Install the normal default ErrorManager.
//        setErrorManager(new ErrorManager());
    }

    /**
     * Format and publish a <tt>LogRecord</tt>.
     *
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
    public synchronized void publish(LogRecord record) {
      if( !isLoggable( record ) ) {
            return;
        }
        super.publish(record);
        flush();
    }

	@Override
	public boolean isLoggable( LogRecord record ) {
		if( record.getLevel().intValue() < minLevel.intValue() || record.getLevel() == Level.OFF || record.getLevel().intValue() > maxLevel.intValue() )
			return false;
		else
			return true;
	}

		
		
    /**
     * Close all the files.
     *
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have <tt>LoggingPermission("control")</tt>.
     */
    public synchronized void close() throws SecurityException {
        super.close();
        // Unlock any lock file.
        if (lockFileName == null) {
            return;
        }
        try {
            // Closing the lock file's FileOutputStream will close
            // the underlying channel and free any locks.
            lockStream.close();
        } catch (Exception ex) {
            // Problems closing the stream.  Punt.
        }
        synchronized(locks) {
            locks.remove(lockFileName);
        }
        new File(lockFileName).delete();
        lockFileName = null;
        lockStream = null;
    }
}

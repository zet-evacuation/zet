/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;

import batch.plugins.AlgorithmPlugin;
import org.zetool.common.debug.Debug;
import org.zetool.common.debug.HTMLLoggerHandler;
import org.zetool.common.debug.SimpleFileHandler;
import org.zetool.common.debug.SimpleLogFormatter;
import org.zetool.common.util.Formatter;

/**
 * The {@code ZETMain} class is the main entry for the graphical user interface of the evacuation tool. It creates an
 * editor window and displays an empty project.
 *
 * @author Jan-Philipp Kappmeier
 */
public class ZETMain {

    /**
     * The version of zet.
     */
    public final static String VERSION;
    /**
     * Soruce code version. Git commit.
     */
    public final static String REVISION;

    /**
     * String to be displayed containing detailed version. If the version is a release, it will contain only the release
     * version. When the version is a SNAPSHOT, the revision will be added.
     */
    public final static String VERSION_FULL;

    /**
     * A more detailed source code revision for debugging purposes. Includes git describe output.
     */
    public final static String DEBUG_VERSION_INFO;

    private final static String GIT_PROPERTIES = "/git.properties";
    private final static String GIT_BUILD_VERSION = "git.build.version";
    private final static String GIT_COMMIT = "git.commit.id.abbrev";
    private final static String GIT_DESCRIBE = "git.commit.id.describe";

    private static Properties loadGitProperties() {
        InputStream inputStream = ZETMain.class.getResourceAsStream(GIT_PROPERTIES);
        Properties p = new Properties();
        try {
            p.load(inputStream);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Error loading " + GIT_PROPERTIES, ex);
        }
        return p;
    }

    private static boolean isSnapshot() {
        return VERSION.contains("SNAPSHOT");
    }

    static {
        Properties gitProperties = loadGitProperties();
        VERSION = gitProperties.getProperty(GIT_BUILD_VERSION);
        REVISION = gitProperties.getProperty(GIT_COMMIT);
        if (isSnapshot()) {
            VERSION_FULL = VERSION + " " + REVISION;
        } else {
            VERSION_FULL = VERSION;
        }
        DEBUG_VERSION_INFO = gitProperties.getProperty(GIT_DESCRIBE);
    }

    /**
     * The file to which the log is written (if specified via command line).
     */
    static String logFile = "output.log";
    /**
     * The file to which the error log is written (if specified via command line).
     */
    static String errFile = "error.log";
    /**
     * The log of the application.
     */
    public static HTMLLoggerHandler gl = new HTMLLoggerHandler();
    static SimpleFileHandler errFileHandler = null;
    static SimpleFileHandler logFileHandler = null;
    private static boolean privateLogging = true;
    public final static PluginManager pm = PluginManagerFactory.createPluginManager();

    /**
     * The logger of the main class.
     */
    private static final Logger log = Logger.getGlobal();

    public static void main(String[] args) {
        setUpLog(false, true, false, true);

        privateLogging = false;
        pm.addPluginsFrom(new File("./plugins").toURI());
        System.out.println(pm);

        AlgorithmPlugin<?, ?> plugin = pm.getPlugin(AlgorithmPlugin.class);

        if (plugin == null) {
            log.log(Level.INFO, "Plugin is null!");
        } else {
            log.log(Level.INFO, plugin.toString());
        }

        try {
            ZETLoader.load(args);
        } catch (NoClassDefFoundError | java.lang.UnsatisfiedLinkError ex) {
            Debug.globalLogger.severe("Error during initialization occured.");
            Debug.globalLogger.severe(ex.toString());
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, "A severe error during initialization occured.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sets up the standard output and the error output to some logging files.
     *
     * @param logging indicates whether standard out is redirected to a file
     * @param err indicates whether error out is redirected to a file
     * @param auto indicates whether the filenames are automatically created
     * @param verbose indicates whether the default output is also used, or not
     */
    public static void setUpLog(boolean logging, boolean err, boolean auto, boolean verbose) {
        try {
            Debug.setUpLogging();

            logging = true;
            err = true;
            Calendar cal = Calendar.getInstance();
            if (auto) {
                logFile = getAutoName(false);
                errFile = getAutoName(true);
                logging = true;
                err = true;
            }
            if (logging) {
                try {
                    if (logFileHandler != null) {
                        logFileHandler.close();
                        log.removeHandler(logFileHandler);
                        logFileHandler = null;
                    }
                    try {
                        logFileHandler = new SimpleFileHandler(logFile);
                    } catch (IllegalStateException ex) {
                        // lock not possible, try another file
                        logFile = getAutoName(false);
                        try {
                            logFileHandler = new SimpleFileHandler(logFile);
                            log.log(Level.INFO, "Logging with new file: {0}", logFile);
                        } catch (IllegalStateException ex2) {
                            // lock even on newly generated file
                            // give out a message and continue...
                            Debug.globalLogger.severe("Error during initialization occured.");
                            Debug.globalLogger.severe(ex.toString());
                            ex2.printStackTrace(System.err);
                            JOptionPane.showMessageDialog(null, "Log file could not be opened. Continue without file logging.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    if (logFileHandler != null) {
                        logFileHandler.setFormatter(new SimpleLogFormatter());
                        log.addHandler(logFileHandler);

                        logFileHandler.setMinLevel(Level.ALL);
                        logFileHandler.setErrLevel(Level.INFO);
                    }

                } catch (IOException | SecurityException ex) {
                    log.severe("Error creating debug out.");
                }
            }
            if (err) {
                if (logging && logFile.equals(errFile)) {
                    logFileHandler.setErrLevel(Level.SEVERE);
                } else {
                    try {
                        if (errFileHandler != null) {
                            errFileHandler.close();
                            log.removeHandler(errFileHandler);
                            errFileHandler = null;
                        }
                        try {
                            errFileHandler = new SimpleFileHandler(errFile);
                        } catch (IllegalStateException ex) {
                            // lock not possible, try another file
                            errFile = getAutoName(true);
                            try {
                                errFileHandler = new SimpleFileHandler(errFile);
                                log.log(Level.SEVERE, "Logging with new file: {0}", errFile);
                            } catch (IllegalStateException ex2) {
                                // lock even on newly generated file
                                // give out a message and continue...
                                Debug.globalLogger.severe("Error during initialization occured.");
                                Debug.globalLogger.severe(ex.toString());
                                ex2.printStackTrace(System.err);
                                JOptionPane.showMessageDialog(null, "Error log file could not be opened. Continue without file logging for errors.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        if (errFileHandler != null) {
                            errFileHandler.setFormatter(new SimpleLogFormatter());
                            log.addHandler(errFileHandler);

                            errFileHandler.setMinLevel(Level.WARNING);
                            errFileHandler.setErrLevel(Level.SEVERE);
                        }
                    } catch (FileNotFoundException ex) {
                        log.severe("Error creating debug error out.");
                    }
                }
            }
            gl = new HTMLLoggerHandler();
            log.addHandler(gl);
            if (logging && !privateLogging) {
                log.log(Level.INFO, "ZET Suite {5}\nLog of " + cal.get(Calendar.YEAR) + "-{0}-{1} {2}-{3}-{4}",
                        new Object[]{Formatter.fillLeadingZeros(cal.get(Calendar.MONTH) + 1, 2),
                            Formatter.fillLeadingZeros(cal.get(Calendar.DAY_OF_MONTH), 2),
                            Formatter.fillLeadingZeros(cal.get(Calendar.HOUR_OF_DAY), 2),
                            Formatter.fillLeadingZeros(cal.get(Calendar.MINUTE), 2),
                            Formatter.fillLeadingZeros(cal.get(Calendar.SECOND), 2),
                            ZETMain.VERSION_FULL});
            }
            if (logging && !logFile.equals(errFile) && !privateLogging) {
                log.log(Level.SEVERE, "Error log of " + cal.get(Calendar.YEAR) + "-{0}-{1} {2}-{3}-{4}", new Object[]{Formatter.fillLeadingZeros(cal.get(Calendar.MONTH) + 1, 2), Formatter.fillLeadingZeros(cal.get(Calendar.DAY_OF_MONTH), 2), Formatter.fillLeadingZeros(cal.get(Calendar.HOUR_OF_DAY), 2), Formatter.fillLeadingZeros(cal.get(Calendar.MINUTE), 2), Formatter.fillLeadingZeros(cal.get(Calendar.SECOND), 2)});
            }
        } catch (IOException | SecurityException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    private static String getAutoName(boolean err) {
        Calendar cal = Calendar.getInstance();
        return err
                ? "zet_" + cal.get(Calendar.YEAR) + "-" + Formatter.fillLeadingZeros(cal.get(Calendar.MONTH) + 1, 2) + "-" + Formatter.fillLeadingZeros(cal.get(Calendar.DAY_OF_MONTH), 2) + "_" + Formatter.fillLeadingZeros(cal.get(Calendar.HOUR_OF_DAY), 2) + "-" + Formatter.fillLeadingZeros(cal.get(Calendar.MINUTE), 2) + "-" + Formatter.fillLeadingZeros(cal.get(Calendar.SECOND), 2) + "_err.log"
                : "zet_" + cal.get(Calendar.YEAR) + "-" + Formatter.fillLeadingZeros(cal.get(Calendar.MONTH) + 1, 2) + "-" + Formatter.fillLeadingZeros(cal.get(Calendar.DAY_OF_MONTH), 2) + "_" + Formatter.fillLeadingZeros(cal.get(Calendar.HOUR_OF_DAY), 2) + "-" + Formatter.fillLeadingZeros(cal.get(Calendar.MINUTE), 2) + "-" + Formatter.fillLeadingZeros(cal.get(Calendar.SECOND), 2) + ".log";
    }
}

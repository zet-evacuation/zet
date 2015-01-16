/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; witzethout even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package zet.gui.main;

import org.zetool.common.debug.Debug;
import org.zetool.common.localization.Localization;
import de.tu_berlin.math.coga.zet.ZETLocalization2;
import org.zetool.common.localization.LocalizationManager;
import org.zetool.common.localization.Localized;
import ds.PropertyContainer;
import de.tu_berlin.coga.zet.model.ZControl;
import event.EventServer;
import event.MessageEvent;
import event.MessageEvent.MessageType;
import gui.GUIControl;
import gui.ZETLoader;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import zet.gui.GUILocalization;
import zet.gui.components.JEventStatusBar;
import zet.gui.main.tabs.JEditView;
import zet.util.ConversionTools;

/**
 * The main window of the ZET application.
 * @author Jan-Philipp Kappmeier, Timon Kelter
 */
@SuppressWarnings( "serial" )
public class JZetWindow extends JFrame implements Localized {

	/** The localization class. */
	static final Localization loc = GUILocalization.loc;
	/** Stores the last mouse position if a mouse position event is sent. */
	private static Point lastMouse = new Point( 0, 0 );
	/** The delimiter used if numbers are stored in a tuple. */
	final static String delimiter = ZETLocalization2.loc.getStringWithoutPrefix( "numberSeparator" );
	private static boolean editing = false;
	// Options
	/** The number format used to display the zoom factor in the text field. */
	private NumberFormat nfZoom = NumberFormat.getPercentInstance();	// Main window components
	/** The status bar. */
	private JEventStatusBar statusBar;
	/** The progress bar. */
	private JProgressBar progressBar;
	/** The editor tab. */
	private JEditView editView;
	/** The tab containing the graph statistic. */
	private JToolBar currentToolbar;
	/** A tabbed pane that allows switching of the different views. */
	private JTabbedPane tabPane;
	/** Decides whether the visualization should be restarted if 'play' is pressed. */
	CardLayout statusBarCardLayout;
	JPanel statusPanel;
	private ArrayList<Tabs> tablist = new ArrayList<>();
	private static class Tabs {
		private final String title;
		private final String toolTip;
		private final JToolBar menuBar;

		private Tabs( String title, String toolTip, JToolBar menuBar ) {
			this.title = title;
			this.toolTip = toolTip;
			this.menuBar = menuBar;
		}
	}

	/**
	 * Creates a new instance of {@code JZetWindow}. Sets the editor position
	 * and size, loads file icon, tool bars and menus.
	 * @param guiControl the control class for the ZET GUI
	 * @param zcontrol the control class for the Z-model
	 */
	public JZetWindow( GUIControl guiControl, ZControl zcontrol ) {
		super();

		// Set up locale information
		LocalizationManager.getManager().setLocale( Locale.getDefault() );
		nfZoom.setMaximumFractionDigits( 2 );

		// Set window position
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		// set size and location, move to visible area if otherwise hidden
		int x = PropertyContainer.getInstance().getAsInt( "settings.editor.window.position.x" );
		int y = PropertyContainer.getInstance().getAsInt( "settings.editor.window.position.y" );
		int width = PropertyContainer.getInstance().getAsInt( "settings.editor.window.position.width" );
		int height = PropertyContainer.getInstance().getAsInt( "settings.editor.window.position.height" );
		boolean maximized = PropertyContainer.getInstance().getAsBoolean( "settings.editor.window.position.maximized" );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if( width < 0 || width > d.width )
			width = d.width/2;
		if( height < 0 || height > d.height )
			height = d.height/2;
		if( x < 0 || x + width > d.width )
			x = (d.width - width) / 2;
		if( y < 0 || y + height > d.height )
			y = (d.height - height)/2;
		setSize( width, height );
		setLocation( x, y );
		if( maximized )
	    setExtendedState( getExtendedState() | JFrame.MAXIMIZED_BOTH );

		getContentPane().setLayout( new BorderLayout() );

		// Create status/progress bar
		statusBarCardLayout = new CardLayout();
		statusPanel = new JPanel( statusBarCardLayout );
		statusBar = new JEventStatusBar();
		statusPanel.add( statusBar, "status" );
		add( statusPanel, BorderLayout.SOUTH );
		progressBar = new JProgressBar( 0, 100 );
		statusPanel.add( progressBar, "progress" );

		// create tabs
		tabPane = new JTabbedPane();
		tabPane.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged( ChangeEvent e ) {
				final int i = tabPane.getSelectedIndex();
				showToolBar( tablist.get( i ).menuBar );
			}
		} );
		getContentPane().add( tabPane, BorderLayout.CENTER );
		ZETLoader.sendMessage( loc.getString( "gui.status.EditorInitialized" ) );

		// window listener, saves stuff when closing
		this.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing( WindowEvent e ) {
				try {
					ZETLoader.ptmInformation.getRoot().reloadFromPropertyContainer();
					ZETLoader.ptmOptions.getRoot().reloadFromPropertyContainer();
					PropertyContainer.saveConfigFile( ZETLoader.ptmInformation, new File( ZETLoader.informationFilename ) );
					PropertyContainer.saveConfigFile( ZETLoader.ptmOptions, new File( ZETLoader.optionFilename ) );
				} catch( IOException ex ) {
					System.err.println( "Error saving information file." );
				}
			}
		} );

		// component listener. updates location information when moved/resized.
		this.addComponentListener( new ComponentAdapter() {
			@Override
			public void componentMoved( ComponentEvent e ) {
				boolean maximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
				if( maximized ) {
					PropertyContainer.getInstance().set( "settings.editor.window.position.maximized", true );
				} else {
					PropertyContainer.getInstance().set( "settings.editor.window.position.maximized", false );
					PropertyContainer.getInstance().set( "settings.editor.window.position.x", getX() );
					PropertyContainer.getInstance().set( "settings.editor.window.position.y", getY() );
					PropertyContainer.getInstance().set( "settings.editor.window.position.width", getWidth() );
					PropertyContainer.getInstance().set( "settings.editor.window.position.height", getHeight() );
				}
			}
		} );

		// set up the icon
		final File iconFile = new File( "./icon.gif" );
		ZETLoader.checkFile( iconFile );
		try {
			setIconImage( ImageIO.read( iconFile ) );
		} catch( IOException e ) {
			ZETLoader.exit( "Error loding icon." );
		}

	}

	public void addMode( String title, String toolTip, Icon icon, JComponent component, JToolBar menu ) {
		tablist.add( new Tabs( title, toolTip, menu ) );
		tabPane.addTab( loc.getString( title ), icon, component, loc.getString( toolTip ) );
	}

	/**
	 * Sets up shortcuts for several actions.
	 */
	public void setUpKeyStrokes() {
		// Register Shortcuts (no-menu-shortcuts)
		KeyStroke up = KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK );
		ActionListener acl = new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				switch( editView.getEastPanelType() ) {
					case Floor:
						editView.setFloorNameFocus();
						break;
					case Room:
						editView.setRoomNameFocus();
						break;
					default:
						System.out.println( "Nothing" );
				}
			}
		};
		tabPane.registerKeyboardAction( acl, "test", up, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	/**
	 * Changes the appearance of the GUI to the selected language.
	 * @see de.tu_berlin.math.coga.common.localization.AbstractLocalization
	 */
	@Override
	public void localize() {
		for( int i = 0; i < tablist.size(); ++i ) {
			tabPane.setTitleAt( i, loc.getString( tablist.get( i ).title ) );
			tabPane.setToolTipTextAt( i, loc.getString( tablist.get( i ).toolTip ) );
		}
		sendMouse( lastMouse );
		Debug.globalLogger.info( loc.getStringWithoutPrefix( "gui.status.LanguageChangedTo" ) );
	}

	/**
	 * Displays the mouse position in the right edge of the status bar
	 * @param position the mouse position in millimeter.
	 */
	public static void sendMouse( Point position ) {
		lastMouse = position;
		String realCoordsMillimeter = "(" + Integer.toString( position.x ) + delimiter + Integer.toString( position.y ) + ")";
		String realCoordsMeter = "(" + LocalizationManager.getManager().getFloatConverter().format( ConversionTools.toMeter( position.x ) ) + delimiter + LocalizationManager.getManager().getFloatConverter().format( ConversionTools.toMeter( position.y ) ) + ")";
		String text = String.format( loc.getString( "gui.EditPanel.Mouse.PositionMillimeterMeter" ), realCoordsMillimeter, realCoordsMeter );
		EventServer.getInstance().dispatchEvent( new MessageEvent<JZetWindow>( null, MessageType.MousePosition, text ) );
	}

	public void sendError( String message ) {
		statusBar.blink( message );
	}

	/**
	 * Displays an error message in the left edge of the status bar
	 * @return true if the editor is in editing mode.
	 */
	public static boolean isEditing() {
		return editing;
	}

	/**
	 * Enables or disables the flag for the editing mode.
	 * @param editing the status
	 */
	public static void setEditing( boolean editing ) {
		JZetWindow.editing = editing;
	}

	/**
	 * Shows a {@code JToolBar} and hides all others.
	 * @param toolBar the tool bar that is shown
	 */
	private void showToolBar( JToolBar newToolbar ) {
		if( currentToolbar != null )
			getContentPane().remove( currentToolbar );
		if( newToolbar != null )
			getContentPane().add( newToolbar, BorderLayout.NORTH );
		currentToolbar = newToolbar;
		currentToolbar.repaint();
	}

	/*****************************************************************************
	 *                                                                           *
	 * Event handler                                                             *
	 *                                                                           *
	 ***************************************************************************
	/**
	 * @param event
	 */
	boolean progressBarEnabled = false;

	/**
	 * Hides the status bar and replaces it with a progress bar.
	 */
	private synchronized void enableProgressBar() {
		statusBarCardLayout.show( statusPanel, "progress" );
		progressBarEnabled = true;
	}

	/**
	 * Disables the progress bar and shows the status bar again.
	 */
	private synchronized void disableProgressBar() {
		progressBar.setValue( 0 );
		progressBarEnabled = false;
		statusBarCardLayout.show( statusPanel, "status" );
	}

	/**
	 * Sets a value for the progress bar.
	 * @param progress a progress value from 0 to 100
	 */
	public synchronized void setProgressValue( int progress ) {
		if( !progressBarEnabled )
			enableProgressBar();
		progressBar.setValue( progress );
		if( progress == 100 )
			disableProgressBar();
	}

	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}

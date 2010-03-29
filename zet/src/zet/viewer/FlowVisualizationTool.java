/**
 * FlowVisualizationTool.java
 * Created: 15.03.2010, 12:29:16
 */
package zet.viewer;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import com.thoughtworks.xstream.XStream;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmProgressEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmStartedEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmTerminatedEvent;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.util.Formatter;
import ds.GraphVisualizationResult;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;
import event.EventListener;
import event.EventServer;
import event.MessageEvent;
import gui.components.framework.Menu;
import gui.editor.GUIOptionManager;
import gui.visualization.Visualization;
import gui.visualization.control.graph.GLGraphControl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import zet.DatFileReaderWriter;
import zet.xml.FlowVisualization;
import zet.xml.XMLReader;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FlowVisualizationTool extends JFrame implements PropertyChangeListener, EventListener<MessageEvent> {

	Localization loc = Localization.getInstance();
	private int sliderAccuracy = 100;
	private JMenu mFile;
	private JMenuItem mnuFileOpen;
	private JMenuItem mnuFileSaveFlow;
	private JMenuItem mnuFileOpenFlow;
	private JMenu mFlow;
	private JMenuItem mnuFlowExecute;
	private JMenuItem mnuFlowRestart;
	private JMenuItem mnuFlowPause;
	final Visualization<GLGraphControl> vis = new Visualization<GLGraphControl>( new GLCapabilities() );
	JEventStatusBar sb = new JEventStatusBar();
	JSlider slider = new JSlider(0,0);
	FlowVisualizationTool theInstance;
	IdentifiableIntegerMapping<Node> xPos;
	IdentifiableIntegerMapping<Node> yPos;
	EarliestArrivalTask sw;
	EarliestArrivalFlowProblem eafp = null;
	GraphVisualizationResult graphVisResult = null;
	boolean pause = false;

	public FlowVisualizationTool() {
		super();
		theInstance = this;
		loc.setLocale( Locale.getDefault() );

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 800, 600 );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( (d.width - getSize().width) / 2, (d.height - getSize().height) / 2 );

		this.setTitle( "Fluss-Visualisierung" );
		this.setSize( 800, 600 );
		this.setLayout( new BorderLayout() );
		add( vis, BorderLayout.CENTER );
		vis.set3DView();
		vis.getCamera().getView().invert();
		vis.getCamera().getPos().z = 140;
		graphVisResult = new GraphVisualizationResult();
		GLGraphControl control = new GLGraphControl( graphVisResult );
		vis.setControl( control );

		vis.addKeyListener( new KeyListener() {

			public void keyTyped( KeyEvent arg0 ) {
			}

			public void keyPressed( KeyEvent arg0 ) {
				//System.out.println( vis.getCamera().toString() );
			}

			public void keyReleased( KeyEvent arg0 ) {
			}
		} );

		EventServer.getInstance().registerListener( this, MessageEvent.class );

		initializeComponents();
	}

	/**
	 * Initializes the components of the visualization window. That are menus,
	 * toolbar and status bar.
	 */
	private void initializeComponents() {
		loc.setLocale( Locale.GERMAN );
		loc.setPrefix( "viewer." );
		sb = new JEventStatusBar();
		add( sb, BorderLayout.SOUTH );

		JMenuBar bar = new JMenuBar();
		mFile = Menu.addMenu( bar, loc.getString( "menuFile" ) );
		mFlow = Menu.addMenu( bar, loc.getString( "menuFlow" ) );

		// Dateimenue
		mnuFileOpen = Menu.addMenuItem( mFile, loc.getString( "menuOpen" ), 'O', aclFile, "open" );
		mnuFileOpenFlow = Menu.addMenuItem( mFile, loc.getString( "menuOpenFlow" ), 'Q', aclFile, "openFlow" );
		mnuFileSaveFlow = Menu.addMenuItem( mFile, loc.getString( "menuSaveFlow" ), 'S', aclFile, "saveFlow" );

		// Flow-Menu
		mnuFlowExecute = Menu.addMenuItem( mFlow, loc.getString( "menuExecute" ), KeyEvent.VK_F5, aclFlow, "execute", 0 );
		mnuFlowPause = Menu.addMenuItem( mFlow, loc.getString( "menuPause" ), 'T', aclFlow, "pause" );
		mnuFlowRestart = Menu.addMenuItem( mFlow, loc.getString( "menuRestart" ), KeyEvent.VK_F6, aclFlow, "restart", 0 );

		setJMenuBar( bar );
		loc.setPrefix( "" );

		add( slider, BorderLayout.NORTH );
		slider.addChangeListener( chl );
		slider.addMouseListener( new MouseListener() {

			boolean wasAnimating = false;
			public void mouseClicked( MouseEvent arg0 ) {

			}

			public void mousePressed( MouseEvent arg0 ) {
				if( vis.isAnimating() ) {
					vis.stopAnimation();
					wasAnimating = true;
				}
			}

			public void mouseReleased( MouseEvent arg0 ) {
				if( wasAnimating )
					vis.startAnimation();
			}

			public void mouseEntered( MouseEvent arg0 ) {

			}

			public void mouseExited( MouseEvent arg0 ) {

			}
		} );
	}

	/**
	 * Starts the application, creates the main window and shows it within a new
	 * thread.
	 * @param arguments command line arguments, are ignored currently
	 */
	public static void main( String[] arguments ) {
		SwingUtilities.invokeLater( new Runnable() {

			@Override
			public void run() {
				// Change look and feel to native
				GUIOptionManager.changeLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

				// Start the viewer in the event-dispatch-thread
				FlowVisualizationTool fv = new FlowVisualizationTool();
				fv.setVisible( true );
			}
		} );
	}

	/**
	 *
	 * @param event
	 */
	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent );//System.out.println( ((AlgorithmProgressEvent) event).getProgress() );
		else if( event instanceof AlgorithmStartedEvent )
			System.out.println( "Algorithmus startet." );
		else if( event instanceof AlgorithmTerminatedEvent )
			System.out.println( "Laufzeit Flussalgorithmus: " + Formatter.formatTimeMilliseconds( event.getAlgorithm().getRuntime() ) );
		else
			System.out.println( event.toString() );
	}

	/** Action listener for the file menu. */
	ActionListener aclFile = new ActionListener() {

		public void actionPerformed( ActionEvent event ) {
			JFileChooser jfc = new JFileChooser();
			jfc.setCurrentDirectory( new File( "./" ) );
			if( event.getActionCommand().equals( "open" ) ) {
				XMLReader reader;
				FlowVisualization fv = null;
				try {
					reader = new XMLReader( "./testinstanz/test.xml" );
					fv = (FlowVisualization)reader.read();
				} catch( IOException ex ) {
					System.err.println( "Fehler beim laden!" );
					ex.printStackTrace();
				}

				GLGraphControl control2 = new GLGraphControl( fv );
				slider.setMaximum( fv.getTimeHorizon() * sliderAccuracy );
				vis.setControl( control2 );
				vis.update();
				vis.repaint();
				if( !vis.isAnimating() )
					vis.startAnimation();

				
				if( true )
					return;
				jfc.setFileFilter( new FileFilter() {

					@Override
					public boolean accept( File f ) {
						return f.isDirectory() || f.getName().toLowerCase().endsWith( ".dat" );
					}

					@Override
					public String getDescription() {
						return "Graphdateien";
					}
				} );
				if( jfc.showOpenDialog( theInstance ) == JFileChooser.APPROVE_OPTION ) {
					try {
						String path = jfc.getSelectedFile().getPath();
						sb.setStatusText( 0, "Lade Datei '" + path + "' " );

						xPos = new IdentifiableIntegerMapping<Node>( 0 );
						yPos = new IdentifiableIntegerMapping<Node>( 0 );
						eafp = DatFileReaderWriter.read( path, xPos, yPos );

					} catch( FileNotFoundException ex ) {
						Logger.getLogger( FlowVisualizationTool.class.getName() ).log( Level.SEVERE, null, ex );
						eafp = null;
						return;
					} catch( IOException ex ) {
						Logger.getLogger( FlowVisualizationTool.class.getName() ).log( Level.SEVERE, null, ex );
						eafp = null;
						return;
					} catch( Exception e ) {
						eafp = null;
						return;
					}
					GraphVisualizationResult graphVisResult = new GraphVisualizationResult( eafp, xPos, yPos );
					sb.setStatusText( 0, "Baue Visualisierung" );
					GLGraphControl control = new GLGraphControl( graphVisResult );
					if( vis.isAnimating() )
						vis.stopAnimation();
					vis.setControl( control );
					vis.update();
					vis.repaint();
				}
			} else if( event.getActionCommand().equals( "openFlow" ) ) {
				jfc.setFileFilter( new FileFilter() {

					@Override
					public boolean accept( File f ) {
						return f.isDirectory() || f.getName().toLowerCase().endsWith( ".flow" );
					}

					@Override
					public String getDescription() {
						return "Früheste-Ankunfts-Flüsse";
					}
				} );
				if( jfc.showOpenDialog( theInstance ) == JFileChooser.APPROVE_OPTION ) {
					try {
						XStream xml_convert = new XStream();
						FileReader input = new FileReader( jfc.getSelectedFile() );
						graphVisResult = (GraphVisualizationResult)xml_convert.fromXML( input );
						loadGraphVisResults();
					} catch( IOException e ) {
						sb.setStatusText( 0, "Fehler beim laden der Datei!" );
					}
				}
			} else if( event.getActionCommand().equals( "saveFlow" ) ) {
				try {
					PrintWriter output = new PrintWriter( new File( "./testinstanz/testoutput.flow" ) );
					XStream xml_convert = new XStream();
					xml_convert.toXML( graphVisResult, output );
				} catch( IOException e ) {
					sb.setStatusText( 0, "Fehler beim schreiben der Datei!" );
				}
			}
		}
	};
	/** Action listener for the flow menu. */
	ActionListener aclFlow = new ActionListener() {

		public void actionPerformed( ActionEvent event ) {
			JFileChooser jfc = new JFileChooser();
			jfc.setCurrentDirectory( new File( "./" ) );
			jfc.setFileFilter( new FileFilter() {

				@Override
				public boolean accept( File f ) {
					return f.isDirectory() || f.getName().toLowerCase().endsWith( ".dat" );
				}

				@Override
				public String getDescription() {
					return "Graphdateien";
				}
			} );
			if( event.getActionCommand().equals( "execute" ) ) {
				if( eafp.getTimeHorizon() <= 0 ) {
					sb.setStatusText( 0, "Schätze Zeithorizont" );
					LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
					estimator.setProblem( eafp );
					estimator.run();
					eafp.setTimeHorizon( estimator.getSolution().getUpperBound() );
					System.out.println( "Geschätzter Zeithorizont: " + estimator.getSolution().getUpperBound() );
				}

				// Fluss bestimmen
				sw = new EarliestArrivalTask( eafp );
				sw.addPropertyChangeListener( theInstance );
				sw.addAlgorithmListener( sb );
				sb.setStatusText( 0, "Berechne earliest arrival flow..." );
				sw.execute();
			} else if( event.getActionCommand().equals( "pause" ) ) {
				if( pause )
					vis.startAnimation();
				else
					vis.stopAnimation();
				pause = !pause;
			} else if( event.getActionCommand().equals( "restart" ) ) {
				vis.getControl().resetTime();
			}
		}
	};
  /** Change listener for the slider. Sets the visualization time. */
	ChangeListener chl = new ChangeListener() {

		public void stateChanged( ChangeEvent event ) {
			int time = slider.getValue();
			vis.getControl().setTime( time * vis.getControl().getNanoSecondsPerStep() / sliderAccuracy );
			vis.repaint();
		}
	};

	/**
	 * Loads a visualization from the data structure. The structure has either to
	 * be set up by a flow computation or loaded from a file.
	 */
	private void loadGraphVisResults() {
		sb.setStatusText( 0, "Baue Visualisierung" );
		slider.setMaximum( graphVisResult.getNeededTimeHorizon() * sliderAccuracy );
		GLGraphControl control = new GLGraphControl( graphVisResult );
		vis.setControl( control );
		vis.update();
		vis.repaint();
		sb.setStatusText( 0, "Fertig. Animation startet" );
		if( !vis.isAnimating() )
			vis.startAnimation();
	}

	public void propertyChange( PropertyChangeEvent pum ) {
		if( sw.getState() == SwingWorker.StateValue.DONE ) {
			if( vis.isAnimating() )
				vis.stopAnimation();

			sb.setStatusText( 0, "Erzeuge Kantenbasierte Variante..." );
			graphVisResult = new GraphVisualizationResult( sw.getEarliestArrivalFlowProblem(), xPos, yPos, sw.getFlowOverTime() );
			graphVisResult.setNeededTimeHorizon( sw.getNeededTimeHorizon() );
			loadGraphVisResults();
		}
	}

	public void handleEvent( MessageEvent event ) {
		if( vis.isAnimating() ) {
			slider.setValue( (int)(100*vis.getControl().getStep()) );
		}
	}
}

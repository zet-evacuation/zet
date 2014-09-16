/**
 * FlowVisualizationTool.java
 * Created: 15.03.2010, 12:29:16
 */
package de.tu_berlin.math.coga.zet.viewer;

import de.tu_berlin.coga.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import de.tu_berlin.coga.netflow.dynamic.LongestShortestPathTimeHorizonEstimator;
import com.thoughtworks.xstream.XStream;
import de.tu_berlin.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.coga.common.algorithm.AlgorithmProgressEvent;
import de.tu_berlin.coga.common.algorithm.AlgorithmStartedEvent;
import de.tu_berlin.coga.common.algorithm.AlgorithmTerminatedEvent;
import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.tu_berlin.coga.common.localization.Localization;
import de.tu_berlin.coga.common.localization.LocalizationManager;
import de.tu_berlin.coga.common.util.units.TimeUnits;
import de.tu_berlin.math.coga.components.framework.Menu;
import de.tu_berlin.math.coga.graph.io.xml.XMLReader;
import de.tu_berlin.math.coga.graph.io.xml.XMLReader.XMLFileData;
import de.tu_berlin.math.coga.graph.io.xml.visualization.FlowVisualization;
import de.tu_berlin.math.coga.graph.io.xml.visualization.GraphVisualization;
import de.tu_berlin.math.coga.zet.DatFileReaderWriter;
import ds.GraphVisualizationResults;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import event.EventListener;
import event.EventServer;
import event.MessageEvent;
import gui.GUIOptionManager;
import gui.visualization.Visualization;
import gui.visualization.control.graph.GLFlowGraphControl;
import gui.visualization.control.graph.GLNashGraphControl;
import de.tu_berlin.coga.util.movies.MovieManager;
import de.tu_berlin.coga.util.movies.ImageFormat;
import de.tu_berlin.coga.util.movies.MovieFormat;
import de.tu_berlin.coga.util.movies.MovieWriters;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
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
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import opengl.framework.abs.DrawableControlable;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class FlowVisualizationTool extends JFrame implements PropertyChangeListener, EventListener<MessageEvent> {
	private enum Modes {
		NashFlow,
		DynamicFlow
	}
	private Modes mode = Modes.DynamicFlow;
	final Visualization<GLFlowGraphControl> visFlow = new Visualization<>( new GLCapabilities() );
	final Visualization<GLNashGraphControl> visNash = new NashFlowVisualization( new GLCapabilities() );
	private ArrayList<Visualization<? extends DrawableControlable>> visualizations = new ArrayList<Visualization<? extends DrawableControlable>>() {
		{
			add( visFlow );
			add( visNash );
		}
	};
	Localization loc = ZETLocalization2.loc;
	private int sliderAccuracy = 100;
	Visualization<? extends DrawableControlable> vis = visFlow;
	JEventStatusBar sb = new JEventStatusBar();
	JSlider slider = new JSlider( 0, 0 );
	FlowVisualizationTool theInstance;
	IdentifiableIntegerMapping<Node> xPos;
	IdentifiableIntegerMapping<Node> yPos;
	EarliestArrivalTask sw;
	EarliestArrivalFlowProblem eafp = null;
	GraphVisualizationResults graphVisResult = null;
	boolean pause = false;

	public FlowVisualizationTool() {
		super();
		theInstance = this;
		LocalizationManager.getManager().setLocale( Locale.getDefault() );

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 800, 600 );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( (d.width - getSize().width) / 2, (d.height - getSize().height) / 2 );

		this.setTitle( "Fluss-Visualisierung" );
		this.setSize( 800, 600 );
		this.setLayout( new BorderLayout() );
		add( vis, BorderLayout.CENTER );
		// Initialize flow visualization with empty flow
		for( Visualization<? extends DrawableControlable> vis : visualizations ) {
			vis.set3DView();
			vis.getCamera().getView().invert();
			vis.getCamera().getPos().z = 140;
			vis.addKeyListener( new KeyListener() {
				public void keyTyped( KeyEvent arg0 ) {
				}

				public void keyPressed( KeyEvent arg0 ) {
				}

				public void keyReleased( KeyEvent arg0 ) {
				}
			} );
		}

    NetworkFlowModel nfm = new NetworkFlowModel( (ZToGraphRasterContainer)null );
		visFlow.setControl( new GLFlowGraphControl( new GraphVisualizationResults( nfm, nfm.getNodeCoordinates() ) ) );
		//visNash.setControl( null ); currently automatically initialized

		EventServer.getInstance().registerListener( this, MessageEvent.class );

		initializeComponents();
	}

	/**
	 * Initializes the components of the visualization window. That are menus,
	 * tool bar and status bar.
	 */
	private void initializeComponents() {
		LocalizationManager.getManager().setLocale( Locale.GERMAN );
		sb = new JEventStatusBar();
		add( sb, BorderLayout.SOUTH );

		JMenuBar bar = new JMenuBar();
		JMenu mFile = Menu.addMenu( bar, "viewer.menuFile" );
		JMenu mFlow = Menu.addMenu( bar, "viewer.menuFlow" );
		JMenu mView = Menu.addMenu( bar, "viewer.menuView" );

		// Dateimenue
		Menu.addMenuItem( mFile, "viewer.menuOpen", 'O', aclFile, "open" );
		Menu.addMenuItem( mFile, "viewer.menuOpenFlow", 'Q', aclFile, "openFlow" );
		Menu.addMenuItem( mFile, "viewer.menuSaveFlow", 'S', aclFile, "saveFlow" );
		Menu.addMenuItem( mFile, "Nash Flow", 'N', aclFile, "nashFlow" );

		// Flow-Menu
		Menu.addMenuItem( mFlow, "viewer.menuExecute", KeyEvent.VK_F5, aclFlow, "execute", 0 );
		Menu.addMenuItem( mFlow, "viewer.menuPause", 'T', aclFlow, "pause" );
		Menu.addMenuItem( mFlow, "viewer.menuRestart", KeyEvent.VK_F6, aclFlow, "restart", 0 );

		// View menu
		Menu.addMenuItem( mView, "viewer.menuScreenshot", KeyEvent.VK_F12, acl, "screenshot", 0 );


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
			System.out.println( "Laufzeit Flussalgorithmus: " + TimeUnits.MilliSeconds );
		else
			System.out.println( event.toString() );
	}
	boolean pressed = false;

	private void switchTo( Modes mode ) {
		if( vis.isAnimating() )
			vis.stopAnimation();
		remove( vis );
		switch( mode ) {
			case DynamicFlow:
				vis = visFlow;
				break;
			case NashFlow:
				vis = visNash;
				break;
		}
		add( vis, BorderLayout.CENTER );
		vis.repaint();
		vis.update();
		repaint();
	}
	/** Action listener for the file menu. */
	ActionListener aclFile = new ActionListener() {
		public void actionPerformed( ActionEvent event ) {
			JFileChooser jfc = new JFileChooser();
			jfc.setCurrentDirectory( new File( "./" ) );
			if( event.getActionCommand().equals( "open" ) ) {
				jfc.setFileFilter( new FileFilter() {
					@Override
					public boolean accept( File f ) {
						return f.isDirectory() || f.getName().toLowerCase().endsWith( ".dat" ) || f.getName().toLowerCase().endsWith( ".xml" );
					}

					@Override
					public String getDescription() {
						return "Graphdateien";
					}
				} );
				if( jfc.showOpenDialog( theInstance ) == JFileChooser.APPROVE_OPTION ) {
					switchTo( Modes.DynamicFlow );
					String path = jfc.getSelectedFile().getPath();
					sb.setStatusText( 0, "Lade Datei '" + path + "' " );
					GLFlowGraphControl control = null;
					if( path.endsWith( ".xml" ) ) {
						XMLReader reader;
						FlowVisualization fv = null;
						//String filename = jfc.getSelectedFile();
						File file = jfc.getSelectedFile();

						XMLFileData fileData = XMLFileData.Invalid;
						try {
							fileData = XMLReader.getFileData( file );
						} catch( FileNotFoundException ex ) {
							Logger.getLogger( FlowVisualizationTool.class.getName() ).log( Level.SEVERE, null, ex );
						} catch( IOException ex ) {
							Logger.getLogger( FlowVisualizationTool.class.getName() ).log( Level.SEVERE, null, ex );
						}

						switch( fileData ) {
							case FlowVisualization:
								try {
									reader = new XMLReader( file );
									fv = (FlowVisualization) reader.readFlowVisualization();
								} catch( IOException ex ) {
									System.err.println( "Fehler beim laden!" );
									ex.printStackTrace( System.err );
								}
								sb.setStatusText( 0, "Baue Visualisierung" );
								control = new GLFlowGraphControl( fv );
								slider.setMaximum( (fv.getTimeHorizon() + 1) * sliderAccuracy );
								break;
							case Graph:
								JOptionPane.showMessageDialog( theInstance,
							"Graphen ohne Koordinaten werden nicht unterstützt.",
							"Formatfehler",
							JOptionPane.ERROR_MESSAGE );
								return;
								//break;
							case GraphView:
								try {
									reader = new XMLReader( file );
									//Network n = reader.readGraph();
									final GraphVisualization gv = reader.readGraphView();
									fv = new FlowVisualization( gv );
									sb.setStatusText( 0, "Baue Visualisierung" );
									control = new GLFlowGraphControl( fv );

								} catch( FileNotFoundException ex ) {
									Logger.getLogger( FlowVisualizationTool.class.getName() ).log( Level.SEVERE, null, ex );
								} catch( IOException ex ) {
									Logger.getLogger( FlowVisualizationTool.class.getName() ).log( Level.SEVERE, null, ex );
								}
								break;
						}

					} else {
						try {
							xPos = new IdentifiableIntegerMapping<>( 0 );
							yPos = new IdentifiableIntegerMapping<>( 0 );
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
						GraphVisualizationResults graphVisResult = new GraphVisualizationResults( eafp, xPos, yPos, null );
						sb.setStatusText( 0, "Baue Visualisierung" );
						control = new GLFlowGraphControl( graphVisResult );
					}
					if( vis.isAnimating() )
						vis.stopAnimation();
					visFlow.setControl( control );
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
					switchTo( Modes.DynamicFlow );
					try {
						XStream xml_convert = new XStream();
						FileReader input = new FileReader( jfc.getSelectedFile() );

						XMLReader reader = new XMLReader( jfc.getSelectedFile() );


						graphVisResult = (GraphVisualizationResults) xml_convert.fromXML( input );
						loadGraphVisResults();
					} catch( IOException e ) {
						sb.setStatusText( 0, "Fehler beim laden der Datei!" );
					}
				}
			} else if( event.getActionCommand().equals( "saveFlow" ) )
				try {
					PrintWriter output = new PrintWriter( new File( "./testinstanz/testoutput.flow" ) );
					XStream xml_convert = new XStream();
					xml_convert.toXML( graphVisResult, output );
				} catch( IOException e ) {
					sb.setStatusText( 0, "Fehler beim schreiben der Datei!" );
				}
			else if( event.getActionCommand().equals( "nashFlow" ) )
				switchTo( Modes.NashFlow );
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
				vis.startAnimation();
				if( true )
					return;
				if( pause )
					vis.startAnimation();
				else
					vis.stopAnimation();
				pause = !pause;
			} else if( event.getActionCommand().equals( "restart" ) )
				vis.getControl().resetTime();
		}
	};
	/** Action listener for the rest. */
	ActionListener acl = new ActionListener() {
		public void actionPerformed( ActionEvent event ) {
			if( event.getActionCommand().equals( "screenshot" ) ) {
				//vis.takeScreenshot( "./screenshot_example.png" );
				if( pressed ) {
					vis.stopAnimation();
					return;
				}
				pressed = true;

				int width = 1920;
				int height = 1080;

				// make a movie...
				String movieFrameName = "movieFrame";
				// TODO BUG: wenn ein projekt noch nicht gespeichert worden ist, liefert das hier iene null pointer exception. (tritt auf, wenn ein video gedreht werden soll)
				//String projectName = zcontrol.getProject().getProjectFile().getName().substring( 0, zcontrol.getProject().getProjectFile().getName().length() - 4 );
				MovieManager movieCreator = vis.getMovieCreator();
//			if( movieFrameName.equals( "" ) )
//				movieCreator.setFramename( projectName );
//			else
				movieCreator.setFramename( movieFrameName );
				String path = "./nashvideo";
				if( !(path.endsWith( "/" ) || path.endsWith( "\\" )) )
					path = path + "/";
				String movieFileName = "nash_example_video";
				movieCreator.setFilename( movieFileName );
				movieCreator.setPath( "./nashvideo" );
				movieCreator.setFramename( "movieFrame" );
				MovieWriters mw = MovieWriters.FFmpeg;

				movieCreator.setMovieWriter( mw.getWriter() );
				//vis.setRecording( RecordingMode.Recording, new Dimension( width, height) );
				movieCreator.setWidth( width );
				movieCreator.setHeight( height );
				movieCreator.setCreateMovie( true );
				movieCreator.setDeleteFrames( false );
				movieCreator.setMovieFormat( MovieFormat.DIVX );
				movieCreator.setFramerate( 30 );
				movieCreator.setBitrate( 6000 );
				vis.setMovieFramerate( 25 );
				movieCreator.setFrameFormat( ImageFormat.PNG );
				//visualizationToolBar.play();
				//if( !vis.isAnimating() )
				vis.startAnimation();

			}
		}
	};
	/** Change listener for the slider. Sets the visualization time. */
	ChangeListener chl = new ChangeListener() {
		public void stateChanged( ChangeEvent event ) {
			int time = slider.getValue();
			if( mode == Modes.DynamicFlow )
				vis.getControl().setTime( time * visFlow.getControl().getNanoSecondsPerStep() / sliderAccuracy );
			// todo Nash-Slider
			vis.repaint();
		}
	};

	/**
	 * Loads a visualization from the data structure. The structure has either to
	 * be set up by a flow computation or loaded from a file.
	 */
	private void loadGraphVisResults() {
		sb.setStatusText( 0, "Baue Visualisierung" );
		slider.setMaximum( (graphVisResult.getTimeHorizon() + 1) * sliderAccuracy );
		GLFlowGraphControl control = new GLFlowGraphControl( graphVisResult );
		visFlow.setControl( control );
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
			graphVisResult = new GraphVisualizationResults( sw.getEarliestArrivalFlowProblem(), xPos, yPos, sw.getFlowOverTime() );
			graphVisResult.setTimeHorizon( sw.getNeededTimeHorizon() );
			loadGraphVisResults();
		}
	}

	public void handleEvent( MessageEvent event ) {
		if( vis.isAnimating() )
			if( mode == Modes.DynamicFlow )
				slider.setValue( (int) (100 * visFlow.getControl().getStep()) );
	}
}

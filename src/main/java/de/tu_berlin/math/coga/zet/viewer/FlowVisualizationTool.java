/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.tu_berlin.math.coga.zet.viewer;

import static gui.visualization.control.ZETGLControl.sizeMultiplicator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
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

import com.thoughtworks.xstream.XStream;
import de.tu_berlin.coga.util.movies.ImageFormat;
import de.tu_berlin.coga.util.movies.MovieFormat;
import de.tu_berlin.coga.util.movies.MovieManager;
import de.tu_berlin.coga.util.movies.MovieWriters;
import de.tu_berlin.math.coga.graph.io.xml.XMLReader;
import de.tu_berlin.math.coga.graph.io.xml.XMLReader.XMLFileData;
import de.tu_berlin.math.coga.graph.io.xml.visualization.FlowVisualization;
import de.tu_berlin.math.coga.graph.io.xml.visualization.GraphVisualization;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import de.zet_evakuierung.visualization.network.draw.GLGraphViews;
import de.zet_evakuierung.visualization.network.model.GLFlowGraphControl;
import de.zet_evakuierung.visualization.network.model.GLNashGraphModel;
import de.zet_evakuierung.visualization.network.model.GraphVisualizationModelContainer;
import de.zet_evakuierung.visualization.network.model.NetworkVisualizationModel;
import ds.GraphVisualizationResults;
import event.EventListener;
import event.EventServer;
import event.MessageEvent;
import gui.GUIOptionManager;
import gui.visualization.EvacuationVisualizationProperties;
import gui.visualization.Visualization;
import gui.visualization.VisualizationOptionManager;
import org.zetool.common.algorithm.AbstractAlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmProgressEvent;
import org.zetool.common.algorithm.AlgorithmStartedEvent;
import org.zetool.common.algorithm.AlgorithmTerminatedEvent;
import org.zetool.common.util.units.TimeUnits;
import org.zetool.components.framework.Menu;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.Node;
import org.zetool.graph.visualization.NodePositionMapping;
import org.zetool.math.vectormath.Vector3;
import org.zetool.netflow.dynamic.LongestShortestPathTimeHorizonEstimator;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.netflow.io.DatFileReaderWriter;
import org.zetool.opengl.framework.abs.Drawable;
import org.zetool.opengl.framework.abs.VisualizationModel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings("serial")
public class FlowVisualizationTool extends JFrame implements PropertyChangeListener, EventListener<MessageEvent> {

    private enum Modes {
        NashFlow,
        DynamicFlow
    }
    private Modes mode = Modes.DynamicFlow;
    private GLFlowGraphControl myC = null;
    private Drawable d = myC;

    final Visualization<Drawable, NetworkVisualizationModel> visFlow = new Visualization<>(new GLCapabilities(GLProfile.getDefault()));
    final Visualization<GLNashGraphModel, GLNashGraphModel> visNash = new NashFlowVisualization(new GLCapabilities(GLProfile.getDefault()));
    private ArrayList<Visualization<? extends Drawable, ? extends VisualizationModel>> visualizations = new ArrayList<Visualization<? extends Drawable, ? extends VisualizationModel>>() {
        {
            add(visFlow);
            add(visNash);
        }
    };
    private int sliderAccuracy = 100;
    Visualization<? extends Drawable, ? extends VisualizationModel> vis = visFlow;
    JEventStatusBar sb = new JEventStatusBar();
    JSlider slider = new JSlider(0, 0);
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

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width - getSize().width) / 2, (d.height - getSize().height) / 2);

        this.setTitle("Fluss-Visualisierung");
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());
        add(vis, BorderLayout.CENTER);
        // Initialize flow visualization with empty flow
        for (Visualization<? extends Drawable, ? extends VisualizationModel> vis : visualizations) {
            vis.set3DView();
            vis.getCamera().getView().invert();
            vis.getCamera().getPos().z = 140;
            vis.addKeyListener(new KeyAdapter() {
            });
        }

        NetworkFlowModel nfm = new NetworkFlowModel((ZToGraphRasterContainer) null);
        NodePositionMapping<Vector3> npm = new NodePositionMapping<>(3, nfm.graph().nodeCount());
        GraphVisualizationResults graphVisResult = new GraphVisualizationResults(nfm, npm);
        NetworkVisualizationModel networkVisualizationModel = new NetworkVisualizationModel();
        networkVisualizationModel.init(graphVisResult.getNetwork().nodes().size(), graphVisResult.getSupersink().id());

        EvacuationVisualizationProperties properties = new EvacuationVisualizationProperties();
        properties.setScaling(sizeMultiplicator);
        properties.setFloorHeight(VisualizationOptionManager.getFloorDistance());

        GraphVisualizationModelContainer graphModel
                = new GraphVisualizationModelContainer.Builder(graphVisResult, networkVisualizationModel)
                        .withVisualizationProperties(properties)
                        .build();
        GLGraphViews graphViews = GLGraphViews.createInstance(networkVisualizationModel, properties, graphVisResult,
                graphModel, false);
        GLFlowGraphControl control = new GLFlowGraphControl(networkVisualizationModel, graphModel, graphViews);

        visFlow.setControl(control, networkVisualizationModel);
        //visNash.setControl( null ); currently automatically initialized

        EventServer.getInstance().registerListener(this, MessageEvent.class);

        initializeComponents();
    }

    /**
     * Initializes the components of the visualization window. That are menus, tool bar and status bar.
     */
    private void initializeComponents() {
        sb = new JEventStatusBar();
        add(sb, BorderLayout.SOUTH);

        JMenuBar bar = new JMenuBar();
        JMenu mFile = Menu.addMenu(bar, "viewer.menuFile");
        JMenu mFlow = Menu.addMenu(bar, "viewer.menuFlow");
        JMenu mView = Menu.addMenu(bar, "viewer.menuView");

        // Dateimenue
        Menu.addMenuItem(mFile, "viewer.menuOpen", 'O', aclFile, "open");
        Menu.addMenuItem(mFile, "viewer.menuOpenFlow", 'Q', aclFile, "openFlow");
        Menu.addMenuItem(mFile, "viewer.menuSaveFlow", 'S', aclFile, "saveFlow");
        Menu.addMenuItem(mFile, "Nash Flow", 'N', aclFile, "nashFlow");

        // Flow-Menu
        Menu.addMenuItem(mFlow, "viewer.menuExecute", KeyEvent.VK_F5, aclFlow, "execute", 0);
        Menu.addMenuItem(mFlow, "viewer.menuPause", 'T', aclFlow, "pause");
        Menu.addMenuItem(mFlow, "viewer.menuRestart", KeyEvent.VK_F6, aclFlow, "restart", 0);

        // View menu
        Menu.addMenuItem(mView, "viewer.menuScreenshot", KeyEvent.VK_F12, acl, "screenshot", 0);

        setJMenuBar(bar);

        add(slider, BorderLayout.NORTH);
        slider.addChangeListener(chl);
        slider.addMouseListener(new MouseAdapter() {
            boolean wasAnimating = false;

            @Override
            public void mousePressed(MouseEvent arg0) {
                if (vis.isAnimating()) {
                    vis.stopAnimation();
                    wasAnimating = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
                if (wasAnimating) {
                    vis.startAnimation();
                }
            }

        });
    }

    /**
     * Starts the application, creates the main window and shows it within a new thread.
     *
     * @param arguments command line arguments, are ignored currently
     */
    public static void main(String[] arguments) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Change look and feel to native
                GUIOptionManager.changeLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Start the viewer in the event-dispatch-thread
                FlowVisualizationTool fv = new FlowVisualizationTool();
                fv.setVisible(true);
            }
        });
    }

    /**
     *
     * @param event
     */
    public void eventOccurred(AbstractAlgorithmEvent event) {
        if (event instanceof AlgorithmProgressEvent);//System.out.println( ((AlgorithmProgressEvent) event).getProgress() );
        else if (event instanceof AlgorithmStartedEvent) {
            System.out.println("Algorithmus startet.");
        } else if (event instanceof AlgorithmTerminatedEvent) {
            System.out.println("Laufzeit Flussalgorithmus: " + TimeUnits.MILLI_SECOND);
        } else {
            System.out.println(event.toString());
        }
    }
    boolean pressed = false;

    private void switchTo(Modes mode) {
        if (vis.isAnimating()) {
            vis.stopAnimation();
        }
        remove(vis);
        switch (mode) {
            case DynamicFlow:
                vis = visFlow;
                break;
            case NashFlow:
                vis = visNash;
                break;
        }
        add(vis, BorderLayout.CENTER);
        vis.repaint();
        vis.update();
        repaint();
    }
    /**
     * Action listener for the file menu.
     */
    ActionListener aclFile = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
            JFileChooser jfc = new JFileChooser();
            jfc.setCurrentDirectory(new File("./"));
            EvacuationVisualizationProperties properties = new EvacuationVisualizationProperties();
            properties.setScaling(sizeMultiplicator);
            properties.setFloorHeight(VisualizationOptionManager.getFloorDistance());
            if (event.getActionCommand().equals("open")) {
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".dat") || f.getName().toLowerCase().endsWith(".xml");
                    }

                    @Override
                    public String getDescription() {
                        return "Graphdateien";
                    }
                });
                GraphVisualizationModelContainer graphModel;
                GLGraphViews graphViews;
                if (jfc.showOpenDialog(theInstance) == JFileChooser.APPROVE_OPTION) {
                    switchTo(Modes.DynamicFlow);
                    String path = jfc.getSelectedFile().getPath();
                    sb.setStatusText(0, "Lade Datei '" + path + "' ");
                    GLFlowGraphControl control = null;
                    NetworkVisualizationModel networkVisualizationModel = new NetworkVisualizationModel();
                    if (path.endsWith(".xml")) {
                        XMLReader reader;
                        FlowVisualization fv = null;
                        //String filename = jfc.getSelectedFile();
                        File file = jfc.getSelectedFile();

                        XMLFileData fileData = XMLFileData.Invalid;
                        try {
                            fileData = XMLReader.getFileData(file);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(FlowVisualizationTool.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(FlowVisualizationTool.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        switch (fileData) {
                            case FlowVisualization:
								try {
                                reader = new XMLReader(file);
                                fv = (FlowVisualization) reader.readFlowVisualization();
                            } catch (IOException ex) {
                                System.err.println("Fehler beim laden!");
                                ex.printStackTrace(System.err);
                            }
                            sb.setStatusText(0, "Baue Visualisierung");

                            networkVisualizationModel.init(fv.getNetwork().nodeCount(), graphVisResult.getSupersink().id());

                            graphModel = new GraphVisualizationModelContainer.Builder(fv, networkVisualizationModel)
                                    .withVisualizationProperties(properties)
                                    .build();
                            graphViews = GLGraphViews.createInstance(networkVisualizationModel, properties,
                                    graphVisResult, graphModel, false);
                            control = new GLFlowGraphControl(networkVisualizationModel, graphModel, graphViews);

                            slider.setMaximum((fv.getTimeHorizon() + 1) * sliderAccuracy);
                            break;
                            case Graph:
                                JOptionPane.showMessageDialog(theInstance,
                                        "Graphen ohne Koordinaten werden nicht unterstützt.",
                                        "Formatfehler",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            //break;
                            case GraphView:
								try {
                                reader = new XMLReader(file);
                                //Network n = reader.readGraph();
                                final GraphVisualization gv = reader.readGraphView();
                                fv = new FlowVisualization(gv);
                                sb.setStatusText(0, "Baue Visualisierung");

                                networkVisualizationModel.init(fv.getNetwork().nodeCount(), graphVisResult.getSupersink().id());

                                graphModel = new GraphVisualizationModelContainer.Builder(fv, networkVisualizationModel)
                                        .withVisualizationProperties(properties)
                                        .build();
                                graphViews = GLGraphViews.createInstance(networkVisualizationModel, properties,
                                        graphVisResult, graphModel, false);
                                control = new GLFlowGraphControl(networkVisualizationModel, graphModel, graphViews);

                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(FlowVisualizationTool.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(FlowVisualizationTool.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        }

                    } else {
                        try {
                            xPos = new IdentifiableIntegerMapping<>(0);
                            yPos = new IdentifiableIntegerMapping<>(0);
                            eafp = DatFileReaderWriter.read(path, xPos, yPos);

                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(FlowVisualizationTool.class.getName()).log(Level.SEVERE, null, ex);
                            eafp = null;
                            return;
                        } catch (IOException ex) {
                            Logger.getLogger(FlowVisualizationTool.class.getName()).log(Level.SEVERE, null, ex);
                            eafp = null;
                            return;
                        } catch (Exception e) {
                            eafp = null;
                            return;
                        }
                        GraphVisualizationResults graphVisResult = new GraphVisualizationResults(eafp, xPos, yPos, null);
                        sb.setStatusText(0, "Baue Visualisierung");

                        networkVisualizationModel.init(graphVisResult.getNetwork().nodes().size(), graphVisResult.getSupersink().id());

                        graphModel = new GraphVisualizationModelContainer
                                .Builder(graphVisResult, networkVisualizationModel)
                                .withVisualizationProperties(properties)
                                .build();
                        graphViews = GLGraphViews.createInstance(networkVisualizationModel, properties, graphVisResult,
                                graphModel, false);
                        control = new GLFlowGraphControl(networkVisualizationModel, graphModel, graphViews);
                    }
                    if (vis.isAnimating()) {
                        vis.stopAnimation();
                    }
                    visFlow.setControl(control, networkVisualizationModel);
                    vis.update();
                    vis.repaint();
                }
            } else if (event.getActionCommand().equals("openFlow")) {
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".flow");
                    }

                    @Override
                    public String getDescription() {
                        return "Früheste-Ankunfts-Flüsse";
                    }
                });
                if (jfc.showOpenDialog(theInstance) == JFileChooser.APPROVE_OPTION) {
                    switchTo(Modes.DynamicFlow);
                    try {
                        XStream xml_convert = new XStream();
                        FileReader input = new FileReader(jfc.getSelectedFile());

                        XMLReader reader = new XMLReader(jfc.getSelectedFile());

                        graphVisResult = (GraphVisualizationResults) xml_convert.fromXML(input);
                        loadGraphVisResults();
                    } catch (IOException e) {
                        sb.setStatusText(0, "Fehler beim laden der Datei!");
                    }
                }
            } else if (event.getActionCommand().equals("saveFlow"))
				try {
                PrintWriter output = new PrintWriter(new File("./testinstanz/testoutput.flow"));
                XStream xml_convert = new XStream();
                xml_convert.toXML(graphVisResult, output);
            } catch (IOException e) {
                sb.setStatusText(0, "Fehler beim schreiben der Datei!");
            } else if (event.getActionCommand().equals("nashFlow")) {
                switchTo(Modes.NashFlow);
            }
        }
    };
    /**
     * Action listener for the flow menu.
     */
    ActionListener aclFlow = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
            JFileChooser jfc = new JFileChooser();
            jfc.setCurrentDirectory(new File("./"));
            jfc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".dat");
                }

                @Override
                public String getDescription() {
                    return "Graphdateien";
                }
            });
            switch (event.getActionCommand()) {
                case "execute":
                    if (eafp.getTimeHorizon() <= 0) {
                        sb.setStatusText(0, "Schätze Zeithorizont");
                        LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
                        estimator.setProblem(eafp);
                        estimator.run();
                        eafp.setTimeHorizon(estimator.getSolution().getUpperBound());
                        System.out.println("Geschätzter Zeithorizont: " + estimator.getSolution().getUpperBound());
                    } // Fluss bestimmen
                    sw = new EarliestArrivalTask(eafp);
                    sw.addPropertyChangeListener(theInstance);
                    sw.addAlgorithmListener(sb);
                    sb.setStatusText(0, "Berechne earliest arrival flow...");
                    sw.execute();
                    break;
                case "pause":
                    vis.startAnimation();
                    if (true) {
                        return;
                    }
                    if (pause) {
                        vis.startAnimation();
                    } else {
                        vis.stopAnimation();
                    }
                    pause = !pause;
                    break;
                case "restart":
                    vis.getControl().resetTime();
                    break;
            }
        }
    };
    /**
     * Action listener for the rest.
     */
    ActionListener acl = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getActionCommand().equals("screenshot")) {
                //vis.takeScreenshot( "./screenshot_example.png" );
                if (pressed) {
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
                movieCreator.setFramename(movieFrameName);
                String path = "./nashvideo";
                if (!(path.endsWith("/") || path.endsWith("\\"))) {
                    path = path + "/";
                }
                String movieFileName = "nash_example_video";
                movieCreator.setFilename(movieFileName);
                movieCreator.setPath("./nashvideo");
                movieCreator.setFramename("movieFrame");
                MovieWriters mw = MovieWriters.FFmpeg;

                movieCreator.setMovieWriter(mw.getWriter());
                //vis.setRecording( RecordingMode.Recording, new Dimension( width, height) );
                movieCreator.setWidth(width);
                movieCreator.setHeight(height);
                movieCreator.setCreateMovie(true);
                movieCreator.setDeleteFrames(false);
                movieCreator.setMovieFormat(MovieFormat.DIVX);
                movieCreator.setFramerate(30);
                movieCreator.setBitrate(6000);
                vis.setMovieFramerate(25);
                movieCreator.setFrameFormat(ImageFormat.PNG);
                //visualizationToolBar.play();
                //if( !vis.isAnimating() )
                vis.startAnimation();

            }
        }
    };
    /**
     * Change listener for the slider. Sets the visualization time.
     */
    ChangeListener chl = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent event) {
            int time = slider.getValue();
            if (mode == Modes.DynamicFlow) {
                vis.getControl().setTime(time * visFlow.getControl().getNanoSecondsPerStep() / sliderAccuracy);
            }
            // todo Nash-Slider
            vis.repaint();
        }
    };

    /**
     * Loads a visualization from the data structure. The structure has either to be set up by a flow computation or
     * loaded from a file.
     */
    private void loadGraphVisResults() {
        sb.setStatusText(0, "Baue Visualisierung");
        slider.setMaximum((graphVisResult.getTimeHorizon() + 1) * sliderAccuracy);

        NetworkVisualizationModel networkVisualizationModel = new NetworkVisualizationModel();
        networkVisualizationModel.init(graphVisResult.getNetwork().nodes().size(), graphVisResult.getSupersink().id());

        EvacuationVisualizationProperties properties = new EvacuationVisualizationProperties();
        properties.setScaling(sizeMultiplicator);
        properties.setFloorHeight(VisualizationOptionManager.getFloorDistance());

        GraphVisualizationModelContainer graphModel = new GraphVisualizationModelContainer
                .Builder(graphVisResult, networkVisualizationModel)
                .withVisualizationProperties(properties)
                .build();
        GLGraphViews graphViews = GLGraphViews.createInstance(networkVisualizationModel, properties, graphVisResult,
                graphModel, false);
        GLFlowGraphControl control = new GLFlowGraphControl(networkVisualizationModel, graphModel, graphViews);
        visFlow.setControl(control, networkVisualizationModel);
        vis.update();
        vis.repaint();
        sb.setStatusText(0, "Fertig. Animation startet");
        if (!vis.isAnimating()) {
            vis.startAnimation();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pum) {
        if (sw.getState() == SwingWorker.StateValue.DONE) {
            if (vis.isAnimating()) {
                vis.stopAnimation();
            }

            sb.setStatusText(0, "Erzeuge Kantenbasierte Variante...");
            graphVisResult = new GraphVisualizationResults(sw.getEarliestArrivalFlowProblem(), xPos, yPos, sw.getFlowOverTime());
            graphVisResult.setTimeHorizon(sw.getNeededTimeHorizon());
            loadGraphVisResults();
        }
    }

    @Override
    public void handleEvent(MessageEvent event) {
        if (vis.isAnimating()) {
            if (mode == Modes.DynamicFlow) {
                slider.setValue((int) (100 * visFlow.getControl().getStep()));
            }
        }
    }
}

/**
 * Class JAbstractPropertyWindow
 * Erstellt 22.04.2008, 18:25:29
 */
package gui.editor.properties;

import ds.PropertyContainer;
import gui.JEditor;
import gui.editor.properties.framework.AbstractPropertyValue;
import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import localization.Localization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class JAbstractPropertyWindow extends JDialog {

	private PropertyTreeModel propertyTreeModel;
	private JPanel contents;		// The surroudning panel on the right side
	private JPanel settings;		// The panel in which the list is shown
	private JScrollPane settingsView;
	private JSplitPane splitPane;
	private JTree tree;
	private JScrollPane treeView;
	private JDialog dialog;
	protected int space = 16;
	private String defaultConfigFile = "default.xml";

	public JAbstractPropertyWindow( JFrame owner, String title, int width, int height, String filename ) {
		super( owner, title, true );
		dialog = this;
		dialog.setResizable( false );
		File configFile = new File( filename );
		getContentPane().setLayout( new BorderLayout() );

		setSize( width, height );
		setLocation( owner.getX() + (owner.getWidth() - width) / 2,
						owner.getY() + (owner.getHeight() - height) / 2 );

		// Initialize model
		try {
		propertyTreeModel = PropertyContainer.loadConfigFile( configFile );
		} catch (Exception e) {}
		contents = new JPanel( new BorderLayout() );
		settings = new JPanel();
		settingsView = new JScrollPane( settings );

		int bigSpace = 20;
		double size[][] = {
			{ // Columns
				bigSpace,
				TableLayout.FILL,
				bigSpace
			},
			{ // Rows
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				bigSpace,
				TableLayout.FILL
			}
		};
		settings.setLayout( new TableLayout( size ) );

		settingsView = new JScrollPane( settings );

		TreeModel model = propertyTreeModel;
		tree = new JTree();
		tree.setModel( model );
		tree.getSelectionModel().addTreeSelectionListener(
						new TreeSelectionListener() {

							public void valueChanged( TreeSelectionEvent e ) {
								TreePath path = e.getNewLeadSelectionPath();
								if( path == null ) {
									return;
								}
								PropertyTreeNode node = (PropertyTreeNode) path.getLastPathComponent();
								settings.removeAll();
								settings.setPreferredSize( null );
								int row = 1;
								int col = 1;
								for( AbstractPropertyValue property : node.getProperties() ) {
									settings.add( property.getPanel(), col + "," + row++ + ", left, top" );
									JLabel nl = new JLabel( property.getInformation() );
									settings.add( nl, col + "," + row++ );
									row++;
								}
								settings.repaint();
								splitPane.repaint();
								settings.setPreferredSize( new Dimension( 250, settings.getPreferredSize().height ) );
								pack();
							}
						} );
		treeView = new JScrollPane( tree );
		contents.add( settingsView, BorderLayout.CENTER );

		contents.add( createButtonPanel(), BorderLayout.SOUTH );

		// Create Split-Pane
		splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, false, treeView, contents );
		splitPane.setResizeWeight( 1.0d );
		splitPane.setPreferredSize( new Dimension( 700, 500 ) );
		splitPane.setDividerLocation( 250 );
		splitPane.setOneTouchExpandable( false );

		getContentPane().add( splitPane, BorderLayout.CENTER );
		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		pack();
	}

	/**
	 * Creates a panel that is placed at the right bottom of the window.
	 * @return
	 */
	protected abstract JPanel createButtonPanel();
	ActionListener aclButton = new ActionListener() {

		public void actionPerformed( ActionEvent e ) {
			JFileChooser jfcProject = new JFileChooser( new File( "./properties/" ) );
			FileFilter filter = new FileFilter() {
				@Override
				public boolean accept( File f ) {
					return f.isDirectory() || f.getName().toLowerCase().endsWith( ".xml" );
				}

				@Override
				public String getDescription() {
					return "Konfigurationsdateien (*.xml)";
				}
			};
			jfcProject.setFileFilter( filter );
			jfcProject.setAcceptAllFileFilterUsed( false );

			File configFile;
			if( e.getActionCommand().equals( "open" ) ) {
				if( jfcProject.showOpenDialog( JEditor.getInstance() ) == JFileChooser.APPROVE_OPTION ) {
					configFile = jfcProject.getSelectedFile();
					try {
					propertyTreeModel = PropertyContainer.loadConfigFile( configFile );
					} catch (Exception ee) {}
					setModel( propertyTreeModel );
				}
			} else if( e.getActionCommand().equals( "save" ) ) {
				if( jfcProject.showSaveDialog( dialog ) == JFileChooser.APPROVE_OPTION ) {
					configFile = jfcProject.getSelectedFile();
					if( !configFile.getName().endsWith( ".xml" ) ) {
						configFile = new File( configFile.getAbsolutePath() + ".xml" );
					}
					// Gib aus was gespeichert wird und return
					PropertyTreeNode root = getRoot();
					saveConfigFile( configFile );
				}
			} else if( e.getActionCommand().equals( "quit" ) ) {
				dispose();
			} else if( e.getActionCommand().equals( "ok" ) ) {
				PropertyContainer.getInstance().applyParameters( propertyTreeModel );
				saveWorking();
				performOK();
				dispose();
			}
		}
	};

	/**
	 * Returns the root node of the property tree.
	 * @return the root node of the property tree.
	 */
	protected PropertyTreeNode getRoot() {
		return propertyTreeModel.getRoot();
	}
	
	/**
	 * Sets a model for the tree view.
	 * @param model
	 */
	public void setModel( PropertyTreeModel model ) {
		propertyTreeModel = model;
		tree.setModel( model );
		tree.revalidate();
	}

	/**
	 * Loads a file containing the configuration in XML-Format.
	 */
	private void saveConfigFile( File file ) {
		try {
			PropertyContainer.saveConfigFile( propertyTreeModel, file );
		} catch( IOException ex ) {
			JOptionPane.showMessageDialog( null,
							Localization.getInstance().getString( "gui.ContactDeveloper" ),
							Localization.getInstance().getString( "gui.Error" ),
							JOptionPane.ERROR_MESSAGE );
		}
	}

	public void saveWorking() {
		try {
			PropertyContainer.saveConfigFile( propertyTreeModel, new File( defaultConfigFile ) );
		} catch( IOException ex ) {
			JOptionPane.showMessageDialog( null,
							Localization.getInstance().getString( "gui.ContactDeveloper" ),
							Localization.getInstance().getString( "gui.Error" ),
							JOptionPane.ERROR_MESSAGE );
		}
	}

	/**
	 * Returns the filename of the currently set temporary option file.
	 * @return the filename and maybe path
	 */
	public String getDefaultConfigFile() {
		return defaultConfigFile;
	}

	/**
	 * Sets the filename and path of the temporary option file.
	 * @param defaultConfigFile the filename
	 */
	public void setDefaultConfigFile( String defaultConfigFile ) {
		this.defaultConfigFile = defaultConfigFile;
	}

	/**
	 * Returns the name of the currently shown property set
	 * @return the name (not the filename!)
	 */
	public String getPropertyName() {
		return propertyTreeModel.getPropertyName();
	}
	
	public abstract void performOK();
}

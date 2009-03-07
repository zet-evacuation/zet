/*
 * VideoOptions.java
 * Created on 8. November 2008, 20:45
 */

package gui;

import gui.components.framework.Button;
import info.clearthought.layout.TableLayout;
import io.movie.MovieWriter;
import io.visualization.ImageFormat;
import io.visualization.MovieFormat;
import io.visualization.MovieWriters;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EnumSet;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import localization.Localization;
import util.Helper;

/**
 * A window allowing to set the options used to create a video. The
 * {@link ImageFormat}, {@link MovieFormat} and the {@link MovieWriter}
 * can be chosen, for the latter it is possible to set advanced options.
 * @author  Jan-Philipp Kappmeier
 */
public class VideoOptions extends javax.swing.JDialog {
	/** The index of the tab with the advanced options for the writer. */
	private final int ADVANCED_TAB_INDEX = 3;
	/** The space between components and the border of the frames and the compontents. */
	private final int space = 10;
	/** The return value if the ok button is clicked. */
	public static final int OK = 0;
	/** The return value if the cancel button is clicked or if the window is closed without using a button. */
	public static final int CANCEL = 1;
	/** The return value, cancel by default. */
	private int retVal = CANCEL;
	/** The localization class. */
	static final Localization loc = Localization.getInstance();
	/** The default cancel button. */
  private JButton btnCancel;
	/** The default ok button. */
  private JButton btnOK;
	/** The tabbed pane. */
	private JTabbedPane tabbedPane;
	/**  The tab with basic format selections. */
  private JPanel tabMovieRecord;
	/**  The tab with advanced options, such as bitrate or resolution. */
	private JPanel tabMovieOptions;
	/**  The tab allowing to set up filenames. */
  private JPanel tabMoviePath;
	/** A radio button representing that only frames are recorded and no video is created. */
	private JRadioButton optFrames;
	/** A radio button representing that a movie is created. */
	private JRadioButton optMovie;
	/** A selection box allowing to switch the movie writer. */
	private JComboBox cbxMovieWriters;
	/** A selection box showing all image formats supported by the selected writer. */
	private JComboBox cbxFrameFormat;
	/** A selection box showing all movie formats supported by the selected writer. */
	private JComboBox cbxMovieFormat;
	/** A check box allowing to enable or disable automatic deleting of t he frames after the movie capture. */
	private JCheckBox chkDeleteFrames;
	/** The width of the movie. */
	private JTextField txtWidth;
	/** The height of the movie. */
	private JTextField txtHeight;
	/** The bitrate in kilobits per second. */
	private JTextField txtBitrate;
	/** The framerate of the movie in frames per second. */
	private JTextField txtFramerate;
	/** A label displaying the approximated filesize. */
	private JLabel lblEstimatedFilesize;
	/** The selected movie writer. */
	private MovieWriters mw = MovieWriters.FFmpeg;
	/** The estimated time of the video that is to be encoded. Used to calculate size. */
	private double estimatedTime = 0;

	/**
	 * Creates the form <code>VideoOptions</code> and sets the position to
	 * centered.
	 * @param owner the parent window that owns this window as child
	 */
	public VideoOptions(java.awt.Frame owner ) {
		super( owner, true );
		setDefaultCloseOperation( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
    setTitle( loc.getString( "gui.visualization.createMovieDialog.title" ) );
    setResizable( false );
		initComponents();
		if( owner != null )
			setLocation( owner.getX () + ( owner.getWidth() - getWidth() ) / 2, owner.getY () + ( owner.getHeight() - getWidth() ) / 2 );
	}

	/**
	 * This method is called from within the constructor to initialize the 
	 * components of the form.
	 */
  private void initComponents() {
    tabbedPane = new JTabbedPane();
    tabMovieRecord = getMovieRecordTab();
		tabMovieOptions = getMovieOptionsTab();
    tabMoviePath = getMoviePathTab();

		loc.setPrefix( "gui.visualization.createMovieDialog." );
    tabbedPane.addTab( loc.getString( "tabVideoRecord" ), null, tabMovieRecord, loc.getString( "tabVideoRecordTooltip") );
    tabbedPane.addTab( loc.getString( "tabVideoOptions" ), null, tabMovieOptions, loc.getString( "tabVideoOptionsTooltip") );
    tabbedPane.addTab( loc.getString( "tabVideoPath" ), null, tabMoviePath, loc.getString( "tabVideoPathTooltip") );
		tabbedPane.addTab( loc.getString( "tabExtended" ), null, mw.getWriter().getAdvancedConfigurationPanel(), loc.getString( "tabExtendedTooltip") );
		loc.setPrefix( "" );
		
		btnCancel = Button.newButton( loc.getString( "gui.OK" ), aclFinish, "ok", loc.getString( "gui.OK.tooltip" ) );
    btnOK = Button.newButton( loc.getString( "gui.Cancel" ), aclFinish, "cancel", loc.getString( "gui.Cancel.tooltip" ) );

		GroupLayout layout = new GroupLayout( getContentPane() );
		getContentPane().setLayout( layout );
		
		int windowWidth = 413;
		int windowHeight = 230;
		layout.setHorizontalGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addGroup( layout.createSequentialGroup().addContainerGap().addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( tabbedPane, GroupLayout.DEFAULT_SIZE, windowWidth, Short.MAX_VALUE ).addGroup( GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent( btnOK ).addPreferredGap( LayoutStyle.ComponentPlacement.RELATED ).addComponent( btnCancel ) ) ).addContainerGap() ) );
		layout.setVerticalGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addGroup( layout.createSequentialGroup().addContainerGap().addComponent( tabbedPane, GroupLayout.PREFERRED_SIZE, windowHeight, GroupLayout.PREFERRED_SIZE ).addPreferredGap( LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE ).addComponent( btnCancel ).addComponent( btnOK ) ).addContainerGap() ) );

		optMovie.setSelected( true );

		pack();
	}

	/**
	 * Returns a panel inserted as a tab in the tab control containing the
	 * basic components to record a movie. Allows selection of a
	 * {@link MovieWriters} and the formats for the images and the movie.
	 * @return the panel
	 */
	private JPanel getMovieRecordTab() {
		double size[][] = // Columns
		{
			{ space, 2*space, TableLayout.PREFERRED, space, TableLayout.FILL, space },
			//Rows
			{ space,
				TableLayout.PREFERRED,	// Wrapper/Writer
				space,
				TableLayout.PREFERRED,	// Option Images
				space/2,
				TableLayout.PREFERRED,	// Image Format
				space,
				TableLayout.PREFERRED,	// Option Video
				space/2,
				TableLayout.PREFERRED,	// Video Format
				space,
				TableLayout.PREFERRED,	// Checkbox Delete Images
				space
			}
		};
		
		JPanel panel = new JPanel( new TableLayout( size ) );
		loc.setPrefix( "gui.visualization.createMovieDialog." );
		
		optFrames = new JRadioButton( loc.getString( "frameOption" ) );
		optFrames.addActionListener( aclRadio );
		optFrames.setActionCommand( "frame" );
		optMovie = new JRadioButton( loc.getString( "movieOption" ) );
		optMovie.addActionListener( aclRadio );
		optMovie.setActionCommand( "movie" );
		
		cbxFrameFormat = new JComboBox();
		cbxMovieFormat = new JComboBox();
		cbxMovieWriters = new JComboBox();
		cbxMovieWriters.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e ) {
				mw = (MovieWriters)cbxMovieWriters.getSelectedItem();	// In the list box are only MovieWriters
				cbxFrameFormat.removeAllItems();
				cbxMovieFormat.removeAllItems();
				for( ImageFormat f : mw.getSupportedImageFormats() )
					cbxFrameFormat.addItem( f );
				for( MovieFormat f : mw.getSupportedMovieFormats() )
					cbxMovieFormat.addItem( f );
				if( tabbedPane.getTabCount() > ADVANCED_TAB_INDEX ) {
					tabbedPane.remove( ADVANCED_TAB_INDEX );
					tabbedPane.insertTab( loc.getString( "gui.visualization.createMovieDialog.tabExtended" ), null, mw.getWriter().getAdvancedConfigurationPanel(), loc.getString( "gui.visualization.createMovieDialog.tabVideoOptionsTooltip" ), ADVANCED_TAB_INDEX );
				}
			}
		});
		for( MovieWriters w : EnumSet.allOf( MovieWriters.class ) )
			cbxMovieWriters.addItem( w );
		
		chkDeleteFrames = new JCheckBox( loc.getString( "deleteFrames" ) );

		panel.add( cbxMovieWriters, "1,1,4,1" );
		panel.add( optFrames, "1,3,4,3" );
		panel.add( optMovie, "1,5,4,5" );
		panel.add( new JLabel( loc.getString( "imageFormat" ) + ":" ), "2,7,2,7" );
		panel.add( cbxFrameFormat, "4,7,4,7" );
		panel.add( new JLabel( loc.getString( "movieFormat" ) + ":" ), "2,9,2,9" );
		panel.add( cbxMovieFormat, "4,9,4,9" );
		panel.add( chkDeleteFrames, "1,11,4,11" );

		loc.setPrefix( "" );
		return panel;
	}

	/**
	 * Returns a panel inserted as a tab in the tab control containing some
	 * components to specify the parameter of the movie. Supported parameter are
	 * bitrate, framerate and resolution. Note that not all of the parameter are
	 * supported by all {@link MovieWriters}.
	 * @return the panel
	 */
	private JPanel getMovieOptionsTab() {
		double size[][] = // Columns
		{
			{ space, TableLayout.PREFERRED, 80, TableLayout.PREFERRED, 80, TableLayout.FILL, space },
			//Rows
			{ space,
				TableLayout.PREFERRED,	// Resolution
				space,
				TableLayout.PREFERRED,	// Bitrate
				space,
				TableLayout.PREFERRED,	// Framerate
				space,
				TableLayout.FILL
			}
		};
		
		JPanel panel = new JPanel( new TableLayout( size ) );
		loc.setPrefix( "gui.visualization.createMovieDialog." );
		
		// Create resolution panel
		txtWidth = new JTextField();
		txtHeight = new JTextField();
		panel.add( new JLabel( loc.getString( "resolution" ) + ": " ), "1,1" );
		panel.add( txtWidth, "2,1" );
		panel.add( new JLabel( " x" ), "3,1" );
		panel.add( txtHeight, "4,1" );

		txtBitrate = new JTextField();
		panel.add( new JLabel( loc.getString( "bitrate" ) + ": " ), "1,3");
		panel.add( txtBitrate, "2,3" );
		panel.add( new JLabel( " kb" ), "3,3" );
		lblEstimatedFilesize = new JLabel();
		panel.add( lblEstimatedFilesize, "4,3,5,3" );

		txtFramerate = new JTextField();
		txtFramerate.setPreferredSize( new Dimension( 80, txtFramerate.getPreferredSize().height ) );
		panel.add( new JLabel( loc.getString( "framerate" ) + ": " ), "1,5" );
		panel.add( txtFramerate, "2,5" );
		panel.add( new JLabel( " " + loc.getString( "framesPerSecond" ) ), "4,5,5,5" );

		txtBitrate.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				lblEstimatedFilesize.setText( " (" + Helper.bitToMaxFilesizeUnit( estimatedTime * 1000 * getBitrate() ) + ")" );
			}
		});
		txtBitrate.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e ) { }

			public void focusLost( FocusEvent e ) {
				lblEstimatedFilesize.setText( " (" + Helper.bitToMaxFilesizeUnit( estimatedTime * 1000 * getBitrate() ) + ")" );
			}
		});
		
		loc.setPrefix( "" );
		return panel;
	}
	
	/**
	 * Returns a panel inserted as a tab in the tab control containing some
	 * components to specify the movie path and the names for the images and
	 * the movie itself.
	 * @return the panel
	 */
	private JPanel getMoviePathTab() {
		JPanel panel = new JPanel();
		panel.add( new JLabel( "Die Pfade stellen sie bitte im Menü 'Extras | Optionen' ein." ) );
		return panel;
	}

	/**
	 * Returns the return value of the dialog to distinguish weather the "OK" or
	 * "Cancel" button was selected.
	 * @return the return value of the dialog.
	 */
	public int getRetVal() {
		return retVal;
	}
	
/*****************************************************************************
 *                                                                           *
 * Getter and Setter for the general movie recording parameter               *
 *                                                                           *
 ****************************************************************************/

	/**
	 * Indicates wheather frames should be saved.
	 * @return true, if frames should be created instead of a video
	 */
	public boolean isFrameMode() {
		return optFrames.isSelected();
	}
	
	/**
	 * Indicates wheather a movie should be saved.
	 * @return true, if a movie should be created instead of frames
	 */
	public boolean isMovieMode() {
		return optMovie.isSelected();
	}
	
	/**
	 * Indicates wheather frames should be deleted after a video is created.
	 * @return true if the frames should be deleted
	 */
	public boolean isDeleteFrames() {
		return chkDeleteFrames.isSelected();
	}
	
	/**
	 * Returns the selected movie format as {@link MovieFormat} enumeration.
	 * @return the movie format
	 */
	public MovieFormat getMovieFormat() {
		return (MovieFormat) cbxMovieFormat.getSelectedItem();
	}

	/**
	 * Returns the selected image format as {@link ImageFormat} enumeration.
	 * @return the image format
	 */
	public ImageFormat getFrameFormat() {
		return (ImageFormat) cbxFrameFormat.getSelectedItem();
	}

/*****************************************************************************
 *                                                                           *
 * Getter and Setter for Option Panel Values                                 *
 *                                                                           *
 ****************************************************************************/

	/**
	 * Returns the bitrate from the video options in kilobits.
	 * @return the bitrate from the video options
	 */
	public int getBitrate() {
		return( Integer.parseInt( "0" + txtBitrate.getText() ) );
	}

	/**
	 * Sets the bitrate displayed in the video options in kilobits.
	 * @param bitrate the bitrate
	 */
	public void setBitrate( int bitrate ) {
		txtBitrate.setText( Integer.toString( bitrate) );
		lblEstimatedFilesize.setText( " (" + Helper.bitToMaxFilesizeUnit( estimatedTime * 1000 * getBitrate() ) + ")" );
	}

	/**
	 * Returns the framerate from the video options in frames per seconds.
	 * @return the framerate from the video options
	 */
	public int getFramerate() {
		return( Integer.parseInt( "0" + txtFramerate.getText() ) );
	}
	
	/**
	 * Sets the framerate displayed in the video options in frames per second
	 * @param framerate the framerate
	 */
	public void setFramerate( int framerate ) {
		txtFramerate.setText( Integer.toString( framerate ) );
	}
	
	/**
	 * Returns the resolution from the video options in pixels.
	 * @return the resolution from the video options
	 */
	public Dimension getResolution() {
		return new Dimension( Integer.parseInt( "0" + txtWidth.getText() ), Integer.parseInt( "0" + txtHeight.getText() ) );
	}
	
	/**
	 * Sets the resolution displayed in the video options in pixels.
	 * @param resolution the resolution
	 */
	public void setResolution( Dimension resolution ) {
		setResolution( resolution.width, resolution.height );
	}
	
	/**
	 * Sets the resolution displayed in the video options in pixels.
	 * @param width the video width
	 * @param height the video height
	 */
	public void setResolution( int width, int height ) {
		txtWidth.setText( Integer.toString( width ) );
		txtHeight.setText( Integer.toString( height ) );
	}

/*****************************************************************************
 *                                                                           *
 * Other stuff, not necessary used as movie parameter.                       *
 *                                                                           *
 ****************************************************************************/
/**
 * Returns the estimated video time that was used for the calculation of the
 * filesize.
 * @return the estimated video time.
 */
 public double getEstimatedTime() {
		return estimatedTime;
	}

	/**
	 * Returns the currently selected movie writer.
	 * @return the currently selected movie writer
	 */
	public MovieWriter getMovieWriter() {
		return mw.getWriter();
	}

	/**
	* Sets the estimated video time used to calculate the filesize.
	* @param estimatedTime the estimated time in seconds
	*/
	public void setEstimatedTime( double estimatedTime ) {
		this.estimatedTime = estimatedTime;
	}

/*****************************************************************************
 *                                                                           *
 * Action listener                                                           *
 *                                                                           *
 ****************************************************************************/
	/** The listener for the OK and Cancel buttons. */	
	ActionListener aclFinish = new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "ok" )) {
				retVal = OK;
				setVisible( false );
			} else if( e.getActionCommand().equals( "cancel" ) ) {
				retVal = CANCEL;
				setVisible( false );
			} else {
				JEditor.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
			}
		}
	};
	/** A listener that enables or disables the elements depending of the radio button status. */
	ActionListener aclRadio = new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "frame" )) {
				optMovie.setSelected( false );
				cbxMovieFormat.setEnabled( false );
				chkDeleteFrames.setEnabled( false );
				cbxFrameFormat.removeAllItems();
				for( ImageFormat f : EnumSet.allOf( ImageFormat.class ) )
					cbxFrameFormat.addItem( f );
			} else if( e.getActionCommand().equals( "movie" ) ) {
				optFrames.setSelected( false );
				cbxMovieFormat.setEnabled( true );
				chkDeleteFrames.setEnabled( true );
				cbxFrameFormat.removeAllItems();
				for( ImageFormat f : mw.getSupportedImageFormats() )
					cbxFrameFormat.addItem( f );
			} else {
				JEditor.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
			}
		}
	};
	
	/**
	* @param args the command line arguments
	*/
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
					VideoOptions dialog = new VideoOptions(new javax.swing.JFrame() );
					dialog.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosing(java.awt.event.WindowEvent e) {
							System.exit(0);
						}
					});
					dialog.setVisible(true);
					if( dialog.getRetVal() == OK )
						System.out.println( "OK" );
					else
						System.out.println( "CANCEL" );
					dialog.dispose();
			}
		});
	}
	
}
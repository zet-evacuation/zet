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
package de.tu_berlin.math.coga.components;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;
import org.zetool.common.util.units.BinaryUnits;
import org.zetool.common.util.Formatter;
import info.clearthought.layout.TableLayout;
import de.tu_berlin.coga.util.movies.MovieWriter;
import de.tu_berlin.coga.util.movies.ImageFormat;
import de.tu_berlin.coga.util.movies.MovieFormat;
import de.tu_berlin.coga.util.movies.MovieWriters;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.EnumSet;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.zetool.components.framework.Button;
import org.zetool.opengl.helper.TextureFontStrings;

/**
 * A dialog window allowing to set the options used to create a video. The
 * {@link ImageFormat}, {@link MovieFormat} and the {@link MovieWriter}
 * can be chosen, for the latter it is possible to set advanced options.
 * @author  Jan-Philipp Kappmeier
 */
public class JVideoOptionsDialog extends javax.swing.JDialog {
	public final static String COMPONENT_LOCALIZATION = "de.tu_berlin.math.coga.components.VODLocalization";
	private static final int COL_TEXT = 0;
	private static final int COL_Y = COL_TEXT + 1;
	private static final int COL_BOLD = COL_Y + 1;
	private static final int COL_LAST = COL_BOLD;
	/** The index of the tab with the advanced options for the writer. */
	private final int ADVANCED_TAB_INDEX = 3;
	/** The space between components and the border of the frames and the compontents. */
	private final int space = 10;
	/** The return value, cancel by default. */
	private int retVal = JOptionPane.CANCEL_OPTION;
	/** The localization class. */
	static final Localization loc = LocalizationManager.getManager().getLocalization( COMPONENT_LOCALIZATION );
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
	/**  The tab for specification of introduction getText of the movie. */
	private JPanel tabIntroText;
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
	/** The bit rate in kilobits per second. */
	private JTextField txtBitrate;
	/** The frame rate of the movie in frames per second. */
	private JTextField txtFramerate;
	/** A label displaying the approximated file size. */
	private JLabel lblEstimatedFilesize;
	/** The selected movie writer. */
	private MovieWriters mw = MovieWriters.FFmpeg;
	/** The estimated time of the video that is to be encoded. Used to calculate size. */
	private double estimatedTime = 0;
	/** The path where videos are stored. */
	private JTextField txtMoviePath;
	private JList lstTextSets;
	private JButton btnAddTextSet;
	private IntroTextTableModel tablemodel;
	private JTable textTable;
	private JButton btnAddText;
	private ArrayList<TextureFontStrings> tfs = new ArrayList<>();
	private TextureFontStrings tfsCurrent;
	final DefaultListModel listModel = new DefaultListModel();

	/**
	 * Creates the form {@code VideoOptions} and sets the position to
	 * centered.
	 * @param owner the parent window that owns this window as child
	 */
	public JVideoOptionsDialog( java.awt.Frame owner ) {
		super( owner, true );
		setDefaultCloseOperation( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
		setTitle( loc.getString( "JVideoOptionsDialog.title" ) );
		setResizable( false );
		initComponents();
		if( owner != null )
			setLocation( owner.getX() + (owner.getWidth() - getWidth()) / 2, owner.getY() + (owner.getHeight() - getWidth()) / 2 );
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
		tabIntroText = getIntroTextTab();

		loc.setPrefix( "JVideoOptionsDialog." );
		tabbedPane.addTab( loc.getString( "tabVideoRecord" ), null, tabMovieRecord, loc.getString( "tabVideoRecordTooltip" ) );
		tabbedPane.addTab( loc.getString( "tabVideoOptions" ), null, tabMovieOptions, loc.getString( "tabVideoOptionsTooltip" ) );
		tabbedPane.addTab( loc.getString( "tabVideoPath" ), null, tabMoviePath, loc.getString( "tabVideoPathTooltip" ) );
		tabbedPane.addTab( loc.getString( "tabExtended" ), null, mw.getWriter().getAdvancedConfigurationPanel(), loc.getString( "tabExtendedTooltip" ) );
		tabbedPane.addTab( loc.getString( "tabIntro" ), null, tabIntroText, loc.getString( "tabIntroTooltip" ) );
		loc.setPrefix( "" );

		btnCancel = Button.newButton( loc.getString( "General.OK" ), aclFinish, "ok", loc.getString( "General.OK.tooltip" ) );
		btnOK = Button.newButton( loc.getString( "General.Cancel" ), aclFinish, "cancel", loc.getString( "General.Cancel.tooltip" ) );

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
			{space, 2 * space, TableLayout.PREFERRED, space, TableLayout.FILL, space},
			//Rows
			{space,
				TableLayout.PREFERRED, // Wrapper/Writer
				space,
				TableLayout.PREFERRED, // Option Images
				space / 2,
				TableLayout.PREFERRED, // Image Format
				space,
				TableLayout.PREFERRED, // Option Video
				space / 2,
				TableLayout.PREFERRED, // Video Format
				space,
				TableLayout.PREFERRED, // Checkbox Delete Images
				space
			}
		};

		JPanel panel = new JPanel( new TableLayout( size ) );
		loc.setPrefix( "JVideoOptionsDialog." );

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
			@Override
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
					tabbedPane.insertTab( loc.getString( "JVideoOptionsDialog.tabExtended" ), null, mw.getWriter().getAdvancedConfigurationPanel(), loc.getString( "JVideoOptionsDialog.tabVideoOptionsTooltip" ), ADVANCED_TAB_INDEX );
				}
			}
		} );
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
			{space, TableLayout.PREFERRED, 80, TableLayout.PREFERRED, 80, TableLayout.FILL, space},
			//Rows
			{space,
				TableLayout.PREFERRED, // Resolution
				space,
				TableLayout.PREFERRED, // Bitrate
				space,
				TableLayout.PREFERRED, // Framerate
				space,
				TableLayout.FILL
			}
		};

		JPanel panel = new JPanel( new TableLayout( size ) );
		loc.setPrefix( "JVideoOptionsDialog." );

		// Create resolution panel
		txtWidth = new JTextField();
		txtHeight = new JTextField();
		panel.add( new JLabel( loc.getString( "resolution" ) + ": " ), "1,1" );
		panel.add( txtWidth, "2,1" );
		panel.add( new JLabel( " x" ), "3,1" );
		panel.add( txtHeight, "4,1" );

		txtBitrate = new JTextField();
		panel.add( new JLabel( loc.getString( "bitrate" ) + ": " ), "1,3" );
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
			@Override
			public void actionPerformed( ActionEvent e ) {
				lblEstimatedFilesize.setText( " (" + Formatter.formatUnit( estimatedTime * 1000 * getBitrate(), BinaryUnits.Bit ) + ")" );
			}
		} );
		txtBitrate.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {}

			@Override
			public void focusLost( FocusEvent e ) {
				lblEstimatedFilesize.setText( " (" + Formatter.formatUnit( estimatedTime * 1000 * getBitrate(), BinaryUnits.Bit ) + ")" );
			}
		} );

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
		double size[][] = {
			// Columns
			{space, TableLayout.FILL, space},
			//Rows
			{space,
				TableLayout.PREFERRED, // Label
				space,
				TableLayout.PREFERRED, // Text box
				space,
				TableLayout.FILL, // Button
				space,}
		};

		JPanel panel = new JPanel( new TableLayout( size ) );

		JLabel label = new JLabel( loc.getString( "JVideoOptionsDialog.moviePath" ) );
		
		txtMoviePath = new JTextField( "./" );
		panel.add( label, "1,1" );
		panel.add( txtMoviePath, "1,3" );
		return panel;
	}

	/**
	 * Returns a panel inserted as a tab in the tab control containing some
	 * components to specify getText displayed as copyright information before
	 * the video starts.
	 * @return the panel
	 */
	private JPanel getIntroTextTab() {
		double size[][] = {
			// Columns
			// Auswahl der Seite -- Tabelle --> default x-pos, button
			{space, 80, space, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED, space},
			//Rows
			// Tabelle -- Buttonreihe
			{space,
				TableLayout.FILL, // Tabelle
				space,
				TableLayout.PREFERRED, // Button
				space,}
		};

		JPanel panel = new JPanel( new TableLayout( size ) );
		loc.setPrefix( "JVideoOptionsDialog." );

		lstTextSets = new JList( listModel );
		lstTextSets.addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged( ListSelectionEvent e ) {
				tfsCurrent = tfs.get( e.getFirstIndex() );
				textTable.repaint();
			}
		} );
		panel.add( lstTextSets, "1,1,1,1" );

		btnAddTextSet = Button.newButton( loc.getString( "addTextSet" ), aclIntroText, "addTextSet", loc.getString( "addTextSet.tooltip" ) );
		panel.add( btnAddTextSet, "1,3,1,3" );

		tablemodel = new IntroTextTableModel();
		textTable = new JTable( tablemodel );
		panel.add( textTable, "3,1,5,1" );

		btnAddText = Button.newButton( loc.getString( "addText" ), aclIntroText, "addText", loc.getString( "addText.tooltip" ) );
		panel.add( btnAddText, "5,3,5,3" );
		btnAddText.setEnabled( !tfs.isEmpty());

		loc.setPrefix( "" );
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
	 * Indicates whether frames should be saved.
	 * @return true, if frames should be created instead of a video
	 */
	public boolean isFrameMode() {
		return optFrames.isSelected();
	}

	/**
	 * Indicates whether a movie should be saved.
	 * @return true, if a movie should be created instead of frames
	 */
	public boolean isMovieMode() {
		return optMovie.isSelected();
	}

	/**
	 * Indicates whether frames should be deleted after a video is created.
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
		return (MovieFormat)cbxMovieFormat.getSelectedItem();
	}

	/**
	 * Returns the selected image format as {@link ImageFormat} enumeration.
	 * @return the image format
	 */
	public ImageFormat getFrameFormat() {
		return (ImageFormat)cbxFrameFormat.getSelectedItem();
	}

	/*****************************************************************************
	 *                                                                           *
	 * Getter and Setter for Option Panel Values                                 *
	 *                                                                           *
	 ****************************************************************************/
	/**
	 * Returns the bit rate from the video options in kilobits.
	 * @return the bit rate from the video options
	 */
	public int getBitrate() {
		return (Integer.parseInt( "0" + txtBitrate.getText() ));
	}

	/**
	 * Sets the bit rate displayed in the video options in kilobits.
	 * @param bitrate the bit rate
	 */
	public void setBitrate( int bitrate ) {
		txtBitrate.setText( Integer.toString( bitrate ) );
		lblEstimatedFilesize.setText( " (" + Formatter.formatUnit( estimatedTime * 1000 * getBitrate(), BinaryUnits.Bit ) + ")" );
	}

	/**
	 * Returns the frame rate from the video options in frames per seconds.
	 * @return the frame rate from the video options
	 */
	public int getFramerate() {
		return (Integer.parseInt( "0" + txtFramerate.getText() ));
	}

	/**
	 * Sets the frame rate displayed in the video options in frames per second
	 * @param framerate the frame rate
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
	 * file size.
	 * @return the estimated video time.
	 */
	public double getEstimatedTime() {
		return estimatedTime;
	}

	/**
	 * Sets the estimated video time used to calculate the file size.
	 * @param estimatedTime the estimated time in seconds
	 */
	public void setEstimatedTime( double estimatedTime ) {
		this.estimatedTime = estimatedTime;
	}

	/**
	 * Returns the currently selected movie writer.
	 * @return the currently selected movie writer
	 */
	public MovieWriter getMovieWriter() {
		return mw.getWriter();
	}

	/**
	 * Returns the path where the movies are stored. The result can be any string
	 * and should be checked.
	 */
	public String getMoviePath() {
		return txtMoviePath.getText();
	}
	
	/**
	 * Sets the text for the movie path. This is only used as a string, no checks
	 * are performed.
	 * @param moviePath the path 
	 */
	public void setMoviePath( String moviePath ) {
		txtMoviePath.setText( moviePath );
	}
	
	/**
	 * Returns the set of texts for the intro.
	 * @return the set of texts for the intro
	 */
	public ArrayList<TextureFontStrings> getTextureFontStrings() {
		return tfs;
	}

	/**
	 * Sets a set of texts for the intro.
	 * @param tfs the set of texts
	 */
	public void setTextureFontStrings( ArrayList<TextureFontStrings> tfs ) {
		this.tfs = tfs;
		btnAddText.setEnabled( !tfs.isEmpty());
		for( int i = 1; i <= tfs.size(); i++ )
			listModel.addElement( "JVideoOptionsDialog.introductionPage" + i );
	}
	/*****************************************************************************
	 *                                                                           *
	 * Action listener                                                           *
	 *                                                                           *
	 ****************************************************************************/
	/** The listener for the OK and Cancel buttons. */
	ActionListener aclFinish = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			switch( e.getActionCommand() ) {
				case "ok":
					retVal = JOptionPane.OK_OPTION;
					setVisible( false );
					break;
				case "cancel":
					retVal = JOptionPane.CANCEL_OPTION;
					setVisible( false );
					break;
				default:
					throw new IllegalStateException( "Unknown command: " + e.getActionCommand() + " in aclFinish." );
			}
		}
	};
	/** A listener that enables or disables the elements depending of the radio button status. */
	ActionListener aclRadio = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			switch( e.getActionCommand() ) {
				case "frame":
					optMovie.setSelected( false );
					cbxMovieFormat.setEnabled( false );
					chkDeleteFrames.setEnabled( false );
					cbxFrameFormat.removeAllItems();
					for( ImageFormat f : EnumSet.allOf( ImageFormat.class ) )
						cbxFrameFormat.addItem( f );
					break;
				case "movie":
					optFrames.setSelected( false );
					cbxMovieFormat.setEnabled( true );
					chkDeleteFrames.setEnabled( true );
					cbxFrameFormat.removeAllItems();
					for( ImageFormat f : mw.getSupportedImageFormats() )
						cbxFrameFormat.addItem( f );
					break;
				default:
					throw new IllegalStateException( "Unknown command: " + e.getActionCommand() + " in aclRadio." );
			}
		}
	};
	/** A listener that handles adding of texts in intro getText tab. */
	ActionListener aclIntroText = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			switch( e.getActionCommand() ) {
				case "addText":
					tfsCurrent.add( "New line", false, 0 );
					textTable.repaint();
					break;
				case "addTextSet":
					tfsCurrent = new TextureFontStrings( true );
					tfs.add( tfsCurrent );
					btnAddText.setEnabled( true );
					listModel.addElement( "Introseite " + (lstTextSets.getComponentCount() + 1) );
					break;
				default:
					throw new IllegalStateException( "Unknown command: " + e.getActionCommand() + " in aclIntroText." );
			}
		}
	};

	private class IntroTextTableModel extends AbstractTableModel {
		@Override
		public Class getColumnClass( int column ) {
			switch( column ) {
				case COL_TEXT:
					return String.class;
				case COL_Y:
					return Double.class;
				case COL_BOLD:
					return Boolean.class;
				default:
					return null;
			}
		}

		@Override
		public String getColumnName( int column ) {
			switch( column ) {
				case COL_TEXT:
					return loc.getStringWithoutPrefix( "JVideoOptionsDialog.textTable.text" );
				case COL_Y:
					return loc.getStringWithoutPrefix( "JVideoOptionsDialog.textTable.y" );
				case COL_BOLD:
					return loc.getStringWithoutPrefix( "JVideoOptionsDialog.textTable.bold" );
				default:
					return null;
			}
		}

		@Override
		public int getRowCount() {
			return tfsCurrent == null ? 0 : tfsCurrent.size();
		}

		@Override
		public int getColumnCount() {
			return COL_LAST + 1;
		}

		@Override
		public Object getValueAt( int row, int column ) {
			switch( column ) {
				case COL_TEXT:
					return tfsCurrent.getText( row );
				case COL_Y:
					return tfsCurrent.getY( row );
				case COL_BOLD:
					return tfsCurrent.getBold( row );
				default:
					return null;
			}
		}

		@Override
		public boolean isCellEditable( int row, int column ) {
			switch( column ) {
				case COL_TEXT:
				case COL_Y:
				case COL_BOLD:
					return true;
				default:
					return true;
			}
		}

		@Override
		public void setValueAt( Object aValue, int row, int column ) {
			switch( column ) {
				case COL_TEXT:
					tfsCurrent.setText( row, (String)aValue );
					break;
				case COL_Y:
					tfsCurrent.setY( row, Double.parseDouble( aValue.toString() ) );
					break;
				case COL_BOLD:
					tfsCurrent.setBold( row, (Boolean)aValue );
				default:
					;
			}
		}
	}
}
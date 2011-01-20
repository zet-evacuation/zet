/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
/*
 * JPlaneImageProperties.java
 * Created on 16. April 2008, 17:53
 */

package gui.editor.planimage;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import gui.components.framework.Button;
import info.clearthought.layout.TableLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import zet.util.ConversionTools;

/**
 *
 * @author  Jan-Philipp Kappmeier
 */
public class JPlanImageProperties extends JComponent {
	public static final int OK = 0;
	public static final int CANCEL = 1;

	JDialog dialog;
	private int retVal = 0;
	JRadioButton optMeter;
	JRadioButton optMillies;
	BufferedImage image;
	int pixelCount = 1;
	int millimeterCount = 1;
	double meterCount = 0.001;
	private static final NumberFormat nfFloat = NumberFormat.getNumberInstance(
					DefaultLoc.getSingleton().getLocale() );
	private static final NumberFormat nfInteger = NumberFormat.getIntegerInstance(
					DefaultLoc.getSingleton().getLocale() );
	private JTextField txtPixel;
	private JTextField txtMillies;
	private Frame parent;
	private JTextField txtOffsetX;
	private JTextField txtOffsetY;
	int xOffset = 0;
	int yOffset = 0;
	double xOffsetMeter = 0;
	double yOffsetMeter = 0;
	double alpha = 0.5;
	private JTextField txtAlpha;
	

	/**
	 * Creates new form JPlaneImageProperties
	 */
	public JPlanImageProperties() {
		dialog = new JDialog();
	}
	
	/**
	 * Creates new form JPlaneImageProperties
	 * @param image 
	 */
	public JPlanImageProperties( BufferedImage image ) {
		dialog = new JDialog();
		this.image = image;
		//initComponents();
	}
	
	/**
	 * Initializes a general dialog. The {@code dialog} variable has to be
	 * initialized before {@code initDialog} is called.
	 */
	private void initDialog( String title ) {
    dialog.setDefaultCloseOperation( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
		dialog.setResizable( false );
		dialog.setTitle( title );
		dialog.setModal( true );
	}
	
	/**
	 * This method finally shows the initialized dialog and sets it to the correct
	 * position.
	 */
	private void finishDialog() {
    dialog.pack();
		dialog.setLocation( parent.getX() + ( parent.getWidth() - dialog.getWidth() ) / 2,
						parent.getY() + ( parent.getHeight() - dialog.getHeight() ) / 2 );
		dialog.setVisible( true );
	}
	
	/**
	 * Creates a dialog used if a new plan image is loaded. It allows to change
	 * the size.
	 */
  private void initLoadDialog() {
		initDialog( "Bildgröße auswählen..." );
		JPanel panel = new JPanel();
		final int space = 20;
		double size[][] = {
			// Columns
			{ space,
				60,
				TableLayout.PREFERRED,
				60,
				TableLayout.PREFERRED,
				space,
				TableLayout.FILL
			},
			//Rows
			{ space,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				space,
				TableLayout.PREFERRED,
				TableLayout.FILL
			}
		};
		panel.setLayout( new TableLayout( size ) );

		JLabel headline = new JLabel( "<html>Größe: <b>" + image.getWidth() + "</b> x <b>" +  image.getHeight() + "</b> Pixel</html>" );
		panel.add( headline, "1, 1, 4, 1, left, top");
		JLabel desc = new JLabel( "pixel entsprechen" );
		panel.add( desc, "2, 3");
		add( panel );
		txtPixel = new JTextField( "1" );
		txtPixel.setMaximumSize( new Dimension( 0, 0 ) );
		txtPixel.setPreferredSize( new Dimension( 0, 0 ) );
		panel.add( txtPixel, "1, 3");
		txtMillies = new JTextField( "1" );
		txtMillies.setPreferredSize( new Dimension( 0, 0 ) );
		panel.add( txtMillies, "3, 3");
		
		// Radio buttons
		JPanel radioPanel = new JPanel();
		double size3[][] = { {space, TableLayout.PREFERRED }, {TableLayout.PREFERRED, TableLayout.PREFERRED} };
		radioPanel.setLayout( new TableLayout( size3 ) );
		createRadioButtons();
		radioPanel.add( optMeter, "1, 0" );
		radioPanel.add( optMillies, "1, 1" );
		panel.add( radioPanel, "4, 2, 4, 4" );

		panel.add( getOKCancelPanel( "okResize" ), "2, 6, 4, 6, right, top" );

		dialog.add( panel );
		finishDialog();
  }

	/**
	 * Creates a dialog used to set an offset for the displayed image.
	 */
	private void initMoveDialog() {
		initDialog( "Position auswählen..." );
		JPanel panel = new JPanel();
		final int space = 20;
		double size[][] = {
			// Columns
			{ space,
				TableLayout.PREFERRED,
				space,
				60,
				space,
				TableLayout.PREFERRED,
				space,
				TableLayout.FILL
			},
			//Rows
			{ space,
				TableLayout.PREFERRED,
				space,
				TableLayout.PREFERRED,
				(int)(space*0.5),
				TableLayout.PREFERRED,
				space,
				TableLayout.PREFERRED,
				space,
				TableLayout.FILL
			}
		};
		panel.setLayout( new TableLayout( size ) );		
		
		// Add Radio-Buttons
		createRadioButtons();
		panel.add( optMeter, "3, 1" );
		panel.add( optMillies, "5, 1" );
		
		// Add Labels
		JLabel xPos = new JLabel( "x-Offset:" );
		panel.add( xPos, "1, 3" );
		JLabel yPos = new JLabel( "y-Offset:" );
		panel.add( yPos, "1, 5" );
		
		// Add text fields
		txtOffsetX = new JTextField( Integer.toString( xOffset ) );
		panel.add( txtOffsetX, "3, 3" );
		txtOffsetY = new JTextField( Integer.toString( yOffset ) );
		panel.add( txtOffsetY, "3, 5" );
		
		// Add OK/Cancel Buttons
		panel.add( getOKCancelPanel( "okMove" ), " 2, 7, 5, 7, right, top" );
		
		dialog.add( panel );
		finishDialog();
	}
	
	/**
	 * Creates a dialog used to set the alpha value for the image.
	 */
	private void initAlphaDialog() {
		initDialog( "Alphawert festlegen..." );
		JPanel panel = new JPanel();
		final int space = 20;
		double size[][] = {
			// Columns
			{ space,
				TableLayout.PREFERRED,
				space,
				60,
				space,
				TableLayout.FILL
			},
			//Rows
			{ space,
				TableLayout.PREFERRED,
				space,
				TableLayout.PREFERRED,
				space,
				TableLayout.FILL
			}
		};
		panel.setLayout( new TableLayout( size ) );		
		
		// Add Labels
		JLabel label = new JLabel( "Alphawert (0-1):" );
		panel.add( label, "1, 1" );
		
		// Add text fields
		txtAlpha = new JTextField( nfFloat.format( alpha ) );
		panel.add( txtAlpha, "3, 1" );
		
		// Add OK/Cancel Buttons
		panel.add( getOKCancelPanel( "okAlpha" ), "1, 3, 3, 3, right, top" );
		
		dialog.add( panel );
		finishDialog();
	}	
	
	ActionListener aclButton = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "okResize" ) ) {
				retVal = OK;
				if( optMeter.isSelected() ) {
					try {
						meterCount = nfFloat.parse( txtMillies.getText() ).doubleValue();
						meterCount = ConversionTools.roundScale3( meterCount );
						millimeterCount = ConversionTools.floatToInt( meterCount );
						pixelCount = nfInteger.parse( txtPixel.getText() ).intValue();
					} catch( ParseException ex ) {
						System.out.println( "Parse Exception in JPlaneImageProperties.java during double cast" );
						return;
					}
				} else {
					try {
						millimeterCount = nfInteger.parse( txtMillies.getText() ).intValue();
						meterCount = ConversionTools.toMeter( millimeterCount );
						pixelCount = nfInteger.parse( txtPixel.getText() ).intValue();
					} catch( ParseException ex ) {
						System.out.println( "Parse Exception in JPlaneImageProperties.java during double cast" );
						return;
					}
				}
				if( millimeterCount < 1 | pixelCount < 1 )
					return;
			} else if( e.getActionCommand().equals( "cancel" ) ) {
				retVal = CANCEL;
			} else if( e.getActionCommand().equals( "okMove" ) ) {
				retVal = OK;
				if( optMeter.isSelected() ) {
					try {
						xOffsetMeter = nfFloat.parse( txtOffsetX.getText() ).doubleValue();
						yOffsetMeter = nfFloat.parse( txtOffsetY.getText() ).doubleValue();
						xOffsetMeter = ConversionTools.roundScale3( xOffsetMeter );
						yOffsetMeter = ConversionTools.roundScale3( yOffsetMeter );
						xOffset = ConversionTools.floatToInt( xOffsetMeter );
						yOffset = ConversionTools.floatToInt( yOffsetMeter );
					} catch( ParseException ex ) {
						System.out.println( "Parse Exception in JPlaneImageProperties.java during double cast" );
						return;
					}
				} else {
					try {
						xOffset = nfInteger.parse( txtOffsetX.getText() ).intValue();
						yOffset = nfInteger.parse( txtOffsetY.getText() ).intValue();
						xOffsetMeter = ConversionTools.toMeter( xOffset );
						yOffsetMeter = ConversionTools.toMeter( yOffset );
					} catch( ParseException ex ) {
						System.out.println( "Parse Exception in JPlaneImageProperties.java during double cast" );
						return;
					}
				}
			} else if( e.getActionCommand().equals( "okAlpha" ) ) {
				retVal = OK;
				try {
					setAlpha( nfFloat.parse( txtAlpha.getText() ).doubleValue() );
				} catch( ParseException ex ) {
					System.out.println( "Parse Exception in JPlaneImageProperties.java during double cast" );
					return;
				}
			} else {
				JOptionPane.showMessageDialog( null,
								DefaultLoc.getSingleton().getString( "gui.ContactDeveloper" ),
								DefaultLoc.getSingleton().getString( "gui.UnknownCommand" ),
								JOptionPane.ERROR_MESSAGE );
				return;
			}
			dialog.setVisible( false );
			dialog.dispose();		
		}
	};
	ActionListener aclRadio = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "meter" ) ) {
				optMillies.setSelected( false );
				optMeter.setSelected( true );
			} else if( e.getActionCommand().equals( "millies" ) ) {
				optMeter.setSelected( false );
				optMillies.setSelected( true );
			} else {
				JOptionPane.showMessageDialog( null,
								DefaultLoc.getSingleton().getString( "gui.ContactDeveloper" ),
								DefaultLoc.getSingleton().getString( "gui.UnknownCommand" ),
								JOptionPane.ERROR_MESSAGE );
			}
		}
	};

	private void createRadioButtons() {
		optMeter = new JRadioButton( "Meter" );
		optMeter.addActionListener(aclRadio);
		optMeter.setActionCommand( "meter" );
		optMillies = new JRadioButton( "Millimeter", true );
		optMillies.addActionListener(aclRadio);
		optMillies.setActionCommand( "millies" );
	}
	
	private JPanel getOKCancelPanel( String okAction ) {
		// OK/Cancel buttons
		int space = 16;
		JPanel buttonPanel = new JPanel( );
		JButton btnOK = Button.newButton( DefaultLoc.getSingleton().getString( "gui.OK" ), aclButton, okAction  );
		JButton btnCancel = Button.newButton( DefaultLoc.getSingleton().getString( "gui.Cancel" ), aclButton, "cancel" );
		double size2[][] = { {TableLayout.PREFERRED, space, TableLayout.PREFERRED }, {TableLayout.PREFERRED} };
		buttonPanel.setLayout( new TableLayout( size2 ) );
		buttonPanel.add( btnOK, "0,0" );
		buttonPanel.add( btnCancel, "2,0" );
		return buttonPanel;
	}
	
	/**
	 * Initializes the {@link #dialog} so that it can be used to set the zoom
	 * for the image. After the dialog is created, it is shown (modal).
	 * It returns an integer value depending on the selected button.
	 * @param parent
	 * @return {@link #OK} or {@link #CANCEL}
	 */
	public int showPlanImageZoomDialog( Frame parent ) {
		if( image == null )
			throw new java.lang.IllegalStateException( "Image not set." );
		this.parent = parent;
		dialog = new JDialog( parent );
		initLoadDialog();
		
		return retVal;
	}
	
	/**
	 * Initializes the {@link #dialog} so that it can be used to set the position
	 * of the image. After the dialog is created, it is shown (modal).
	 * It returns an integer value depending on the selected button.
	 * @param parent
	 * @return {@link #OK} or {@link #CANCEL}
	 */
	public int showPlanMoveDialog( Frame parent ) {
		this.parent = parent;
		dialog = new JDialog( parent );
		initMoveDialog();
		
		return retVal;
	}
	
	/**
	 * Initializes the {@link #dialog} so that it can be used to set the alpha
	 * value for the image. After the dialog is created, it is shown (modal).
	 * It returns an integer value depending on the selected button.
	 * @param parent
	 * @return {@link #OK} or {@link #CANCEL}
	 */
	public int showPlanAlphaDialog( Frame parent ) {
		this.parent = parent;
		dialog = new JDialog( parent );
		initAlphaDialog();
		
		return retVal;
	}
	
	/**
	 * Returns the supposed size of the image in meters in proportion to the
	 * pixels.
	 * @return the meter-scaled width
	 */
	public double getMeterCount() {
		return meterCount;
	}

	/**
	 * Returns the supposed size of the image in millimeters in proportion to the
	 * pixels.
	 * @return the millimeter-scaled width
	 */
	public int getMillimeterCount() {
		return millimeterCount;
	}

	/**
	 * Returns the reference value for the size in pixels.
	 * @return the pixel
	 */
	public int getPixelCount() {
		return pixelCount;
	}

	public int getXOffset() {
		return xOffset;
	}

	public void setXOffset( int xOffset ) {
		this.xOffset = xOffset;
		xOffsetMeter = ConversionTools.toMeter( xOffset );
	}

	public double getXOffsetMeter() {
		return xOffsetMeter;
	}

	public void setXOffsetMeter( double xOffsetMeter ) {
		this.xOffsetMeter = ConversionTools.roundScale3( xOffsetMeter );
		xOffsetMeter = ConversionTools.floatToInt( xOffsetMeter );
	}

	public int getYOffset() {
		return yOffset;
	}

	public void setYOffset( int yOffset ) {
		this.yOffset = yOffset;
		yOffsetMeter = ConversionTools.toMeter( yOffset );
	}

	public double getYOffsetMeter() {
		return yOffsetMeter;
	}

	public void setYOffsetMeter( double yOffsetMeter ) {
		this.yOffsetMeter = ConversionTools.roundScale3( yOffsetMeter );
		yOffsetMeter = ConversionTools.floatToInt( yOffsetMeter );
	}

	/**
	 * Returns the currently set alpha value for the image.
	 * @return the alpha value
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * <p>Sets the alpha value for the image. The values shall be within 0 and 1,
	 * <p>Values less than 0 or greater than 1 are allowed, as this is only an
	 * input dialog and, for some reason, it may be necessary to have other
	 * values.</p>
	 * @param alpha the new alpha value
	 */
	public void setAlpha( double alpha ) {
		this.alpha = alpha;
	}
}

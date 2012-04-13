/**
 * RotateApp.java
 * Created: 15.12.2011, 16:43:25
 */
package math;

import de.tu_berlin.math.coga.math.Conversion;
import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class RotateApp extends JDialog {
	
	@SuppressWarnings( "serial" )
private class JMyComponent extends JComponent {
		double width = 200;
		double height = 100;
		double angle = 30;
		double p = 0.5;
		double r1;
		double r2;
		double r1Cor = 0;
		double r2Cor = 0;
		double pCor = 0;
		//double pc1 = 0;
			int[] x = new int[4];
			int[] y = new int[4];
		
		/**
		 * Is called whenever the window is redrawn on the screen
		 * @param g the graphics context of the component
		 */
		@Override
		public void paint( Graphics g ) {
			super.paint( g );
			
			compute();

			// Set up the java2d graphics
			Graphics2D g2d = (Graphics2D)g;

			// Set up good quality
			g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

			double lc = getWidth()/2-width/2;
			double bc = getHeight()/2+height/2;

			g2d.translate( lc, bc );

			// axis
			g2d.drawLine( 0, (int)-lc, 0, getHeight() );
			g2d.drawLine( (int)-bc, 0, getWidth(), 0);

			//rotate
			g2d.rotate( -angle*Conversion.ANGLE2DEG );
			g2d.setColor( Color.blue );
			g2d.drawLine( (int)(0+width), 0, (int)(0+width), (int)(0-height) );
			g2d.drawLine( 0, 0, (int)(0+width), 0);
			g2d.drawLine( 0, (int)(0-height), (int)(0+width), (int)(0-height) );
			g2d.drawLine( 0, 0, 0, (int)(0-height) );

			// fixpunkt
			x[0] = (int)width;
			y[0] = (int)(p*height);
			
			if( (r1Cor == 0) ) {
				x[1] = (int)(width-r1);
				y[1] = 0;
			} else {
				x[2] = (0); // point 2 and 1 change order in drawing if angle < 45 and in extreme case!
				y[2] = (int)(height-pCor);
			}
			
			if( r2Cor == 0) {
				x[3] = (int)(width-r2);
				y[3] = (int)height;
			} else {
				x[3] = (int)r1;
				y[3] = (int)(p*height+pCor);
			}

			if( r1Cor == 0 && r2Cor == 0 ) {
				x[2] = (int)(width-r2-r1);
				y[2] = (int)((1-p)*height);
			} else {
				if( angle < 45 ) {
					x[2] = 0;
					y[2] = (int)+pCor;
				} else {
					x[1] = (int)r2;
					y[1] = (int)(p*height-pCor);
				}
			}
			
			// draw the points
			g2d.setColor( Color.red );
			for( int i = 0; i < 4; ++i )
				g2d.fillOval( x[i]-3, -y[i]-3, 6, 6);

			// draw the lines
			g2d.setColor( Color.black );
			for( int i = 0; i < 4; ++i )
				g2d.drawLine( x[i], -y[i], x[(i+1)%4], -y[(i+1)%4]);
			
			// Fläche:
			double a;
			double b;
			if( Math.abs( y[0] - y[1] ) < 0.001 ) {
				System.out.println( "Case 1" );
				a = Math.abs(x[0] - x[1]);
				b = Math.abs(y[1]-y[2]);
			} else {
				System.out.println( "Case 2" );
				a = Math.abs(y[0] - y[1]);//throw new IllegalStateException();
				b = Math.abs(x[1]-x[2]);
			}
			//b = Math.abs(y[1]-y[3]);
			System.out.println( a*b);
		}		
		
		private void compute() {
			r1 = Math.tan( (angle)*Conversion.ANGLE2DEG ) * (p)*height;
			if( angle == 0 ) { // if angle = 0 -> r2 = infinity, leads to problems!
				r2Cor = width;
				pCor = 0;
				return;
			}
			
			r2 = Math.tan( (180-90-angle)*Conversion.ANGLE2DEG ) * (1-p)*height;
			
			if( width -r2 -r1 < 0 ) {
				double diff = Math.abs(width-r2-r1);
				if( angle < 45 ) {
					r2Cor = r2-diff;
					r1Cor = 0;
					pCor = Math.tan( angle*Conversion.ANGLE2DEG ) * (width-r1);
				} else {
					r1Cor = r1-diff;
					r2Cor = 0;
					pCor = Math.tan( (180-90-angle)*Conversion.ANGLE2DEG ) * (width-r2);
				}
			} else {
				r1Cor = r2Cor = 0;
			} // begin test
			
			double rectHeight ;
		}

		public void setAngle( double angle ) {
			this.angle = angle;
			repaint();
		}

		public void setP( double p ) {
			this.p = p;
			repaint();
		}
		
		
	}
	
	public RotateApp() {
//		parent = this;
		super((Frame)null,true);
		
		setSize( 650, 450 );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( (d.width - getSize().width) / 2, (d.height - getSize().height) / 2 );
		
		this.setLayout( new BorderLayout() );
		
		final int maxP = 1000;
		
		//add( ps, BorderLayout.CENTER );
		final JMyComponent my = new JMyComponent( );
		add( my, BorderLayout.CENTER );
		
		int space = 10;
		JPanel buttonPanel = new JPanel( );
		final JSlider sldAngle = new JSlider(0,90);
		final JSlider sldP = new JSlider(0,maxP);
		
		//JButton btnOK = Button.newButton( DefaultLoc.getSingleton().getString( "gui.OK" ), aclButton, "ok"  );
		//JButton btnCancel = Button.newButton( DefaultLoc.getSingleton().getString( "gui.Cancel" ), aclButton, "cancel" );
		double size2[][] = { {TableLayout.FILL, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space }, {space, TableLayout.PREFERRED, space } };
		buttonPanel.setLayout( new TableLayout( size2 ) );
		buttonPanel.add( sldAngle, "1,1" );
		buttonPanel.add( sldP, "3,1" );
		sldAngle.setValue( 30 );
		sldP.setValue( maxP/2 );
		
		sldP.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged( ChangeEvent e ) {
				my.setP( (1./maxP)*sldP.getValue() );
			}
		});
		
		sldAngle.addChangeListener( new ChangeListener() {

			@Override
			public void stateChanged( ChangeEvent e ) {
				my.setAngle( sldAngle.getValue() );
			}
		});
		add( buttonPanel, BorderLayout.SOUTH );
	}
	
	
	
	ActionListener aclButton = new ActionListener() {

		@Override
		public void actionPerformed( ActionEvent e ) {
			throw new UnsupportedOperationException( "Not supported yet." );
		}
	};
	
	
	public static void main( String[] args ) {
		RotateApp ra = new RotateApp();
		ra.setVisible( true );
		System.exit( 0 );
	}
}

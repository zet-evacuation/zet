/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.components;

import gui.GUIOptionManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.swing.UIManager;
import junit.framework.TestCase;
import org.zetool.opengl.helper.TextureFontStrings;
import org.junit.Test;

/**
 *
 * @author kapman
 */
public class VideoOptionsDialogTest extends TestCase {

	
	/**
	 * @param args the command line arguments
	 */
	@Test
	public void testDialogCreation() throws InterruptedException, InvocationTargetException {
		//java.awt.EventQueue.invokeAndWait( new Runnable() {
		//	public void run() {
		System.out.println( "Creating dialog." );
		// Change look and feel to native
		GUIOptionManager.changeLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		JVideoOptionsDialog dialog = new JVideoOptionsDialog( new javax.swing.JFrame() );

		ArrayList<TextureFontStrings> tfs = new ArrayList<>();
		TextureFontStrings tf = new TextureFontStrings( true );
		tf.add( "test", false, 100 );
		tf.add( "test1", false, 200 );
		tf.add( "test2", false, 300 );
		tf.add( "test3", false, 400 );
		tfs.add( tf );
		dialog.setTextureFontStrings( tfs );
		dialog.setLocation( 100, 100 );
		dialog.addWindowListener( new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing( java.awt.event.WindowEvent e ) {
				System.exit( 0 );
			}
		} );
		//dialog.setVisible( true );
		//if( dialog.getRetVal() == JOptionPane.OK_OPTION )
		//	System.out.println( "OK" );
		//else
		//	System.out.println( "CANCEL" );
		dialog.dispose();
		System.out.println( "disposed." );
	}
		//} );
	//}
}

package de.tu_berlin.math.coga.batch.gui.dialog;

import com.l2fprod.common.swing.JDirectoryChooser;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import static javax.swing.JOptionPane.getRootFrame;

/**
 *
 * @author Martin Groß
 * @author Jan-Philipp Kappmeier
 */
public class AddDirectoryWizard extends JDialog {

  private final static String title = "Choose directories ...";

  public AddDirectoryWizard( Component parent ) {
    super( getWindowForComponent( parent ), title, ModalityType.APPLICATION_MODAL );

    initComponents();

    final File iconFile = new File( "./icon.gif" );

    try {
      setIconImage( ImageIO.read( iconFile ) );
    } catch( IOException ex ) {
      ex.printStackTrace( System.err );
    }
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
    chooser = new JDirectoryChooser( System.getProperty( "user.dir" ) );
    chooser.setAutoscrolls( true );
    chooser.setControlButtonsAreShown( false );
    chooser.setShowingCreateDirectory( false );
    chooser.setUI( new WindowsDirectoryChooserUI( chooser ) );
    jPanel1.setLayout( new BorderLayout() );
    jPanel1.add( chooser, BorderLayout.CENTER );
    setLocationRelativeTo( parent );
    chooser.ensureFileIsVisible( chooser.getCurrentDirectory() );
  }

  /**
   * Copied from java api, as this method is somehow not public for whatever
   * reason. Returns the specified component's toplevel <code>Frame</code> or
   * <code>Dialog</code>.
   *
   * @param parentComponent the <code>Component</code> to check for a
   * <code>Frame</code> or <code>Dialog</code>
   * @return the <code>Frame</code> or <code>Dialog</code> that contains the
   * component, or the default frame if the component is <code>null</code>, or
   * does not have a valid <code>Frame</code> or <code>Dialog</code> parent
   * @exception HeadlessException if <code>GraphicsEnvironment.isHeadless</code>
   * returns <code>true</code>
   * @see java.awt.GraphicsEnvironment#isHeadless
   */
  static Window getWindowForComponent( Component parentComponent )
          throws HeadlessException {
    if( parentComponent == null ) {
      return getRootFrame();
    }
    if( parentComponent instanceof Frame || parentComponent instanceof Dialog ) {
      return (Window) parentComponent;
    }
    return getWindowForComponent( parentComponent.getParent() );
  }

  private JDirectoryChooser chooser;
  private boolean accepted;

  //@SuppressWarnings("unchecked")
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jButton1 = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();
    chkFollowLinks = new javax.swing.JCheckBox();
    chkRecursive = new javax.swing.JCheckBox();

        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    //setTitle("Choose directories...");
    //setIconImages(null);
    jPanel1.setBorder( javax.swing.BorderFactory.createTitledBorder( "Choose directories:" ) );

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout( jPanel1 );
    jPanel1.setLayout( jPanel1Layout );
    jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
            .addGap( 0, 390, Short.MAX_VALUE )
    );
    jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
            .addGap( 0, 224, Short.MAX_VALUE )
    );

    jButton1.setText( "OK" );
    jButton1.addActionListener( (java.awt.event.ActionEvent evt) -> {
      jButton1ActionPerformed( evt );
    } );

    jButton2.setText( "Cancel" );
    jButton2.addActionListener( (java.awt.event.ActionEvent evt) -> {
      jButton2ActionPerformed( evt );
    } );

    chkFollowLinks.setText( "Follow links" );
    chkFollowLinks.setToolTipText( "If this is checked, symbolic links to files and folder are resolved, otherwise they are ignored." );

    chkRecursive.setSelected( true );
    chkRecursive.setText( "Recurse" );
    chkRecursive.setToolTipText( "If this is checked, then subdirectories are automatically included." );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout( getContentPane() );
    getContentPane().setLayout( layout );
    layout.setHorizontalGroup(
            layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
            .addGroup( layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
                            .addComponent( chkRecursive )
                            .addComponent( chkFollowLinks )
                            .addGroup( layout.createSequentialGroup()
                                    .addComponent( jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE )
                                    .addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.UNRELATED )
                                    .addComponent( jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE ) ) )
                    .addContainerGap() )
            .addComponent( jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
    );
    layout.setVerticalGroup(
            layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
            .addGroup( javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addComponent( jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                    .addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED )
                    .addComponent( chkRecursive )
                    .addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED )
                    .addComponent( chkFollowLinks )
                    .addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.UNRELATED )
                    .addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.BASELINE )
                            .addComponent( jButton1 )
                            .addComponent( jButton2 ) )
                    .addContainerGap() )
    );

    pack();
  }

  private void jButton1ActionPerformed( java.awt.event.ActionEvent evt ) {
    accepted = true;
    setVisible( false );
    dispose();
  }

  private void jButton2ActionPerformed( java.awt.event.ActionEvent evt ) {
    accepted = false;
    setVisible( false );
    dispose();
  }

  /**
   * @param args the command line arguments
   */
  public static void main( String args[] ) {
    java.awt.EventQueue.invokeLater( new Runnable() {

      public void run() {

        new AddDirectoryWizard( null ).setVisible( true );
      }
    } );
  }

  private static javax.swing.JButton jButton1;
  private static javax.swing.JButton jButton2;
  private static javax.swing.JCheckBox chkFollowLinks;
  private static javax.swing.JCheckBox chkRecursive;
  private static javax.swing.JPanel jPanel1;

  public File[] getSelectedFiles() {
    return chooser.getSelectedFiles();
  }

  public boolean isAccepted() {
    return accepted;
  }

  public boolean isFollowingLinks() {
    return chkFollowLinks.isSelected();
  }

  public boolean isRecursive() {
    return chkRecursive.isSelected();
  }
}

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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
/*
 * JProgressBarDialog.java
 * Created on 24. Januar 2008, 23:35
 */

package gui.components.progress;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import batch.tasks.AlgorithmTask;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JProgressBarDialog extends javax.swing.JDialog {
	private Runnable task;
	private boolean autoClose = false;
	
	/** Creates new form JProgressBarDialog
	 * @param parent
	 * @param title 
	 * @param modal
	 * @param task 
	 */
	public JProgressBarDialog(java.awt.Frame parent, String title, boolean modal, Runnable task ) {
		super( parent, title, modal );
		initComponents();
		setLocation( parent.getX() + ( parent.getWidth() - this.getWidth() ) / 2, parent.getY() + ( parent.getHeight() - this.getHeight() ) / 2 );
		btnClose.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				if( !AlgorithmTask.getInstance().isDone() )
					AlgorithmTask.getInstance().cancel( true );
				dispose();
			}
		});
		lblTaskInformation.setText( " " );
		lblTaskDetailedInformation.setText( " " );
		lblTaskName.setText( " " );
		this.task = task;
	}

	public JProgressBarDialog(java.awt.Frame parent, String title, boolean modal, boolean autoClose, Runnable task ) {
		super( parent, title, modal );
		initComponents();
		setLocation( parent.getX() + ( parent.getWidth() - this.getWidth() ) / 2, parent.getY() + ( parent.getHeight() - this.getHeight() ) / 2 );
		btnClose.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				if( !AlgorithmTask.getInstance().isDone() )
					AlgorithmTask.getInstance().cancel( true );
				dispose();
			}
		});
		lblTaskInformation.setText( " " );
		lblTaskDetailedInformation.setText( " " );
		lblTaskName.setText( " " );
		this.task = task;
		this.autoClose = autoClose;
	}

	public void executeTask() {
		// Execute task
		AlgorithmTask worker = AlgorithmTask.getNewInstance();
		worker.setTask( task );
		worker.addPropertyChangeListener( pcl );
		try {
			worker.executeAlgorithm( true );
		} catch( Exception ex ) {
			System.out.println( "Fehler trat auf" );
		} finally { }
	}
	
	protected Runnable getTask() {
		return task;
	}
	
	protected void handleProgressEvent( int progress ) {
		progressBar.setValue( progress );
//		lblTaskInformation.setText( AlgorithmTask.getInstance().getProgressInformation() );
//		lblTaskDetailedInformation.setText( AlgorithmTask.getInstance().getDetailedProgressInformation() );
//		lblTaskName.setText( AlgorithmTask.getInstance().getName() );
		if( AlgorithmTask.getInstance().isDone() ) {
			btnClose.setText( "Schließen" );
			if( autoClose )
				setVisible( false );
		}
	}
	
	protected void setExecutionNumber( String text ) {
		lblTaskName.setText( text );
	}
	
	protected void setTaskInformation( String text ) {
		lblTaskDetailedInformation.setText( text );
	}
	
	protected void setTaskName( String text ) {
		lblTaskInformation.setText( text );
	}
	
	protected void setProgress( int progress ) {
		progressBar.setValue( progress );
	}

	protected PropertyChangeListener pcl = new PropertyChangeListener() {
		public void propertyChange( PropertyChangeEvent evt ) {
			if( evt.getPropertyName().equals( "progress" ) ) {
				int progress = (Integer)evt.getNewValue();
				handleProgressEvent( progress );
			}
		}
	};
	
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    lblTaskName = new javax.swing.JLabel();
    progressBar = new javax.swing.JProgressBar();
    lblTaskInformation = new javax.swing.JLabel();
    lblTaskDetailedInformation = new javax.swing.JLabel();
    btnClose = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    lblTaskName.setText("jLabel1");

    lblTaskInformation.setText("jLabel2");

    lblTaskDetailedInformation.setText("jLabel3");

    btnClose.setText("Abbrechen");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
              .addComponent(lblTaskName)
              .addComponent(lblTaskInformation)
              .addComponent(lblTaskDetailedInformation)))
          .addGroup(layout.createSequentialGroup()
            .addGap(156, 156, 156)
            .addComponent(btnClose)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lblTaskName)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(lblTaskInformation)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(lblTaskDetailedInformation)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnClose)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
		
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnClose;
  private javax.swing.JLabel lblTaskName;
  private javax.swing.JLabel lblTaskDetailedInformation;
  private javax.swing.JLabel lblTaskInformation;
  private javax.swing.JProgressBar progressBar;
  // End of variables declaration//GEN-END:variables
}

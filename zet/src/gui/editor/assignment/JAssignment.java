/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * JDistribution.java
 * Created on 16. Dezember 2007, 19:30
 */
package gui.editor.assignment;

import ds.Project;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import de.tu_berlin.math.coga.common.localization.Localization;

/**
 * Displays a dialoge that allows editing assignments and the associated
 * probability distributions.
 * @author Gordon Schlechter
 */
public class JAssignment extends JDialog {
	/**
	 * Creates a new instance of JDistribution
	 * @param owner
	 * @param p 
	 * @param title
	 * @param width
	 * @param height 
	 */
	public JAssignment( JFrame owner, Project p, String title, int width, int height ) {
		super( owner, title, true );

		getContentPane().setLayout( new BorderLayout() );
		getContentPane().add( new JAssignmentPanel( this, p ), BorderLayout.CENTER );

		// Add close button
		JPanel pnlButton = new JPanel (new BorderLayout ());
		JButton btnClose = new JButton (Localization.getInstance().getString( "gui.Quit" ));
		btnClose.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				JAssignment.this.setVisible( false );
			}
		});
		pnlButton.add (btnClose, BorderLayout.EAST);
		pnlButton.setBorder (new EmptyBorder (0,0,16,16));
		getContentPane ().add (pnlButton, BorderLayout.SOUTH);
		
		pack();
		
		setSize( width, height );
		setLocation( owner.getX () + ( owner.getWidth() - width ) / 2,
						owner.getY () + ( owner.getHeight() - height ) / 2 );
	}
}

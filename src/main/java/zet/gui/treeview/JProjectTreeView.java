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
package zet.gui.treeview;

import de.zet_evakuierung.model.Project;
import de.zet_evakuierung.model.Area;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.ZControl;
import de.zet_evakuierung.model.FloorInterface;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.zetool.components.framework.Menu;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JProjectTreeView extends JDialog implements TreeSelectionListener, MouseListener, ActionListener {
	private ProjectTreeModel projectTreeModel;
	private JTree tree;
	private Project project;
	private JPanel propertyPanel;
	private JTextArea text;
	private boolean popupTrigger = false;
	private JPopupMenu popUp;
	private ProjectTreeNode selectedNode;
	private ZControl zcontrol;

	public JProjectTreeView( JFrame owner, String title, int width, int height, ZControl zcontrol ) {
		super( owner, title, false );	// non-modal dialog window

		setSize( width, height );
		setLocation( owner.getX() + (owner.getWidth() - width) / 2, owner.getY() + (owner.getHeight() - height) / 2 );

		this.project = zcontrol.getProject();
		this.zcontrol = zcontrol;

		initModel();

		JSplitPane sp = new JSplitPane( JSplitPane.VERTICAL_SPLIT );

		tree = new JTree( projectTreeModel );
		JScrollPane scrollTree = new JScrollPane( tree );
		tree.getSelectionModel().addTreeSelectionListener( this );
		tree.addMouseListener( this );
		//this.add( scrollTree, BorderLayout.CENTER );
		sp.setLeftComponent( scrollTree );
		propertyPanel = new JPanel( new BorderLayout() );
		sp.setRightComponent( propertyPanel );
		text = new JTextArea();
		JScrollPane scrollText = new JScrollPane( text );
		propertyPanel.add( scrollText, BorderLayout.CENTER );
		add( sp );

		popUp = new JPopupMenu( "label" );
		Menu.addMenuItem( popUp, "Löschen", this, "deleteNode" );
	}

	private void initModel() {
		ProjectNode root = new ProjectNode( project );
		projectTreeModel = new ProjectTreeModel( root );
		// Iterate through the project.
		// building structure
		BuildingPlanNode buildingPlanNode = new BuildingPlanNode( project.getBuildingPlan() );
		root.add( buildingPlanNode );

		// Add floors, rooms, areas recursiveley
		for( FloorInterface floor : project.getBuildingPlan().getFloors() ) {
			FloorNode floorNode = new FloorNode( floor );
			buildingPlanNode.add( floorNode );
			for( Room room : floor ) {
				RoomNode roomNode = new RoomNode( room );
				floorNode.add( roomNode );
				for( Area area : room.getAreas() ) {
					AreaNode areaNode = new AreaNode( area );
					roomNode.add( areaNode );
				}
			}

		}
	}

	@Override
	public void valueChanged( TreeSelectionEvent e ) {
		TreePath path = e.getNewLeadSelectionPath();
		if( path == null )
			return;
		ProjectTreeNode node = (ProjectTreeNode)path.getLastPathComponent();
		text.setText( node.getInformationText() );
		//pack();
	}

	@Override
	public void mouseClicked( MouseEvent e ) { }

	/**
	 * {@inheritDoc }
	 * <p>Due to cross platform this method is used to check if a pop-up on a node
	 * should be shown. If a mouse is pressed using the pop-up button, this will
	 * already be set {@code true} if the button is pressed. The action only takes
	 * place when the button is released. </p>
	 * @param event the mouse event
	 */
	@Override
	public void mousePressed( MouseEvent event ) {
		if( event.isPopupTrigger() )
			popupTrigger = true;
	}

	/**
	 * Shows a pop-up menu for a node if the pup-up button is pressed (or was
	 * pressed already).
	 * @param evt the mouse event
	 */
	@Override
	public void mouseReleased( MouseEvent evt ) {
		if( evt.isPopupTrigger() || popupTrigger ) { // check for right mouse click
			popupTrigger = false;
			// select the element under the mosue. stop if mouse was not on a node
			TreePath selPath = this.tree.getPathForLocation( evt.getX(), evt.getY() );
			if( selPath == null )
				return;
			// find out the node
			selectedNode = (ProjectTreeNode) selPath.getLastPathComponent();
			// select the node
			TreeNode[] nodes = ((DefaultTreeModel)this.tree.getModel()).getPathToRoot( selectedNode );
			TreePath path = new TreePath( nodes );
			tree.makeVisible( path );
			tree.scrollPathToVisible( path );
			tree.setSelectionPath( path );
			// show pop-up
			popUp.show( evt.getComponent(), evt.getX(), evt.getY() );
		}
	}

	@Override
	public void mouseEntered( MouseEvent e ) { }

	@Override
	public void mouseExited( MouseEvent e ) { }

	@Override
	public void actionPerformed( ActionEvent e ) {
		System.out.println( selectedNode.toString() + " soll gelöscht werden." );
		if( selectedNode instanceof FloorNode ) {
			zcontrol.deleteFloor( ((FloorNode)selectedNode).zFormatData );
			projectTreeModel.removeNodeFromParent( selectedNode );
		} else if( selectedNode instanceof RoomNode ) {
			zcontrol.deletePolygon( ((RoomNode)selectedNode).zFormatData );
			projectTreeModel.removeNodeFromParent( selectedNode );
		} else if( selectedNode instanceof AreaNode ) {
			zcontrol.delete( ((AreaNode)selectedNode).zFormatData );
			projectTreeModel.removeNodeFromParent( selectedNode );
		} else {
			JOptionPane.showMessageDialog( this, "Ein Objekt vom Typ " + selectedNode.toString() + " kann nicht gelöscht werden.", "Fehler", JOptionPane.ERROR_MESSAGE );
		}
	}
}

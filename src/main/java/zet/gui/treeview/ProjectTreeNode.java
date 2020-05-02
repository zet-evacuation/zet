/**
 * ProjectTreeNode.java
 * Created: 21.01.2011, 10:42:17
 */
package zet.gui.treeview;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Overrides the user object such that a z format element is returned.
 * @author Jan-Philipp Kappmeier
 */
public abstract class ProjectTreeNode<T> extends DefaultMutableTreeNode {

	T zFormatData;

	public ProjectTreeNode( T zFormatData ) {
		super( zFormatData );
		this.zFormatData = zFormatData;
	}

	@Override
	public T getUserObject() {
		return zFormatData;
	}

	public T getzFormatData() {
		return zFormatData;
	}

	public abstract String getInformationText();
}

/**
 * ProjectNode.java
 * Created: 21.01.2011, 11:06:53
 */
package zet.gui.treeview;

import de.tu_berlin.coga.zet.model.Project;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ProjectNode extends ProjectTreeNode<Project> {

	public ProjectNode( Project zFormatData ) {
		super( zFormatData );
	}

	@Override
	public String getInformationText() {
		StringBuilder sb = new StringBuilder();
		sb.append( zFormatData.getName() + "\n" );
		sb.append( zFormatData.getProjectFile().toString() + "\n" );
		return sb.toString();
	}

	@Override
	public String toString() {
		return "Projekt";
	}
}

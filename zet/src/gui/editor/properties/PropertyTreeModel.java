/**
 * Class TreeModel.java
 * Erstellt 21.02.2008, 23:36:35
 */
package gui.editor.properties;

import gui.editor.properties.converter.PropertyTreeConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Martin Groß, Jan-Philipp Kappmeier
 */
@XStreamAlias( "zp" )
@XStreamConverter( PropertyTreeConverter.class )
public class PropertyTreeModel extends DefaultTreeModel {
	private String propertyName = "";

  public PropertyTreeModel( PropertyTreeNode root ) {
    super( root );
  }

	public String getPropertyName() {
		return propertyName == null ? "" : propertyName;
	}

	public void setPropertyName( String propertyName ) {
		this.propertyName = propertyName;
	}

  @Override
  public PropertyTreeNode getRoot() {
    return( PropertyTreeNode ) root;
  }
}

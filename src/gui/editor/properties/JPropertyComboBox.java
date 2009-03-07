/**
 * Class JPropertyComboBox
 * Erstellt 03.07.2008, 23:59:56
 */

package gui.editor.properties;

import java.io.File;
import javax.swing.JComboBox;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JPropertyComboBox extends JComboBox {
	PropertyFilesSelectionModel model;
	
	public JPropertyComboBox( ) {
		super( PropertyFilesSelectionModel.getInstance () );
		model = (PropertyFilesSelectionModel)getModel();
	}

	public File getSelectedFile() {
		if( getSelectedIndex() < 0 )
			return null;
		return model.getFile( getSelectedIndex() );
	}
}

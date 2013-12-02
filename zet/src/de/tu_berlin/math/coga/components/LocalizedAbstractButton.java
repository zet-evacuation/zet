package de.tu_berlin.math.coga.components;

import de.tu_berlin.math.coga.common.localization.Localized;
import de.tu_berlin.math.coga.components.framework.Menu;
import javax.swing.AbstractButton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class LocalizedAbstractButton implements Localized {
	private final AbstractButton toLocalize;
	private final String locString;

	public LocalizedAbstractButton( AbstractButton guiObject, String locString ) {
		this.toLocalize = guiObject;
		this.locString = locString;
		//localize();
	}
	
	@Override
	public final void localize() {
		String text = Menu.getLocalization().getString( locString );
		toLocalize.setText( Menu.extractMnemonic( text ) );
		toLocalize.setMnemonic( Menu.getMnemonic( text ) );
	}
}

package de.tu_berlin.math.coga.components;

import org.zetool.common.localization.Localized;
import de.tu_berlin.math.coga.components.framework.Menu;
import java.util.ArrayList;
import javax.swing.AbstractButton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Localizer {
	private volatile static Localizer instance;
	private ArrayList<Localized> localized = new ArrayList<>();
	
	private Localizer() {
	}
	
	public static Localizer instance() {
		if( instance == null )
			synchronized( Localizer.class ) {
				// this is needed if two threads are waiting at the monitor at the
				// time when singleton was getting instantiated
				if( instance == null )
					instance = new Localizer();
			}
		return instance;
	}
	
	public <T extends Localized> T registerNewComponent( T t ) {
		localized.add( t );
		return t;
	}
	
	public <T extends AbstractButton> T registerNewComponent(T t, String locString ) {
		LocalizedAbstractButton abs = new LocalizedAbstractButton( t, locString );
		registerNewComponent( abs );
		return t;
	}

	public void updateLocalization() {
		for( Localized l : localized )
			l.localize();
	}
	
private class LocalizedAbstractButton implements Localized {
		private final AbstractButton toLocalize;
		private final String locString;

		private LocalizedAbstractButton( AbstractButton guiObject, String locString ) {
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

}

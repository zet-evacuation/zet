/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.gui.main.tabs.editor;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;
import org.zetool.common.localization.Localized;
import de.zet_evakuierung.model.ZControl;
import gui.GUIControl;
import info.clearthought.layout.TableLayout;
import java.text.NumberFormat;
import javax.swing.JPanel;
import zet.gui.GUILocalization;

/**
 *
 * @param <U> The displayed object
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings("serial")
public abstract class JInformationPanel<U> extends JPanel implements Localized {
	/** The localization class. */
	protected Localization loc;
	protected static NumberFormat nfFloat = LocalizationManager.getManager().getFloatConverter();
	protected static NumberFormat nfInteger = LocalizationManager.getManager().getIntegerConverter();
	/** The control object for the loaded project. */
	protected ZControl projectControl;
	protected GUIControl guiControl;
	protected U current;

	public JInformationPanel() {
		this( new double[] {TableLayout.FILL} );
	}
	
	protected JInformationPanel( double[] rows ) {
		this( columns(), rows );
	}

	protected JInformationPanel( double[] columns, double[] rows ) {
		super( new TableLayout( columns, rows ) );
		loc = GUILocalization.loc;
	}
	
	private static double[] columns() {
		return new double[] {10, TableLayout.FILL, 10};
	}
	public void setControl(  ZControl control, GUIControl guiControl ) {
		this.projectControl = control;
		this.guiControl = guiControl;		
	}

	public void update( U current ) {
		this.current = current;
		update();
	}

	public abstract void update();
	
	@Override
	public void localize() {
	
	}
}

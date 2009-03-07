/**
 * Class JIndividualCell
 * Erstellt 23.04.2008, 11:39:56
 */

package gui.ca;

import gui.components.AbstractFloor;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.NumberFormat;
import localization.Localization;

/**
 * The swing component <code>JIndividualCell</code> represents an individual of
 * the {@link CellularAutomaton} in the quick on-line visualization. It has
 * a green color and has some information about the Individual.
 * @author Jan-Philipp Kappmeier
 */
public class JIndividualCell extends JCellPolygon {
	Individual individual;
	private static final NumberFormat nfFloat = NumberFormat.getNumberInstance( Localization.getInstance().getLocale() );
	private CellularAutomaton ca;
	private boolean showIndividualNames;

	public JIndividualCell( AbstractFloor floor, Individual individual, CellularAutomaton ca) {
		super( floor, Color.green );
		this.individual = individual;
		this.ca = ca;
		setToolTipText();
		showIndividualNames = PropertyContainer.getInstance().getAsBoolean( "editor.options.cavis.showIndividualNames" );
	}

	public JIndividualCell( AbstractFloor floor, Color lineColor, Individual individual, CellularAutomaton ca ) {
		super( floor, Color.green, lineColor);
		this.individual = individual;
		this.ca = ca;
		setToolTipText();
		showIndividualNames = PropertyContainer.getInstance().getAsBoolean( "editor.options.cavis.showIndividualNames" );
	}
	
	@Override
	protected void setToolTipText() {
		String s = "<html>";
		s += "Alter: " + Integer.toString( individual.getAge() ) + "<br>";
		nfFloat.setMaximumFractionDigits( 2 );
		s += "Wunschgeschwindigkeit: " + nfFloat.format( ca.absoluteSpeed( individual.getMaxSpeed() ) ) + "m/s";
		s += "</html>";
		setToolTipText( s );
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		if( !showIndividualNames )
			return;
		Graphics2D g2 = (Graphics2D) g;
		drawName( Integer.toString( individual.getNumber() ), g2, Color.black );
	}

}

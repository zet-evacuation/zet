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
package algo.ca.rule;

import localization.Localization;
import ds.ca.CAController;
import algo.ca.parameter.ParameterSet;
import ds.ca.Cell;

/**
 * @author Daniel Plümpe
 */
public abstract class AbstractRule implements Rule {
	private CAController caController;
	protected ParameterSet parameters;

	protected CAController caController() {
		return caController;
	}

	@Override
	public boolean executableOn( Cell cell ) {
		return (cell.getIndividual() != null);
	}

	@Override
	final public void execute( Cell cell ) {
		if( !executableOn( cell ) )
			return;

		onExecute( cell );
	}

	abstract protected void onExecute( Cell cell );

	public void setCAController( CAController caController ) {
		if( this.caController != null )
			throw new RuntimeException( Localization.getInstance().getString( "algo.ca.rule.RuleAlreadyHaveCAControllerException" ) );

		if( caController == null )
			throw new RuntimeException( Localization.getInstance().getString( "algo.ca.rule.CAControllerIsNullException" ) );

		this.caController = caController;
		this.parameters = caController.getParameterSet();
	}
}

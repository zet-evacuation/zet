package algo.ca.rule;

import localization.Localization;
import ds.ca.CAController;
import algo.ca.parameter.ParameterSet;
import ds.ca.Cell;

/**
 * @author Daniel Pluempe
 *
 */
public abstract class AbstractRule implements Rule {

	private CAController caController;
	protected ParameterSet parameters;

	protected CAController caController() {
		return caController;
	}

	//@Override
	//abstract public boolean executableOn( Cell cell );
	@Override
	public boolean executableOn( Cell cell ) {
		return ( cell.getIndividual() != null );
	}

	@Override
	final public void execute( Cell cell ) {
		if( !executableOn( cell ) ) {
			return;
		}

		onExecute( cell );
	}

	abstract protected void onExecute( Cell cell );

	public void setCAController( CAController caController ) {
		if( this.caController != null ) {
			throw new RuntimeException( Localization.getInstance().getString( "algo.ca.rule.RuleAlreadyHaveCAControllerException" ) );
		}

		if( caController == null ) {
			throw new RuntimeException( Localization.getInstance().getString( "algo.ca.rule.CAControllerIsNullException" ) );
		}

		this.caController = caController;
		this.parameters = caController.getParameterSet();
	}
}

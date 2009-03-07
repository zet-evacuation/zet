/*
 * Created on 23.01.2008
 */
package algo.ca;

import java.util.Iterator;
import algo.ca.rule.Rule;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * <p>The abstract base class for rule sets. A <code>RuleSet</code> basically is
 * a container for {@link Rule} objects. The rules fall into two different
 * types: the initialization rules and the loop rules. </p>
 * <p>When a new instance is created, it should load the rules itself,
 * so all child classes need to implement the  method {@code selfInit()}. It
 * is intended to load the rules out of the {@link PropertyContainer}, but this
 * can easyly be omitted. Nevertheless <code>selfInit()</code> needs to be
 * overwritten at least with an empty method.</p>
 * <p>As the objects are divided in two parts, a <code>RuleSet</code> provides
 * three different iterators: one that iterates through all known rules, one
 * iterating the initialization rules and a third one iterating the loop rules.
 * </p>
 * @author Jan-Philipp Kappmeier
 */
public abstract class RuleSet implements Iterable<Rule> {
	/** The <code>ArrayList</code> containing all rules in only one instance. */
	private ArrayList<Rule>allRules;
	/** The <code>ArrayList</code> containing all initialization rules, maybe twice or more often. */
	private ArrayList<Rule>primaryRules;
	/** The <code>ArrayList</code> containing all loop rules, maybe twice or more often. */
	private ArrayList<Rule>loopRules;

	/**
	 * Creates a new instance of <code>RuleSet</code> and initializes the
	 * container. The abstract method {@link selfInit()} is called, that should
	 * load all neccessary rules.
	 */
	public RuleSet() {
		allRules = new ArrayList<Rule>();
		primaryRules = new ArrayList<Rule>();
		loopRules = new ArrayList<Rule>();
		selfInit();
	}
	
	/**
	 * Returns an <code>Iterator</code> that iterates through all known rules. All
	 * rules are only contained once.
	 * @return the iterator
	 */
	@Override
	public Iterator<Rule> iterator() {
		return allRules.iterator();
	}
	
	/**
	 * Adds a new {@link Rule} to both, the initialization list and the loop list.
	 * @param rule the rule
	 */
	public void add( Rule rule ) {
		if( !allRules.add( rule ) )
			allRules.add( rule );		
		primaryRules.add( rule );
		loopRules.add( rule );
	}

	/**
	 * Adds a new {@link Rule} only to the specified lists. In any case the rule
	 * is added to the list of all used rules.
	 * @param rule the rule that is to be inserted
	 * @param useInPrimarySet true if the rule should be added to the primary set
	 * @param useInLoopSet true if the rule should be added to the loop set
	 * @throws java.lang.IllegalArgumentException if two movement rules are inserted
	 */
	public void add( Rule rule, boolean useInPrimarySet, boolean useInLoopSet  ) throws IllegalArgumentException{
		if( !allRules.add( rule ) )
			allRules.add( rule );
		if( useInPrimarySet )
			primaryRules.add( rule );
		if( useInLoopSet ) //&& !(rule instanceof MovementRule) )
			loopRules.add( rule );
	}
	
	/**
	 * Creates a new instance of the {@link Rule} interface. The object has the
	 * specified type and is created using the default constructor, thus the
	 * <code>Rule</code> shall have at least this public constructor.
	 * @param ruleName the classname of the rule without the classpath "algo.ca.rule"
	 * @return the new instace
	 */
	public static Rule createRule( String ruleName ) {
		Class<?> ruleClass = null;
		Rule rule = null;
		try {
			ruleClass = Class.forName( "algo.ca.rule." + ruleName );
			if( util.DebugFlags.RULESET )
				System.out.println( "Rule " + ruleName + " wird geladen." );
			rule = (Rule) ruleClass.getConstructor().newInstance();
		} catch( ClassNotFoundException e ) {
			e.printStackTrace();
		} catch( NoSuchMethodException e ) {
			e.printStackTrace();
		} catch( InstantiationException e ) {
			e.printStackTrace();
		} catch( IllegalAccessException e ) {
			e.printStackTrace();
		} catch( IllegalArgumentException e ) {
			e.printStackTrace();
		} catch( InvocationTargetException e ) {
			e.printStackTrace();
		}
		return rule;
	}

	/**
	 * Creates a new instance of <code>RuleSet</code> of a specified class. The
	 * default constructor is called, thus the rule set shall have at least this
	 * public constructor.
	 * @param ruleSetName the classname of the rule set without the classpath "algo.ca"
	 * @return the new instance
	 */
	public static RuleSet createRuleSet( String ruleSetName ) {
		Class<?> ruleSetClass = null;
		RuleSet ruleSet = null;
		try {
			ruleSetClass = Class.forName( "algo.ca." + ruleSetName );
			ruleSet = (RuleSet) ruleSetClass.getConstructor().newInstance();
		} catch( ClassNotFoundException e ) {
			e.printStackTrace();
		} catch( NoSuchMethodException e ) {
			e.printStackTrace();
		} catch( InstantiationException e ) {
			e.printStackTrace();
		} catch( IllegalAccessException e ) {
			e.printStackTrace();
		} catch( IllegalArgumentException e ) {
			e.printStackTrace();
		} catch( InvocationTargetException e ) {
			e.printStackTrace();
		}
		return ruleSet;
	}
	
	/**
	 * Returns an <code>Iterator</code> that iterates through the initialization
	 * rules. These rules can be added twice or more often.
	 * @return the iterator
	 */
	public Iterator<Rule> loopIterator() {
		return loopRules.iterator();
	}
	
	/**
	 * Returns an <code>Iterator</code> that iterates through the loop rules.
	 * These rules can be added twice or more often.
	 * @return the iterator
	 */
	public Iterator<Rule> primaryIterator() {
		return primaryRules.iterator();
	}
	
	/**
	 * Performs the initialization. This method is called by the constructor and
	 * is supposed to load the rules contained in the <code>RuleSet</code>
	 */
	protected abstract void selfInit();
}
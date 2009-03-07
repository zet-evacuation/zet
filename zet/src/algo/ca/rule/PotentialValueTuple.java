package algo.ca.rule;

import ds.ca.StaticPotential;

/**
 * This class defines a tuple
 * of StaticPotentials and the Individuals distance from that ExitCell, 
 * to which the StaticPotentials refers. This class/tuple implements the
 * interface Comparable in order to sort a collection of tuples by their
 * distance to the ExitCell, to which the StaticPotential refers.
 * @author marcel
 *
 */
public class PotentialValueTuple implements Comparable<PotentialValueTuple>
{
	/**
	 * the Individuals distance from that ExitCell, 
	 * to which the StaticPotentials refers
	 */
	private int lengthOfWay;
	
	/**
	 * The StaticPotential of the tuple.
	 */
	private StaticPotential staticPotential;
	
	public PotentialValueTuple(int loW, StaticPotential sp)
	{
		this.lengthOfWay = loW;
		this.staticPotential = sp;
	}
	
	/**
	 * Returns the lenthOfWay attribute.
	 * @return The lenthOfWay attribute.
	 */
	public int getLengthOfWay()
	{
		return this.lengthOfWay;
	}
	
	/**
	 * Returns the StaticPotential attribute.
	 * @return The StaticPotential attribute.
	 */
	public StaticPotential getStaticPotential()
	{
		return this.staticPotential;
	}
	
	public int compareTo(PotentialValueTuple t)
	{
		if (t.getLengthOfWay() == this.getLengthOfWay())
			return 0;
		else if (t.getLengthOfWay() < this.getLengthOfWay())
			return 1;
		else
			return -1;
	}
}
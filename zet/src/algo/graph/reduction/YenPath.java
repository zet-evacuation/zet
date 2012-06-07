/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.reduction;

import ds.graph.Node;
import java.util.List;
import java.util.Vector;


/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 673 $
 * @latest $Date: 2009-02-05 01:19:18 -0700 (Thu, 05 Feb 2009) $
 */
public class YenPath
{
	List<Node> _vertex_list = new Vector<>();
	double _weight = -1;
	
	public YenPath(){};
	
	public YenPath(List<Node> _vertex_list, double _weight)
	{
		this._vertex_list = _vertex_list;
		this._weight = _weight;
	}

	public double get_weight()
	{
		return _weight;
	}
	
	public void set_weight(double weight)
	{
		_weight = weight;
	}
	
	public List<Node> get_vertices()
	{
		return _vertex_list;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object right)
	{
		if(right instanceof YenPath)
		{
			YenPath r_path = (YenPath) right;
			return _vertex_list.equals(r_path._vertex_list);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return _vertex_list.hashCode();
	}
	
	public String toString()
	{
		return _vertex_list.toString()+":"+_weight;
	}
}
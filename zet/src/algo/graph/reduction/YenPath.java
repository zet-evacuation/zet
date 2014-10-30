package algo.graph.reduction;

import de.tu_berlin.coga.graph.Node;
import java.util.List;
import java.util.Vector;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 673 $
 * @latest $Date: 2009-02-05 01:19:18 -0700 (Thu, 05 Feb 2009) $
 */
public class YenPath {
  List<Node> nodeList = new Vector<>(); // TODO: update data structure
  double weight = -1;
  double capacity = -1;

  public YenPath() {
  }

  ;

	public YenPath( List<Node> nodeList, double weight, double capacity ) {
    this.nodeList = nodeList;
    this.weight = weight;
    this.capacity = capacity;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight( double weight ) {
    this.weight = weight;
  }

  public double getCapacity() {
    return capacity;
  }

  public void setCapacity( double capac ) {
    capacity = capac;
  }

  public List<Node> getNodes() {
    return nodeList;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object object ) {
    if( object instanceof YenPath ) {
      YenPath path = (YenPath)object;
      return nodeList.equals( path.nodeList );
    }
    return false;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return nodeList.hashCode();
  }

  @Override
  public String toString() {
    return nodeList.toString() + ":" + weight + ":" + capacity;
  }
}

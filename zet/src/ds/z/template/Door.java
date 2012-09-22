/**
 * Door.java
 * Created: 20.09.2012, 17:06:55
 */
package ds.z.template;

import java.util.Objects;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Door {
	String name;
	int size;
	double priority;

	public Door( String name, int size, double priority ) {
		this.name = name;
		this.size = size;
		this.priority = priority;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 41 * hash + Objects.hashCode( this.name );
		hash = 41 * hash + this.size;
		hash = 41 * hash + (int)(Double.doubleToLongBits( this.priority ) ^ (Double.doubleToLongBits( this.priority ) >>> 32));
		return hash;
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		final Door other = (Door)obj;
		if( !Objects.equals( this.name, other.name ) )
			return false;
		if( this.size != other.size )
			return false;
		if( Double.doubleToLongBits( this.priority ) != Double.doubleToLongBits( other.priority ) )
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public double getPriority() {
		return priority;
	}
	
	
	
	
}

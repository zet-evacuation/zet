/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package opengl.framework.abs;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @author Daniel Pluempe
 *
 * @param <T> The type of the graphic object that is controlled by this class
 * @param <U> The type of the object in the data structure (the model) that is controlled by this class
 * @param <V> The type of the visualization results that will displayed by the graphic objects associated with this class  
 * @param <W> The type of the child graphic objects
 * @param <X> The type of the child control objects
 */
public abstract class AbstractControl<T extends Drawable, U, V extends VisualizationResult, W extends Drawable, X extends AbstractControl<W, ?, ?, ?, ?, ?>, Y extends Controlable> implements control, Iterable<X> {

	private V visResult;
	private T drawable;
	private U controlled;
	
	protected ArrayList<X> childControls;
	
	protected Y mainControl;

	public AbstractControl( U controlled, V visResult, Y mainControl ) {
		this.controlled = controlled;
		this.visResult = visResult;
		this.childControls = new ArrayList<X>();
		this.mainControl = mainControl;
	}

	protected void setView( T view ) {
		this.drawable = view;
	}

	public T getView() {
		return drawable;
	}

	public ArrayList<X> getChildControls() {
		return childControls;
	}

	/**
	 * Returns the controlled model object
	 * @return the model object
	 */
	public U getControlled() {
		return controlled;
	}
	// TODO evtl. protected... mal sehen
	
	protected void add(X childControl) {
		childControls.add(childControl);
	}
	
	protected void clear() {
		childControls.clear();
	}

	public final Y getMainControl() {
		return mainControl; 
	}

	public Iterator<X> iterator() {
		return childControls.iterator();
	}
	
    public Iterator<X> fullIterator(){
        return iterator();
    }

    public V getVisResult(){
        return visResult;
    }
    
    public int size(){
        return childControls.size();
    }
}


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
 * @author Daniel Plümpe, Jan-Philipp Kappmeier
 *
 * @param <T> The type of the graphic object that is view by this class
 * @param <U> The type of the object in the data structure (the model) that is view by this class
 * @param <V> The type of the visualization results that will displayed by the graphic objects associated with this class  
 * @param <W> The type of the child graphic objects
 * @param <X> The type of the child control objects
 */
//public abstract class AbstractControl<T extends Drawable, U, V extends VisualizationResult, W extends Drawable, X extends AbstractControl<W, ?, ?, ?, ?, ?>, Y extends Controlable> implements control, Iterable<X> {
public abstract class AbstractControl<T extends AbstractControl<?, ?>, U extends AbstractDrawable<?, ?>> implements control, Iterable<T> {

	protected U view;
	
	protected ArrayList<T> childControls;
	

	/**
	 * No view is set. During construction process, a view must be set! Call
	 * {@link #setView(U)}.
	 */
	protected AbstractControl() {
		this.childControls = new ArrayList<T>();
	}

	public AbstractControl( U controlled ) {
		this.view = controlled;
		this.childControls = new ArrayList<T>();
	}

	protected void setView( U view ) {
		this.view = view;
	}

	public U getView() {
		return view;
	}

	public ArrayList<T> getChildControls() {
		return childControls;
	}
	
	protected void add(T childControl) {
		childControls.add( childControl );
	}
	
	protected void clear() {
		childControls.clear();
	}

	public Iterator<T> iterator() {
		return childControls.iterator();
	}
	
    public Iterator<T> fullIterator(){
        return iterator();
    }

    public int size(){
        return childControls.size();
    }
}

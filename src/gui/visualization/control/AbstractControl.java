package gui.visualization.control;

import io.visualization.VisualizationResult;
import java.util.ArrayList;
import java.util.Iterator;
import opengl.framework.abs.drawable;

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
public abstract class AbstractControl<T extends drawable, U, V extends VisualizationResult, W extends drawable, X extends AbstractControl<W, ?, ?, ?, ?>> implements control, Iterable<X> {

	private V visResult;
	private T drawable;
	private U controlled;
	
	protected ArrayList<X> childControls;
	
	protected GLControl mainControl;

	public AbstractControl( U controlled, V visResult, GLControl mainControl ) {
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

	/**
	 * Returns the controlled model object
	 * @return the model object
	 */
	protected U getControlled() {
		return controlled;
	}
	
	protected void add(X childControl) {
		childControls.add(childControl);
	}
	
	protected void clear() {
		childControls.clear();
	}

	public final GLControl getMainControl() {
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


package opengl.framework.abs;

import gui.visualization.control.AbstractControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import opengl.drawingutils.GLVector;

/**
 * 
 * @author Jan-Philipp Kapmeier, Daniel Plümpe
 *
 * @param <T> The type of the object used for culling with this object
 * @param <U> The type of the children view object
 * @param <V> The type of the associated control object
 * @param <W> The type of the associated children control object
 */
public abstract class AbstractDrawable<U extends AbstractDrawable<?, ?, ?>, V extends AbstractControl<?, ?, ?, U, W>, W extends AbstractControl<U, ?, ?, ?, ?>> implements drawable {
//public abstract class AbstractDrawable<T extends CullingShape, U extends AbstractDrawable<?,?,?,?>, V extends AbstractControl<?, ?, ?, U, W>, W extends AbstractControl<U,?,?,?,?>> implements drawable {

	//private CullingTester tester;
	protected static GLU glu = new GLU();
	protected static GLUquadric quadObj = glu.gluNewQuadric();
	protected static int individualDisplayMode = GLU.GLU_FILL;
	//private boolean isInvalid;
	protected V control;
	protected int displayList = 0;
	protected boolean repaint = true;
	protected boolean callChildren = true;
	protected GLVector position = new GLVector();

	public AbstractDrawable( V control ) {
//	public AbstractDrawable(V control, T cullingShape ) {
		this.control = control;
		update();
	}

	public V getControl() {
		return control;
	}

	public boolean isVisible() {
		return true;
	}

//	public CullingTester getTester() {
//		return tester;
//	}

//	public void setTester( CullingTester val ) {
//		this.tester = val;
//	}
	/**
	 * Calls {@link #draw( GLAutoDrawable) } for all contained objects.
	 * @param drawable
	 */
	public void drawAllChildren( GLAutoDrawable drawable ) {
		for( W child : control ) {
			child.getView().draw( drawable );
		}
	}

	public void staticDrawAllChildren( GLAutoDrawable drawable ) {
		for( W child : control ) {
			child.getView().performStaticDrawing( drawable );
		}
	}

	@Override
	final public void draw( GLAutoDrawable drawable ) {
		if( !callChildren ) {
			performDrawing( drawable );
			return;
		}
		beginDraw( drawable );
		performDrawing( drawable );
		endDraw( drawable );
	}

	/**
	 * This method is called prior to performing the actual 
	 * drawing. In its default behavior, it translates the
	 * <code>AbstractDrawable</code> to the origin.
	 * @param drawable
	 */
	public void beginDraw( GLAutoDrawable drawable ) {
		drawable.getGL().glPushMatrix();
		position.translate( drawable );
	}

	/**
	 * This method is called after the drawing has been performed.
	 * In its default behavior, it tranlates the <code>AbstractDrawable</code>
	 * back to where it has been.
	 * @param drawable
	 */
	public void endDraw( GLAutoDrawable drawable ) {
		drawable.getGL().glPopMatrix();
	}

	public void performDrawing( GLAutoDrawable drawable ) {
		drawAllChildren( drawable );
	}

	public void performStaticDrawing( GLAutoDrawable drawable ) {
	}

	public abstract void update();
}


/*
 * BspMain.java
 * Created: 14.11.2010, 19:15:29
 */
package opengl.bsp;

import de.tu_berlin.math.coga.math.vectormath.Plane;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import opengl.framework.JFlyingEyePanel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BspMain extends JFlyingEyePanel {

	static int printMode = 2;


	static List<Plane> planes;
	//static List<Vector3> vertices;
	static List<Vector3> faceNormals;
	static int drawmode = 2;
	static Vector3 eye;
	static BspTree _bsp;
	//static Queue<Triangle> model;
	static Queue<Triangle> originalList;
	static Queue<Triangle> copiedList;
	static BspMain instance;

	// current rotation of object
	float _rotObjectX = 0.0f;
	float _rotObjectY = 0.0f;

	double[] modelview = new double[16];
	double[] modelviewInv = new double[16];
	DoubleBuffer modelviewBuf = DoubleBuffer.wrap( modelview );
	DoubleBuffer modelviewInvBuf = DoubleBuffer.wrap( modelviewInv );
	static DynamicTriangleMesh mesh;

	public BspMain() {
		super();
		//vertices = new ArrayList<Vector3>();
		//planes = new ArrayList<Plane>();
		//faceNormals = new ArrayList<Vector3>();
	}

	@Override
	public void initGFX( GLAutoDrawable drawable ) {
		// Use debug pipeline
		// drawable.setGL(new DebugGL(drawable.getGL()));

		GL gl = drawable.getGL();
		System.err.println("INIT GL IS: " + gl.getClass().getName());

		// Enable VSync
		gl.setSwapInterval(1);

		// Setup the drawing area and shading mode
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.


		// add some light
		float light_position[] = { 5.0f, 5.0f, 5.0f, 0.0f };
		float LightDiffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float LightAmbient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
		gl.glLightfv( GL.GL_LIGHT0, GL.GL_AMBIENT, LightAmbient, 0 );
		gl.glLightfv( GL.GL_LIGHT0, GL.GL_DIFFUSE, LightDiffuse, 0 );
		gl.glLightfv( GL.GL_LIGHT0, GL.GL_POSITION, light_position, 0 );
		gl.glShadeModel( GL.GL_SMOOTH );
		gl.glEnable( GL.GL_LIGHT0 );

		//gl.glClearColor( 1.0f, 1.0f, 1.0f, 0.0f );
		gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
		gl.glDisable( GL.GL_DEPTH_TEST );
		gl.glDisable( GL.GL_BLEND );

		eye = new Vector3();
	}

	@Override
	public void updateViewport( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		GL gl = drawable.getGL();
		if( glu == null )
			glu = new GLU();

		if (height <= 0) // avoid a divide by zero error!
				height = 1;
		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, h, 1.0, 200.0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void display( GLAutoDrawable drawable ) {
		gl = drawable.getGL();
		if( drawmode == 2 ) // draw ploygons
			gl.glEnable( GL.GL_LIGHTING );
		else
			gl.glDisable( GL.GL_LIGHTING );

		gl.glClear( GL.GL_COLOR_BUFFER_BIT );
		gl.glBlendFunc( GL.GL_ZERO, GL.GL_SRC_COLOR );

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();
		if( glu == null )
			glu = new GLU();
		glu.gluLookAt( 0, 0, +100, 0.0, 0.0, 0, 0.0, 1.0, 0.0 );

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glColor3f(1, 0, 0); gl.glVertex3f(-1,-1, 0);
		gl.glColor3f(0, 0, 1); gl.glVertex3f( 1,-1, 0);
		gl.glColor3f(0, 1, 0); gl.glVertex3f( 0, 1, 0);
		gl.glEnd();

			_rotObjectY += 0.66;
			//_rotObjectY += 0.1;

		gl.glRotatef( _rotObjectX, 1.0f, 0.0f, 0.0f );
		gl.glRotatef( _rotObjectY, 0.0f, 1.0f, 0.0f );

		/** compute viewer position */
		gl.glGetDoublev( GL.GL_MODELVIEW_MATRIX, modelviewBuf );
		invert( modelview, modelviewInv );
		double[] origin = { 0, 0, 0, 1 };
		double[] e = new double[4];
		multMatrixVector( modelviewInv, origin, e );

		switch( printMode ) {
			case 0:
				draw( copiedList, Sign.Zero );
				break;
			case 1:
				draw( originalList, Sign.Zero );
				break;
			case 2:
			if( _bsp != null ) {
				eye.x = e[0];
				eye.y = e[1];
				eye.z = e[2];
				//BspTree.traverse( _bsp );
				traverse( _bsp );
				//draw( model, Sign.Zero );
			}
				break;
		}

		//glutSwapBuffers();
		gl.glFlush();
	}

	/**
	 * Loads the identity matrix to the given array in OpenGL-notion
	 * @param m
	 */
	void identity( double[] m ) {
		if( m.length != 16 )
			throw new IllegalArgumentException( "m must be of length 16" );
    m[0+4*0] = 1; m[0+4*1] = 0; m[0+4*2] = 0; m[0+4*3] = 0;
    m[1+4*0] = 0; m[1+4*1] = 1; m[1+4*2] = 0; m[1+4*3] = 0;
    m[2+4*0] = 0; m[2+4*1] = 0; m[2+4*2] = 1; m[2+4*3] = 0;
    m[3+4*0] = 0; m[3+4*1] = 0; m[3+4*2] = 0; m[3+4*3] = 1;
	}

	boolean invert( double[] src, double[] inverse ) {
		if( src.length != 16 || inverse.length != 16 )
			throw new IllegalArgumentException( "matrices must be of length 16" );

		double t;
		int i, j, k, swap;
		double[][] tmp = new double[4][4];

		identity( inverse );

		for( i = 0; i < 4; i++ )
			for( j = 0; j < 4; j++ )
				tmp[i][j] = src[i * 4 + j];

		for( i = 0; i < 4; i++ ) {
			/* look for largest element in column. */
			swap = i;
			for( j = i + 1; j < 4; j++ )
				if( Math.abs( tmp[j][i] ) > Math.abs( tmp[i][i] ) )
					swap = j;

			if( swap != i )
				/* swap rows. */
				for( k = 0; k < 4; k++ ) {
					t = tmp[i][k];
					tmp[i][k] = tmp[swap][k];
					tmp[swap][k] = t;

					t = inverse[i * 4 + k];
					inverse[i * 4 + k] = inverse[swap * 4 + k];
					inverse[swap * 4 + k] = t;
				}

			if( tmp[i][i] == 0 )
				/* no non-zero pivot.  the matrix is singular, which
				shouldn't happen.  This means the user gave us a bad
				matrix. */
				return false;

			t = tmp[i][i];
			for( k = 0; k < 4; k++ ) {
				tmp[i][k] /= t;
				inverse[i * 4 + k] /= t;
			}
			for( j = 0; j < 4; j++ )
				if( j != i ) {
					t = tmp[j][i];
					for( k = 0; k < 4; k++ ) {
						tmp[j][k] -= tmp[i][k] * t;
						inverse[j * 4 + k] -= inverse[i * 4 + k] * t;
					}
				}
		}
		return true;
	}

	void multMatrixVector( double[] m, double[] v, double[] result ) {
		if( m.length != 16 )
			throw new IllegalArgumentException( "m must be of length 16" );
		if( v.length != 4 || result.length != 4 )
			throw new IllegalArgumentException( "v, result must be of length 4" );

		result[0] = m[0] * v[0] + m[4] * v[1] + m[8] * v[2] + m[12] * v[3];
		result[1] = m[1] * v[0] + m[5] * v[1] + m[9] * v[2] + m[13] * v[3];
		result[2] = m[2] * v[0] + m[6] * v[1] + m[10] * v[2] + m[14] * v[3];
		result[3] = m[3] * v[0] + m[7] * v[1] + m[11] * v[2] + m[15] * v[3];
	}
	
	void draw( Queue<Triangle> triangles, Sign sign ) {

		if( triangles == null )
			return;

		// TODO draw triangles in list such that hidden lines are drawn
		// differently from non-hidden lines


		// FIXME sign is ignored, that can't be right??!!!!

		// don't draw hidden edges
		if( drawmode == 0 ) {
			// the drawing is done twice

			// first draw black filled polygons two remove edges underneath
			gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );
			gl.glColor3f( 1.0f, 0.0f, 0.0f ); // draw them black (background color)

			gl.glBegin( GL.GL_TRIANGLES );
			for( Triangle tri : triangles ) {

				gl.glVertex3d( tri.v[0].x, tri.v[0].y, tri.v[0].z );
				gl.glVertex3d( tri.v[1].x, tri.v[1].y, tri.v[1].z );
				gl.glVertex3d( tri.v[2].x, tri.v[2].y, tri.v[2].z );
			}
			gl.glEnd();

			// switch back the wireframe mode and white color and draw again
			gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_LINE );
			gl.glColor3d( 1.0, 1.0, 1.0 );
		} else if( drawmode == 1 ) {// recolor hidden edges
			// the drawing is done twice

			// enable blending for the first drawing
			gl.glEnable( GL.GL_BLEND );

			gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );
			// we are using glBlendFunc(GL_ZERO, GL_SRC_COLOR), so lines rendered in white underneath the face become blue
			gl.glColor4d( 0.0, 0.0, 1.0, 1.0 );

			gl.glBegin( GL.GL_TRIANGLES );
			for( Triangle tri : triangles ) {

				gl.glVertex3d( tri.v[0].x, tri.v[0].y, tri.v[0].z );
				gl.glVertex3d( tri.v[1].x, tri.v[1].y, tri.v[1].z );
				gl.glVertex3d( tri.v[2].x, tri.v[2].y, tri.v[2].z );
			}
			gl.glEnd();

			// disable blending and switch back the wireframe mode
			gl.glDisable( GL.GL_BLEND );

			gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_LINE );
			gl.glColor3d( 1.0, 1.0, 1.0 );
		} else if( drawmode == 2 ) { // draw polygons
			gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );
			gl.glColor3d( 1.0, 1.0, 1.0 );
		}

		gl.glBegin( GL.GL_TRIANGLES );
		for( Triangle tri : triangles ) {

			// add a normal
			gl.glNormal3d( tri.faceNormal.x, tri.faceNormal.y, tri.faceNormal.z );

			gl.glVertex3d( tri.v[0].x, tri.v[0].y, tri.v[0].z );
			gl.glVertex3d( tri.v[1].x, tri.v[1].y, tri.v[1].z );
			gl.glVertex3d( tri.v[2].x, tri.v[2].y, tri.v[2].z );
		}
		gl.glEnd();
	}


	/** traverses the bsp and calls the draw() function on each node */
	void traverse( BspTree bsp ) {
		if( bsp == null )
			return;

		// traverse bsp and draw each node
		// Note: the current eye position is given
		// in the eye variable

		if( bsp.getPartitionPlane().isInPositiveSide( eye ) ) { // isInPositiveSide( bsp.partitionPlane, BspMain.eye ) ) {
			traverse( bsp.getNegativeSide() );
			// the sign is ignored, so we can set zero (should not be like this, or?
			draw( bsp.getOppDir(), Sign.Zero );
			draw( bsp.getSameDir(), Sign.Zero );
			traverse( bsp.getPositiveSide() );
		} else {
			traverse( bsp.getPositiveSide() );
			// the sign is ignored, so we can set zero
			draw( bsp.getOppDir(), Sign.Zero );
			draw( bsp.getSameDir(), Sign.Zero );
			traverse( bsp.getNegativeSide() );
		}
	}

	@Override
	public void keyPressed( KeyEvent e ) {
		switch( e.getKeyCode() ) {
			case KeyEvent.VK_A:
				printMode = ++printMode % 3;
				//System.out.println( "Printmode: " + printMode );
				break;
			case KeyEvent.VK_D:
				drawmode = ++drawmode % 3;
				//System.out.println( "Drawmode: " + drawmode );
				break;
		}
	}

	public static void main( String[] args ) throws FileNotFoundException, IOException, Exception {
		//String path = "D:\\Desktop\\BSP\\cg1_ex3\\meshes\\teapot.off";
		//String path = "D:\\Desktop\\BSP\\cg1_ex3\\meshes\\bunnysimple.off";
		//String path = "D:\\Desktop\\BSP\\cg1_ex3\\meshes\\sphere.off";
		//String path = "D:\\Desktop\\BSP\\cg1_ex3\\meshes\\head.off";
		//String path = "D:\\Desktop\\BSP\\cg1_ex3\\meshes\\dragon.off";

		//String path = "./testinstanz/off/teapot.off";
		//String path = "./testinstanz/off/bunnysimple.off";
		//String path = "./testinstanz/off/sphere.off";
		//String path = "D:\\Desktop\\BSP\\cg1_ex3\\meshes\\head.off";
		//String path = "./testinstanz/off/dragon.off";

    //String path = "./testinstanz/test.off";

		//String path = "./testinstanz/off/dl1/cone.off";
		String path = "./testinstanz/off/dl1/icosa.off";
		//String path = "./testinstanz/off/dl1/mctet.off";
		//String path = "./testinstanz/off/dl1/mctri.off";
		//String path = "./testinstanz/off/dl1/octa.off";
		//String path = "./testinstanz/off/dl1/tetra.off";


		JFrame window = new JFrame( "BSP Test Application" );
		window.setSize( 800, 600 );

		//GLCanvas canvas = new GLCanvas();


		BspMain main = new BspMain();
		//canvas.addGLEventListener( main );

		instance = main;
		instance.setBackground( Color.white );
		window.add( main );

		// Read the stuff
		OFFReader off = new OFFReader();
		off.readOff(path );
		mesh = off.getMesh();

		System.out.println( "Bsp has " + mesh.vertexCount() + " vertices" );
		/** need to unitize the model before computing normals etc.
		to avoid numerical problems due to very small triangles in
		high res model */
		System.out.print( "Unitizing model... " );
		mesh.unitize( 50 );
		System.out.println( "done." );

		System.out.print( "Computing plane equations and normals..." );
		for( Triangle t : mesh ) {
			if( t.plane == null )
				throw new Exception( "ERROR" );
			t.computePlane(); // TODO warum braucht man das hier?
			//t.faceNormal = t.plane.getNormal();
			//planes.add( t.plane );
			//faceNormals.add( t.faceNormal );
		}
		System.out.println( " done." );

		System.out.print( "Copy to the original list... " );
		originalList = new LinkedList<Triangle>( );
		copiedList = new LinkedList<Triangle>( );
		for( Triangle t: mesh ) {
			final Triangle newTriangle = new Triangle( t.v[0].clone(), t.v[1].clone(), t.v[2].clone(), -1, -1, -1 );
			newTriangle.plane = new Plane();
			newTriangle.computePlane();
			newTriangle.faceNormal = newTriangle.plane.getNormal();
			copiedList.add( newTriangle );
			originalList.add( t );
		}
		System.out.println( "done." );

		System.out.print( "Building bsp, this may take a long time..." );
		BinarySpacePartitioningTreeBuilder bspBuilder = new BinarySpacePartitioningTreeBuilder();
		bspBuilder.setProblem( mesh );
		bspBuilder.run();
		_bsp = bspBuilder.getSolution();
		System.out.println( " done." );

		System.out.println( "Runtime: " + bspBuilder.getRuntimeAsString() );

		window.setVisible( true );
		main.startAnimation();
	}
}

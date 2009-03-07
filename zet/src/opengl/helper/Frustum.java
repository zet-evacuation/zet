package opengl.helper;

import util.vectormath.Plane;
import util.vectormath.Vector3; 

/**
 *  @author Jan-Philipp Kappmeier
 */
public class Frustum {

    public static final double ANGLE2DEG = Math.PI / 180.0;
    public static final double DEG2ANGLE = 180.0 / Math.PI;

    private double fov = 45.0;

    private double aspect = 800 / 600;

    private double nearDist = 0.1;

    private double farDist = 100;

    private double nearWidth;

    private double nearHeight;

    private double farWidth;

    private double farHeight;

    /**
     *  declaration of the 8 edges of the frustum
     */
    private Vector3 farTopLeft;

    private Vector3 farTopRight;

    private Vector3 farBottomLeft;

    private Vector3 farBottomRight;

    private Vector3 nearTopLeft;

    private Vector3 nearTopRight;

    private Vector3 nearBottomLeft;

    private Vector3 nearBottomRight;

    /**
     *  declaration of the bording planes
     */
    private static final int topPlane = 0;

    private static final int bottomPlane = 1;

    private static final int leftPlane = 2;

    private static final int rightPlane = 3;

    private static final int nearPlane = 4;

    private static final int farPlane = 5;

    private Plane[] planes = new Plane[6];

    public Frustum () {
		planes[0] = new Plane();
		planes[1] = new Plane();
		planes[2] = new Plane();
		planes[3] = new Plane();
		planes[4] = new Plane();
		planes[5] = new Plane();
		recalc();
	}

    public void setAll (double fov, double aspect, double nearDist, double farDist) {
		this.fov = fov;
		this.aspect = aspect;
		this.nearDist = nearDist;
		this.farDist = farDist;
		recalc();
	}

    /**
     *  Resets the aspect parameter.
     *  	 @param aspect
     */
    public void setAspect (double aspect) {
		this.aspect = aspect;
		recalc();
	}

    public double getAspect () {
		return aspect;
	}

    public double getFov () {
		return fov;
	}

    public void setFov (double fov) {
		this.fov = fov;
		recalc();
	}

    public double getFarDist () {
		return farDist;
	}

    public void setFarDist (double farDist) {
		this.farDist = farDist;
		recalc();
	}

    public double getNearDist () {
		return nearDist;
	}

    public void setNearDist (double nearDist) {
		this.nearDist = nearDist;
		recalc();
	}

    public double getFarHeight () {
		return farHeight;
	}

    public double getFarWidth () {
		return farWidth;
	}

    public double getNearHeight () {
		return nearHeight;
	}

    public double getNearWidth () {
		return nearWidth;
	}

    private void recalc () {
		nearHeight = 2 * Math.tan( fov * ANGLE2DEG * 0.5 ) * nearDist;
		farHeight  = 2 * Math.tan( fov * ANGLE2DEG * 0.5 ) * farDist;
		nearWidth  = nearHeight * aspect;
		farWidth   = farHeight  * aspect;
	}

    public void update (Vector3 pos, Vector3 view, Vector3 up) {
		// this method is not very efficient!
		// more efficient way to compute the six planes (by lighthouse 3d.com)
		// pl[NEARP].setNormalAndPoint(-Z,nc);
		// pl[FARP].setNormalAndPoint(Z,fc);
		//
		// Vec3 aux,normal;
		//
		// aux = (nc + Y*nh) - p;
		// aux.normalize();
		// normal = aux  * X;
		// pl[TOP].setNormalAndPoint(normal,nc+Y*nh);
		// 
		// aux = (nc - Y*nh) - p;
		// aux.normalize();
		// normal = X  * aux;
		// pl[BOTTOM].setNormalAndPoint(normal,nc-Y*nh);
		//	
		// aux = (nc - X*nw) - p;
		// aux.normalize();
		// normal = aux  * Y;
		// pl[LEFT].setNormalAndPoint(normal,nc-X*nw);
		//
		// aux = (nc + X*nw) - p;
		// aux.normalize();
		// normal = Y  * aux;
		// pl[RIGHT].setNormalAndPoint(normal,nc+X*nw);
		Vector3 right;

		right = view.crossProduct( up ); // view * up;

	  // calculate positions of far plane edges
		Vector3 farCenter = new Vector3( pos );
		farCenter.add(up);
		farCenter.scalarMultiplicate( farDist );
		farTopLeft = new Vector3( farCenter );
		//Vector3 t = new Vector3(up);	// with temp vector and without doing-all-in-one
		//t.scalarMultiplicate( farHeight*0.5 );
		farTopLeft.add( new Vector3(up).scalaryMultiplication( farHeight*0.5 ) );
		farTopLeft.sub( new Vector3(right).scalaryMultiplication( farWidth*0.5 ) );
		farTopRight = new Vector3( farCenter );
		farTopRight.add( new Vector3(up).scalaryMultiplication( farHeight*0.5 ) );
		farTopRight.add( new Vector3(right).scalaryMultiplication( farWidth*0.5 ) );
		//farTopRight= farCenter + (camera->getUp()  * frustum.farHeight*0.5) + (camera->getRight()  * frustum.farWidth*0.5);
		farBottomLeft = new Vector3( farCenter );
		farBottomLeft.sub( new Vector3(up).scalaryMultiplication( farHeight*0.5 ) );
		farBottomLeft.sub( new Vector3(right).scalaryMultiplication( farWidth*0.5 ) );
		//farBottomLeft= farCenter - (camera->getUp()  * frustum.farHeight*0.5) - (camera->getRight()  * frustum.farWidth*0.5);
		farBottomRight = new Vector3( farCenter );
		farBottomRight.sub( new Vector3(up).scalaryMultiplication( farHeight*0.5 ) );
		farBottomRight.add( new Vector3(right).scalaryMultiplication( farWidth*0.5 ) );
		//farBottomRight = farCenter - (camera->getUp()  * frustum.farHeight*0.5) + (camera->getRight()  * frustum.farWidth*0.5);

	  // calculate positions of near plane edges
		Vector3 nearCenter = new Vector3( pos );
		nearCenter.add(up);
		nearCenter.scalarMultiplicate( nearDist );
		nearTopLeft = new Vector3( nearCenter );
		nearTopLeft.add( new Vector3(up).scalaryMultiplication( nearHeight*0.5 ) );
		nearTopLeft.sub( new Vector3(right).scalaryMultiplication( nearWidth*0.5 ) );
		//nearTopLeft= nearCenter + (camera->getUp()  * frustum.nearHeight*0.5) - (camera->getRight()  * frustum.nearWidth*0.5);
		nearTopRight = new Vector3( nearCenter );
		nearTopRight.add( new Vector3(up).scalaryMultiplication( nearHeight*0.5 ) );
		nearTopRight.add( new Vector3(right).scalaryMultiplication( nearHeight*0.5 ) );
		//nearTopRight= nearCenter + (camera->getUp()  * frustum.nearHeight*0.5) + (camera->getRight()  * frustum.nearWidth*0.5);
		nearBottomLeft = new Vector3( nearCenter );
		nearBottomLeft.sub( new Vector3(up).scalaryMultiplication( nearHeight*0.5 ) );
		nearBottomLeft.sub( new Vector3(right).scalaryMultiplication( nearHeight*0.5 ) );
	  //nearBottomLeft= nearCenter - (camera->getUp()  * frustum.nearHeight*0.5) - (camera->getRight()  * frustum.nearWidth*0.5);
		nearBottomRight = new Vector3( nearCenter );
		nearBottomRight.sub( new Vector3(up).scalaryMultiplication( nearHeight*0.5 ) );
		nearBottomRight.add( new Vector3(right).scalaryMultiplication( nearHeight*0.5 ) );
		//nearBottomRight = nearCenter - (camera->getUp()  * frustum.nearHeight*0.5) + (camera->getRight()  * frustum.nearWidth*0.5);

		// now compute the six planes bounding the frustum. the points are given in counter clockwise
		// order so that all normals point inside the frustum. that will us easyly allow to check if a point is
		// inside the frustum
		planes[topPlane].setPlane( nearTopRight, nearTopLeft, farTopLeft );
		planes[bottomPlane].setPlane( nearBottomLeft, nearBottomRight, farBottomRight );
		planes[leftPlane].setPlane( nearTopLeft, nearBottomRight, farBottomLeft );
		planes[rightPlane].setPlane( nearBottomRight, nearTopRight, farBottomRight );
		planes[nearPlane].setPlane( nearTopLeft, nearTopRight, nearBottomRight );
		planes[farPlane].setPlane( farTopRight, farTopLeft, farBottomLeft );
	}

    /**
     *  Calculates the frustum height at a given distance.
     *  	 If the distance is longer than the furstums far distance or shorter than the near distance
     *  	 zero is returned. Otherwise the width is calculated using the formula
     *  	 <b>height = 2 \tan( fov \frac{1}{2} ANGLE2DEG ) distance </b>
     *  	 @param dist The distance for what the height is calculated
     *  	 @return Height of the frustum at the specified distance
     */
    double getFrustumHeight (double dist) {
		if( dist < nearDist || dist > farDist )
			return 0;

		double val = 2 * Math.tan( fov * ANGLE2DEG * 0.5 ) * dist;
		return val;
	}

    /**
     *  Calculates the frustum width at a given distance.
     *  	 If the distance is longer than the furstums far distance or shorter than the near distance
     *  	 zero is returned. Otherwise the width is calculated using the formula
     *  	 <b>width = height aspect</b>
     *  	 @param dist The distance for what the width is calculated
     *  	 @return Width of the furstum at the specified distance
     */
    double getFrustumWidth (double dist) {
		if( dist < nearDist || dist > farDist )
			return 0;
		double val = getFrustumHeight( dist ) * aspect;
		return val;
	}
}
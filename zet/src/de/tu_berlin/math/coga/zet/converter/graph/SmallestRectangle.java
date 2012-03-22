/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.math.vectormath.Vector2;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import java.awt.Point;
import java.util.List;
/**
 *
 * @author schwengf
 */
public class SmallestRectangle {
    
   Vector2 center,xAxis,yAxis;
   double x_extent,y_extent;
   Vector2[] axis = new Vector2[2];

   public void initialize()
   {
       center = new Vector2(0,0);
       xAxis = new Vector2(0,0);
       yAxis = new Vector2(0,0);
       x_extent = 0.0;
       y_extent = 0.0;
   }
   
   public void computeSmallestRectangle(int numPoints, List<Vector2> points)
   {
       initialize();
       
       //compute mean of points
       center.setX(points.get(0).getX());
       center.setY(points.get(0).getY());

       for (int i=1; i<points.size(); i++)
       {
           center.addTo(points.get(i));
       }
       double inv = 1.0/((double) numPoints);
       center.scalarMultiplicateTo(inv);
       System.out.println("Center: " + center.getX() + " " + center.getY());
       //Compute the covariance matrix of the points
       double sumXX = 0.0;
       double sumXY = 0.0;
       double sumYY = 0.0;
       
       for (int i=0;i<numPoints;i++)
       {
           //System.out.println("Point: " + points.get(i).getX() + "Y:" + points.get(i).getY());
           //System.out.println("Center: " + center.getX() + "Y:" + center.getY());
           Vector2 diff = points.get(i).sub(center);
           sumXX += (diff.getX()*diff.getX()); 
           //System.out.println("sumXX: " + sumXX);
           sumXY += (diff.getX()*diff.getY());
           //System.out.println("sumXY: " + sumXY);
           sumYY += (diff.getY()*diff.getY());
           //System.out.println("sumXX: " + sumYY);
       }
       sumXX *= inv; sumXY *= inv; sumYY *= inv;
       System.out.println("XX: " + sumXX + "XY: " + sumXY + "YY: " + sumYY);
       
       //Compute Eigenvectors and values of points       
       Matrix Cov = new Matrix(2,2); 
       Cov.set(0,0,sumXX); Cov.set(0,1,sumXY);Cov.set(1,0,sumXY);Cov.set(1,1,sumYY);
       //Cov.set(0,0,1); Cov.set(0,1,3);Cov.set(1,0,3);Cov.set(1,1,4);
       System.out.println("Matrix 1: " + Cov.get(0,0));
       System.out.println("Matrix 2: " + Cov.get(0,1));
       System.out.println("Matrix 3: " + Cov.get(1,0));
       System.out.println("Matrix 4: " + Cov.get(1,1));
       EigenvalueDecomposition eigen = Cov.eig();
       Matrix eigval = eigen.getD();
       Matrix eigvec = eigen.getV();
            
       System.out.println("eigenvalues 1: " + eigval.get(0,0));
       System.out.println("eigenvalues 2: " + eigval.get(0,1));
       System.out.println("eigenvalues 3: " + eigval.get(1,0));
       System.out.println("eigenvalues 4: " + eigval.get(1,1));
       
       x_extent = eigval.get(0,0); y_extent = eigval.get(1,1);
       System.out.println("x_extent: " + x_extent + "y_extent: " + y_extent);
       xAxis = new Vector2(eigvec.get(0,0),eigvec.get(0,1));
       System.out.println("xAxis: " + xAxis.getX() + "x_axis: " + xAxis.getY());
       yAxis = new Vector2(eigvec.get(1,0),eigvec.get(1,1));
       System.out.println("xAxis: " + yAxis.getX() + "x_axis: " + yAxis.getY());
       
       Vector2 diff = points.get(0).sub(center);
       System.out.println("p0: " + diff.getX() + "p0: "+ diff.getY());
       axis[0] = xAxis; axis[1] = yAxis;
       Vector2 min = new Vector2(diff.dotProduct(xAxis), diff.dotProduct(yAxis));
       System.out.println("Min1: " + min.getX() + "Min1: " + min.getY());
       Vector2 max = new Vector2();
       max.setX(min.getX());max.setY(min.getY());
       System.out.println("Max1: " + max.getX() + "Max1: " + max.getY());
       for (int i=0;i<numPoints; i++)
       {
           diff = points.get(i).sub(center);
           for (int j=0; j<2; j++)
           {
               double d = diff.dotProduct(axis[j]);
               if (j==0){
                   if (d < min.getX() ){
                       min.setX(d); 
                   }
                   else if (d > max.getX()){
                       max.setX(d);
                   }
               }
               else {
                   if (d < min.getY() ){
                       min.setY(d); }
                   else if (d > max.getY()){
                       max.setY(d);}
               }
           }
               
       }
       System.out.println("Min: " + min.getX() + "Min: " + min.getY());
       System.out.println("Max: " + max.getX() + "Max: " + max.getY());
       Vector2 vect = axis[0].scalarMultiplicate(0.5*(min.getX()+max.getX())).add(axis[0].scalarMultiplicate(0.5*(min.getY()+max.getY())));
       center.addTo(vect);
       System.out.println("X: " + center.getX() + "Y:" + center.getY());
       x_extent = 0.5*(max.getX() - min.getX());
       y_extent = 0.5*(max.getY() - min.getY());
       System.out.println("xextent: " + x_extent + "yextent: " + y_extent);
       
       getRectanglePoints();
       
       }
   
       public void getRectanglePoints()
       {
           Vector2 extxAxis = axis[0].scalarMultiplicate(x_extent);
           Vector2 extyAxis = axis[1].scalarMultiplicate(y_extent);
           
           Vector2 p1 = center.sub(extxAxis).sub(extyAxis);
           Point po1 = new Point((int)p1.getX(),(int)p1.getY());
           Vector2 p2 = center.add(extxAxis).sub(extyAxis);
           Point po2 = new Point((int)p2.getX(),(int)p2.getY());
           Vector2 p3 = center.add(extxAxis).add(extyAxis);
           Point po3 = new Point((int)p3.getX(),(int)p3.getY());
           Vector2 p4 = center.sub(extxAxis).add(extyAxis);
           Point po4 = new Point((int)p4.getX(),(int)p4.getY());
           
           System.out.println("P1: " + po1 + "P2: " + po2 + "P3: " + po3 + "P4: " + po4);
       }
       
       }
   
    


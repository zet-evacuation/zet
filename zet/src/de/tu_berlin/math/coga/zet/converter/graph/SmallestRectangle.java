/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.math.vectormath.Vector2;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
/**
 *
 * @author schwengf
 */
public class SmallestRectangle {
    
   Vector2 center,xAxis,yAxis;
   double x_extent,y_extent;
   Vector2[] axis = new Vector2[2];
   List<Point> recPoints;

   public void initialize()
   {
       center = new Vector2(0,0);
       xAxis = new Vector2(0,0);
       yAxis = new Vector2(0,0);
       x_extent = 0.0;
       y_extent = 0.0;
   }
   
   public List<Point> computeSmallestRectangle(int numPoints, List<Vector2> points)
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
       
       //Compute the covariance matrix of the points
       double sumXX = 0.0;
       double sumXY = 0.0;
       double sumYY = 0.0;
       
       for (int i=0;i<numPoints;i++)
       {
           Vector2 diff = points.get(i).sub(center);
           sumXX += (diff.getX()*diff.getX()); 
           sumXY += (diff.getX()*diff.getY());
           sumYY += (diff.getY()*diff.getY());
       }
       sumXX *= inv; sumXY *= inv; sumYY *= inv;
       
       //Compute Eigenvectors and values of points       
       Matrix Cov = new Matrix(2,2); 
       Cov.set(0,0,sumXX); Cov.set(0,1,sumXY);Cov.set(1,0,sumXY);Cov.set(1,1,sumYY);

       //System.out.println("Matrix 1: " + Cov.get(0,0) + Cov.get(0,1)+ Cov.get(1,0) + Cov.get(1,1));
       
       EigenvalueDecomposition eigen = Cov.eig();
       Matrix eigval = eigen.getD();
       Matrix eigvec = eigen.getV();
            
       //System.out.println("eigenvalues 1: " + eigval.get(0,0) + eigval.get(0,1) + eigval.get(1,0) + eigval.get(1,1));
       
       x_extent = eigval.get(0,0); y_extent = eigval.get(1,1);
       xAxis = new Vector2(eigvec.get(0,0),eigvec.get(0,1));
       yAxis = new Vector2(eigvec.get(1,0),eigvec.get(1,1));
       
       Vector2 diff = points.get(0).sub(center);
       axis[0] = xAxis; axis[1] = yAxis;
       Vector2 min = new Vector2(diff.dotProduct(xAxis), diff.dotProduct(yAxis));
       Vector2 max = new Vector2();
       max.setX(min.getX());max.setY(min.getY());

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
       Vector2 vect = axis[0].scalarMultiplicate(0.5*(min.getX()+max.getX())).add(axis[0].scalarMultiplicate(0.5*(min.getY()+max.getY())));
       center.addTo(vect);
       x_extent = 0.5*(max.getX() - min.getX());
       y_extent = 0.5*(max.getY() - min.getY());
       //System.out.println("x_extent: " + x_extent + "y_extent: " + y_extent);
       getRectanglePoints();
       getNodeRectanglePoints();
       
       return recPoints;
       
       }
   
       public void getRectanglePoints()
       {
           recPoints = new LinkedList<>();
           
           Vector2 extxAxis = axis[0].scalarMultiplicate(x_extent);
           //System.out.println("axis[0]: " + axis[0].getX() + " " + axis[0].getY());
           Vector2 extyAxis = axis[1].scalarMultiplicate(y_extent);
           //System.out.println("axis[1]: " + axis[1].getX() + " " + axis[1].getY());
           
           //System.out.println("Center: " + center.getX() + " " + center.getY());
           Vector2 p1 = center.sub(extxAxis).sub(extyAxis);           
           Point po1 = new Point((int)p1.getX(),(int)p1.getY());
           
           Vector2 p2 = center.add(extxAxis).sub(extyAxis);
           Point po2 = new Point((int)p2.getX(),(int)p2.getY());
           Vector2 p3 = center.add(extxAxis).add(extyAxis);
           Point po3 = new Point((int)p3.getX(),(int)p3.getY());
           Vector2 p4 = center.sub(extxAxis).add(extyAxis);
           Point po4 = new Point((int)p4.getX(),(int)p4.getY());
           
           recPoints.add(po1);recPoints.add(po2);recPoints.add(po3);recPoints.add(po4);
           //System.out.println("Points: " + po1 + po2 + po3 + po4 );
       }
       
       public void getNodeRectanglePoints()
       {
           double xmax = Double.MIN_VALUE;
           double ymax = Double.MIN_VALUE;
           double xmin = Double.MAX_VALUE;
           double ymin = Double.MAX_VALUE;
           Point miny = new Point(0,0);
           Point minx = new Point(0,0);
           Point maxy = new Point(0,0);
           Point maxx = new Point(0,0);
           Point NW = new Point(0,0); Point NE = new Point(0,0);
           Point SW = new Point(0,0); Point SE = new Point(0,0);
           int numxval =0; int numyval =0;
           //saves the different x and y-points 
           List<Double> xpoints = new LinkedList<>(); List<Double> ypoints = new LinkedList<>() ;
           List<Point> firstVal = new LinkedList<>(); List<Point> secondVal = new LinkedList<>();
           
           for (Point p: recPoints)
           {
               if (!(xpoints.contains(p.getX()))){
                   xpoints.add(p.getX());
                   numxval++;
               }
               if (!(ypoints.contains(p.getY()))){
                   ypoints.add(p.getY());
                   numyval++;
               }             
           }
           //System.out.println("numxval: " + numxval +  "numyval: " + numyval);
                
           for (Point p: recPoints)
           {
                    if (p.getX() < xmin){
                        xmin = p.getX();
                        minx = p;
                    }
                    if (p.getY() < ymin){
                        ymin = p.getY();
                        miny = p;
                    }
                    if (p.getX() > xmax){
                        xmax = p.getX();
                        maxx = p;
                    }
                    if (p.getY() > ymax){
                        ymax = p.getY();
                        maxy = p;
                    }   
           }
                
           recPoints = new LinkedList<>();
           //works only well if none of the edges are axis aligned...
           if (numxval==4 && numyval ==4)
           {
              recPoints.add(minx);recPoints.add(miny);recPoints.add(maxy);recPoints.add(maxx);
           }
           // rectangle is y-axis aligned
           else if (numxval==2 && numyval == 4)
           {
                  //System.out.println("y-axis aligned");
                  double y_min1 = Double.MAX_VALUE;
                  double y_min2 = Double.MAX_VALUE;
                  for (Point p: recPoints){
                      if (p.getX() == xmin){
                          firstVal.add(p);
                      }
                      else{
                          secondVal.add(p);
                      }
                  }
                  for (int i=0;i<2;i++)
                  {
                      Point first = firstVal.get(i);
                      Point second = secondVal.get(i);
                      
                      if ( first.getY() < y_min1)
                      {
                          NW = first;
                          if (i==0){
                              SW = firstVal.get(i+1);
                          }
                          else{
                              SW = firstVal.get(i-1);
                          }
                          y_min1 = first.getY();
                      }
                      if ( second.getY() < y_min2)
                      {
                          NE = second;
                          if (i==0){
                              SE = secondVal.get(i+1);
                          }
                          else{
                              SE = secondVal.get(i-1);
                          }
                          y_min2 = second.getY();
                      }
                  }
                  recPoints.add(NW);recPoints.add(NE); recPoints.add(SW);recPoints.add(SE); 
           }
           //rectangle is x-axis aligned
           else if (numxval==4 && numyval==2)
           {
                  //System.out.println("x-axis aligned");
                  double x_min1 = Double.MAX_VALUE;
                  double x_min2 = Double.MAX_VALUE;
                  for (Point p: recPoints){
                      if (p.getY() == ymin){
                          firstVal.add(p);
                      }
                      else{
                          secondVal.add(p);
                      }
                  }
                  for (int i=0;i<2;i++)
                  {
                      Point first = firstVal.get(i);
                      Point second = secondVal.get(i);
                      
                      if ( first.getX() < x_min1)
                      {
                          NW = first;
                          if (i==0){
                              SW = firstVal.get(i+1);
                          }
                          else{
                              SW = firstVal.get(i-1);
                          }
                          x_min1 = first.getX();
                      }
                      if ( second.getX() < x_min2)
                      {
                          NE = second;
                          if (i==0){
                              SE = secondVal.get(i+1);
                          }
                          else{
                              SE = secondVal.get(i-1);
                          }
                          x_min2 = second.getX();
                      }
                  }
                  recPoints.add(NW);recPoints.add(NE); recPoints.add(SW);recPoints.add(SE); 
           }
           else if (numxval==2 && numyval==2 ) 
           {
               //System.out.println("axis aligned in both directions");
               NW = new Point((int)xmin,(int)ymin);
               NE = new Point((int)xmax,(int)ymin);
               SW = new Point((int)xmin,(int)ymax);
               SE = new Point((int)xmax,(int)ymax);
               recPoints.add(NW); recPoints.add(NE);recPoints.add(SW); recPoints.add(SE);
           }
               
       }
       }
   
    


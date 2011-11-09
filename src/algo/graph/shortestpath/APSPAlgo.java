/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.shortestpath;


import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.ListSequence;


 /*
 * @author schwengf
 */
public class APSPAlgo {
  
  NetworkFlowModel model;
  int numNodes;  
  int maxdist = Integer.MIN_VALUE;
  int[][] weight; 
  int[][] used;
  int [][][] A_k; 
  int l;
  int m;
  int numEdges=0;
  int[][] Q_l;
  int[][] P_k;
  int[][][] C;
  int[][] Delta;
  int[][] dist;
  int[][] A_new;
  int[][] B_new;
  
  
  public APSPAlgo(NetworkFlowModel model)
  {
     this.model = model;
     this.numNodes = model.getGraph().numberOfNodes() - 1; 
     System.out.println("NumNodes: " + numNodes);
     used = new int[numNodes][numNodes];
     for (int i=0; i< numNodes ; i++)
     {
         for (int j=0; j< numNodes ; j++)
         {
             used[i][j] = 0;
         }
     }
     for (Edge edge: model.getGraph().edges())
     {
         //System.out.println("Kante: " + edge + "Transitzeit: " + model.getTransitTime(edge));
         if (model.getTransitTime(edge) > this.maxdist)
         {
             this.maxdist = model.getTransitTime(edge);
         }
     }
     
  }
    
  public void Step_one()
  {
    l = (int) Math.ceil(Math.log(numNodes)/Math.log(2)); 
    //System.out.println("l: " + l);
    for (int k=1; k< Integer.MAX_VALUE; k++)
    {
        if (Math.pow(2,k) == maxdist)
        {
            break;
        }
        else if (Math.pow(2,k) > maxdist)
        {
            maxdist = (int)Math.pow(2,k);
            break;
        }      
    } 

    m = (int) (Math.log(maxdist)/Math.log(2));
    System.out.println("m: " + m);
    System.out.println("Maxdist: " + maxdist);
    weight = new int[numNodes][numNodes]; // matrix D
    dist = new int[numNodes][numNodes];
    for (Edge edge: model.getGraph().edges())
    {
      if (!edge.isIncidentTo(model.getSupersink()))
      {
        weight[edge.start().id()-1][edge.end().id()-1] = model.getTransitTime(edge);
        used[edge.start().id()-1][edge.end().id()-1] = 1;
        //System.out.println("Distance " + weight[edge.start().id()][edge.end().id()] + " " + edge.start() + "---" + edge.end());
        weight[edge.end().id()-1][edge.start().id()-1] = model.getTransitTime(edge);
        used[edge.end().id()-1][edge.start().id()-1] = 1;
      }
       
    }
    
    for (int i=0; i< numNodes ; i++)
    {
        weight[i][i] = 0;
        dist[i][i] = Integer.MAX_VALUE;
        
        for (int j=0; j< numNodes ; j++)
        {
            if (used[i][j] != 1 && i != j)
            {
                weight[i][j] = Integer.MAX_VALUE;
                dist[i][j] = Integer.MAX_VALUE;
            }
            else if (i!= j)
            {
                dist[i][j] = weight[i][j];
            }
            System.out.println("original weight " + weight[i][j] + " " + i + " " + j);
        }
    }
    for (int k=1 ; k < m+2 ; k++)
    {
        int [][] res = distance_product(weight, weight);
        if (k==1)
        {
            for (int i=0; i<numNodes; i++)
            {
                for (int j=0 ; j<numNodes ; j++)
                {
                    System.out.println("i:" + i + " j:" + j + "distance_product: " + res[i][j]);
                }
            }
        }
        weight = clip(res,0,2*maxdist);
    }
  System.out.println("Step 1 done");      
  }
  
  public void Step_two()
  {
      A_k = new int[l+1][numNodes][numNodes];
      
      for (int i=0 ; i< numNodes ; i++)
      {
          for (int j=0 ; j< numNodes ; j++)
          {
              A_k[0][i][j] = weight[i][j] - maxdist;
              //System.out.println("i:" + i + " j:" + j + "A_0: " + A_k[0][i][j]);
          }
      }
      
      for (int k=1 ; k< l+1 ; k++)
      {
          int[][] res = distance_product(A_k[k-1],A_k[k-1]);
          A_k[k] = clip(distance_product(A_k[k-1],A_k[k-1]),-maxdist,maxdist);
      }
  System.out.println("Step 2 done");     
  }
  
  public void Step_three()
  {
     C = new int [l+1][numNodes][numNodes];
     int[][] Q_k = new int[numNodes][numNodes];
     Q_l = new int[numNodes][numNodes];
     P_k = new int[numNodes][numNodes];
     for (int i=0 ; i< numNodes ; i++)
     {
         for (int j=0 ; j< numNodes ; j++)
         {
             C[l][i][j] = -maxdist;
             Q_l[i][j] = Integer.MAX_VALUE;
         }
     }
     int[][] P_l = new int[numNodes][numNodes];
     P_l = clip(weight, 0, maxdist);
     
     for (int k=l-1; k > -1 ; k--)
     {       
         int[][] first = intersect(clip(distance_product(P_l,A_k[k]), -maxdist, maxdist), C[k+1]);
         int[][] second = intersect_neg(clip(distance_product(Q_l,A_k[k]),-maxdist,maxdist), C[k+1]);   
         
         C[k] = union(first, second);
         P_k = union(P_l,Q_l);      
         Q_k = chop(C[k],1-maxdist,maxdist); 
         Q_l = Q_k;
         P_l = P_k;

     }
   System.out.println("Step 3 done");       
  }
  
  
  public void Step_four()
  {
     int[][][] B_k = new int [l+1][numNodes][numNodes];

     int[][] R = new int[numNodes][numNodes];
     Delta = new int[numNodes][numNodes];
     for (int k=1 ; k< l+1 ; k++)
     {
         B_k[k] = BiggerZero(C[k]);                       
     }

     B_k[0] = BiggerLessThan(P_k, maxdist);
     
     for (int i=0; i< numNodes; i++)
     {
         
         for (int j=0 ; j< numNodes; j++)
         {
             if (P_k[i][j]< 0)
             {
                 R[i][j] = ((P_k[i][j] % maxdist) + maxdist) % maxdist;
             }
             else
             {
                 R[i][j] = P_k[i][j] % maxdist;
             }
             //System.out.println("i:" + i + " j:" + j + "R: " + R[i][j]);
             int sum=0;
             for (int k=0; k< l+1; k++)
             {
                 sum += B_k[k][i][j]*(int)Math.pow(2.0, (double) k);
             }
             Delta[i][j] = sum*maxdist + R[i][j];
             
         }
     }
     for (int i=0; i<numNodes;i++)
     {
         for (int j=0; j<numNodes;j++)
         {
             System.out.println("i:" + i + " j:" + j + "Delta: " + Delta[i][j]);
         }
     }
    System.out.println("Step 4 done");  
  }
  
     public int[][] Compute_Matrix_of_Witnesses()
   {
       int [][] Delta_new = new int[numNodes][numNodes]; 
       int [][] witnesses = new int[numNodes][numNodes];
       int [][] successors = new int[numNodes][numNodes];
       
       
       for (int i=0; i< numNodes ; i++)
       {
           for (int j=0; j< numNodes ; j++)
           {
               if (Delta[i][j] % (3*maxdist) >= 0 )
               {
                   Delta_new[i][j] = Delta[i][j] % (3*maxdist);
               }
               else
               {
                   Delta_new[i][j] = Integer.MAX_VALUE;
               }
               //System.out.println("i:" + i + " j:" + j + "Delta_neu_1: " + Delta_new[i][j]);
               
           }
       }
       
       int [][] a_prime = new int[numNodes][numNodes];
       int [][] b_prime = new int[numNodes][numNodes];
       
       for (int i=0; i< numNodes ; i++)
       {
           for (int j=0; j< numNodes ; j++)
           {
               if (dist[i][j] != Integer.MAX_VALUE)
               {
                   a_prime[i][j] = numNodes*dist[i][j] + j ;
               }
               else
               { 
                   a_prime[i][j] = Integer.MAX_VALUE;
               }
               //System.out.println("i:" + i + " j:" + j + " a_crime: " + a_prime[i][j]);
               if (Delta_new[i][j] != Integer.MAX_VALUE)
               {
                    b_prime[i][j] = numNodes*Delta_new[i][j];
               }
               else
               {
                   b_prime[i][j] = Integer.MAX_VALUE;
               }
               
               //System.out.println("i:" + i + " j:" + j + " b_crime: " + b_prime[i][j]);
               if (Delta_new[i][j] != Integer.MAX_VALUE)
               {
                   b_prime[i][j] = numNodes*Delta_new[i][j];
               }
               else
               {
                   b_prime[i][j] = Integer.MAX_VALUE;
               }
               //System.out.println("i:" + i + " j:" + j + " b_crime: " + b_prime[i][j]);
               if (Delta_new[i][j] != Integer.MAX_VALUE)
               {
                   b_prime[i][j] = numNodes*Delta_new[i][j];
               }
               else
               {
                   b_prime[i][j] = Integer.MAX_VALUE;
               }
               
           }
       }
       int [][] C_prime = distance_product(a_prime,b_prime); 
       
       for (int i=0; i< numNodes ; i++)
       {
           for (int j=0; j< numNodes ; j++)
           {
               successors[i][j] = Integer.MAX_VALUE;
               witnesses[i][j] = (C_prime[i][j] % numNodes);
           
               if (Delta_new[i][j] != Integer.MAX_VALUE && (i!=j) )
               {
                   successors[i][j] = witnesses[i][j];
               }
               else
               {
                   successors[i][j] = Integer.MAX_VALUE;
               }
               //System.out.println("i:" + i + " j:" + j + "witness 1: " + successors[i][j] );
               
           }
       }
       System.out.println("Step 5 done");     
       return successors;
   }
     
  
  public int [][] distance_product_naive(int[][] a, int[][] b)
  {     
      int[][] result = new int[a.length][a.length];
      int sum =0 ;
      for (int i=0 ; i< a.length ; i++)
      {
          for (int j=0; j< a.length; j++)
          {
              int Min = Integer.MAX_VALUE;
              for (int k=0 ; k < a.length ; k++)
              {
                  
                  if (a[i][k] == Integer.MAX_VALUE || b[k][j] == Integer.MAX_VALUE)
                  {
                      sum = Integer.MAX_VALUE;
                  }
                  else
                  {
                      sum = a[i][k] + b[k][j];
                  }
                  if (sum < Min)
                  {
                      Min = sum;
                  }
                  
              }
              result[i][j] = Min;
          }
      } 
      return result;
  }
     
  //ToDo: mittels Strassen Algorithmus Matrix Multiplikation schneller machen   
  public int[][] distance_product(int[][] a, int[][] b)
  {
      int [][] result = new int[a.length][a.length];
      int [][] a_prime = new int[a.length][a.length];
      int [][] b_prime = new int[a.length][a.length];
      int [][] c_prime = new int[a.length][a.length];
      double omega = 2.376;
      
      if (maxdist*Math.pow(numNodes,omega) > Math.pow(numNodes, 3))
      {
          for (int i=0; i<a.length ; i++)
          {
              for (int j=0; j<a.length ; j++)
              {
                  if (a[i][j] <= maxdist)
                  { 
                      a_prime[i][j] = (int) Math.pow((double) (a.length +1) ,(double) (maxdist - a[i][j]));
                      b_prime[i][j] = (int) Math.pow((double) (a.length +1) ,(double) (maxdist - b[i][j]));
                  }
                  else
                  {
                      a_prime[i][j] = 0;
                      b_prime[i][j] = 0;
                  } 
              }    
          }
          
        /*for (int i=0; i<a.length; i++)
        {
            for (int j=0; j<a.length ; j++)
            {
                System.out.println("b' : " + b_prime[i][j]);
            }
        }*/  
        c_prime = Strassen_product_cut(a_prime,b_prime,3);
        for (int i=0; i<a.length; i++)
        {
            for (int j=0; j<a.length; j++)
            {
                System.out.println("c_prime: " + c_prime[i][j]);
            }
        }
        
        for (int i=0; i<a.length; i++)
        {
            for (int j=0; j<a.length ; j++)
            {
                if (c_prime[i][j] > 0)
                {
                    result[i][j] = 2*maxdist - (int) Math.floor(Math.log10(c_prime[i][j])/Math.log10(a.length+1));
                }
            }
        }
      }
      else
      {
          result = distance_product_naive(a,b);
      }
      return result;
  }
  
  public int[][] find_dimension(int[][] A)
  {
     int[][] A_changed = new int[1][1];
     int dimension=0;
     for (int k=0; k<Integer.MAX_VALUE; k++)
          {
              if (A.length == Math.pow(2,k))
              {
                  dimension = A.length;
                  A_changed = new int [dimension][dimension];
                  A_changed = A;
                  break;
              }
              else if (Math.pow(2, k) > A.length)
              {
                  dimension = (int) Math.pow(2, k);
                  A_changed = new int [dimension][dimension];
                  setMatrix(A_changed, A, 0, 0);
                  
                  //fülle Rest mit 0 auf, falls vorhanden
                  for (int i= A.length; i<dimension ; i++)
                  {
                      for (int j=A.length; j<dimension ; j++)
                      {
                          A_changed[i][j] = 0;
                      }
                  }
                  for (int i=0; i<dimension; i++)
                  {
                      for (int j=0; j<dimension; j++)
                      {
                          System.out.println("i:" + i + "j" + j + "A_new: " + A_changed[i][j]);
                      }
                  }
                  break;
               }
           }  
     return A_changed;
  }
  
  public int[][] Strassen_product(int[][] A, int[][] B) {
    return Strassen_product_cut(A, B, 1);
  }
  
  public int[][] Strassen_product_cut(int [][] A, int[][] B, int cut)
  {
      boolean firstrun = true;
      int dimension=0;
      //define matrix of size n = 2^k
      if (firstrun)
      {
          A_new = find_dimension(A);
          B_new = find_dimension(B);
          firstrun = false;
      }
    
    dimension = A_new.length;
    
    if (dimension <= cut) 
    {
        System.out.println("done>>>>>>>>>>>>>>>>>>");
      // normal multiplizieren
      int[][] C = getMatrix(matmult(A_new, B_new),0,A.length,0,A.length);
      for (int i=0; i<A.length;i++)
      {
          for (int j=0; j<A.length; j++)
          {
              System.out.println("C am Ende: " + C[i][j]);
          }
      }
      return C;
    }
    
    int dim = dimension/2;     // sollte ohne Rest aufgehen, da m Zweierpotenz
    System.out.println("Dimension: " + dim);
    int[][] a11 = getMatrix(A, 0, dim-1,   0, dim-1);
    int[][] a12 = getMatrix(A, 0, dim-1, dim,   dimension-1);
    int[][] a21 = getMatrix(A, dim,   dimension-1,   0, dim-1);
    int[][] a22 = getMatrix(A, dim,   dimension-1, dim,   dimension-1);
    int[][] b11 = getMatrix(B, 0, dim-1,   0, dim-1);
    int[][] b12 = getMatrix(B, 0, dim-1, dim,   dimension-1);
    int[][] b21 = getMatrix(B, dim,   dimension-1,   0, dim-1);
    int[][] b22 = getMatrix(B, dim,   dimension-1, dim,   dimension-1);

    System.out.println("Done first part");
    // Matrizen m1 .. m7 berechnen
    // dazu zwei Hilfsmatrizen d1, d2 für Zwischenwerte
    int[][] d1 = minusMatrix(a12,a22);
    int[][] d2 = plusMatrix(b21,b22);
    int[][] m1 = Strassen_product(d1, d2);
    
    d1 = plusMatrix(a11,a22);
    d2 = plusMatrix(b11,b22);
    int[][] m2 = Strassen_product(d1, d2);
    
    d1 = minusMatrix(a11,a21);
    d2 = plusMatrix(b11,b12);
    int[][] m3 = Strassen_product(d1, d2);
    
    d1 = plusMatrix(a11,a12);
    int[][] m4 = Strassen_product(d1, b22);
    
    d1 = minusMatrix(b12,b22);
    int[][] m5 = Strassen_product(a11, d1);
    
    d1 = minusMatrix(b21,b11);
    int[][] m6 = Strassen_product(a22, d1);
    
    d1 = plusMatrix(a21,a22);
    int[][] m7 = Strassen_product(d1, b11);
  
    int [][] C11 = plusMatrix(m1,m2);
    C11 = minusMatrix(C11,m4);
    C11 = plusMatrix(C11,m6);
    
    int[][] C12 = plusMatrix(m4,m5);
    
    int[][] C21 = plusMatrix(m6,m7);
    
    int[][] C22 = minusMatrix(m2,m3);
    C22 = plusMatrix(C22,m5);
    C22 = minusMatrix(C22,m7);
    
    // Gesamtmatrix zusammensetzen
    int[][] C = new int[A_new.length][A_new.length];
    setMatrix(C,C11,0,0);
    setMatrix(C,C12,0,dim);
    setMatrix(C,C21,dim,0);
    setMatrix(C,C22,dim, dim);
    
    int[][] result = getMatrix(C,0,A.length,0,A.length);
    for (int i=0; i<A.length; i++)
    {
        for (int j=0; j<A.length; j++)
        {
            System.out.println("Result: " + result[i][j]);
        }
    }
    return result;
  }
  
  public int[][] getMatrix(int[][] A,int i0, int i1, int j0, int j1) 
  {
    // erzeugt eine Teilmatrix A(i0..i1, j0..j1)
    
    int m = i1 - i0 + 1;
    int n = j1 - j0 + 1;
    
    int[][] part = new int[m][n];
    
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        part[i][j] = A[i + i0][j + j0];
      }
    }
    return part;
  }
  
  void setMatrix(int[][] A, int[][] part, int i0, int j0) {
    // belegt die Matrix mit einer Teilmatrix
    
    int p = part.length;
    for (int i = 0; i < p; i++) {
      for (int j = 0; j < p; j++) {
        A[i+ i0][j + j0] = part[i][j];
      }
    }
  }

  
  public int[][] minusMatrix(int[][] A, int[][] B)
  {
      int[][] diff = new int[A.length][A.length];
      for (int i=0; i<A.length; i++)
      {
          for (int j=0; j<A.length ; j++)
          {
              diff[i][j] = A[i][j] -B[i][j];
          }
      }
      return diff;
  }

  public int[][] plusMatrix(int[][] A, int[][] B)
  {
      int[][] diff = new int[A.length][A.length];
      for (int i=0; i<A.length; i++)
      {
          for (int j=0; j<A.length ; j++)
          {
              diff[i][j] = A[i][j] + B[i][j];
          }
      }
      return diff;
  }
  
  public int[][] matmult(int[][] A, int[][] B) {
    // einfache Matrix-Multiplikation    
    int[][] C = new int[A.length][A.length];
    
    for (int i = 0; i < A.length; i++) {
      for (int j = 0; j < A.length; j++) {
        C[i][j] = 0;
        for (int k = 0; k < A.length; k++) {
          C[i][j] += A[i][k] * B[k][j];
        }
      }
    }   
    return C;
  }

  
  public int[][] clip(int [][] A, int a, int b)
  {
      int[][] result = new int[A.length][A.length];
      for (int i=0 ; i< A.length ; i++)
      {
          for (int j=0 ; j< A.length ; j++)
          {
              if (A[i][j] < a)
              {
                  result[i][j] = a;
              }
              else if (A[i][j] > b)
              {
                  result[i][j] = Integer.MAX_VALUE;
              }
              else
              {
                  result[i][j] = A[i][j];
              }
          }
      }
     
      return result;
  }
  
  public int[][] chop(int [][] A, int a, int b)
  {
      int[][] result = new int[A.length][A.length];
      for (int i=0 ; i< A.length ; i++)
      {
          for (int j=0 ; j< A.length ; j++)
          {
              if (A[i][j] >= a && A[i][j] <= b)
              {
                  result[i][j] = A[i][j];
              }
              else
              {
                  result[i][j] = Integer.MAX_VALUE;
              }
          }
      }
      return result;
  }
  
  public int[][] intersect(int[][] A, int[][] B)
  {
      int [][] result = new int[A.length][A.length];
      for (int i=0 ; i< A.length ; i++)
      {
          for (int j=0 ; j< A.length ; j++)
          {
              if (B[i][j] < 0)
              {
                  
                  result[i][j] = A[i][j];
              }
              else
              {
                  result[i][j] = Integer.MAX_VALUE;
              }
          }
      }
      
      return result;
  }
  
  public int [][] intersect_neg(int[][] A, int [][] B)
  {
      int [][] result = new int[A.length][A.length];
      
      for (int i=0 ; i< A.length ; i++)
      {
          for (int j=0 ; j< A.length ; j++)
          {
              if (B[i][j] >= 0)
              {
                  result[i][j] = A[i][j];
              }
              else
              {
                  result[i][j] = Integer.MAX_VALUE;
              }
          }
      }
      return result;
  }
  
  public int [][] union(int[][] A, int [][] B)
  {
      int [][] result = new int[A.length][A.length];
      for (int i=0 ; i< A.length ; i++)
      {
          for (int j=0 ; j< A.length ; j++)
          {
              if (A[i][j] != Integer.MAX_VALUE)
              {
                  result[i][j] = A[i][j];
              }
              else if (A[i][j] == Integer.MAX_VALUE && B[i][j] == Integer.MAX_VALUE)
              {
                  result[i][j] = Integer.MAX_VALUE;
              }
              else if (A[i][j] == Integer.MAX_VALUE && B[i][j] != Integer.MAX_VALUE)
              {
                  result[i][j] = B[i][j];
              }
          }
      }
          
      return result;
  }
  
  public int[][] BiggerZero(int[][] C)
  {
      int[][] result = new int [C.length][C.length];
      for (int i=0; i< C.length ; i++)
      {
          for (int j=0 ; j< C.length ; j++)
          {
              if (C[i][j] >= 0)
              {
                  result[i][j] = 1;
              }
              else
              {
                  result[i][j] = 0;
              }
          }
      }

      return result;    
  }
  
  public int[][] BiggerLessThan(int[][] C, int M)
  {
      int[][] result = new int [C.length][C.length];
      for (int i=0; i< C.length ; i++)
      {
          for (int j=0 ; j< C.length ; j++)
          {
              if (C[i][j] >= 0 && C[i][j] < M)
              {
                  result[i][j] = 0;
              }
              else
              {
                  result[i][j] = 1;
              }
          }
      }
      return result;   
  }
  
  
  public int[][]  run()
  {
      
      Step_one();
      Step_two();
      Step_three();
      Step_four();
      int[][] succ = Compute_Matrix_of_Witnesses();
      return  succ;
     
  }
    
}

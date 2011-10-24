/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.shortestpath;


import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import java.lang.Math;
 /*
 * @author schwengf
 */
public class APSPAlgo {
  
  NetworkFlowModel model;
  int numNodes;  
  int maxdist;
  int[][] weight; 
  int[][] used;
  int [][] A_k; 
  int l;
  int[][] Q_l;
  int[][] P_k;
  int[][][] C;
  int[][] Delta;
  
  
  public APSPAlgo(NetworkFlowModel model)
  {
     this.model = model;
     this.numNodes = model.getGraph().numberOfNodes(); 
     this.maxdist = model.getTransitTimes().maximum();
     used = new int[numNodes][numNodes];
     for (int i=0; i< numNodes ; i++)
     {
         for (int j=0; j< numNodes ; j++)
         {
             used[i][j] = 0;
         }
     }
  }
    
  public void Step_one()
  {
    l = (int) Math.ceil(Math.log(numNodes)); 
    System.out.println("aNZAHL kNOTEN: " + l);
    double m = Math.log(maxdist);
    weight = new int[numNodes][numNodes]; // matrix D
    for (Edge edge: model.getGraph().edges())
    {
        weight[edge.start().id()][edge.end().id()] = model.getTransitTime(edge);
        used[edge.start().id()][edge.end().id()] = 1;
        System.out.println("Distance " + weight[edge.start().id()][edge.end().id()] + " " + edge.start() + "---" + edge.end());
        weight[edge.end().id()][edge.start().id()] = model.getTransitTime(edge);
        used[edge.end().id()][edge.start().id()] = 1;
       
    }
    for (int i=0; i< numNodes ; i++)
    {
        weight[i][i] = Integer.MAX_VALUE;
        
        for (int j=0; j< numNodes ; j++)
        {
            if (used[i][j] != 1)
            {
                weight[i][j] = Integer.MAX_VALUE;
            }
            System.out.println("dist " + weight[i][j] + " " + i + " " + j);
        }
    }
    
    int [][] res = distance_product(weight, weight);
    
    for (int k=1 ; k < m+2 ; k++)
    {
        weight = clip(res,0,2*maxdist);
    }
    
    
  }
  
  public void Step_two()
  {
      int [][] A_0 = new int[numNodes][numNodes];
      A_k = new int[numNodes][numNodes];
      
      for (int i=0 ; i< numNodes ; i++)
      {
          for (int j=0 ; j< numNodes ; j++)
          {
              A_0[i][j] = weight[i][j] - maxdist;
          }
      }
      A_k = distance_product(A_0, A_0);
      
      for (int k=1 ; k< l+1 ; k++)
      {
          A_k = clip(A_k,-maxdist,maxdist);
      }
  }
  
  public void Step_three()
  {
     C = new int [numNodes][numNodes][l+1];
     int[][] Q_k = new int[numNodes][numNodes];
     Q_l = new int[numNodes][numNodes];
     P_k = new int[numNodes][numNodes];
     for (int i=0 ; i< numNodes ; i++)
     {
         for (int j=0 ; j< numNodes ; j++)
         {
             C[i][j][l] = -maxdist;
             Q_l[i][j] = Integer.MAX_VALUE;
         }
     }
     int[][] P_l = new int[numNodes][numNodes];
     P_l = clip(weight, 0, maxdist);
    
     
     for (int k=l-1; k > -1 ; k--)
     {
         int[][] first = intersect(clip(distance_product(P_l,A_k), -maxdist, maxdist), C[k+1]);
         int[][] second = intersect_neg(clip(distance_product(Q_l,A_k),-maxdist,maxdist), C[k+1]);
         C[k] = union(first, second);
         P_k = union(P_l,Q_l);
         Q_k = chop(C[k],1-maxdist,maxdist); 
         Q_l = Q_k;
         P_l = P_k;
     }
    
  }
  
  public void Step_four()
  {
     int[][][] B_k = new int [numNodes][numNodes][l];

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
             R[i][j] = P_k[i][j] % maxdist;
             
             int sum = 0;
             for (int k=0; k< numNodes; k++)
             {
                 sum += B_k[i][j][k]*(int)Math.pow(2.0, (double) k);
             }
             Delta[i][j] = sum*maxdist + R[i][j];
         }
     }
     
  }
  
  public int [][] distance_product(int[][] a, int[][] b)
  {     
      int[][] result = new int[a.length][a.length];
      for (int i=0 ; i< a.length ; i++)
      {
          for (int j=0; j< a.length; j++)
          {
              int Min = Integer.MAX_VALUE;
              for (int k=0 ; k < a.length ; k++)
              {
                  if (a[i][k] + b[k][j] < Min)
                  {
                      result[i][j] = a[i][k] + b[k][j];  
                      Min = result[i][j];
                  }
              }
          }
      }
      return result;
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
      //System.out.println("A lenght: " + A.length);
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
              else if (A[i][j] == Integer.MAX_VALUE && B[i][j] == Integer.MAX_VALUE)
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
              if (C[i][j] >= 0 && C[i][j] <= M)
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
  public void run()
  {
      Step_one();
      Step_two();
      Step_three();
      Step_four();
      int[][] res = Delta;
  }
    
}

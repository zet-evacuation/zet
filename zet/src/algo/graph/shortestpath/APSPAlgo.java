/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.shortestpath;


import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.Edge;


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
  int[][] Q_l;
  int[][] P_k;
  int[][][] C;
  int[][] Delta;
  int[][] dist;
  
  
  public APSPAlgo(NetworkFlowModel model)
  {
     this.model = model;
     this.numNodes = model.getGraph().numberOfNodes() - 1; 
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
         System.out.println("Kante: " + edge + "Transitzeit: " + model.getTransitTime(edge));
         if (model.getTransitTime(edge) > this.maxdist)
         {
             this.maxdist = model.getTransitTime(edge);
         }
     }
     
  }
    
  public void Step_one()
  {
    l = (int) Math.ceil(Math.log(numNodes)/Math.log(2)); 
    System.out.println("l: " + l);
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
   
    /*weight[0][1] = 2;
    weight[0][2] = Integer.MAX_VALUE;
    weight[0][3] = Integer.MAX_VALUE;
    weight[0][4] = 3;
    weight[1][0] = 2;
    weight[1][2] = 6;
    weight[1][3] = Integer.MAX_VALUE;
    weight[1][4] = 2;
    weight[2][0] = Integer.MAX_VALUE;
    weight[2][1] = 6;
    weight[2][3] = 2;
    weight[2][4] = 1;
    weight[3][0] = Integer.MAX_VALUE;
    weight[3][1] = Integer.MAX_VALUE;
    weight[3][2] = 2;
    weight[3][4] = 3;
    weight[4][0] = 3;
    weight[4][1] = 2;
    weight[4][2] = 1;
    weight[4][3] = 3;

    
    for (int i=0; i<numNodes ; i++)
    {
        weight[i][i] = 0;
    }*/
    
    /*weight[0][1] = 2;
    weight[0][2] = Integer.MAX_VALUE;
    weight[1][0] = 2;
    weight[1][2] = 2;
    weight[2][0] = Integer.MAX_VALUE;
    weight[2][1] = 2;
    
    for (int i=0; i<numNodes ; i++)
    {
        weight[i][i] = 0;
    }*/
    
    for (int i=0; i< numNodes ; i++)
    {
        //weight[i][i] = Integer.MAX_VALUE;
        weight[i][i] = 0;
        dist[i][i] = Integer.MAX_VALUE;
        
        for (int j=0; j< numNodes ; j++)
        {
            if (used[i][j] != 1 && i != j)
            {
                weight[i][j] = Integer.MAX_VALUE;
                dist[i][j] = Integer.MAX_VALUE;
            }
            else
            {
                dist[i][j] = weight[i][j];
            }
            System.out.println("original weight " + weight[i][j] + " " + i + " " + j);
        }
    }
    
    for (int k=1 ; k < m+2 ; k++)
    {
        //System.out.println("k-te Iteration: " + k);
        //int [][] dist = distance_product(weight, weight);
        int [][] res = distance_product(weight, weight);
        weight = clip(res,0,2*maxdist);
    }
    
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
          //System.out.println("k-te Iteration: " + k);
          A_k[k] = clip(distance_product(A_k[k-1],A_k[k-1]),-maxdist,maxdist);
          /*for (int i=0 ; i< numNodes ; i++)
            {
              for (int j=0 ; j< numNodes ; j++)
              {
                  System.out.println("i:" + i + " j:" + j + "A_1: " + A_k[k][i][j]);
              }
            }*/
      }
      
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
         //System.out.println("k-te Iteration: " + k);
        
         /*for (int i=0; i< numNodes; i++)
        {
            for (int j=0 ; j< numNodes; j++)
            {
                System.out.println("i:" + i + " j:" + j + "P_l: " + P_l[i][j]);
            }
        }
         for (int i=0; i< numNodes; i++)
        {
            for (int j=0 ; j< numNodes; j++)
            {
                System.out.println("i:" + i + " j:" + j + "A_k: " + A_k[k][i][j]);
            }
        }
         for (int i=0; i< numNodes; i++)
        {
            for (int j=0 ; j< numNodes; j++)
            {
                System.out.println("i:" + i + " j:" + j + "Q_l: " + Q_l[i][j]);
            }
        }
         for (int i=0; i< numNodes; i++)
        {
            for (int j=0 ; j< numNodes; j++)
            {
                System.out.println("i:" + i + " j:" + j + "C_k+1: " + C[k+1][i][j]);
            }
        }
   
         System.out.println("_____________________________________");*/
         int[][] first = intersect(clip(distance_product(P_l,A_k[k]), -maxdist, maxdist), C[k+1]);
         int[][] second = intersect_neg(clip(distance_product(Q_l,A_k[k]),-maxdist,maxdist), C[k+1]);   
         
         C[k] = union(first, second);
         P_k = union(P_l,Q_l);      
         Q_k = chop(C[k],1-maxdist,maxdist); 
         Q_l = Q_k;
         P_l = P_k;
         if (k==0)
         {
        for (int i=0; i< numNodes; i++)
        {
            for (int j=0 ; j< numNodes; j++)
            {
                System.out.println("i:" + i + " j:" + j + "C[k]: " + C[k][i][j]);
            }
        }
        for (int i=0; i< numNodes; i++)
        {
            for (int j=0 ; j< numNodes; j++)
            {
                System.out.println("i:" + i + " j:" + j + "P_k: " + P_k[i][j]);
            }
        }
        for (int i=0; i< numNodes; i++)
        {
            for (int j=0 ; j< numNodes; j++)
            {
                System.out.println("i:" + i + " j:" + j + "Q_k: " + Q_k[i][j]);
            }
        }
         }
     }
         
  }
  
  
  public void Step_four()
  {
     int[][][] B_k = new int [l+1][numNodes][numNodes];

     int[][] R = new int[numNodes][numNodes];
     Delta = new int[numNodes][numNodes];
     for (int k=1 ; k< l+1 ; k++)
     {
         B_k[k] = BiggerZero(C[k]); 
        
         System.out.println("k-te Iteration: " + k);
          for (int i=0; i< numNodes; i++)
          {
                for (int j=0 ; j< numNodes; j++)
                {
                    System.out.println("i" + i + " j:" + j + " B_k" + B_k[k][i][j]);
                }         
           }
             
                       
     }
     for (int i=0; i< numNodes; i++)
          {
                for (int j=0 ; j< numNodes; j++)
                {
                    System.out.println("i" + i + " j:" + j + " P_k am Ende" + P_k[i][j]);
                }         
           }
     
     B_k[0] = BiggerLessThan(P_k, maxdist);
     System.out.println("Bk=0 ");
     for (int i=0; i< numNodes; i++)
          {
                for (int j=0 ; j< numNodes; j++)
                {
                    System.out.println("i" + i + " j:" + j + " B_k" + B_k[0][i][j]);
                }         
           }
     
     for (int i=0; i< numNodes; i++)
     {
         
         for (int j=0 ; j< numNodes; j++)
         {
             R[i][j] = P_k[i][j] % maxdist;
             System.out.println("i:" + i + " j:" + j + "R: " + R[i][j]);
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
     
  }
  
     public int[][] Compute_Matrix_of_Witnesses()
   {
       int [][] Delta_new_1 = new int[numNodes][numNodes]; 
       int [][] Delta_new_2 = new int[numNodes][numNodes];
       int [][] Delta_new_3 = new int[numNodes][numNodes];
       int [][] witnesses_1 = new int[numNodes][numNodes];
       int [][] witnesses_2 = new int[numNodes][numNodes];
       int [][] witnesses_3 = new int[numNodes][numNodes];
       int [][] successors = new int[numNodes][numNodes];
       
       
       for (int i=0; i< numNodes ; i++)
       {
           for (int j=0; j< numNodes ; j++)
           {
               //Delta_new_1[i][j] = Delta[i][j] % (3*maxdist);
               //System.out.println("Delta_neu: " + Delta_new_1[i][j]);
               if (Delta[i][j] % (3*maxdist) >= maxdist )
               {
                   Delta_new_1[i][j] = Delta[i][j] % (3*maxdist);
               }
               else
               {
                   Delta_new_1[i][j] = Integer.MAX_VALUE;
               }
               /*if (Delta[i][j] % 3*maxdist >= maxdist && Delta[i][j] % 3*maxdist <= 2*maxdist)
               {
                    Delta_new_2[i][j] = Delta[i][j] % 3*maxdist;
               }
               else
               {
                   Delta_new_2[i][j] = Integer.MAX_VALUE;
               }
               if (Delta[i][j] % 3*maxdist >= 2*maxdist && Delta[i][j] % 3*maxdist < 3*maxdist)
               {
                    Delta_new_3[i][j] = Delta[i][j] % 3*maxdist;
               }
               else
               {
                   Delta_new_3[i][j] = Integer.MAX_VALUE;
               }*/
           }
       }
       int [][] a_crime = new int[numNodes][numNodes];
       int [][] b_crime_1 = new int[numNodes][numNodes];
       int [][] b_crime_2 = new int[numNodes][numNodes];
       int [][] b_crime_3 = new int[numNodes][numNodes];
       
       for (int i=0; i< numNodes ; i++)
       {
           for (int j=0; j< numNodes ; j++)
           {
               if (dist[i][j] != Integer.MAX_VALUE)
               {
                   a_crime[i][j] = numNodes*dist[i][j] + j ;
               }
               else
               { 
                   a_crime[i][j] = Integer.MAX_VALUE;
               }
               //System.out.println("i:" + i + " j:" + j + " a_crime: " + a_crime[i][j]);
               if (Delta_new_1[i][j] != Integer.MAX_VALUE)
               {
                    b_crime_1[i][j] = numNodes*Delta_new_1[i][j];
               }
               else
               {
                   b_crime_1[i][j] = Integer.MAX_VALUE;
               }
               System.out.println("i:" + i + " j:" + j + "Delta_neu: " + Delta_new_1[i][j] + " b_crime: " + b_crime_1[i][j]);
               b_crime_2[i][j] = numNodes*Delta_new_2[j][i];
               b_crime_3[i][j] = numNodes*Delta_new_3[j][i];
           }
       }
       int [][] C_crime_1 = distance_product(a_crime,b_crime_1); 
        for (int i=0; i< numNodes ; i++)
       {
           for (int j=0; j< numNodes ; j++)
           {
               System.out.println("i:" + i + " j:" + j + "C_crime_1: " + C_crime_1[i][j] );
           }
       }
       int [][] C_crime_2 = distance_product(a_crime,b_crime_1);
       int [][] C_crime_3 = distance_product(a_crime,b_crime_1);
       
       for (int i=0; i< numNodes ; i++)
       {
           for (int j=0; j< numNodes ; j++)
           {
               witnesses_1[i][j] = (C_crime_1[i][j] % numNodes);
               System.out.println("i:" + i + " j:" + j + "witness 1: " + witnesses_1[i][j] );
               /*witnesses_2[i][j] = (C_crime_1[i][j] % numNodes) +1;
               System.out.println("i:" + i + " j:" + j + "witness 2: " + witnesses_1[i][j] );
               witnesses_3[i][j] = (C_crime_1[i][j] % numNodes) +1;
               System.out.println("i:" + i + " j:" + j + "witness 3: " + witnesses_1[i][j] );*/
               
               //System.out.println("Succ: " );
           }
       }
           
       return successors;
   }
  
  public int [][] distance_product(int[][] a, int[][] b)
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
                      //result[i][j] = a[i][k] + b[k][j]; 
                      //result[i][j] = sum;
                      //System.out.println("i:" + i + " j:" + j + " k:" + k + "a_i_k" + a[i][k] + "b_i_k" + b[k][j] + "result" + result[i][j]);
                      Min = sum;
                  }
                  
              }
              result[i][j] = Min;
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
  
  
  public void run()
  {
      Step_one();
      Step_two();
      Step_three();
      Step_four();
      //Compute_Matrix_of_Witnesses();
     
  }
    
}

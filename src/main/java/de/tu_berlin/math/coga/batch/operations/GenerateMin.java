/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.batch.operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.ceil;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GenerateMin {
/**********************************************
 *  GOTO (Grid On TOrus generator)            *
 *  Copyright 1991 by Andrew V. Goldberg      *
 *  Deprtment of Computer Science, Stanford U *
 *  Use permited subject to a proper          *
 *  acknowledgement.                          *
 **********************************************
 */

/*
***********************************************

 To compile under UNIX, do "cc -O goto.c -lm"

***********************************************
*/

//#include <values.h>
//#include <stdio.h>
//#include <math.h>

/* for random number generator */

final static int B = 13415821;
final static int MOD = 100000000;
final static int  MOD1 = 10000;
final static int MAXYCOST = 8 ;

/* global variables */

static int X, Y, XDEG, YDEG, seed;
static int MAXCAP, SMALLCAP, RETCOST, MAXCOST,MAXDEG;
static int N, M, M1, EXTRA_N, EXTRA_M;

static long[] pcost;
static long[] pcapacity;

static double ALPHA;
static long S, T, SUPPLY;

/* random number generator following Sedgewick's book */
static public int mult( int p, int q ) {
  int p1,p0,q1,q0;

  p1 = p / MOD1; p0 = p % MOD1;
  q1 = q / MOD1; q0 = q % MOD1;
  return((((p0 * q1 + p1 * q0) % MOD1) * MOD1 + p0 * q0) % MOD);  
}

static public int random_int( int a, int b) {

  int r;

  r = b - a;

  seed = (mult(seed, B)+1) % MOD;
  return(a + (int) (((double) seed * (double) r) / (double) MOD));

}

static long random_bit()

{
  
  return(random_int(0,1)); 

}

static int random_capacity( long dist) 
/* returns random capacity conditioned by dist */

{
double ans;

  ans = (double) random_int(1, MAXCAP);
  ans = ans / Math.pow((double) ALPHA, (double) dist-1);
  return((int) Math.ceil(ans));

}

static int random_cost( char dir )
/* returnes random cost conditioned by dist and u */

{
int ans;

  if (dir == 0) /* x direction */
    ans = random_int(0, MAXCOST);
  else
    ans = random_int(0, MAXYCOST);
  return(ans);

}

static long grid_to_id( long x, long y)
{

return((y * X) + x + 1);

}

static int node_loc( int x, int y)
{

return((y * X) + x);

}

static int arc_loc( int x1, int y1, int x2, int y2)
{
int dist;

  if (y1 == y2) {
    if (x1 < x2)
      dist = x2 - x1;
    else
      dist = X - (x1 - x2);
    return((XDEG + YDEG) * node_loc(x1, y1) + dist - 1);
  }
  else {
    if (y1 < y2)
      dist = y2 - y1;
    else
      dist = Y - (y1 - y2);
    return((XDEG + YDEG) * node_loc(x1, y1) + dist + XDEG - 1);
  }
}

static void build_example( int X, int Y, int XDEG, int YDEG) {
int x,y,z,i,u,c;

  /* generate */
  for (y = 0; y < Y; y++) {
    for (x = 0; x < X; x++) {
      /* x-direction edges */
      for (i = 1; i <= XDEG; i++){
        z = (x+i) % X;
        u = random_capacity(i);
        c = random_cost( (char)0);
        pcost[arc_loc(x, y, z, y)] = c;
        pcapacity[arc_loc(x, y, z, y)] = u;
        if ((x >= z) && (grid_to_id(x,y) != T))
          SUPPLY += pcapacity[arc_loc(x, y, z, y)];
        if (grid_to_id(z,y) == T)
          SUPPLY += pcapacity[arc_loc(x, y, z, y)];
      }
      /* y-direction edges */
      for (i = 1; i <= YDEG; i++){
        z = (y+i) % Y;
        u = random_int(1, MAXCAP);
        c = random_cost( (char)1); 
        pcost[arc_loc(x, y, x, z)] = c;
        pcapacity[arc_loc(x, y, x, z)] = u;
      }
    }
  }
  
  /* print nodes */
  System.out.format("n %8d %10d\n", S, SUPPLY);
  System.out.format("n %8d %10d\n", T, -SUPPLY);

  /* print arcs */
  for (y = 0; y < Y; y++)
    for (x = 0; x < X; x++) {
      /* x direction */
      for (i = 1; i <= XDEG; i++){
        z = (x+i) % X;
        if (z > x) {
          System.out.format("a %8d %8d %10d %10d %10d \n", 
                  grid_to_id(x, y),
                  grid_to_id(z, y),
                  0, pcapacity[arc_loc(x, y, z, y)],
                  pcost[arc_loc(x, y, z, y)]);
	}
        else {
          if (grid_to_id(x, y) != T)
            System.out.format("a %8d %8d %10d %10d %10d \n", 
                    grid_to_id(x, y),
                    T,
                    0, pcapacity[arc_loc(x, y, z, y)],
                    pcost[arc_loc(x, y, z, y)]);
          if (grid_to_id(z, y) != S)
            System.out.format("a %8d %8d %10d %10d %10d \n", 
                    S,
                    grid_to_id(z, y),
                    0, pcapacity[arc_loc(x, y, z, y)],
                    pcost[arc_loc(x, y, z, y)]);
         }
      }
      /* y direction */
      for (i = 1; i <= YDEG; i++){
        z = (y+i) % Y;
        System.out.format("a %8d %8d %10d %10d %10d \n", 
                grid_to_id(x, y),
                grid_to_id(x, z),
                0, pcapacity[arc_loc(x, y, x, z)],
                pcost[arc_loc(x, y, x, z)]);
      }
    }
  if (EXTRA_N > 0) {
    /* connect extra nodes */
    System.out.format("a %8d %8d %10d %10d %10d \n",
            S, X*Y+1,
            0, SMALLCAP, MAXCOST/2);
    System.out.format("a %8d %8d %10d %10d %10d \n",
            N, T,
            0, SMALLCAP, MAXCOST/2);
    for (i = 1; i < EXTRA_N; i++)
      System.out.format("a %8d %8d %10d %10d %10d \n",
              X*Y+i, X*Y+i+1,
              0, SMALLCAP, MAXCOST/2);

    for (i = 1; i<= EXTRA_M; i++) {
      /* select random grid node other then S,T */
      x = random_int(2, X*Y-1);
      /* select random extra node */
      y = random_int(X*Y + 1, N);
      if (random_bit() != 0)
        System.out.format("a %8d %8d %10d %10d %10d \n",
                x,y,
                0, random_capacity(XDEG), random_cost( (char)1));
      else
        System.out.format("a %8d %8d %10d %10d %10d \n",
                y,x,
                0, random_capacity(XDEG), random_cost( (char)1));
    }
  }
  else
    /* scatter extra arcs in the sceleton graph */
    for (i = 1; i <= EXTRA_M; i++) {
      /* select random grid node other then S,T */
      x = random_int(1, X*Y-1);
      /* select different grid node other then S,T */
      do
        y = random_int(1, X*Y-1);
      while (x == y);
      /* connect x and y */
      System.out.format("a %8d %8d %10d %10d %10d \n",
              x,y,
              0, random_capacity(XDEG), random_cost((char)1));
    }

  /* return path */
  for (x = 0; x <  X; x++) {
    for (y = 0; y < Y-1; y++) {
      System.out.format("a %8d %8d %10d %10d %10d \n",
              grid_to_id(x, y), grid_to_id(x, y+1),
              0, SUPPLY, RETCOST);
    }
    if (x < X-1)
      System.out.format("a %8d %8d %10d %10d %10d \n",
              grid_to_id(x, Y-1), grid_to_id(x+1, 0),
              0, SUPPLY, RETCOST);
  }
}

static int extra_arcs()

{
 
 if (EXTRA_N > 0)
   return(M - (EXTRA_N + 1)             /* to connect extra nodes */
            - X *Y*(XDEG + YDEG)        /* torus arcs */
            - Y*((XDEG*(XDEG+1))/2)     /* cut arcs */
            + 2*XDEG         /* cut arcs into S and out of T */
            - (X*Y - 1));    /* return path */
 else
   return(M - X *Y*(XDEG + YDEG)        /* torus arcs */
            - Y*((XDEG*(XDEG+1))/2)     /* cut arcs */
            + 2*XDEG         /* cut arcs into S and out of T */
            - (X*Y - 1));    /* return path */
            
}


public static void main( String[] args ) throws IOException
//int argc;
////char* argv[];

{


  
  
   InputStreamReader input = new InputStreamReader(System.in);
BufferedReader reader = new BufferedReader(input); 

  /* get input */

  //N = Integer.parseInt( reader.readLine() );
  //M = Integer.parseInt( reader.readLine() );
  //MAXCAP = Integer.parseInt( reader.readLine() );
  //MAXCOST = Integer.parseInt( reader.readLine() );
  //seed = Integer.parseInt( reader.readLine() );

  N = 15;
  M = 6*N;
  MAXCAP = 10;
  MAXCOST = 8;
  seed = 1;


/* check input for correctness */
  if (N < 15) { 
    System.err.println("ERROR: (# nodes) < 15\n"); 
    exit(3); 
  }
  if (M < 6*N) { 
    System.err.println("ERROR: (# arcs) < 6*(# nodes)\n"); 
    exit(3); 
  }
  if (M > (long) (pow((double) N, ((double) 5 / (double) 3)))) {
    System.err.println("ERROR: (#arcs) > (# nodes)^(5/3)\n"); 
    exit(3); 
  }
  if (MAXCAP < 8) {
    System.err.println("ERROR: MAXCAP < 8\n");
    exit(3); 
  }
  if (MAXCOST < 8) {
    System.err.println("ERROR: MAXCOST < 8\n");
    exit(3); 
  }

  System.out.format("c Grid example: N = %d M = %d MAXCAP = %d MAXCOST = %d SEED = %d\n",
         N, M, MAXCAP, MAXCOST, seed);


  /* initialize length, width, and remaining nodes */
  Y = 1;
  do Y++; while (Y*Y*Y <= N);
  Y--;
  X = Y*Y;
  do Y++; while (X*Y <= N);
  Y--;
  do X++; while (X*Y <= N);
  X--;

  EXTRA_N = N - X*Y;

  /* initialize degrees and remaining arcs */
  YDEG = 1;
  do {
    YDEG++;
    XDEG = YDEG*YDEG;
  } while (extra_arcs() >= 0);
  YDEG--;
  XDEG = YDEG*YDEG;
      do YDEG++; while (extra_arcs() >= 0);
      YDEG--;
  if (YDEG >= Y) YDEG = Y-1;
  do XDEG++; while (extra_arcs() >= 0);
  XDEG--;
  if (XDEG >= X) XDEG = X-1;
  
  EXTRA_M = extra_arcs();


  if (XDEG >= YDEG)
    MAXDEG = XDEG;
  else
    MAXDEG = YDEG;
  S = 1;
  T = X*Y;

  SMALLCAP = (int) ceil(sqrt((double) MAXCAP));

  /* choose cost of return path arcs */
    RETCOST = MAXCOST / Y;
  if (RETCOST == 0)
    RETCOST = 1;

  SUPPLY = SMALLCAP;

  ALPHA = pow((double) MAXCAP, (double) 1 / (double) (MAXDEG+2));

  /* allocate storage */
    pcapacity = new long[X*Y*(XDEG+YDEG)];// (long *) calloc(X*Y*(XDEG+YDEG), sizeof(long));
      pcost = new long[X*Y*(XDEG+YDEG)]; //(long *) calloc(X*Y*(XDEG+YDEG), sizeof(long));

  System.out.format("c X=%d Y=%d XDEG=%d YDEG=%d\n",
         X, Y, XDEG, YDEG);
  System.out.format("p min %d %d\n", N, M);
  build_example(X,Y,XDEG,YDEG);


}


  
}

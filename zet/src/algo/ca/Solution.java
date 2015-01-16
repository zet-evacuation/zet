/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.ca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {
  Set<Pair> pairs = new HashSet<>();
  Pair[] done;
  
    private class Node {
        Node treeParent;
        Node left;
        Node right;
        int index;
        final int data;
        
        Node parent; // for use with union find
//      int level;
//      int height;
      int rank;
        Node ancestor;
      Color colour;
              
       public Node( int number ) {
            this.data = number;
        }

      List<Node> children() {
        return adjacent( false );
      }

      List<Node> adjacent( boolean isstack ) {
        List<Node> l = new ArrayList<>();

        if( this.left != null ) {
          l.add( this.left );
//          this.left.level = this.level + 1;
        }

        if( this.right != null ) {
          l.add( this.right );
//          this.right.level = this.level + 1;
        }

        if( isstack ) {
          Collections.reverse( l );
        }

        return l;
      }

    }
    
    private class CartesianTree {
        Node root;
        Node last;
    }
    
    enum Color {White, Black};
    
    Node[] indices;
            
    public int[] solution(String S , int[] P, int[] Q) {
        // write your code in Java SE 8
        
        // Use leaste common ancestor approach
        
        // build a data structure for a cartesian tree
        
        //System.out.println( "Input Array: " + S );
      
        int[] convertedArray = convertString( S );
        //System.out.println( "Converted to int: " + Arrays.toString( convertedArray ) );
        
        indices = new Node[S.length()];
        
        CartesianTree ct = buildTree( convertedArray, indices );
        
        // We now have a tree
        
        //System.out.println( "Tarjan's Algorithm" );
        //Tarjan( ct, convertedArray.length );
        
        // build pairs
        for( int i = 0; i < P.length; ++i ) {
          Pair newPair = new Pair( indices[P[i]], indices[Q[i]]);
          newPair.i = i;
          pairs.add( newPair );
        }
        done = new Pair[P.length];
        
        LCA( ct.root );
        
        int[] solution = new int[P.length];
        for( int i = 0; i < solution.length; ++i ) {
          solution[i] = done[i].solution;
        }
        
        //System.out.println( "Solution: " + Arrays.toString( solution ) );
        
        return convertedArray;   
    }
    
    public int[] convertString( String S ) {
        int[] ret = new int[S.length()];
        for( int i = 0; i < S.length(); ++i ) {
            switch( S.charAt(i) ) {
                case 'A':
                    ret[i] = 1;
                    continue;
                case 'C':
                    ret[i] = 2;
                    continue;
                case 'G':
                    ret[i] = 3;
                    continue;
                case 'T':
                    ret[i] = 4;
                    continue;
            }
        }
        return ret;
    }
    
    public CartesianTree buildTree( int[] input, Node indices[] ) {
      CartesianTree ct = new CartesianTree();
      
      for( int i = 0; i < input.length; ++i ) {
        Node newNode = append( ct, input[i] );
        indices[i] = newNode;
        newNode.index = i;
      }
      
      return ct;
    }
    
    public Node append( CartesianTree ct, int value ) {
      Node newNode = new Node( value );
      if( ct.root == null ) {
        // first node becomes root
        ct.root = newNode;
      } else {
        iterateUpAndInsert( ct, ct.last, newNode);
      }
      ct.last = newNode;
      return newNode;
    }
    
    public void iterateUpAndInsert( CartesianTree ct, Node start, Node newNode ) {
      Node y = start;
      Node x = newNode;
      do {
        if( y == null ) {
          // we are at the root. current root becomes left child
          //System.out.println( "Setting " + newNode.data + " as new root and " + start.data + " as left child." );
          newNode.left = start;
          start.treeParent = newNode;
          ct.root = newNode;
          return;
        } else if( y.data == x.data ) {
          addRightChild( y, x );
          return;
        } else if( y.data < x.data ) {
          addRightChild( y, x );
          return;
        } else {
          y = y.treeParent;
        }
      } while(true);
    }
    
    public void addRightChild( Node node, Node child ) {
      //System.out.println( "Adding " + child.data + " as right child of " + node.data );
      if( node.right != null ) {
        //System.out.println( "Moving " + node.right.data + " to the left of " + child.data );
      }
      child.treeParent = node;
      child.left = node.right;
      node.right = child;
    }

  void LCA( Node u ) {
    MakeSet( u );
    Find( u ).ancestor = u;
    for( Node v : u.children() ) {
      LCA( v );
      Union( u, v );
      Find( u ).ancestor = u;
    }
    u.colour = Color.Black;
    Node v;
    Set<Pair> newPairs = new HashSet<>();
    for( Pair uv : pairs ) {
      //if( uv.u() != u && uv.v() != u ) {
       // continue;
      //}
      Node newU;
      newU = uv.u();
      v = uv.v();
      if( v.colour == Color.Black
              && newU != null && v != null
              && Find( newU ) == Find( v ) // required, but algorithm [3th Ed. p584] doesn't have it.
              ) {
        newPairs.add( uv );
        //System.out.println( "Tarjan's Lowest Common Ancestor of {" + newU.data + ", " + v.data + "}: " + Find( v ).ancestor.data + " with intdex " + Find( v ).ancestor.index );
        uv.solution = Find( v ).ancestor.data;
      }
    }
    for( Pair p : newPairs ) {
      pairs.remove( p );
      done[p.i] = p;
    }
  }
  
  void MakeSet( Node x ) {
    x.parent = x;
    x.rank = 0;
  }

  void Union( Node x, Node y ) {
    Link( Find( x ), Find( y ) );
  }

  void Link( Node x, Node y ) {
    if( x.rank > y.rank ) {
      y.parent = x;
    } else {
      x.parent = y;
      if( x.rank == y.rank ) {
        y.rank += 1;
      }
    }
  }

  Node Find( Node x ) {
    if( x != x.parent ) {
      x.parent = Find( x.parent );
    }

    return x.parent;
  }
  
  
  class Pair {
    Node u;
    Node v;
    int i;
    int solution;

    Pair( Node u, Node v ) {
      this.u = u;
      this.v = v;
    }

    List<Pair> pair() {
      List<Pair> list = new ArrayList<>();
      list.add( this );
      return list;
    }

    Node u() {
      return this.u;
    }

    Node v() {
      return this.v;
    }
  }

    public static void main(String [] args) {
      Solution ct = new Solution();
      int[] P = {2,5,0};
      int[] Q = {4,5,6};
      ct.solution( "CAGCCTA", P, Q );
    }
}

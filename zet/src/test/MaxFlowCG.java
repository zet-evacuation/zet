package test;

import de.tu_berlin.math.coga.algorithm.shortestpath.Dijkstra;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.container.mapping.IdentifiableObjectMapping;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.StaticPath;
import ds.graph.flow.PathBasedFlow;
import ds.graph.flow.StaticPathFlow;
import ds.graph.network.Network;
import gurobi.GRB;
import gurobi.GRBConstr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import java.util.LinkedList;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class MaxFlowCG {
  GRBEnv env;
  GRBModel model;
  IdentifiableObjectMapping<Edge, GRBConstr> edgeConstraint;
  Network network;
  IdentifiableIntegerMapping<Edge> capacities;
  Node s;
  Node t;
  int pathCount = 0;

  int maxFlow = 0;
  PathBasedFlow solution = new PathBasedFlow();
  LinkedList<PathVariableTuple> usedPaths = new LinkedList<>();
  
  public MaxFlowCG() throws GRBException {
    network = new Network( 4, 5 );

    s = network.getNode( 0 );
    Node v = network.getNode( 1 );
    Node w = network.getNode( 2 );
    t = network.getNode( 3 );

    Edge e1 = network.createAndSetEdge( s, v );
    Edge e2 = network.createAndSetEdge( v, t );
    Edge e3 = network.createAndSetEdge( v, w );
    Edge e4 = network.createAndSetEdge( s, w );
    Edge e5 = network.createAndSetEdge( w, t );

    capacities = new IdentifiableIntegerMapping<>( 5 );
    capacities.set( e1, 2 );
    capacities.set( e2, 1);
    capacities.set( e3, 1 );
    capacities.set( e4, 1 );
    capacities.set( e5, 2 );

    env = new GRBEnv( "maxflowcg.log" );
    model = new GRBModel( env );

    edgeConstraint = new IdentifiableObjectMapping<>( 5, GRBConstr.class );
  }

  /**
   * Initializes the constraints for each of the arcs in the network. The
   * constraints are empty and only get filled with variables if new paths
   * are added to the model.
   * @throws GRBException if a problem occurs
   */
  public void initializeModel() throws GRBException {
    // Define the variables and so on
    model.setObjective( new GRBLinExpr(), GRB.MAXIMIZE );

    // create constraints (without variables yet) for each arc
    for( Edge e : network.edges() ) {
      GRBConstr c0 = model.addConstr( new GRBLinExpr(), GRB.LESS_EQUAL,
              capacities.get( e ), "e" + e.id() );
      edgeConstraint.set( e, c0 );
    }

    model.update();
    model.optimize();
  }

  /**
   * Solves the maximum flow problem by iterativley adding variables and calling
   * the Gurobi LP solver.
   * @throws GRBException if a problem occurs
   */
  public void solve() throws GRBException {
    StaticPath p = findImprovingPath();
    while( p.length() > 0 ) {
      addColumn( p );
      model.optimize();
      p = findImprovingPath();
    }
    maxFlow = (int)model.get( GRB.DoubleAttr.ObjVal );
  }

  /**
   * Computes a shortest path according to the dual variables (or shadow costs).
   * If the costs are smaller than 1, a new path can be added as variable and is
   * returned. Otherwise, an empty path is returned.
   * @return the shortest path if its length is < 1, empty path otherwise.
   * @throws GRBException if something does not work
   */
  private StaticPath findImprovingPath() throws GRBException {
    IdentifiableIntegerMapping<Edge> costs =
            new IdentifiableIntegerMapping<>( capacities.getDomainSize() );

    for( Edge e : network.edges() ) {
      GRBConstr c0 = edgeConstraint.get( e );
      double shadow_price = c0.get( GRB.DoubleAttr.Pi );
      costs.set( e, (int) shadow_price );
    }

    Dijkstra d = new Dijkstra( network, costs, s );
    d.run();
    double dist = d.getDistance( t );

    // calculate the shortest path
    StaticPath p = new StaticPath();
    if( dist >= 1 )
      return p;

    p.addFirstEdge( d.getLastEdge( t ) );
    while( !p.first().start().equals( s ) ) {
      p.addFirstEdge( d.getLastEdge( p.first().start() ) );
    }
    
    return p;
  }

  /**
   * Adds a new path as a variable to the optimization model. The variable is
   * introduced to the constraint belonging to the edges on the path.
   * @param path the path
   * @throws GRBException if something went wrong 
   */
  public void addColumn( StaticPath path ) throws GRBException {
    GRBVar pathVar = model.addVar( 0.0, GRB.INFINITY, 1.0, GRB.CONTINUOUS, "p" + pathCount++ );
    model.update();
    for( Edge e : path ) {
      model.chgCoeff( edgeConstraint.get( e ), pathVar, 1.0 );
    }
    model.update();
    usedPaths.add( new PathVariableTuple( path, pathVar ) );
  }
  
  /**
   * Generates a path based flow solution.
   * @throws GRBException if variable access doesn't work
   */
  public void buildSolution() throws GRBException {
    for( PathVariableTuple pathVariable : usedPaths ) {
      double value = pathVariable.var.get( GRB.DoubleAttr.X );
      if( value > 0.1 ) {
        solution.addPathFlow( new StaticPathFlow( pathVariable.p, (int)value ) );
      }
    }
  }

  public static void main( String... args ) throws GRBException {
    MaxFlowCG cg = new MaxFlowCG();
    cg.initializeModel();
    cg.solve();
    cg.buildSolution();
    System.out.println( cg.solution );
  }


  /**
   * Stores a {@link StaticPath} and the according {@code GRBVar} representing
   * the variable in the LP model. Used to retrieve the flow value on a path in
   * an optimal solution.
   */
  private class PathVariableTuple {
    StaticPath p;
    GRBVar var;

    public PathVariableTuple( StaticPath p, GRBVar var ) {
      this.p = p;
      this.var = var;
    }
  }
}


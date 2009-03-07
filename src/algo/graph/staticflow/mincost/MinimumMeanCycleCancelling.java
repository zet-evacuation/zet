
package algo.graph.staticflow.mincost;

import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.StaticPath;
import util.DebugFlags;
import algo.graph.staticflow.StaticTransshipment;
import ds.graph.Mappings;
import ds.graph.ResidualNetwork;

import localization.Localization;
import tasks.AlgorithmTask;

/**
 * The class <code>MinimumMeanCycleCancelling</code> 
 * implments the Algorithms with the same name from
 * "Combinatorial Optimization" by "Bernhard Korte, Jens Vygen"
 * The Algorithms can be called either synchronously using 
 * the static compute method, or asynchronously using the 
 * runnable interface
 */
public class MinimumMeanCycleCancelling implements Runnable
{

    //A variable to store the reult of the calculation
    //only necessary for asynchronous calls
    //synchronous calls return the result direclty
    private IdentifiableIntegerMapping<Edge> result = null;

    public IdentifiableIntegerMapping<Edge> getResult()
    {
        return result;
    }
    //some prvate field to store the problem instance
    private Network network;
    private IdentifiableIntegerMapping<Edge> edgeCapacities;
    private IdentifiableIntegerMapping<Edge> costs;
    private IdentifiableIntegerMapping<Node> supplies;

    /**
     * This constructore is used the the to prepare the object for
     * asynchronous calls.
     * @param network The network.
     * @param edgeCapacities The capacities of all edges in the network.
     * @param supplies Supplies and demands of all nodes in the network.
     * @param cost The cost asigned to each edge
     */
    public MinimumMeanCycleCancelling(Network network,
            IdentifiableIntegerMapping<Edge> edgeCapacities,
            IdentifiableIntegerMapping<Edge> costs,
            IdentifiableIntegerMapping<Node> supplies)
    {
        this.network = network;
        this.edgeCapacities = edgeCapacities;
        this.costs = costs;
        this.supplies = supplies;
    }

    public static IdentifiableIntegerMapping<Edge> compute(Network network,
            IdentifiableIntegerMapping<Edge> edgeCapacities,
            IdentifiableIntegerMapping<Edge> costs,
            IdentifiableIntegerMapping<Node> supplies)
    {
        MinimumMeanCycleCancelling instance =
                new MinimumMeanCycleCancelling(
                network,
                edgeCapacities,
                costs,
                supplies);
        instance.compute();
        return instance.getResult();

    }

    public void compute()
    {
        //Do we have everything we need?
        m_invalidate();

        // 1. Find a b-flow.               
        StaticTransshipment trans = new StaticTransshipment(network, edgeCapacities, supplies);
        trans.run();

        //get the residual network
        ResidualNetwork residualNetwork = trans.getResidualNetwork();

        if (residualNetwork == null)
        {
            //Invalid instance.
            //Die gracefully.
            result = null;
            AlgorithmTask.getInstance().publish("Minimum mean cycle cancelling algorithm",
                    "WARNING: no transshipments were found!");
            return;
        }

        IdentifiableIntegerMapping<Edge> residualCosts =
                m_expandCostFunction(costs, network, residualNetwork);

        //Repeate this loop as long as we still have MMC to augment
        while (true)
        {

            
            //Debugging Code
            ////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////
            if (DebugFlags.MEL_LONG)
            {
                System.out.print("Residual network:" + residualNetwork);
                System.out.println("Costs: " + Mappings.toString
                        (residualNetwork.edges(), residualCosts));
            }
            if (DebugFlags.MEL && !DebugFlags.MEL_LONG)
            {
                System.out.println("The residual network has " + 
                        residualNetwork.nodes().size() + " nodes and " + 
                        residualNetwork.edges().size() + " edges.");
            }
            if (DebugFlags.MEL)
            {
                System.out.println("Calling Minimum Mean Cycle Detector.");
            }
            ////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////


            // 2. Find a minimal circuit
            StaticPath staticPath = MinimumMeanCycleDetector.detect(residualNetwork, residualCosts);

            //break if acyclic
            if (staticPath == null)
            {
                break;
            }

            //break if this cycle has a non-negative total weight
            int totalWeight = 0;
            for (Edge edge : staticPath)
            {
                totalWeight += residualCosts.get(edge);
            }

            if (totalWeight >= 0)
            {
                if (DebugFlags.MEL_LONG)
                {
                    System.out.println("Impossible to augment furter - Optimal Flow!\n");
                }

                break;
            }

            //compute gamma as the minimum over all rest capacities of the cycle
            int gamma = Integer.MAX_VALUE;
            for (Edge edge : staticPath)
            {
                gamma = Math.min(residualNetwork.residualCapacities().get(edge), gamma);
            }

            if (DebugFlags.MEL_LONG)
            {
                System.out.println("Augmenting along Static Path:" + staticPath + " by Gamma='" + gamma + "'\n");
            }

            //augment along C by gamma
            for (Edge edge : staticPath)
            {
                residualNetwork.augmentFlow(edge, gamma);
            }
        }
        //Save the result
        result = (IdentifiableIntegerMapping<Edge>) residualNetwork.flow().clone();
    }

    
    public void run()
    {
        compute();
    }

    private void m_invalidate()
    {
        if (getNetwork() == null)
            throw new NullPointerException(Localization.getInstance (
			).getString ("algo.graph.staticflow.mincost.NetworkNullException"));
        
        if (getEdgeCapacities() == null)
            throw new NullPointerException(Localization.getInstance (
			).getString ("algo.graph.staticflow.mincost.CapacitiesNullException"));
        
        if (getCosts() == null)
            throw new NullPointerException(Localization.getInstance (
			).getString ("algo.graph.staticflow.mincost.CostsNullException"));
        
        if (getSupplies() == null)
            throw new NullPointerException(Localization.getInstance (
			).getString ("algo.graph.staticflow.mincost.SuppliesNullException"));
    }

    
    
    // The get an set method for the problem instance properties
    public Network getNetwork()
    {
        return network;
    }

    public void setNetwork(Network network)
    {
        this.network = network;
    }

    public IdentifiableIntegerMapping<Edge> getEdgeCapacities()
    {
        return edgeCapacities;
    }

    public void setEdgeCapacities(IdentifiableIntegerMapping<Edge> edgeCapacities)
    {
        this.edgeCapacities = edgeCapacities;
    }

    public IdentifiableIntegerMapping<Edge> getCosts()
    {
        return costs;
    }

    public void setCosts(IdentifiableIntegerMapping<Edge> costs)
    {
        this.costs = costs;
    }

    public IdentifiableIntegerMapping<Node> getSupplies()
    {
        return supplies;
    }

    public void setSupplies(IdentifiableIntegerMapping<Node> supplies)
    {
        this.supplies = supplies;
    }

    /**
     * This method expand the given cost function over some network to cover 
     * also the residual network
     * @param oldCosts The old cost function to be expanded
     * @param sNetwork The network.
     * @param resNetwork The residual network
     * @return an new costs function that is identical with the old function
     * on the old domain. On all other edges in the residual network it returns
     * either the ngated cost of the oposite edge if it exists or 0.
     */
    public IdentifiableIntegerMapping<Edge> m_expandCostFunction(
            IdentifiableIntegerMapping<Edge> oldCosts,
            Network sNetwork,
            ResidualNetwork resNetwork)
    {
        resNetwork = resNetwork.clone();
        resNetwork.showAllEdges();
        IdentifiableIntegerMapping<Edge> newCosts =
                oldCosts.clone();

        if (newCosts.getDomainSize() < resNetwork.getEdgeCapacity())
        {
            newCosts.setDomainSize(resNetwork.getEdgeCapacity());
        }

        for (Edge edge : resNetwork.edges())
        {
            if (edge != null)
            {
                if (sNetwork.contains(edge))
                    newCosts.set(edge, oldCosts.get(edge));
                else
                {
                    Edge opEdge = sNetwork.getEdge(edge.end(), edge.start());
                    if (opEdge != null)
                        newCosts.set(edge, -oldCosts.get(opEdge));
                    else
                        newCosts.set(edge, 0);
                }
            }
        }
        return newCosts;
    }
}

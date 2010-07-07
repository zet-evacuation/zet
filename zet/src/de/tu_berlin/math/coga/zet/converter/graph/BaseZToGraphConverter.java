/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import converter.RasterContainerCreator;
import converter.ZToGraphMapping;
import converter.ZToGraphRasterContainer;
import converter.ZToGraphRasterSquare;
import converter.ZToGraphRoomRaster;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.util.Level;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.PropertyContainer;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableDoubleMapping;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;
import ds.z.BuildingPlan;
import ds.z.PlanPoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author gross
 */
public abstract class BaseZToGraphConverter extends Algorithm<BuildingPlan, NetworkFlowModel> {

    protected IdentifiableDoubleMapping<Edge> exactTransitTimes;
    protected ZToGraphMapping mapping;
    protected NetworkFlowModel model;
    protected BuildingPlan plan;
    protected ZToGraphRasterContainer raster;

    @Override
    protected NetworkFlowModel runAlgorithm(BuildingPlan problem) {
        mapping = new ZToGraphMapping();
        model = new NetworkFlowModel();
        plan = problem;
        raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer(plan);
        mapping.setRaster(raster);
        model.setZToGraphMapping(mapping);

        createNodes();
        // create edges, their capacities and the capacities of the nodes
        createEdgesAndCapacities();
        // connect the nodes of different rooms with edges
        //Hashtable<Edge, ArrayList<ZToGraphRasterSquare>> doorEdgeToSquare = connectRooms(raster, model);
        // calculate the transit times for all edges
        //computeTransitTimes(raster, model, doorEdgeToSquare);
        // dublicate the edges and their transit times (except those concerning the super sink)
        createReverseEdges(model);
        // adjust transit times according to stair speed factors
        multiplyWithUpAndDownSpeedFactors();

        model.setNetwork(model.getGraph().getAsStaticNetwork());
        model.setTransitTimes(exactTransitTimes.round());
        return model;
    }

    protected abstract void createNodes();

    protected abstract void createEdgesAndCapacities();

    protected abstract void computeTransitTimes();

    protected void createReverseEdges(NetworkFlowModel model) {
        int edgeIndex = model.getGraph().numberOfEdges();
        model.setNumberOfEdges(edgeIndex * 2 - model.getGraph().degree(model.getSupersink()));
        for (Edge edge : model.getGraph().edges()) {
            if (!edge.isIncidentTo(model.getSupersink())) {
                Edge newEdge = new Edge(edgeIndex++, edge.end(), edge.start());
                mapping.setEdgeLevel(newEdge, mapping.getEdgeLevel(edge).getInverse());
                model.setEdgeCapacity(newEdge, model.getEdgeCapacity(edge));
                model.setTransitTime(newEdge, model.getTransitTime(edge));
                model.getGraph().setEdge(edge);
            }
        }
    }

    protected void multiplyWithUpAndDownSpeedFactors() {
        for (Edge edge : model.getGraph().edges()) {
            if (!edge.isIncidentTo(model.getSupersink())) {
                switch (mapping.getEdgeLevel(edge)) {
                    case Higher:
                        exactTransitTimes.divide(edge, mapping.getUpNodeSpeedFactor(edge.start()));
                        break;
                    case Lower:
                        exactTransitTimes.divide(edge, mapping.getDownNodeSpeedFactor(edge.start()));
                        break;
                }
            }
        }
    }
}

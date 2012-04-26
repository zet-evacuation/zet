/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input.converter;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.BaseZToGraphConverter;
import ds.z.Project;
import gui.GraphConverterAlgorithms;

/**
 *
 * @author gross
 */
public class ProjectToNetworkFlowModelConverter extends Algorithm<Project, NetworkFlowModel> {
    
    @Override
    protected NetworkFlowModel runAlgorithm(Project problem) {        
        GraphConverterAlgorithms last = GraphConverterAlgorithms.NonGridGraph;
        BaseZToGraphConverter conv = last.converter();
        conv.setProblem( problem.getBuildingPlan() );
        conv.run();       
        return conv.getSolution();
    }
}

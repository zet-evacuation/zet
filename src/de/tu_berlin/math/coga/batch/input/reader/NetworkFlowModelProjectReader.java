/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input.reader;

import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.BaseZToGraphConverter;
import ds.z.Project;
import gui.AlgorithmControl;
import gui.GraphConverterAlgorithms;

/**
 *
 * @author gross
 */
public class NetworkFlowModelProjectReader extends InputProjectReader<NetworkFlowModel> {

    private String[] properties;

    @Override
    public String[] getProperties() {
        if (properties == null) {
            properties = new String[2];
            if (!isProblemSolved()) {
                NetworkFlowModel model = runAlgorithm(getProblem());
                properties[0] = "" + model.getNetwork().numberOfNodes();
                properties[1] = "" + model.getNetwork().numberOfEdges();
            } else {
                properties[0] = "" + getSolution().getNetwork().numberOfNodes();
                properties[1] = "" + getSolution().getNetwork().numberOfEdges();
            }
        }
        return properties;
    }

    @Override
    protected NetworkFlowModel runAlgorithm(Project project) {        
        GraphConverterAlgorithms last = GraphConverterAlgorithms.NonGridGraph;
        final BaseZToGraphConverter conv = last.converter();
        conv.setProblem( project.getBuildingPlan() );
        conv.run();       
        return conv.getSolution();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch;

import algo.graph.staticflow.maxflow.EdmondsKarp;
import algo.graph.staticflow.maxflow.PushRelabelHighestLabel;
import de.tu_berlin.math.coga.batch.input.FileCrawler;
import de.tu_berlin.math.coga.batch.input.converter.MaximumFlowProblemConverter;
import de.tu_berlin.math.coga.batch.input.reader.InputFileReader.Optimization;
import de.tu_berlin.math.coga.batch.input.reader.RMFGENMaximumFlowFileReader;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.flow.MaximumFlow;
import ds.graph.problem.MaximumFlowProblem;
import ds.graph.problem.RawMaximumFlowProblem;
import java.io.File;

/**
 *
 * @author Martin
 */
public class Test {

    public static void main(String[] args) {
        FileCrawler crawler = new FileCrawler(false, false);
        for (File file : crawler.listFiles(new File("d:\\test"))) {
            System.out.print(file.getName());
            RMFGENMaximumFlowFileReader reader = new RMFGENMaximumFlowFileReader();
            reader.setFile(file);
            reader.setOptimization(Optimization.SPEED);
            reader.run();
            RawMaximumFlowProblem rawInstance = reader.getSolution();
            MaximumFlowProblemConverter converter = new MaximumFlowProblemConverter();
            converter.setProblem(rawInstance);
            converter.run();
            MaximumFlowProblem instance = converter.getSolution();
            //System.out.println(instance.getSink());
            //System.out.println(instance.getSource());
            //for (int i = 0; i < instance.getNetwork().numberOfEdges(); i++) {
            //   System.out.println(instance.getNetwork().edges().get(i) + " " + instance.getCapacities().get(instance.getNetwork().edges().get(i)));
            //}            
            Algorithm<MaximumFlowProblem, MaximumFlow> algo = new PushRelabelHighestLabel();
            algo.setProblem(instance);
            algo.run();
            System.out.println(": " + (algo.getSolution().getFlowValue() * 1.0 / rawInstance.getScaling()));
        }
    }
}

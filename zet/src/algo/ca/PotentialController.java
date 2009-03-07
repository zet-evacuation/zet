/*
 * Created on 23.01.2008
 *
 */
package algo.ca;

import java.util.ArrayList;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.ExitCell;
import ds.ca.PotentialManager;
import ds.ca.StaticPotential;
import ds.ca.TargetCell;

/**
 * @author Daniel Pluempe
 *
 */
public interface PotentialController {
    public CellularAutomaton getCA();
    public void setCA(CellularAutomaton ca);
    public PotentialManager getPm();
    public void setPm(PotentialManager pm);
    public void updateDynamicPotential(double diffusion, double decay);
    public StaticPotential mergePotentials(ArrayList<StaticPotential> potentialsToMerge);
    public void increaseDynamicPotential (Cell cell);
    public void decreaseDynamicPotential (Cell cell);
    public StaticPotential calculateStaticPotential(ArrayList<ExitCell> exitBlock);
    public StaticPotential getRandomStaticPotential();
    public StaticPotential getNearestExitStaticPotential(Cell c);
    public String dynamicPotentialToString();
    public void generateSafePotential();
}

/*
 * Created on 21.01.2008
 *
 */
package algo.ca.rule;

import ds.ca.CAController;

/**
 * @author Daniel Pluempe
 *
 */
public interface Rule {
    public void execute(ds.ca.Cell cell);
    public boolean executableOn(ds.ca.Cell cell);
    public void setCAController(CAController caController);
}

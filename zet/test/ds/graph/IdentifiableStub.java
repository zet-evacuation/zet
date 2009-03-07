/*
 * IdentifiableStub.java
 *
 * Created on 27. November 2007, 19:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ds.graph;

import ds.graph.*;

/**
 *
 * @author mouk
 */
public final class IdentifiableStub implements Identifiable {
    
    /** Creates a new instance of IdentifiableStub */
    private int _id = -1;
    public int id()
    {
        return _id;
    }
    public void setId(int value)
    {
        _id = value;
    }
    public IdentifiableStub clone()
    {
        return null;
    }
    public IdentifiableStub()
    {
    }
    public IdentifiableStub(int value)
    {
        _id = value;
    }
    
}

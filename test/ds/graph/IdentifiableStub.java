/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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

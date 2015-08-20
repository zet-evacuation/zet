/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.visualization.util;

import gui.visualization.control.FlowHistroryTriple;
import java.util.ArrayList;

/**
 *
 * @author mouk
 */
public class FlowCalculator
{

    protected ArrayList<Double> _list;

    public FlowCalculator()
    {
        _list = new ArrayList<Double>();

    }

    public void add(int time, double flow, int duration)
    {
        if (time < 0)
            throw new IllegalArgumentException("Time can't be negative");
        if (flow < 0)
            throw new IllegalArgumentException("Flow can't be negative");
        if (duration < 0)
            throw new IllegalArgumentException("Duration can't be negative");
        
        if(duration == 0 )
            return;

        //the smallest unit of duration is one, which mean the flow flows
        //for only one time unit

        duration--;

        Double placeHolder = new Double(0d);
        int size = getList().size();
        int capacity = time + duration;

        //Not necessary but may be more efficent
        getList().ensureCapacity(capacity);
        while (size++ <= capacity)
            getList().add(placeHolder);


        for (int x = time; x <= time + duration; x++)
        {
            Double oldValue = getList().get(x);


            //Update the value
            oldValue = new Double(oldValue.doubleValue() + flow);
            //store the new value
            getList().set(x, oldValue);

        }
    }

    public ArrayList<FlowHistroryTriple> getCalculatedFlow()
    {
        ArrayList<FlowHistroryTriple> retList =
                new ArrayList<FlowHistroryTriple>();
        if (_list.size() == 0)
            return retList;

        int time = 0;
        Double flow = _list.get(0);
        int duration = 1;

        for (int i = 1; i < _list.size(); i++)
        {
            double nValue = _list.get(i).doubleValue();
            if (nValue != flow)
            {
                //Add the last flow to return list
                FlowHistroryTriple tripel =
                        new FlowHistroryTriple( time, flow, duration);
                
                retList.add(tripel);

                //Lets start a new constant-flow
                time = i;
                flow = _list.get(i).doubleValue();
                duration = 1;
            }
            else
            {
                //Just increase the duration
                duration++;
            }
            
        }
        //Add last element if exists
        if(flow.doubleValue() > 0 )
            retList.add(new FlowHistroryTriple( time, flow, duration));
            

        return retList;
    }

    public ArrayList<Double> getList()
    {
        return _list;
    }
}

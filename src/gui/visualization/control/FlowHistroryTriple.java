/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the 
template in the editor.
 */
package gui.visualization.control;


/**
 *
 * @author mouk
 */
public class FlowHistroryTriple extends VisHistoryTriple<Integer, Double, Integer>
{
    public FlowHistroryTriple(int time, double flow, int duration)
    {
        super(new Integer(time), new Double(flow), new Integer(duration));

        _time = time;
        _flow = flow;
        _duration = duration;
    }
    
    public FlowHistroryTriple(int time, Double flow, int duration)
    {
        super(new Integer(time), flow, new Integer(duration));

        _time = time;
        _flow = flow;
        _duration = duration;
    }

    public FlowHistroryTriple(Integer time, Double flow, Integer duration)
    {
        super(time, flow, duration);

        _time = time.intValue();
        _flow = flow.doubleValue();
        _duration = duration.intValue();
    }

    private int _time;
    private double _flow;
    private int _duration;

    public int getTime()
    {
        return _time;
    }

    public void setTime(int time)
    {
        this._time = time;
        super.setFirstValue(new Integer(time));
    }

    public double getFlow()
    {
        return _flow;
    }

    public void setFlow(double flow)
    {
        this._flow = flow;
        super.setSecondValue(new Double(flow));
    }

    public int getDuration()
    {
        return _duration;
    }

    public void setDuration(int duration)
    {
        this._duration = duration;
        super.setThirdValue(new Integer(duration));
    }

    /**
     * Sets the first value to vale of parameter v1
     * @param v1
     */
    @Override
    public void setFirstValue(Integer v1)
    {
        super.setFirstValue(v1);
        _time = v1.intValue();
    }

    /**
     * Sets the second value to vale of parameter v2
     * @param v2
     */
    @Override
    public void setSecondValue(Double v2)
    {
        setSecondValue(v2);
        _flow = v2.doubleValue();
    }

    /**
     * Sets the third value to vale of parameter v3
     * @param v3
     */
    @Override
    public void setThirdValue(Integer v3)
    {
        setThirdValue(v3);
        _duration = v3.intValue();
    }

    
    
}

package com.mardpop.jrocket.vehicle.gnc;

import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class SimpleGNC 
{    
    protected final Vec3 moment = new Vec3();

    public void update(double time) 
    {
        // to override
    }

    public Vec3 getControlMoment()
    {
        return moment;
    }
}

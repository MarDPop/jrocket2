package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class FrameAcceleration 
{    
    protected final Vec3 acceleration = new Vec3(0, 0, -9.806);
    
    public Vec3 getAcceleration(double height, Vec3 velocity)
    {
        return this.acceleration;
    }
}

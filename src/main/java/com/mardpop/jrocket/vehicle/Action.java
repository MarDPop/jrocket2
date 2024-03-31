package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class Action 
{
    public final Vec3 force = new Vec3();
    
    public final Vec3 position = new Vec3();
    
    public final Vec3 moment = new Vec3();   
    
    public Vec3 getTorque(Vec3 CG)
    {
        return Vec3.add(this.moment,Vec3.cross(Vec3.subtract(this.position, CG), this.force));
    }
    
}

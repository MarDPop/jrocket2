package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.*;

/**
 *
 * @author mariu
 */
public class Propulsion 
{
    public final Thruster thruster;
    
    public final Vec3 thrustVector = new Vec3(1,0,0);
    
    public final Vec3 thrustPostion = new Vec3();
    
    public final Inertia fuel_inertia = new Inertia();
    
    public Propulsion(Thruster thruster)
    {
        this.thruster = thruster;
    }
    
    
}

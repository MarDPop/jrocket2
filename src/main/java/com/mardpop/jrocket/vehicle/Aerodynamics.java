package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.*;

/**
 *
 * @author mariu
 */
public class Aerodynamics 
{
    public final Vec3 force = new Vec3();
    
    public final Vec3 position = new Vec3();
    
    public final Vec3 moment = new Vec3();
    
    protected double mach;
    
    protected double airspeed;
    
    protected double dynamicPressure;
    
    protected final Vec3 unitVector = new Vec3();
    
    public void update(Rocket rocket, Air air)
    {
        
    }
    
}

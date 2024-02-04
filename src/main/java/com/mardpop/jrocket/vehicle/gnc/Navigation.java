package com.mardpop.jrocket.vehicle.gnc;

import com.mardpop.jrocket.vehicle.Rocket;

/**
 *
 * @author mariu
 */
public class Navigation 
{
    protected final Rocket rocket;
    
    protected double[] values;
    
    Navigation(Rocket rocket)
    {
        this.rocket = rocket;
    }
    
    final double[] getValues()
    {
        return this.values;
    }
    
    void update(double time)
    {
        // for override
    }
    
}

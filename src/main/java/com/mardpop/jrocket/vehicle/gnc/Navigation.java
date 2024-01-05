package com.mardpop.jrocket.vehicle.gnc;

import com.mardpop.jrocket.vehicle.Rocket;
import java.util.ArrayList;

/**
 *
 * @author mariu
 */
public class Navigation 
{
    protected final Rocket rocket;
    
    protected double[] values;
    
    public ArrayList<Sensor> sensors = new ArrayList<>();
    
    Navigation(Rocket rocket)
    {
        this.rocket = rocket;
    }
    
    void update(double time)
    {
        
    }
    
}

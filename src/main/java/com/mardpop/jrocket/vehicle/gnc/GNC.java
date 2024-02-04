package com.mardpop.jrocket.vehicle.gnc;

import com.mardpop.jrocket.vehicle.Rocket;

/**
 *
 * @author mariu
 */
public class GNC 
{
    protected Guidance guidance;
    
    protected Navigation navigation;
    
    protected Control control;
    
    public GNC(Rocket rocket)
    {
        this.navigation = new Navigation(rocket);
        this.guidance = new Guidance(this.navigation);
        this.control = new Control(this.guidance);
    }
    
    public void set(Guidance guidance, Navigation navigation, Control control)
    {
        this.guidance = guidance;
        this.navigation = navigation;
        this.control = control;
    }
    
    public void update(double time)
    {
        this.navigation.update(time);
    }
}

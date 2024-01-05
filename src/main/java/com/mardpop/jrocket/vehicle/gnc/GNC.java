package com.mardpop.jrocket.vehicle.gnc;

/**
 *
 * @author mariu
 */
public class GNC 
{
    protected Guidance guidance;
    
    protected Navigation navigation;
    
    protected Control control;
    
    public void set(Guidance guidance, Navigation navigation, Control control)
    {
        this.guidance = guidance;
        this.navigation = navigation;
        this.control = control;
    }
    
    public void update(double time)
    {
        
    }
}

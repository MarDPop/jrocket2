package com.mardpop.jrocket.vehicle.gnc;

/**
 *
 * @author mariu
 */
public class Guidance 
{
    protected final Navigation navigation;
    
    protected final double[] navigationValues;
    
    protected final double[] guidanceValues;
    
    public Guidance(Navigation navigation)
    {
        this.navigation = navigation;
        this.navigationValues = navigation.getValues();
        this.guidanceValues = new double[this.navigationValues.length];
    }
    
    public void update(double time)
    {
        
    }
}

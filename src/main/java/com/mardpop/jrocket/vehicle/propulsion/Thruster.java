package com.mardpop.jrocket.vehicle.propulsion;

/**
 *
 * @author mariu
 */
public class Thruster 
{    
    protected double thrust;
    
    protected double massRate;
    
    public Thruster(){}
    
    public Thruster(double thrust, double ISP)
    {
        this.thrust = thrust;
        this.massRate = thrust/(ISP*9.806);
    }
    
    public void update(double pressure, double time) 
    {
        // for override
    }
    
    public double getThrust()
    {
        return this.thrust;
    }
    
    public double getMassRate()
    {
        return this.massRate;
    }
}

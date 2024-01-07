package com.mardpop.jrocket.vehicle;

/**
 *
 * @author mariu
 */
public class Thruster 
{    
    protected double thrust;
    
    protected double massRate;
    
    public Thruster(){}
    
    public Thruster(double thrust, double massRate)
    {
        this.thrust = thrust;
        this.massRate = massRate;
    }
    
    public void update(double pressure, double time) {}
    
    public double getThrust()
    {
        return this.thrust;
    }
    
    public double getMassRate()
    {
        return this.massRate;
    }
}

package com.mardpop.jrocket.vehicle;

/**
 *
 * @author mariu
 */
public class SolidThruster extends Thruster 
{
    protected FuelTank solidFuel;
    
    public FuelTank getFuel()
    {
        return this.solidFuel;
    }
}

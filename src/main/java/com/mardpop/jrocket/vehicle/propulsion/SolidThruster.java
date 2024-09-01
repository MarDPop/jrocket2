package com.mardpop.jrocket.vehicle.propulsion;

/**
 *
 * @author mariu
 */
public class SolidThruster extends Thruster 
{
    protected PropellantTank solidFuel;
    
    public PropellantTank getFuel()
    {
        return this.solidFuel;
    }

    public Propulsion toPropulsion()
    {
        return new Propulsion(this, this.solidFuel);
    }
}

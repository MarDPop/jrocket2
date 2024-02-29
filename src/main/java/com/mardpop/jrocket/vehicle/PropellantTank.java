package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public abstract class PropellantTank extends Component
{
    public final Propellant fuel;
    
    protected Vec3 centerOfMassChange;
    
    PropellantTank(Propellant fuel)
    {
        this.fuel = fuel;
    }
    
    public abstract void setMass(double mass);
    
    public void takePropellant(double dm)
    {
        this.setMass(mass - dm);
    }
    
    @Override
    public Vec3 getCenterOfMass()
    {
        return Vec3.add(this.centerOfMass, this.centerOfMassChange);
    }
}

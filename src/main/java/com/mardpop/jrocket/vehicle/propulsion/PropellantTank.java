package com.mardpop.jrocket.vehicle.propulsion;

import com.mardpop.jrocket.util.Vec3;
import com.mardpop.jrocket.vehicle.Component;
import com.mardpop.jrocket.vehicle.Inertia;

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
        this.setMass(this.inertia.mass - dm);
    }
    
    public Vec3 getCenterOfMass()
    {
        return Vec3.add(this.inertia.COM, this.centerOfMassChange);
    }
}

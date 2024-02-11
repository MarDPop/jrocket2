package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public abstract class FuelTank extends Component
{
    public final Fuel fuel;
    
    protected double I_rr;
    
    protected double I_xx;
    
    protected Vec3 centerOfMassChange;
    
    FuelTank(Fuel fuel)
    {
        this.fuel = fuel;
    }
    
    public abstract void setMass(double mass);
    
    public abstract void update(double dm);
    
    @Override
    public double[] getInertia()
    {
        return new double[]{this.I_xx, this.I_rr, this.I_rr};
    }
    
    @Override
    public Vec3 getCenterOfMass()
    {
        return Vec3.add(this.centerOfMass, this.centerOfMassChange);
    }
}

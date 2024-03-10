package com.mardpop.jrocket.atmosphere;

import com.mardpop.jrocket.util.Matrix3;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class AerodynamicQuantities 
{
    private double airspeed;

    private double zeta;
    
    private double dynamicPressure;

    private double mach;
    
    private final Vec3 unitVectorBody = new Vec3();
    
    private final Vec3 momentVectorBody = new Vec3();
    
    private final Vec3 liftVectorBody = new Vec3();
    
    public void update(Vec3 velocity, Matrix3 CS, Air air, Vec3 wind)
    {
        Vec3 relativeWind = Vec3.subtract(wind, velocity);
        Matrix3.mult(CS, relativeWind,this.unitVectorBody);
        this.airspeed = this.unitVectorBody.magnitude();
        this.mach = this.airspeed*air.getInvSoundSpeed();
        this.dynamicPressure = 0.5*air.getDensity()*this.airspeed*this.airspeed;
        
        this.unitVectorBody.scale(1.0/(this.airspeed + 1e-24));
        
        double offXcomponent = unitVectorBody.y()*unitVectorBody.y() + unitVectorBody.z()*unitVectorBody.z();
        this.zeta = Math.sqrt(offXcomponent + 1e-32); // note this is not angle of attack
        double t = 1.0/this.zeta;
        this.momentVectorBody.y(this.unitVectorBody.z()*t);
        this.momentVectorBody.z(-this.unitVectorBody.y()*t);
        
        this.liftVectorBody.fromCross(this.momentVectorBody, this.unitVectorBody);
    }
    
    public double getAirspeed()
    {
        return airspeed;
    }

    public double getZeta()
    {
        return zeta;
    }

    public double getAngleOfAttack()
    {
        return Math.atan2(this.unitVectorBody.z(), this.unitVectorBody.x());
    }
    
    public double getDynamicPressure()
    {
        return dynamicPressure;
    }

    public double getMach()
    {
        return mach;
    }
    
    public Vec3 getUnitVectorBody()
    {
        return unitVectorBody;
    }

    public Vec3 getLiftVectorBody()
    {
        return liftVectorBody;
    }
    
    public Vec3 getMomentVectorBody()
    {
        return momentVectorBody;
    }
}

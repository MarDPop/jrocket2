package com.mardpop.jrocket.atmosphere;

import com.mardpop.jrocket.util.Matrix3;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class AerodynamicQuantities 
{
    private double mach;
    
    private double airspeed;
    
    private double dynamicPressure;
    
    private final Vec3 unitVectorBody = new Vec3();
    
    private final Vec3 momentVectorBody = new Vec3();
    
    private final Vec3 liftVectorBody = new Vec3();
    
    public void update(Vec3 velocity, Matrix3 CS, Air air, Vec3 wind)
    {
        Vec3 relativeWind = Vec3.subtract(wind, velocity);
        this.unitVectorBody.mult(CS, relativeWind);
        this.airspeed = this.unitVectorBody.magnitude();
        this.unitVectorBody.scale(1.0/this.airspeed);
        this.mach = this.airspeed*air.getInvSoundSpeed();
        this.dynamicPressure = 0.5*air.getDensity()*this.airspeed*this.airspeed;
        
        double t = 1.0/Math.sqrt(unitVectorBody.y()*unitVectorBody.y() + unitVectorBody.z()*unitVectorBody.z());
        this.momentVectorBody.y(-this.unitVectorBody.z()*t);
        this.momentVectorBody.z(this.unitVectorBody.y()*t);
        
        this.liftVectorBody.fromCross(this.unitVectorBody, this.momentVectorBody);
    }
    
    public double getMach()
    {
        return mach;
    }
    
    public double getAirspeed()
    {
        return airspeed;
    }
    
    public double getDynamicPressure()
    {
        return dynamicPressure;
    }
    
    public Vec3 getUnitVectorBody()
    {
        return unitVectorBody;
    }
    
}

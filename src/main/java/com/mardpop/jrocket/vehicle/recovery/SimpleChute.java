package com.mardpop.jrocket.vehicle.recovery;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.util.Vec3;

public class SimpleChute 
{
    public static final double CD = 1.2;

    public final double CDA;

    public final double deployTime;

    public final double deploymentDuration;

    private final double constantFactor;

    public SimpleChute(double area, double deployTime, double deploymentDuration)
    {
        this.CDA = area*CD;
        this.deployTime = deployTime;
        this.deploymentDuration = deploymentDuration;
        this.constantFactor = CDA/deploymentDuration;
    }

    public Vec3 bodyForce(double time, final AerodynamicQuantities aero)
    {
        if(time < deployTime)
        {
            return null;
        }
        double CD;
        if(time > deployTime + deploymentDuration)
        {
            CD = this.CDA;
        }   
        else
        {
            CD = this.constantFactor*(time - deployTime);
        }

        return Vec3.mult(aero.getUnitVectorBody(), CD*aero.getDynamicPressure());
    }
    
}

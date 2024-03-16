package com.mardpop.jrocket.vehicle.aerodynamics;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class AerodynamicsBallistic extends Aerodynamics 
{
    public final double CDA;
    
    public AerodynamicsBallistic(double CD, double A)
    {
        this.CDA = CD*A;
    }
    
    @Override
    public void update(AerodynamicQuantities aero)
    {
        double drag = this.CDA*aero.getDynamicPressure();
        Vec3 wind = aero.getUnitVectorBody();
        this.force.x = wind.x*drag;
        this.force.y = wind.y*drag;
        this.force.z = wind.z*drag;
    }
}

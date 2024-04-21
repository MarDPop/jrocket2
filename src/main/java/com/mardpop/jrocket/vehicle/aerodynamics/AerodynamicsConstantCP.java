package com.mardpop.jrocket.vehicle.aerodynamics;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.util.Vec3;

public class AerodynamicsConstantCP extends Aerodynamics
{
    public final double CD0;
    
    public final double CN;

    public AerodynamicsConstantCP(final double CD0, final double CN, final double CPx, 
        final double ReferenceArea)
    {
        this.CD0 = CD0*ReferenceArea;
        this.CN = 0*CN*ReferenceArea;
        this.position.x = CPx;
    }

    @Override
    public void update(final AerodynamicQuantities aero)
    {
        final double drag = this.CD0*aero.getDynamicPressure();
        final double lift = this.CN*aero.getDynamicPressure();

        final Vec3 wind = aero.getUnitVectorBody();
        final Vec3 liftVector = aero.getLiftVectorBody();
        this.force.x = liftVector.x*lift + wind.x*drag;
        this.force.y = liftVector.y*lift + wind.y*drag;
        this.force.z = liftVector.z*lift + wind.z*drag;
    }
}

package com.mardpop.jrocket.vehicle.aerodynamics;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.util.Vec3;

public class AerodynamicsConstantCP extends Aerodynamics
{
    public final double CD0;
    
    public final double CN;

    public AerodynamicsConstantCP(double CD0, double CN, double CPx, double ReferenceArea)
    {
        this.CD0 = CD0*ReferenceArea;
        this.CN = -0*CN*ReferenceArea;
        this.position.x = CPx;
    }

    @Override
    public void update(AerodynamicQuantities aero)
    {
        final double drag = this.CD0*aero.getDynamicPressure();
        final double lift = this.CN*aero.getDynamicPressure();

        Vec3 wind = aero.getUnitVectorBody();
        Vec3 liftVector = aero.getLiftVectorBody();
        this.force.x = liftVector.x*lift + wind.x*drag;
        this.force.y = liftVector.y*lift + wind.y*drag;
        this.force.z = liftVector.z*lift + wind.z*drag;
    }
}

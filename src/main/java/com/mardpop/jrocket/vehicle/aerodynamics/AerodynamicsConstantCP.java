package com.mardpop.jrocket.vehicle.aerodynamics;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.util.Vec3;

public class AerodynamicsConstantCP extends Aerodynamics
{
    private final double CN;

    private final double CD0;

    public AerodynamicsConstantCP(double CP, double CN, double CD0)
    {
        this.CN = CN;
        this.CD0 = CD0;
        this.position.x = CP;
    }

    @Override
    public void update(AerodynamicQuantities aero)
    {
        double drag = this.CD0*aero.getDynamicPressure();
        double lift = this.CN*aero.getDynamicPressure();

        Vec3 wind = aero.getUnitVectorBody();
        Vec3 liftVector = aero.getLiftVectorBody();
        this.force.x = wind.x*drag + liftVector.x*lift;
        this.force.y = wind.y*drag + liftVector.y*lift;
        this.force.z = wind.z*drag + liftVector.z*lift;
    }
}

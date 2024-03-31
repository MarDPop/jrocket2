package com.mardpop.jrocket.vehicle.aerodynamics;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.util.TableD;
import com.mardpop.jrocket.util.Vec3;

public class AerodynamicsTable extends Aerodynamics 
{
    private final TableD coef;

    public AerodynamicsTable(TableD coef)
    {
        this.coef = coef;
    }

    @Override
    public void update(AerodynamicQuantities aero)
    {
        double[] coef = this.coef.get(aero.getZeta());

        double drag = coef[0]*aero.getDynamicPressure();
        double lift = coef[1]*aero.getDynamicPressure();
        double moment = coef[2]*aero.getDynamicPressure();

        Vec3 wind = aero.getUnitVectorBody();
        Vec3 liftVector = aero.getLiftVectorBody();
        Vec3 momentVector = aero.getMomentVectorBody();
        
        this.force.x = wind.x*drag + liftVector.x*lift;
        this.force.y = wind.y*drag + liftVector.y*lift;
        this.force.z = wind.z*drag + liftVector.z*lift;
        this.moment.x = moment*momentVector.x;
        this.moment.y = moment*momentVector.y;
        this.moment.z = moment*momentVector.z;

    }
}

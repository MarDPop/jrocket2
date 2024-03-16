package com.mardpop.jrocket.vehicle.aerodynamics;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.util.Vec3;

public class AerodynamicsBasicCoefficients extends Aerodynamics {

    public final double CD0;

    public final double CL_alpha;

    public final double CM_alpha;

    public final double K;
    
    /**
     * Constructor
     * @param CD0
     * @param CL_alpha
     * @param CM_alpha
     * @param K
     * @param Aref
     * @param Lref
     */
    public AerodynamicsBasicCoefficients(double CD0, double CL_alpha, double CM_alpha,
        double K, double Aref)
    {
        this.CD0 = CD0*Aref;
        this.CL_alpha = CL_alpha*Aref;
        this.CM_alpha = CM_alpha*Aref;
        this.K = K/Aref;
    }
    
    @Override
    public void update(AerodynamicQuantities aero)
    {
        double zeta = aero.getZeta();
        double CL = this.CL_alpha*zeta;
        double CD = this.CD0 + this.K*CL*CL;

        zeta = Math.min(zeta, 0.1);
        double CM = this.CM_alpha*zeta;
        CL = this.CL_alpha*zeta;

        double drag = CD*aero.getDynamicPressure();
        double lift = CL*aero.getDynamicPressure();
        double moment = CM*aero.getDynamicPressure();
        Vec3 windVector = aero.getUnitVectorBody();
        Vec3 liftVector = aero.getLiftVectorBody();
        Vec3 momentVector = aero.getMomentVectorBody();

        this.force.x = liftVector.x*lift + windVector.x*drag;
        this.force.y = liftVector.y*lift + windVector.y*drag;
        this.force.z = liftVector.z*lift + windVector.z*drag;

        this.moment.x = momentVector.x*moment;
        this.moment.y = momentVector.y*moment;
        this.moment.z = momentVector.z*moment;
    }
    
}

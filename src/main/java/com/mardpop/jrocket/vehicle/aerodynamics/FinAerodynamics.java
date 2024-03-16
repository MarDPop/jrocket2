package com.mardpop.jrocket.vehicle.aerodynamics;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class FinAerodynamics extends Aerodynamics
{
    private double CD0;
    
    private double K;
    
    private double dCMdalpha;
    
    private double dCLdalpha;
    
    private double sRef;
    
    private double dCLdel;
    
    private double dCDdel;
    
    private double dCMdel;
    
    private double dCRdel;
    
    private double finARef;
    
    private double finXLoc;
    
    private double finCOPRadial;
    
    private final double[] deflections;
    
    private final double[] finDirectionY;
    
    private final double[] finDirectionZ;
    
    public FinAerodynamics(int numFins)
    {
        this.deflections = new double[numFins];
        this.finDirectionY = new double[numFins];
        this.finDirectionZ = new double[numFins];
        
        double deltaAngle = Math.PI*2.0/numFins;
        for(int i = 0; i < numFins; i++)
        {
            this.finDirectionY[i] = Math.cos(deltaAngle*i);
            this.finDirectionZ[i] = Math.sin(deltaAngle*i);
        }
    }
    
    public void setCoef(double CD0, double K, double dCMdalpha, double dCLdalpha, double aRef, double lRef)
    {
        this.CD0 = CD0*aRef;
        this.K = K/aRef;
        this.dCLdalpha = dCLdalpha*aRef;
        this.dCMdalpha = dCMdalpha*aRef*lRef;
        this.sRef = aRef;
    }
    
    public void setFinCoef(double dCDdel, double dCLdel, double dCMdel, double aRef, double xLoc, double finCOPRadial)
    {
        this.dCDdel = dCDdel*aRef;
        this.dCLdel = dCLdel*aRef;
        this.dCMdel = dCMdel*aRef + this.dCLdel*xLoc;
        this.finARef = aRef;
        this.finXLoc = xLoc;
        this.finCOPRadial = finCOPRadial;
        this.dCRdel = this.dCLdalpha*finCOPRadial;
    }
    
    public void setDeflection(int i, double angle)
    {
        this.deflections[i] = angle;
    }
    
    public double getDeflection(int i)
    {
        return this.deflections[i];
    }
    
    @Override
    public void update(AerodynamicQuantities aero)
    {
        
        for(int iFin = 0; iFin < this.deflections.length; iFin++)
        {
            double angleDyn = aero.getDynamicPressure()*this.deflections[iFin];
            double finLift = this.dCLdel*angleDyn;
            double finDrag = this.dCDdel*angleDyn;
            double finTorque = this.dCMdel*angleDyn;
            double finXTorque = this.dCRdel*angleDyn;

            this.force.x = finDrag;
            this.force.y = -finLift*this.finDirectionZ[iFin];
            this.force.z = finLift*this.finDirectionY[iFin];
            
            this.moment.x = finXTorque;
            this.moment.y = finTorque*this.finDirectionY[iFin];
            this.moment.z = finTorque*this.finDirectionZ[iFin];
        }
        
        double momentArmY = -aero.getUnitVectorBody().z;
        double momentArmZ = aero.getUnitVectorBody().y;
        double alpha = Math.sqrt(momentArmY*momentArmY + momentArmZ*momentArmZ);
        double CL = this.dCLdalpha*alpha;
        double CD = this.CD0 + this.K*CL*CL;
        double CM = this.dCMdalpha*alpha;
        
        Vec3 drag = Vec3.mult(aero.getUnitVectorBody(), CD*aero.getDynamicPressure());
        this.force.subtract(drag);
        
        Vec3 lift = Vec3.cross(aero.getUnitVectorBody(), new Vec3(0,momentArmY,momentArmZ));
        lift.scale(CL*aero.getDynamicPressure()/lift.magnitude());
        this.force.add(lift);
        
        double bMoment = CM*aero.getDynamicPressure();
        this.moment.y = this.moment.y + bMoment*momentArmY;
        this.moment.z = this.moment.z + bMoment*momentArmZ;
        
        
    }
}

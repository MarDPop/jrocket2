package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Util;
import java.util.ArrayList;

/**
 *
 * @author mariu
 */
public class PropellantTankInterpolated extends PropellantTank 
{
    private final double[] fuelMasses;
    
    private final double[] fuelInertias; // Ixx, Irr, CG
    
    private final double[] deltaInertia;
    
    private int mIdx;
    
    public PropellantTankInterpolated(Propellant fuel, ArrayList<Double> fuelMasses, ArrayList<Double> fuelInertias)
    {
        super(fuel);
        
        if(fuelMasses.size()*3 != fuelInertias.size())
        {
            throw new IllegalArgumentException("fuel masses and inertias must have correct lengths");
        }
        
        if(!Util.isSorted(fuelMasses))
        {
            throw new IllegalArgumentException("please provide fuel masses in descending order");
        }
        
        this.fuelMasses = new double[fuelMasses.size()];
        this.fuelInertias = new double[fuelInertias.size()];
        for(int i = 0; i < this.fuelMasses.length; i++)
        {
            this.fuelMasses[i] = fuelMasses.get(i);
        }
        for(int i = 0; i < this.fuelInertias.length; i++)
        {
            this.fuelInertias[i] = fuelInertias.get(i);
        }
        
        this.deltaInertia = new double[this.fuelInertias.length];
        for(int i = 1; i < this.fuelMasses.length; i++)
        {
            double invDm = 1.0/(this.fuelMasses[i] - this.fuelMasses[i - 1]);
            int idx = (i - 1)*3;
            this.deltaInertia[idx] = (this.fuelInertias[idx] - this.fuelInertias[idx - 3])*invDm;
            this.deltaInertia[idx + 1] = (this.fuelInertias[idx + 1] - this.fuelInertias[idx - 2])*invDm;
            this.deltaInertia[idx + 2] = (this.fuelInertias[idx + 2] - this.fuelInertias[idx - 1])*invDm;
        }
    }

    @Override
    public void setMass(double mass) {
        this.mass = mass;
        if(mass < 0)
        {
            this.mass = 0;
            this.inertia.Ixx = 0;
            this.inertia.Iyy = 0;
            this.inertia.Izz = 0;
            return;
        }
        
        if(mass > this.fuelMasses[0])
        {
            this.mass = this.fuelMasses[0];
            this.inertia.Ixx = this.fuelInertias[0];
            this.inertia.Iyy = this.fuelInertias[1];
            this.inertia.Izz = this.fuelInertias[2];
            this.mIdx = 0;
            return;
        }
        
        while(this.fuelMasses[this.mIdx + 1] > this.mass)
        {
            this.mIdx++;
        }
        
        while(this.fuelMasses[this.mIdx] < this.mass)
        {
            this.mIdx--;
        }
        
        int idx = this.mIdx*3;
        double dM = this.mass - this.fuelMasses[this.mIdx];
        this.inertia.Ixx = this.fuelInertias[idx] + this.deltaInertia[idx]*dM;
        this.inertia.Izz = this.inertia.Iyy = this.fuelInertias[idx + 1] + this.deltaInertia[idx + 1]*dM;
        this.centerOfMassChange.x(this.fuelInertias[idx + 2] + this.deltaInertia[idx + 2]*dM);
    }
    
}

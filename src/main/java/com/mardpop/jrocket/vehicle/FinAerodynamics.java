package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;

/**
 *
 * @author mariu
 */
public class FinAerodynamics extends Aerodynamics
{
    private final double[] deflections;
    
    FinAerodynamics(int numFins)
    {
        this.deflections = new double[numFins];
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
        
    }
}

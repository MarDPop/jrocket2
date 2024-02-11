package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class Component
{
    protected double mass;
    
    protected Vec3 centerOfMass;
    
    protected final double[] inertia = new double[3];
    
    public double getMass()
    {
        return this.mass;
    }
    
    public Vec3 getCenterOfMass()
    {
        return this.centerOfMass;
    }
    
    public double[] getInertia()
    {
        return inertia;
    }
    
    public double[] getInertiaFromPoint(Vec3 point)
    {
        Vec3 arm = Vec3.subtract(point, this.centerOfMass);
        double d = arm.dot(arm);
        return this.inertia;
    }
    
}

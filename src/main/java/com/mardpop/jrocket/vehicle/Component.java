package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Matrix3;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class Component
{
    protected double mass;
    
    protected Vec3 centerOfMass;
    
    protected Matrix3 orientation;
    
    protected final Inertia inertia = new Inertia();
    
    public double getMass()
    {
        return this.mass;
    }
    
    public Vec3 getCenterOfMass()
    {
        return this.centerOfMass;
    }
    
    public Inertia getInertia()
    {
        return inertia;
    }
    
    public Inertia getInertiaFromPoint(Vec3 point)
    {
        Vec3 arm = Vec3.subtract(point, this.centerOfMass);
        double x2 = arm.x()*arm.x();
        double y2 = arm.y()*arm.y();
        double z2 = arm.z()*arm.z();
        double xy = -arm.x()*arm.y();
        double xz = -arm.x()*arm.z();
        double yz = -arm.y()*arm.z();

        
        
        return this.inertia;
    }
    
}

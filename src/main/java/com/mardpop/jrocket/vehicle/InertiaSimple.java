package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Matrix3;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class InertiaSimple 
{
    double mass;
    
    double Ixx;
    
    double Irr;
    
    double CGx;
    
    
    void combine(InertiaSimple a, InertiaSimple b)
    {
        this.mass = a.mass + b.mass;
        double invMass = 1.0/this.mass;
        this.CGx = (a.CGx*a.mass + b.CGx*b.mass)*invMass;
        this.Irr = a.Irr + b.Irr;
        double dx_a = a.CGx - this.CGx;
        double dx_b = b.CGx - this.CGx;
        this.Ixx = a.Ixx + b.Ixx + a.mass*dx_a*dx_a + b.mass*dx_b*dx_b; 

    }
}

package com.mardpop.jrocket.vehicle;

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
    
    public InertiaSimple() {}
    
    public InertiaSimple(double mass, double Ixx, double Irr, double CGx)
    {
        this.mass = mass;
        this.Ixx = Ixx;
        this.Irr = Irr;
        this.CGx = CGx;
    }
    
    public void copy(InertiaSimple other)
    {
        this.mass = other.mass;
        this.Ixx = other.Ixx;
        this.Irr = other.Irr;
        this.CGx = other.CGx;
    }
    
    public void combine(InertiaSimple a, InertiaSimple b)
    {
        this.mass = a.mass + b.mass;
        double invMass = 1.0/this.mass;
        this.CGx = (a.CGx*a.mass + b.CGx*b.mass)*invMass;
        this.Irr = a.Irr + b.Irr;
        double dxa = a.CGx - this.CGx;
        double dxb = b.CGx - this.CGx;
        this.Ixx = a.Ixx + b.Ixx + a.mass*dxa*dxa + b.mass*dxb*dxb; 

    }
}

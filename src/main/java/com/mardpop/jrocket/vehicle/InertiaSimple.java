package com.mardpop.jrocket.vehicle;

/**
 *
 * @author mariu
 */
public class InertiaSimple 
{
    public double mass;
    
    public double Ixx;
    
    public double Irr;
    
    public double CGx;

    public InertiaSimple() {}
    
    public InertiaSimple(double mass, double Ixx, double Irr, double CGx)
    {
        this.mass = mass;
        this.Ixx = Ixx;
        this.Irr = Irr;
        this.CGx = CGx;
    }

    public InertiaSimple(InertiaSimple a, InertiaSimple b)
    {
        this.mass = a.mass + b.mass;
        this.CGx = (a.CGx*a.mass + b.CGx*b.mass)/this.mass;
        this.Ixx = a.Ixx + b.Ixx;
        double dxa = a.CGx - this.CGx;
        double dxb = b.CGx - this.CGx;
        this.Irr = a.Irr + b.Irr + a.mass*dxa*dxa + b.mass*dxb*dxb; 
    }
    
    public void copy(InertiaSimple other)
    {
        this.mass = other.mass;
        this.Ixx = other.Ixx;
        this.Irr = other.Irr;
        this.CGx = other.CGx;
    }
}

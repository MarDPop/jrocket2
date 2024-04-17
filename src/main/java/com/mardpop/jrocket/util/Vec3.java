package com.mardpop.jrocket.util;

/**
 *
 * @author mariu
 */
public class Vec3 {
    
    public double x;

    public double y;

    public double z;
    
    public Vec3(){}
    
    public Vec3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vec3(double[] v)
    {
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
    }
    
    public Vec3(Vec3 v)
    {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    public void zero()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    
    public void set(Vec3 v)
    {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    public void add(Vec3 v)
    {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }
    
    public void subtract(Vec3 v)
    {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
    }
    
    public void scale(double scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
    }
    
    public static Vec3 add(Vec3 a, Vec3 b) 
    {
        return new Vec3(a.x + b.x, a.y + b.y, a.z + b.z);
    }
    
    public static Vec3 subtract(Vec3 a, Vec3 b)
    {
        return new Vec3(a.x - b.x, a.y - b.y, a.z - b.z);
    }
    
    public static Vec3 mult(Vec3 a, double m)
    {
        return new Vec3(a.x*m, a.y*m, a.z*m);
    }
    
    public static Vec3 cross(Vec3 a, Vec3 b) 
    {
        return new Vec3(a.y*b.z - a.z*b.y,
            a.z*b.x - a.x*b.z,
            a.x*b.y - a.y*b.x);
    }
    
    public void fromCross(Vec3 a, Vec3 b) 
    {
        this.x = a.y*b.z - a.z*b.y;
        this.y = a.z*b.x - a.x*b.z;
        this.z = a.x*b.y - a.y*b.x;
    }
    
    public double dot(Vec3 v)
    {
        return v.x*this.x + v.y*this.y + v.z*this.z;
    }
    
    public double magnitude()
    {
        return Math.sqrt(x*x + y*y + z*z);
    }
    
    public void normalize() 
    {
        this.scale(1.0/magnitude());
    }
    
    public double[] data()
    {
        return new double[]{x,y,z};
    }
    
    @Override
    public String toString()
    {
        return String.format("[%.6e, %.6e, %.6e]", this.x, this.y, this.z);
    }
}

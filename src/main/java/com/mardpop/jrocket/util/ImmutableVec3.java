package com.mardpop.jrocket.util;

public class ImmutableVec3 
{   
    public final double x;

    public final double y;

    public final double z;
    
    public ImmutableVec3()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    
    public ImmutableVec3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public ImmutableVec3(double[] v)
    {
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
    }
    
    public ImmutableVec3(ImmutableVec3 v)
    {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public ImmutableVec3(Vec3 v)
    {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    public ImmutableVec3 plus(Vec3 b) 
    {
        return new ImmutableVec3(x + b.x, y + b.y, z + b.z);
    }
    
    public ImmutableVec3 minus(Vec3 b)
    {
        return new ImmutableVec3(x - b.x, y - b.y, z - b.z);
    }
    
    public ImmutableVec3 mult(double m)
    {
        return new ImmutableVec3(x*m, y*m, z*m);
    }
    
    public ImmutableVec3 cross(ImmutableVec3 b) 
    {
        return new ImmutableVec3(y*b.z - z*b.y,
            z*b.x - x*b.z,
            x*b.y - y*b.x);
    }
    
    public double dot(ImmutableVec3 v)
    {
        return v.x*x + v.y*y + v.z*z;
    }
    
    public double magnitude()
    {
        return Math.sqrt(x*x + y*y + z*z);
    }
    
    public ImmutableVec3 getUnit() 
    {
        double n = 1.0/Math.sqrt(x*x + y*y + z*z);
        return new ImmutableVec3(x*n, y*n, z*n);
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

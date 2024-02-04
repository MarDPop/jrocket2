package com.mardpop.jrocket.util;

import java.util.Arrays;

/**
 *
 * @author mariu
 */
public class Vec3 {
    
    final double[] data = new double[3];
    
    public Vec3(){}
    
    public Vec3(double x, double y, double z)
    {
        this.data[0] = x;
        this.data[1] = y;
        this.data[2] = z;
    }
    
    public Vec3(double[] v)
    {
        System.arraycopy(v, 0, this.data, 0, 3);
    }
    
    public Vec3(Vec3 v)
    {
        System.arraycopy(v.data, 0, this.data, 0, 3);
    }
    
    public void zero()
    {
        Arrays.fill(this.data, 0);
    }
    
    public void set(Vec3 v)
    {
        System.arraycopy(v.data, 0, this.data, 0, 3);
    }
    
    public void add(Vec3 v)
    {
        data[0] += v.data[0];
        data[1] += v.data[1];
        data[2] += v.data[2];
    }
    
    public void subtract(Vec3 v)
    {
        data[0] -= v.data[0];
        data[1] -= v.data[1];
        data[2] -= v.data[2];
    }
    
    public void scale(double scalar)
    {
        data[0] *= scalar;
        data[1] *= scalar;
        data[2] *= scalar;
    }
    
    public static Vec3 add(Vec3 a, Vec3 b) 
    {
        return new Vec3(a.data[0] + b.data[0], a.data[1] + b.data[1], a.data[2] + b.data[2]);
    }
    
    public static Vec3 subtract(Vec3 a, Vec3 b)
    {
        return new Vec3(a.data[0] - b.data[0], a.data[1] - b.data[1], a.data[2] - b.data[2]);
    }
    
    public static Vec3 mult(Vec3 a, double m)
    {
        return new Vec3(a.data[0]*m, a.data[1]*m, a.data[2]*m);
    }
    
    public static Vec3 cross(Vec3 a, Vec3 b) 
    {
        return new Vec3(a.data[1]*b.data[2] - a.data[2]*b.data[1],
            a.data[2]*b.data[0] - a.data[0]*b.data[2],
            a.data[0]*b.data[1] - a.data[1]*b.data[0]);
    }
    
    public void fromCross(Vec3 a, Vec3 b) 
    {
        this.data[0] = a.data[1]*b.data[2] - a.data[2]*b.data[1];
        this.data[1] = a.data[2]*b.data[0] - a.data[0]*b.data[2];
        this.data[2] = a.data[0]*b.data[1] - a.data[1]*b.data[0];
    }
    
    public double dot(Vec3 v)
    {
        return v.data[0]*this.data[0] + v.data[1]*this.data[1] + v.data[2]*this.data[2];
    }
    
    public double magnitude()
    {
        return Math.sqrt(data[0]*data[0] + data[1]*data[1] + data[2]*data[2]);
    }
    
    public void normalize() 
    {
        this.scale(1.0/magnitude());
    }
    
    public double[] data()
    {
        return this.data;
    }
    
    public double x()
    {
        return this.data[0];
    }
    
    public double y() 
    {
        return this.data[1];
    }
    
    public double z() 
    {
        return this.data[2];
    }
    
    public void x(double x) 
    {
        this.data[0] = x;
    }
    
    public void y(double y)
    {
        this.data[1] = y;
    }
    
    public void z(double z)
    {
        this.data[2] = z;
    }
    
    @Override
    public String toString()
    {
        return String.format("[%f, %f, %f]", this.data[0], this.data[1], this.data[2]);
    }
}

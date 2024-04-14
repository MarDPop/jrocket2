package com.mardpop.jrocket.util;

/**
 *
 * @author mariu
 */
public class Quaternion {

    public double x;

    public double y;

    public double z;

    public double w;

    public Quaternion() {}

    public Quaternion(double i, double j, double k, double w) {
        this.x = i;
        this.y = j;
        this.z = k;
        this.w = w;
    }

    public Quaternion(Quaternion p)
    {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
        this.w = p.w;
    }
    
    public void set(Quaternion p) 
    {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
        this.w = p.w;
    }

    public Vec3 vector() {
        return new Vec3(x,y,z);
    }

    public double scalar() {
        return this.w;
    }
    
    public double[] data()
    {
        return new double[]{w,x,y,z};
    }

    public Quaternion conjugate() 
    {
        return new Quaternion(-this.x,-this.y,-this.z,this.w);
    }

    public double norm() 
    {
        return Math.sqrt(x*x + y*y + z*z + w*w);
    } 

    public static Quaternion add(Quaternion a, Quaternion b)
    {
        return new Quaternion(a.x + b.x, a.y + b.y,
             a.z + b.z, a.w + b.w);
    }

    public static Quaternion subtract(Quaternion a, Quaternion b)
    {
        return new Quaternion(a.x - b.x, a.y - b.y,
             a.z - b.z, a.w - b.w);
    }

    public void add(Quaternion p)
    {
        this.x += p.x;
        this.y += p.y;
        this.z += p.z;
        this.w += p.w;
    }

    public void subtract(Quaternion p) 
    {
        this.x -= p.x;
        this.y -= p.y;
        this.z -= p.z;
        this.w -= p.w;
    }

    public void scale(double a) 
    {
        this.x *= a;
        this.y *= a;
        this.z *= a;
        this.w *= a;
    }
    
    public void normalize()
    {
        this.scale(1.0/this.norm());
    }

    public Quaternion mult(Quaternion p) 
    {
        Quaternion q = new Quaternion();
        this.x = this.w*this.x + this.x*p.w + this.y*p.z - this.z*p.y;
        this.y = this.w*this.y - this.x*p.z + this.y*p.w + this.z*p.x;
        this.z = this.w*this.z + this.x*p.y - this.y*p.x + this.z*p.w;
        this.w = this.w*this.w - this.x*p.x - this.y*p.y - this.z*p.z;
        return q;
    }

    public static Quaternion fromAxisAngle(Vec3 axis, double angle) {
        Quaternion q = new Quaternion();
        double s = Math.sin(angle/2);
        
        q.x = axis.x*s;
        q.y = axis.y*s;
        q.z = axis.z*s;
        q.w = Math.cos(angle/2);
        return q;
    }
    
    public void fromRotationMatrix(Matrix3 R)
    {
        double trace = R.a00 + R.a11 + R.a22;
        if( trace > 0 ) {
          double s = 0.5 / Math.sqrt(trace + 1.0);
          this.w = 0.25 / s;
          this.x = ( R.a21 - R.a12 ) * s;
          this.y = ( R.a02 - R.a20 ) * s;
          this.z = ( R.a10 - R.a01 ) * s;
        } else {
          if ( R.a00 > R.a11 && R.a00 > R.a22 ) {
            double s = Math.sqrt( 1.0 + R.a00 - R.a11 - R.a22 );
            double sInv = 0.5/s;
            this.x = 0.5*s;

            this.w = ( R.a21 - R.a12 ) * sInv;
            this.y = ( R.a01 + R.a10 ) * sInv;
            this.z = ( R.a02 + R.a20 ) * sInv;
          } else if (R.a11 > R.a22) {
            double s = Math.sqrt( 1.0 + R.a11 - R.a00 - R.a22 );
            double sInv = 0.5/s;
            this.y = 0.5*s;

            this.w = ( R.a02 - R.a20 ) * sInv;
            this.x = ( R.a01 + R.a10 ) * sInv;
            this.z = ( R.a12 + R.a21 ) * sInv;
          } else {
            double s = Math.sqrt( 1.0 + R.a22 - R.a00 - R.a11 );
            double sInv = 0.5/s;
            this.z = 0.5*s;

            this.w = ( R.a10 - R.a01 ) * sInv;
            this.x = ( R.a02 + R.a20 ) * sInv;
            this.y = ( R.a12 + R.a21 ) * sInv;
          }
        }
    }
    
    public void setRotationMatrix(Matrix3 R)
    {
        double qi2 = x*x;
        double qj2 = y*y;
        double qk2 = z*z;
        
        double qij = x*y;
        double qik = x*z;
        double qjk = y*z;
        double qiw = x*w;
        double qjw = y*w;
        double qkw = z*w;
        
        double factor = 2.0/(qi2 + qj2 + qk2 + w*w);
        
        R.a00 = 1.0 - factor*(qj2 + qk2);
        R.a11 = 1.0 - factor*(qi2 + qk2);
        R.a22 = 1.0 - factor*(qi2 + qj2);
        
        R.a01 = factor*(qij - qkw);
        R.a02 = factor*(qik + qjw);
        R.a10 = factor*(qij + qkw);
        
        R.a12 = factor*(qjk - qiw);
        R.a20 = factor*(qik - qjw);
        R.a21 = factor*(qjk + qiw);
    }
    
    public void setRotationMatrixUnit(Matrix3 R) 
    {
        double qi2 = x*x;
        double qj2 = y*y;
        double qk2 = z*z;
        
        double qij = x*y;
        double qik = x*z;
        double qjk = y*z;
        double qiw = x*w;
        double qjw = y*w;
        double qkw = z*w;
        
        R.a00 = 1.0 - 2*(qj2 + qk2);
        R.a11 = 1.0 - 2*(qi2 + qk2);
        R.a22 = 1.0 - 2*(qi2 + qj2);
        
        R.a01 = 2*(qij - qkw);
        R.a02 = 2*(qik + qjw);
        R.a10 = 2*(qij + qkw);
        
        R.a12 = 2*(qjk - qiw);
        R.a20 = 2*(qik - qjw);
        R.a21 = 2*(qjk + qiw);
    }
    
    public Matrix3 toRotationMatrix() {
        Matrix3 a = new Matrix3();
        this.setRotationMatrix(a);
        return a;
    }
    
    @Override
    public String toString()
    {
        return String.format("[%f, %f, %f, %f]", this.x, this.y, this.z, this.w);
    }
}

package com.mardpop.jrocket.util;

/**
 *
 * @author mariu
 */
public class Quaternion {
    final double[] data = new double[4];

    public Quaternion() {}

    public Quaternion(double i, double j, double k, double w) {
        this.data[0] = i;
        this.data[1] = j;
        this.data[2] = k;
        this.data[3] = w;
    }

    public Quaternion(Quaternion p)
    {
        System.arraycopy(p.data, 0, this.data, 0, 4);
    }
    
    public void set(Quaternion p) 
    {
        System.arraycopy(p.data, 0, this.data, 0, 4);
    }

    public Vec3 vector() {
        return new Vec3(this.data);
    }

    public double scalar() {
        return this.data[3];
    }
    
    public double x()
    {
        return this.data[0];
    }
    
    public void x(double x)
    {
        this.data[0] = x;
    }
    
    public double y()
    {
        return this.data[1];
    }
    
    public void y(double y)
    {
        this.data[1] = y;
    }
    
    public double z()
    {
        return this.data[2];
    }
    
    public void z(double z)
    {
        this.data[2] = z;
    }
    
    public double w()
    {
        return this.data[3];
    }
    
    public void w(double w)
    {
        this.data[3] = w;
    }
    
    public double[] data()
    {
        return this.data;
    }

    public Quaternion conjugate() 
    {
        return new Quaternion(-this.data[0],-this.data[1],-this.data[2],this.data[3]);
    }

    public double norm() 
    {
        return Math.sqrt(data[0]*data[0] + data[1]*data[1] + data[2]*data[2] + data[3]*data[3]);
    } 

    public static Quaternion add(Quaternion a, Quaternion b)
    {
        return new Quaternion(a.data[0] + b.data[0], a.data[1] + b.data[1],
             a.data[2] + b.data[2], a.data[3] + b.data[3]);
    }

    public static Quaternion subtract(Quaternion a, Quaternion b)
    {
        return new Quaternion(a.data[0] - b.data[0], a.data[1] - b.data[1],
             a.data[2] - b.data[2], a.data[3] - b.data[3]);
    }

    public void add(Quaternion p)
    {
        this.data[0] += p.data[0];
        this.data[1] += p.data[1];
        this.data[2] += p.data[2];
        this.data[3] += p.data[3];
    }

    public void subtract(Quaternion p) 
    {
        this.data[0] -= p.data[0];
        this.data[1] -= p.data[1];
        this.data[2] -= p.data[2];
        this.data[3] -= p.data[3];
    }

    public void scale(double a) 
    {
        this.data[0] *= a;
        this.data[1] *= a;
        this.data[2] *= a;
        this.data[3] *= a;
    }
    
    public void normalize()
    {
        this.scale(1.0/this.norm());
    }

    public Quaternion mult(Quaternion p) 
    {
        Quaternion q = new Quaternion();
        this.data[0] = this.data[3]*this.data[0] + this.data[0]*p.data[3] + this.data[1]*p.data[2] - this.data[2]*p.data[1];
        this.data[1] = this.data[3]*this.data[1] - this.data[0]*p.data[2] + this.data[1]*p.data[3] + this.data[2]*p.data[0];
        this.data[2] = this.data[3]*this.data[2] + this.data[0]*p.data[1] - this.data[1]*p.data[0] + this.data[2]*p.data[3];
        this.data[3] = this.data[3]*this.data[3] - this.data[0]*p.data[0] - this.data[1]*p.data[1] - this.data[2]*p.data[2];
        return q;
    }

    public static Quaternion fromAxisAngle(Vec3 axis, double angle) {
        Quaternion q = new Quaternion();
        double s = Math.sin(angle/2);
        
        q.data[0] = axis.data[0]*s;
        q.data[1] = axis.data[1]*s;
        q.data[2] = axis.data[2]*s;
        q.data[3] = Math.cos(angle/2);
        return q;
    }
    
    public void fromRotationMatrix(Matrix3 R)
    {
        double trace = R.data[0] + R.data[4] + R.data[8];
        if( trace > 0 ) {
          double s = 0.5 / Math.sqrt(trace+ 1.0);
          this.data[3] = 0.25/s;
          this.data[0] = ( R.data[7] - R.data[5] ) * s;
          this.data[1] = ( R.data[2] - R.data[6] ) * s;
          this.data[2] = ( R.data[3] - R.data[1] ) * s;
        } else {
          if ( R.data[0] > R.data[4] && R.data[0] > R.data[8] ) {
            double s = Math.sqrt( 1.0f + R.data[0] - R.data[4] - R.data[8]);
            double sInv = 0.5/s;
            this.data[3] = (R.data[7] - R.data[5] ) * sInv;
            this.data[0] = 0.5*s;
            this.data[1] = (R.data[1] + R.data[3] ) * sInv;
            this.data[2] = (R.data[2] + R.data[6] ) * sInv;
          } else if (R.data[4] > R.data[8]) {
            double s = Math.sqrt( 1.0f + R.data[4] - R.data[0] - R.data[8]);
            double sInv = 0.5/s;
            this.data[3] = (R.data[2] - R.data[6] ) * sInv;
            this.data[0] = (R.data[1] + R.data[3] ) * sInv;
            this.data[1] = 0.5*s;
            this.data[2] = (R.data[5] + R.data[7] ) * sInv;
          } else {
            double s = Math.sqrt( 1.0f + R.data[8] - R.data[0] - R.data[4] );
            double sInv = 0.5/s;
            this.data[3] = (R.data[3] - R.data[1] ) * sInv;
            this.data[0] = (R.data[2] + R.data[6] ) * sInv;
            this.data[1] = (R.data[5] + R.data[7] ) * sInv;
            this.data[2] = 0.5*s;
          }
        }
    }
    
    public void setRotationMatrix(Matrix3 R)
    {
        double qi2 = data[0]*data[0];
        double qj2 = data[1]*data[1];
        double qk2 = data[2]*data[2];
        
        double qij = data[0]*data[1];
        double qik = data[0]*data[2];
        double qjk = data[1]*data[2];
        double qiw = data[0]*data[3];
        double qjw = data[1]*data[3];
        double qkw = data[2]*data[3];
        
        double factor = 2.0/(qi2 + qj2 + qk2 + data[3]*data[3]);
        
        R.data[0] = 1.0 - factor*(qj2 + qk2);
        R.data[4] = 1.0 - factor*(qi2 + qk2);
        R.data[8] = 1.0 - factor*(qi2 + qj2);
        
        R.data[1] = factor*(qij - qkw);
        R.data[2] = factor*(qik + qjw);
        R.data[3] = factor*(qij + qkw);
        
        R.data[5] = factor*(qjk - qiw);
        R.data[6] = factor*(qik - qjw);
        R.data[7] = factor*(qjk + qiw);
    }
    
    public void setRotationMatrixUnit(Matrix3 R) 
    {
        double qi2 = data[0]*data[0];
        double qj2 = data[1]*data[1];
        double qk2 = data[2]*data[2];
        
        double qij = data[0]*data[1];
        double qik = data[0]*data[2];
        double qjk = data[1]*data[2];
        double qiw = data[0]*data[3];
        double qjw = data[1]*data[3];
        double qkw = data[2]*data[3];
        
        R.data[0] = 1.0 - 2*(qj2 + qk2);
        R.data[4] = 1.0 - 2*(qi2 + qk2);
        R.data[8] = 1.0 - 2*(qi2 + qj2);
        
        R.data[1] = 2*(qij - qkw);
        R.data[2] = 2*(qik + qjw);
        R.data[3] = 2*(qij + qkw);
        
        R.data[5] = 2*(qjk - qiw);
        R.data[6] = 2*(qik - qjw);
        R.data[7] = 2*(qjk + qiw);
    }
    
    public Matrix3 toRotationMatrix() {
        Matrix3 a = new Matrix3();
        this.setRotationMatrix(a);
        return a;
    }
    
    @Override
    public String toString()
    {
        return String.format("[%f, %f, %f, %f]", this.data[0], this.data[1], this.data[2], this.data[3]);
    }
}

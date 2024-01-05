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

    public Quaternion(Quaternion p) {
        System.arraycopy(p.data, 0, this.data, 0, 4);
    }

    public Vec3 vector() {
        return new Vec3(this.data);
    }

    public double scalar() {
        return this.data[3];
    }

    public Quaternion conjugate() {
        return new Quaternion(-this.data[0],-this.data[1],-this.data[2],this.data[3]);
    }

    public double norm() {
        return Math.sqrt(data[0]*data[0] + data[1]*data[1] + data[2]*data[2] + data[3]*data[3]);
    } 

    public void add(Quaternion p) {
        this.data[0] += p.data[0];
        this.data[1] += p.data[1];
        this.data[2] += p.data[2];
        this.data[3] += p.data[3];
    }

    public void subtract(Quaternion p) {
        this.data[0] -= p.data[0];
        this.data[1] -= p.data[1];
        this.data[2] -= p.data[2];
        this.data[3] -= p.data[3];
    }

    public void scale(double a) {
        this.data[0] *= a;
        this.data[1] *= a;
        this.data[2] *= a;
        this.data[3] *= a;
    }

    public Quaternion hamiltonProduct(Quaternion p) {
        Quaternion q = new Quaternion();
        q.data[3] = this.data[3]*q.data[3] - this.data[0]*p.data[0] - this.data[1]*p.data[1] - this.data[2]*p.data[2];
        q.data[0] = this.data[3]*q.data[0] + this.data[0]*p.data[3] + this.data[1]*p.data[2] - this.data[2]*p.data[1];
        q.data[1] = this.data[3]*q.data[1] - this.data[0]*p.data[2] + this.data[1]*p.data[3] + this.data[2]*p.data[0];
        q.data[2] = this.data[3]*q.data[2] + this.data[0]*p.data[1] - this.data[1]*p.data[0] + this.data[2]*p.data[3];
        return q;
    }

    public static Quaternion fromAxisAngle(Vec3 axis, double angle) {
        Quaternion q = new Quaternion();
        double s = Math.sin(angle/2);
        q.data[3] = Math.cos(angle/2);
        q.data[0] = axis.data[0]*s;
        q.data[1] = axis.data[1]*s;
        q.data[2] = axis.data[2]*s;
        return q;
    }
    
    public void setRotationMatrix(Matrix3 R) {
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
    
    public Matrix3 toRotationMatrix() {
        Matrix3 a = new Matrix3();
        this.setRotationMatrix(a);
        return a;
    }
}

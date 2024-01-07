package com.mardpop.jrocket.util;

/**
 *
 * @author mariu
 */
public class Matrix3 {
    
    public final double data[] = new double[9];
    
    public Matrix3() {}
    
    public Matrix3(Vec3 x, Vec3 y, Vec3 z) {
        System.arraycopy(x.data, 0, this.data, 0, 3);
        System.arraycopy(y.data, 0, this.data, 3, 3);
        System.arraycopy(z.data, 0, this.data, 6, 3);
    }
    
    public Matrix3(double[] data) {
        System.arraycopy(data, 0, this.data, 0, 9);
    }
    
    public Matrix3(Matrix3 A) {
        System.arraycopy(A.data, 0, this.data, 0, 9);
    }
    
    public static Matrix3 fromAngleAxis(double angle, Vec3 axis) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double c1 = 1 - c;
        
        double xs = s*axis.data[0];
        double ys = s*axis.data[1];
        double zs = s*axis.data[2];
        
        double xc = c1*axis.data[0];
        double yc = c1*axis.data[1];
        double zc = c1*axis.data[2];
        
        double xyc = axis.data[0]*xc;
        double xzc = axis.data[0]*zc;
        double yzc = axis.data[1]*zc;
        
        Matrix3 R = new Matrix3();
        R.data[0] = c + xc*axis.data[0];
        R.data[4] = c + yc*axis.data[1];
        R.data[8] = c + zc*axis.data[2];
        
        R.data[1] = xyc - zs;
        R.data[2] = xzc + ys;
        R.data[3] = xyc + zs;
        
        R.data[5] = yzc - xs;
        R.data[6] = xzc - ys;
        R.data[7] = yzc + xs;
        
        
        return R;
    }
    
    public double[] data()
    {
        return this.data;
    }
    
    public Vec3 mult(Vec3 u) 
    {
        Vec3 v = new Vec3();
        v.mult(this, u);
        return v;
    }
    
    public void mult(Matrix3 A, Matrix3 B)
    {
        for(int i = 0; i < 9; i += 3)
        {
            for(int j = 0; j < 3; j++)
            {
                int idx = 0;
                for(int k = 0; k < 3; k++)
                {
                    this.data[i + k] += A.data[i + j]*B.data[k + idx];
                }
                idx += 3;
            }
        }
    }
    
    public Matrix3 mult(Matrix3 B)
    {
        Matrix3 C = new Matrix3();
        C.mult(this,B);
        return C;
    }
    
    public Matrix3 getInverse()
    {
        double det = data[0] * (data[4] * data[8] - data[7] * data[5]) -
             data[1] * (data[3] * data[8] - data[5] * data[6]) +
             data[2] * (data[3] * data[7] - data[4] * data[6]);

        double invdet = 1.0 / det;

        Matrix3 minv = new Matrix3(); // inverse of matrix m
        minv.data[0] = (data[4] * data[8] - data[7] * data[5]) * invdet;
        minv.data[1] = (data[2] * data[7] - data[1] * data[8]) * invdet;
        minv.data[2] = (data[1] * data[5] - data[2] * data[4]) * invdet;
        minv.data[3] = (data[5] * data[6] - data[3] * data[8]) * invdet;
        minv.data[4] = (data[0] * data[8] - data[2] * data[6]) * invdet;
        minv.data[5] = (data[3] * data[2] - data[0] * data[5]) * invdet;
        minv.data[6] = (data[3] * data[7] - data[6] * data[4]) * invdet;
        minv.data[7] = (data[6] * data[1] - data[0] * data[7]) * invdet;
        minv.data[8] = (data[0] * data[4] - data[3] * data[1]) * invdet;
        
        return minv;
    }
    
    public Matrix3 getTranspose() 
    {
        Matrix3 t = new Matrix3();
        t.data[0] = this.data[0];
        t.data[1] = this.data[3];
        t.data[2] = this.data[6];
        t.data[3] = this.data[1];
        t.data[4] = this.data[4];
        t.data[5] = this.data[7];
        t.data[6] = this.data[2];
        t.data[7] = this.data[5];
        t.data[8] = this.data[8];
        return t;
    }
    
    public double get(int i, int j)
    {
        return this.data[i*3 + j];
    }
    
    public void set(int i, int j, double v)
    {
        this.data[i*3 + j] = v;
    }
    
    public double get(int k)
    {
        return this.data[k];
    }
    
    public void set(int k, double v)
    {
        this.data[k] = v;
    }
}

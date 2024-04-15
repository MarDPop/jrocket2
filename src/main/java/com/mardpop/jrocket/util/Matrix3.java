package com.mardpop.jrocket.util;

/**
 *
 * @author mariu
 */
public class Matrix3 {
    
    public double a00;
    public double a01;
    public double a02;
    public double a10;
    public double a11;
    public double a12;
    public double a20;
    public double a21;
    public double a22;
    
    public Matrix3() {}

    public Matrix3(double a11, double a12, double a13, double a21, double a22,
         double a23, double a31, double a32, double a33) 
    {
        this.a00 = a11;
        this.a01 = a12;
        this.a02 = a13;
        this.a10 = a21;
        this.a11 = a22;
        this.a12 = a23;
        this.a20 = a31;
        this.a21 = a32;
        this.a22 = a33;
    }
    
    public Matrix3(Vec3 x, Vec3 y, Vec3 z) 
    {
        this.a00 = x.x;
        this.a01 = x.y;
        this.a02 = x.z;
        this.a10 = y.x;
        this.a11 = y.y;
        this.a12 = y.z;
        this.a20 = z.x;
        this.a21 = z.y;
        this.a22 = z.z;
    }
    
    public Matrix3(double[] data) 
    {
        this.a00 = data[0];
        this.a01 = data[1];
        this.a02 = data[2];
        this.a10 = data[3];
        this.a11 = data[4];
        this.a12 = data[5];
        this.a20 = data[6];
        this.a21 = data[7];
        this.a22 = data[8];
    }
    
    public Matrix3(Matrix3 A) 
    {
        this.a00 = A.a00;
        this.a01 = A.a01;
        this.a02 = A.a02;
        this.a10 = A.a10;
        this.a11 = A.a11;
        this.a12 = A.a12;
        this.a20 = A.a20;
        this.a21 = A.a21;
        this.a22 = A.a22;
    }

    public Matrix3(double roll, double pitch, double yaw)
    {
        double cr = Math.cos(roll);
        double sr = Math.sin(roll);
        double cp = Math.cos(pitch);
        double sp = Math.sin(pitch);
        double cy = Math.cos(yaw);
        double sy = Math.sin(yaw);

        this.a00 = cy*cp;
        double tmp = cy*sp;
        this.a01 = tmp*sr - sy*cr;
        this.a02 = tmp*cr + sy*sr;
        this.a10 = sy*cp;
        tmp = sy*sp;
        this.a11 = tmp*sr + cy*cr;
        this.a12 = tmp*cr - cy*sr;
        this.a20 = -sp;
        this.a21 = cp*sr;
        this.a22 = cp*cr;
    }
    
    /**
     * Generates a matrix representation of a rotation around a given axis.
     *
     * @param  angle  the angle of rotation in radians
     * @param  axis    the axis of rotation
     * @return         the resulting rotation matrix
     */
    public static Matrix3 fromAngleAxis(double angle, Vec3 axis) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double c1 = 1 - c;
        
        double xs = s*axis.x;
        double ys = s*axis.y;
        double zs = s*axis.z;
        
        double xc = c1*axis.x;
        double yc = c1*axis.y;
        double zc = c1*axis.z;
        
        double xyc = axis.x*xc;
        double xzc = axis.y*zc;
        double yzc = axis.z*zc;
        
        Matrix3 R = new Matrix3();
        R.a00 = c + xc*axis.x;
        R.a11 = c + yc*axis.y;
        R.a22 = c + zc*axis.z;
        
        R.a01 = xyc - zs;
        R.a02 = xzc + ys;
        R.a10 = xyc + zs;
        
        R.a12 = yzc - xs;
        R.a20 = xzc - ys;
        R.a21 = yzc + xs;
        
        
        return R;
    }
    
    /**
     * Get the data as an array of doubles.
     *
     * @return         an array of doubles containing the data
     */
    public final double[] getData()
    {
        return new double[] {a00, a01, a02, a10, a11, a12, a20, a21, a22};
    }

    public final double getDeterminant()
    {
        // return a00*a11*a22 + a01*a12*a20 + a02*a10*a21 - a02*a11*a20 - a01*a10*a22 - a00*a12*a21;
        return a00 * (a11 * a22 - a21 * a12) -
             a01 * (a10 * a22 - a12 * a20) +
             a02 * (a10 * a21 - a11 * a20);
    }
    
    /**
     * Multiply a 3x3 matrix by a 3D vector and store the result in another 3D vector.
     *
     * @param  A   the 3x3 matrix to multiply
     * @param  u   the 3D vector to multiply with the matrix
     * @param  v   the resulting 3D vector
     */
    public static void mult(Matrix3 A, Vec3 u, Vec3 v)
    {
        v.x = A.a00*u.x + A.a01*u.y + A.a02*u.z;
        v.y = A.a10*u.x + A.a11*u.y + A.a12*u.z;
        v.z = A.a20*u.x + A.a21*u.y + A.a22*u.z;
    }
    
    /**
     * Transposes a matrix and multiplies it by two vectors.
     *
     * @param  A   the matrix to be transposed and multiplied
     * @param  u   the first vector to be multiplied
     * @param  v   the second vector to be multiplied
     */
    public static void transposeMult(Matrix3 A, Vec3 u, Vec3 v)
    {
        v.x = A.a00*u.x + A.a10*u.y + A.a20*u.z;
        v.y = A.a01*u.x + A.a11*u.y + A.a21*u.z;
        v.z = A.a02*u.x + A.a12*u.y + A.a22*u.z;
    }
    
    public Vec3 mult(Vec3 u) 
    {
        Vec3 v = new Vec3();
        mult(this, u, v);
        return v;
    }
    
    public Vec3 transposeMult(Vec3 u)
    {
        Vec3 v = new Vec3();
        transposeMult(this, u, v);
        return v; 
    }
    
    /**
     * Multiplies two 3x3 matrices A and B and stores the result in matrix C.
     *
     * @param  A	Matrix3 object representing matrix A
     * @param  B	Matrix3 object representing matrix B
     * @param  C	Matrix3 object where the result of the matrix multiplication will be stored
     */
    public static void mult(Matrix3 A, Matrix3 B, Matrix3 C)
    {
        C.a00 = A.a00*B.a00 + A.a01*B.a10 + A.a02*B.a20;
        C.a01 = A.a00*B.a01 + A.a01*B.a11 + A.a02*B.a21;
        C.a02 = A.a00*B.a02 + A.a01*B.a12 + A.a02*B.a22;
        C.a10 = A.a10*B.a00 + A.a11*B.a10 + A.a12*B.a20;
        C.a11 = A.a10*B.a01 + A.a11*B.a11 + A.a12*B.a21;
        C.a12 = A.a10*B.a02 + A.a11*B.a12 + A.a12*B.a22;
        C.a20 = A.a20*B.a00 + A.a21*B.a10 + A.a22*B.a20;
        C.a21 = A.a20*B.a01 + A.a21*B.a11 + A.a22*B.a21;
        C.a22 = A.a20*B.a02 + A.a21*B.a12 + A.a22*B.a22;
    }

    /**
     * Transposes the given matrices A and B and stores the result in matrix C.
     *
     * @param  A	Matrix3 object representing matrix A
     * @param  B	Matrix3 object representing matrix B
     * @param  C	Matrix3 object to store the transposed result
     */
    public static void transposeMult(Matrix3 A, Matrix3 B, Matrix3 C)
    {
        C.a00 = A.a00*B.a00 + A.a10*B.a01 + A.a20*B.a02;
        C.a01 = A.a01*B.a00 + A.a11*B.a01 + A.a21*B.a02;
        C.a02 = A.a02*B.a00 + A.a12*B.a01 + A.a22*B.a02;
        C.a10 = A.a00*B.a10 + A.a10*B.a11 + A.a20*B.a12;
        C.a11 = A.a01*B.a10 + A.a11*B.a11 + A.a21*B.a12;
        C.a12 = A.a02*B.a10 + A.a12*B.a11 + A.a22*B.a12;
        C.a20 = A.a00*B.a20 + A.a10*B.a21 + A.a20*B.a22;
        C.a21 = A.a01*B.a20 + A.a11*B.a21 + A.a21*B.a22;
        C.a22 = A.a02*B.a20 + A.a12*B.a21 + A.a22*B.a22;
    }
    
    /**
     * Multiplies this matrix with Matrix B and outputs the result
     *
     * @param  B   description of parameter
     * @return          description of return value
     */
    public Matrix3 mult(Matrix3 B)
    {
        Matrix3 C = new Matrix3();
        mult(this,B,C);
        return C;
    }
    
    /**
     * Calculates the inverse of the matrix.
     *
     * @return  the inverse of the matrix
     */
    public Matrix3 getInverse()
    {
        double det = a00 * (a11 * a22 - a21 * a12) -
             a01 * (a10 * a22 - a12 * a20) +
             a02 * (a10 * a21 - a11 * a20);

        double invdet = 1.0 / det;

        Matrix3 minv = new Matrix3(); // inverse of matrix m
        minv.a00 = (a11 * a22 - a21 * a12) * invdet;
        minv.a01 = (a02 * a21 - a01 * a22) * invdet;
        minv.a02 = (a01 * a12 - a02 * a11) * invdet;
        minv.a10 = (a12 * a20 - a10 * a22) * invdet;
        minv.a11 = (a00 * a22 - a02 * a20) * invdet;
        minv.a12 = (a10 * a02 - a00 * a12) * invdet;
        minv.a20 = (a10 * a21 - a20 * a11) * invdet;
        minv.a21 = (a20 * a01 - a00 * a21) * invdet;
        minv.a22 = (a00 * a11 - a10 * a01) * invdet;
        
        return minv;
    }
    
    /**
     * Get the transpose of the current Matrix3 object.
     *
     * @return          the transposed Matrix3 object
     */
    public Matrix3 getTranspose() 
    {
        Matrix3 t = new Matrix3();
        t.a00 = this.a00;
        t.a01 = this.a10;
        t.a02 = this.a20;
        t.a10 = this.a01;
        t.a11 = this.a11;
        t.a12 = this.a21;
        t.a20 = this.a02;
        t.a21 = this.a12;
        t.a22 = this.a22;
        return t;
    }

    /**
     * Get the first row of the matrix as a new Vec3 object.
     *
     * @return         	A new Vec3 object containing the values of the first row.
     */
    public Vec3 getRow0()
    {
        return new Vec3(this.a00, this.a01, this.a02);
    }

    /**
     * Get the second row of the matrix as a new Vec3 object.
     *
     * @return         	A new Vec3 object containing the values of the second row.
     */
    public Vec3 getRow1()
    {
        return new Vec3(this.a10, this.a11, this.a12);
    }

    /**
     * Get the third row of the matrix as a new Vec3 object.
     *
     * @return         	A new Vec3 object containing the values of the third row.
     */
    public Vec3 getRow2()
    {
        return new Vec3(this.a20, this.a21, this.a22);
    }

    public Vec3 getCol0()
    {
        return new Vec3(this.a00, this.a10, this.a20);
    }

    public Vec3 getCol1()
    {
        return new Vec3(this.a01, this.a11, this.a21);
    }

    public Vec3 getCol2()
    {
        return new Vec3(this.a02, this.a12, this.a22);
    }
    
    /**
     * Set the values of this matrix from another Matrix3 object.
     *
     * @param  other   the Matrix3 object to set values from
     * @return         void
     */
    public void setFrom(Matrix3 other)
    {
        this.a00 = other.a00;
        this.a01 = other.a01;
        this.a02 = other.a02;
        this.a10 = other.a10;
        this.a11 = other.a11;
        this.a12 = other.a12;
        this.a20 = other.a20;
        this.a21 = other.a21;
        this.a22 = other.a22;
    }
    
    @Override
    public String toString()
    {
        return String.format("[[%f, %f, %f],%n[%f, %f, %f],%n[%f, %f, %f]]", 
                this.a00, this.a01, this.a02,
                this.a10, this.a11, this.a12,
                this.a20, this.a21, this.a22);
    }
}

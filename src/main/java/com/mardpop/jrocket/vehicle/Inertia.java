package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Matrix3;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class Inertia 
{
    double mass;
    
    double Ixx;
    
    double Iyy;
    
    double Izz;
    
    double Ixy;
    
    double Ixz;
    
    double Iyz;
    
    final Vec3 CG = new Vec3();
    
    final Matrix3 I = new Matrix3();
    
    Matrix3 getMatrix()
    {
        double[] data = I.getData();
        data[0] = Ixx;
        data[1] = -Ixy;
        data[2] = -Ixz;
        data[3] = -Iyz;
        data[4] = Iyy;
        data[5] = -Ixy;
        data[6] = -Ixz;
        data[7] = -Iyz;
        data[8] = Izz;        
        return I;
    }
    
    void combine(Inertia a, Inertia b)
    {
        this.mass = a.mass + b.mass;
        double invMass = 1.0/this.mass;
        this.CG.x((a.CG.x()*a.mass + b.CG.x()*b.mass)*invMass);
        this.CG.y((a.CG.y()*a.mass + b.CG.y()*b.mass)*invMass);
        this.CG.x((a.CG.z()*a.mass + b.CG.z()*b.mass)*invMass);
        
        Vec3 d_a = Vec3.subtract(a.CG, this.CG);
        Vec3 d_b = Vec3.subtract(b.CG, this.CG);
        
        double dx2_a = d_a.x()*d_a.x();
        double dy2_a = d_a.y()*d_a.y();
        double dz2_a = d_a.z()*d_a.z();
        
        double dxdy_a = -d_a.x()*d_a.y();
        double dxdz_a = -d_a.x()*d_a.z();
        double dydz_a = -d_a.y()*d_a.z();
        
        double dx2_b = d_b.x()*d_b.x();
        double dy2_b = d_b.y()*d_b.y();
        double dz2_b = d_b.z()*d_b.z();
        
        double dxdy_b = -d_b.x()*d_b.y();
        double dxdz_b = -d_b.x()*d_b.z();
        double dydz_b = -d_b.y()*d_b.z();
        
        this.Ixx = a.Ixx + b.Ixx + a.mass*(dy2_a + dz2_a) + b.mass*(dy2_b + dz2_b); 
        this.Iyy = a.Iyy + b.Iyy + a.mass*(dx2_a + dz2_a) + b.mass*(dx2_b + dz2_b);
        this.Izz = a.Izz + b.Izz + a.mass*(dy2_a + dx2_a) + b.mass*(dy2_b + dx2_b);
        
        this.Ixy = a.Ixy + b.Ixy + a.mass*dxdy_a + b.mass*dxdy_b;
        this.Ixz = a.Ixz + b.Ixz + a.mass*dxdz_a + b.mass*dxdz_b;
        this.Iyz = a.Iyz + b.Iyz + a.mass*dydz_a + b.mass*dydz_b;
    }
}

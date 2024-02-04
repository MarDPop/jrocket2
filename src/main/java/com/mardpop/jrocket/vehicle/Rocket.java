package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.vehicle.gnc.Guidance;
import com.mardpop.jrocket.vehicle.gnc.Navigation;
import com.mardpop.jrocket.vehicle.gnc.Control;
import com.mardpop.jrocket.atmosphere.Atmosphere;
import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.util.Matrix3;
import com.mardpop.jrocket.util.Quaternion;
import com.mardpop.jrocket.util.Util;
import com.mardpop.jrocket.util.Vec3;
import com.mardpop.jrocket.vehicle.gnc.GNC;

/**
 *
 * @author mariu
 */
public class Rocket extends State 
{    
    final Inertia inertia = new Inertia();
    
    
    final Vec3 forces = new Vec3();
    
    final Vec3 moments = new Vec3();
    
    
    final Inertia inertiaEmpty = new Inertia();
    
        
    public final Matrix3 CS = new Matrix3();
    
    private Atmosphere atm;
    
    public final AerodynamicQuantities aero = new AerodynamicQuantities();
    
    
    public final Propulsion propulsion;
    
    public final Aerodynamics aerodynamics;
    
    public final FrameAcceleration frameAcceleration = new FrameAcceleration();
    
    
    public final GNC gnc;
    

    Rocket(Propulsion propulsion, Aerodynamics aerodynamics, GNC gnc) 
    {
        this.propulsion = propulsion;
        this.aerodynamics = aerodynamics;
        this.gnc = gnc;
    }
    
    void setAtmosphere(Atmosphere atm)
    {
        this.atm = atm;
    }
    
    Vec3 getAngularAcceleration()
    {
        Matrix3 I = this.inertia.getMatrix();
        Vec3 Iw = I.mult(this.angular_velocity);
        Vec3 rhs = Vec3.subtract(this.moments, Vec3.cross(this.angular_velocity, Iw));
        I = I.getInverse();
        return I.mult(rhs);
    }
    
    void updateForces(double time) 
    {
        this.orientation.setRotationMatrixUnit(this.CS);
        this.inertia.combine(this.inertiaEmpty, this.propulsion.propellant.fuelInertia);
        
        this.atm.update(this.position.z(), time);
        this.aero.update(this.velocity, this.CS, this.atm.air, this.atm.wind);
        
        this.gnc.update(time);
        
        this.propulsion.update(this.atm.air.getPressure(), time);
        this.aerodynamics.update(aero);
        
        this.forces.set(this.aerodynamics.force);
        this.forces.add(this.propulsion.force);
        
        this.moments.set(this.aerodynamics.getTorque(this.inertia.CG));
        this.moments.add(this.propulsion.getTorque(this.inertia.CG));
    }
    
    void update(double time, double dt)
    {
        double dt_2 = dt*0.5;
        this.updateForces(time);
        
        Vec3 acceleration0 = Vec3.mult(this.forces, 1.0/this.inertia.mass);
        acceleration0.add(this.frameAcceleration.getAcceleration(this.position.z(), this.velocity));
        
        Vec3 angularAcceleration0 = this.getAngularAcceleration();
        
        Vec3 position0 = new Vec3(this.position);
        Vec3 velocity0 = new Vec3(this.velocity);
        Quaternion orientation0 = new Quaternion(this.orientation);
        Vec3 angularRate0 = new Vec3(this.angular_velocity);
        
        this.position.add(Vec3.mult(Vec3.add(this.velocity, Vec3.mult(acceleration0, dt_2)), dt));
        this.velocity.add(Vec3.mult(acceleration0, dt));
        
        Quaternion qd0 = Util.getQuaternionDelta(this.orientation, this.angular_velocity, dt);
        this.orientation.add(qd0);
        this.angular_velocity.add(Vec3.mult(angularAcceleration0, dt));
        
        this.orientation.normalize();
    }
    
    
    
    
    
}

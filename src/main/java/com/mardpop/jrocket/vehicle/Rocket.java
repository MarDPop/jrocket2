package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.vehicle.propulsion.Propulsion;
import com.mardpop.jrocket.vehicle.aerodynamics.Aerodynamics;
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

    private final Vec3 wind = new Vec3();

    private FrameAccelerationLTP frameAcceleration = new FrameAccelerationLTP(0.0);
    
    
    public final Propulsion propulsion;
    
    public final Aerodynamics aerodynamics;

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

    void updateState(double time)
    {
        this.orientation.setRotationMatrixUnit(this.CS);
        this.inertia.combine(this.inertiaEmpty, this.propulsion.propellant.getInertia());
        
        this.atm.update(this.position.z, time);

        this.wind.x = this.CS.a00*this.atm.wind.east + this.CS.a01*this.atm.wind.north;
        this.wind.y = this.CS.a10*this.atm.wind.east + this.CS.a11*this.atm.wind.north;
        this.wind.z = this.CS.a20*this.atm.wind.east + this.CS.a21*this.atm.wind.north;
        this.aero.update(this.velocity, this.CS, this.atm.air, this.wind);
        
        this.gnc.update(time);
    }
    
    void updateForces(double time, double dt) 
    {
        this.propulsion.update(this.atm.air.getPressure(), time, dt);
        this.aerodynamics.update(aero);
        
        this.forces.set(this.aerodynamics.force);
        this.forces.add(this.propulsion.force);
        
        this.moments.set(this.aerodynamics.getTorque(this.inertia.COM));
        this.moments.add(this.propulsion.getTorque(this.inertia.COM));
    }
    
    void update(double time, double dt)
    {
        double dt_2 = dt*0.5;
        this.updateState(time);
        this.updateForces(time, dt);

        Vec3 position0 = new Vec3(this.position);
        Vec3 velocity0 = new Vec3(this.velocity);
        Quaternion orientation0 = new Quaternion(this.orientation);
        Vec3 angularRate0 = new Vec3(this.angular_velocity);
        
        Vec3 acceleration0 = Vec3.mult(this.forces, 1.0/this.inertia.mass);
        acceleration0.add(this.frameAcceleration.computeAcceleration(this.position.z, this.velocity));
        
        Vec3 angularAcceleration0 = this.getAngularAcceleration();

        this.position.add(Vec3.mult(velocity0, dt));
        this.velocity.add(Vec3.mult(acceleration0, dt));
        
        Quaternion qd0 = Util.getQuaternionDelta(this.orientation, this.angular_velocity, dt);
        this.orientation.add(qd0);
        this.angular_velocity.add(Vec3.mult(angularAcceleration0, dt));

        this.updateState(time + dt);
        this.updateForces(time, 0);

        Vec3 acceleration1 = Vec3.mult(this.forces, 1.0/this.inertia.mass);
        acceleration1.add(this.frameAcceleration.computeAcceleration(this.position.z, this.velocity));
        
        Vec3 angularAcceleration1 = this.getAngularAcceleration();

        this.position.set(Vec3.add(position0, Vec3.mult(Vec3.add(velocity0, this.velocity), dt_2)));
        this.velocity.set(Vec3.add(velocity0, Vec3.mult(Vec3.add(acceleration0, acceleration1), dt_2)));
        
        Quaternion qd = Util.getQuaternionDelta(this.orientation, Vec3.add(this.angular_velocity, angularRate0), dt_2);
        this.orientation.set(Quaternion.add(orientation0, qd));
        this.angular_velocity.set(Vec3.add(angularRate0, Vec3.mult(Vec3.add(angularAcceleration0, angularAcceleration1), dt_2)));

        this.orientation.normalize();
    }
    
    
    
    
    
}

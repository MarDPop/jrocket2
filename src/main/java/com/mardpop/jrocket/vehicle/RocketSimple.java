package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.atmosphere.Atmosphere;
import com.mardpop.jrocket.util.*;
import com.mardpop.jrocket.vehicle.gnc.GNC;

/**
 *
 * @author mariu
 */
public class RocketSimple extends State 
{
    final InertiaSimple inertia = new InertiaSimple();
    
    
    final Vec3 forces = new Vec3();
    
    final Vec3 moments = new Vec3();
    
    
    final InertiaSimple inertiaEmpty = new InertiaSimple();
    
    final InertiaSimple fuelInertia = new InertiaSimple();
    
    private boolean hasFuel = true;
    
        
    public final Matrix3 CS = new Matrix3();
    
    private final Atmosphere atm = new Atmosphere();
    
    public final AerodynamicQuantities aero = new AerodynamicQuantities();
    
    private double g0;
    
    
    public final Thruster thruster;
    
    public final Aerodynamics aerodynamics;
    
    
    RocketSimple(Thruster thruster, Aerodynamics aerodynamics, GNC gnc) 
    {
        this.thruster = thruster;
        this.aerodynamics = aerodynamics;
    }
    
    void setGround(double pascal, double kelvin, double gravity, double latitude)
    {
        this.g0 = gravity;
        double R0 = Earth.earthRadius(latitude);
        this.atm.setConstantTemperature(kelvin, pascal, gravity, 100, 6200, R0);
    }
    
    void computeEnvironment(double time)
    {
        this.hasFuel = this.fuelInertia.mass > 0;
        
        this.orientation.setRotationMatrixUnit(this.CS);
        this.inertia.combine(this.inertiaEmpty, this.fuelInertia);
        
        this.atm.update(this.position.z(), time);
        this.aero.update(this.velocity, this.CS, this.atm.air, this.atm.wind);
    }
    
    void updateForces(double time) 
    {
        this.aerodynamics.update(aero);
        this.forces.set(this.aerodynamics.force);
        
        if(this.hasFuel)
        {
            this.thruster.update(this.atm.air.getPressure(), time);
            this.forces.x(this.forces.x() + this.thruster.getThrust());
        }
        
        this.moments.set(this.aerodynamics.moment);
    }
    
        
    Vec3 getAngularAcceleration()
    {
        double invIrr = 1.0/this.inertiaEmpty.Irr;
        Vec3 angularAcceleration = new Vec3(this.moments.x()/this.inertiaEmpty.Ixx,this.moments.y()*invIrr, this.moments.z()*invIrr);
        return angularAcceleration;
    }
    
    void step(double dt)
    {
        Vec3 acceleration0 = Vec3.mult(this.forces, 1.0/this.inertia.mass);
        acceleration0.z(acceleration0.z() - this.g0);
        
        this.position.add(Vec3.mult(Vec3.add(this.velocity, Vec3.mult(acceleration0, dt*0.5)), dt));
        this.velocity.add(Vec3.mult(acceleration0, dt));
        
        Quaternion qd0 = Util.getQuaternionDelta(this.orientation, this.angular_velocity, dt);
        this.orientation.add(qd0);
        this.angular_velocity.add(Vec3.mult(this.getAngularAcceleration(), dt));
        
        this.orientation.normalize();
        
        if(this.hasFuel)
        {
            this.inertia.mass -= this.thruster.getMassRate()*dt;
        }
    }

    void update(double time, double dt)
    {        
        this.computeEnvironment(time);
        this.updateForces(time);
        this.step(dt);
    }
}

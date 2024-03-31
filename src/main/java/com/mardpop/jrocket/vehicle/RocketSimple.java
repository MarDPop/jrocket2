package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.atmosphere.Atmosphere;
import com.mardpop.jrocket.util.*;
import com.mardpop.jrocket.vehicle.aerodynamics.Aerodynamics;
import com.mardpop.jrocket.vehicle.gnc.SimpleGNC;
import com.mardpop.jrocket.vehicle.propulsion.Thruster;

/**
 *
 * @author mariu
 */
public class RocketSimple extends State 
{
    private InertiaSimple inertia = new InertiaSimple();
    
    
    private final Vec3 forces = new Vec3();
    
    private final Vec3 moments = new Vec3();
    
    
    private final InertiaSimple inertiaEmpty = new InertiaSimple();
    
    private final InertiaSimple inertiaFuel = new InertiaSimple();
    
    private boolean hasFuel = true;
    
        
    public final Matrix3 coordinateSystem = new Matrix3();
    
    private final Atmosphere atm = new Atmosphere();
    
    public final AerodynamicQuantities aero = new AerodynamicQuantities();
    
    private double g0;
    
    
    public final Thruster thruster;
    
    public final Aerodynamics aerodynamics;
    
    public final SimpleGNC gnc;
    
    
    public RocketSimple(Thruster thruster, Aerodynamics aerodynamics, SimpleGNC gnc) 
    {
        this.thruster = thruster;
        this.aerodynamics = aerodynamics;
        this.gnc = gnc;
    }
    
    public void setGround(double pascal, double kelvin, double gravity, double latitude)
    {
        this.g0 = gravity;
        this.atm.setConstantTemperature(kelvin, pascal, gravity, 100, 6200, Earth.earthRadius(latitude));
    }
    
    public void setLaunchOrientation(double pitch, double heading)
    {
        double s = Math.cos(pitch);
        double c = Math.sqrt(1.0 - s*s);
        double ct = Math.cos(heading);
        double st = Math.sin(heading);
        Vec3 x = new Vec3(c*ct,c*st,s);
        Vec3 y = new Vec3(-st,ct,0);
        Vec3 z = Vec3.cross(x, y);
        
        Matrix3 CS = new Matrix3(x,y,z);
        
        this.coordinateSystem.setFrom(CS);
        this.orientation.fromRotationMatrix(CS);
    }
    
    public void setInertia(InertiaSimple empty, InertiaSimple fuel)
    {
        this.inertiaEmpty.copy(empty);
        this.inertiaFuel.copy(fuel);
    }
    
    public double getMass()
    {
        return this.inertia.mass;
    }
    
    public void computeEnvironment(double time)
    {
        this.orientation.setRotationMatrixUnit(this.coordinateSystem);
        this.inertia = new InertiaSimple(this.inertiaEmpty, this.inertiaFuel);
        
        this.atm.update(this.position.z, time);
        this.aero.update(this.velocity, this.coordinateSystem, this.atm.air, this.atm.wind);
    }
    
    void updateForces(double time) 
    {
        this.aerodynamics.update(aero);
        this.forces.set(this.aerodynamics.force);
        
        if(this.hasFuel)
        {
            this.thruster.update(this.atm.air.getPressure(), time);
            this.forces.x = this.forces.x + this.thruster.getThrust();
        }
        // add damping
        final double damping = 0.001;
        this.moments.set(Vec3.subtract(this.aerodynamics.moment, Vec3.mult(this.angular_velocity, damping)));
        final double arm = this.aerodynamics.position.x - this.inertia.CGx;
        this.moments.y += arm*this.aerodynamics.force.z;
        this.moments.z -= arm*this.aerodynamics.force.y;
        this.moments.add(this.gnc.getControlMoment());
    }
    
    Vec3 getAngularAcceleration()
    {
        double invIrr = 1.0/this.inertiaEmpty.Irr;
        return new Vec3(this.moments.x/this.inertiaEmpty.Ixx,this.moments.y*invIrr, this.moments.z*invIrr);
    }
    
    void step(double dt)
    {
        Vec3 acceleration0 = this.coordinateSystem.transposeMult(this.forces);
        acceleration0.scale(1.0/this.inertia.mass);
        acceleration0.z = acceleration0.z - this.g0;
        acceleration0.scale(dt*0.5);
        
        this.velocity.add(acceleration0);
        this.position.add(Vec3.mult(this.velocity, dt));
        this.velocity.add(acceleration0);
        
        Quaternion qd0 = Util.getQuaternionDelta(this.orientation, this.angular_velocity, dt);
        this.orientation.add(qd0);
        this.angular_velocity.add(Vec3.mult(this.getAngularAcceleration(), dt));
        
        this.orientation.normalize();
        
        if(this.hasFuel)
        {
            double mass0 = this.inertiaFuel.mass;
            this.inertiaFuel.mass -= this.thruster.getMassRate()*dt;
            mass0 = this.inertiaFuel.mass / mass0;
            this.inertiaFuel.Irr *= mass0;
            this.inertiaFuel.Ixx *= mass0;
            this.hasFuel = this.inertiaFuel.mass > 0;
        }
    }

    public void push(double time, double dt)
    {        
        this.computeEnvironment(time);
        this.gnc.update(time);
        this.updateForces(time);
        this.step(dt);
    }
}

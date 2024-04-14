package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.atmosphere.Atmosphere;
import com.mardpop.jrocket.util.*;
import com.mardpop.jrocket.vehicle.aerodynamics.Aerodynamics;
import com.mardpop.jrocket.vehicle.gnc.SimpleGNC;
import com.mardpop.jrocket.vehicle.propulsion.CommercialMotor;

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
    
        
    public final Matrix3 coordinateSystem = new Matrix3();
    
    private final Atmosphere atm = new Atmosphere();
    
    public final AerodynamicQuantities aero = new AerodynamicQuantities();
    
    private double g0;
    
    
    public final CommercialMotor thruster;
    
    public final Aerodynamics aerodynamics;
    
    public final SimpleGNC gnc;

    private final Vec3 wind = new Vec3();


    private int thrusterTIdx = 0;
    
    
    public RocketSimple(CommercialMotor thruster, Aerodynamics aerodynamics, SimpleGNC gnc) 
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
    
    public void setInertia(InertiaSimple empty)
    {
        this.inertiaEmpty.copy(empty);
    }
    
    public double getMass()
    {
        return this.inertia.mass;
    }

    public void init()
    {
        this.thrusterTIdx = 0;
        double[] values = this.thruster.getValuesAtTime(0, thrusterTIdx);
        InertiaSimple propInertia = new InertiaSimple(values[1], values[2], values[3], values[4]);
        this.inertia = new InertiaSimple(inertiaEmpty, propInertia);
    }
    
    public void computeEnvironment(double time)
    {
        this.orientation.setRotationMatrixUnit(this.coordinateSystem);
        
        this.atm.update(this.position.z, time);
        this.aero.update(this.velocity, this.coordinateSystem, this.atm.air, this.wind);
    }
    
    void updateForces(double time) 
    {
        this.aerodynamics.update(aero);
        this.forces.set(this.aerodynamics.force);

        if(time < this.thruster.time_final)
        {
            while(this.thruster.getTimes()[this.thrusterTIdx + 1] < time)
            {
                this.thrusterTIdx++;
            }

            double[] values = this.thruster.getValuesAtTime(time, thrusterTIdx);
            this.forces.x += values[0];
            InertiaSimple propInertia = new InertiaSimple(values[1], values[2], values[3], values[4]);
            this.inertia = new InertiaSimple(this.inertiaEmpty, propInertia);
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
        final double invIrr = 1.0/this.inertiaEmpty.Irr;
        return new Vec3(this.moments.x/this.inertiaEmpty.Ixx,this.moments.y*invIrr, this.moments.z*invIrr);
    }
    
    void step(double dt)
    {
        Vec3 acceleration0 = this.coordinateSystem.transposeMult(this.forces);

        final double gravity = this.g0*this.inertia.mass;
        
        if((this.position.z < 1e-6 && acceleration0.z < gravity))
        {
            acceleration0 = new Vec3();
        }
        else
        {
            acceleration0.z -= gravity;
            acceleration0.scale(dt*0.5/this.inertia.mass);
        }
        
        this.velocity.add(acceleration0);
        this.position.add(Vec3.mult(this.velocity, dt));
        this.velocity.add(acceleration0);
        
        Quaternion qd0 = Util.getQuaternionDelta(this.orientation, this.angular_velocity, dt);
        this.orientation.add(qd0);
        this.angular_velocity.add(Vec3.mult(this.getAngularAcceleration(), dt));
        
        this.orientation.normalize();
    }

    public void push(double time, double dt)
    {        
        this.computeEnvironment(time);
        this.gnc.update(time);
        this.updateForces(time);
        this.step(dt);
    }
}

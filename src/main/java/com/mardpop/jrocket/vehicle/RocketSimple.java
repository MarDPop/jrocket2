package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.atmosphere.Atmosphere;
import com.mardpop.jrocket.util.*;
import com.mardpop.jrocket.vehicle.aerodynamics.AerodynamicsConstantCP;
import com.mardpop.jrocket.vehicle.gnc.SimpleGNC;
import com.mardpop.jrocket.vehicle.propulsion.CommercialMotor;
import com.mardpop.jrocket.vehicle.recovery.SimpleChute;

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
    
        
    public final Matrix3 coordinateSystem = new Matrix3(); // row major
    
    private final Atmosphere atm = new Atmosphere();
    
    public final AerodynamicQuantities aero = new AerodynamicQuantities();
    
    private double g0 = 9.806;

    private double launchRailHeight = 0.0;
    
    
    public final CommercialMotor thruster;
    
    public final AerodynamicsConstantCP aerodynamics;

    public final SimpleChute chute;
    
    public final SimpleGNC gnc;

    private final Vec3 wind = new Vec3();


    private int thrusterTIdx = 0;
    
    
    public RocketSimple(CommercialMotor thruster, AerodynamicsConstantCP aerodynamics, SimpleChute chute, 
        SimpleGNC gnc, InertiaSimple empty) 
    {
        this.thruster = thruster;
        this.aerodynamics = aerodynamics;
        this.chute = chute;
        this.gnc = gnc;
        this.inertiaEmpty.copy(empty);
    }
    
    public double getMass()
    {
        return this.inertia.mass;
    }

    public void init(final Matrix3 CS, final double pascal, final double kelvin, final double gravity, 
        final double latitude, final double launchRailHeight)
    {
        this.g0 = gravity;
        this.launchRailHeight = Double.max(0.0, launchRailHeight);
        this.atm.setConstantTemperature(kelvin, pascal, gravity, 100, 6200, Earth.earthRadius(latitude));
        this.coordinateSystem.setFrom(CS);
        this.orientation.fromRotationMatrix(CS);
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
    
    void updateForces(final double time) 
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

        if(time > this.chute.deployTime)
        {
            this.forces.add(this.chute.bodyForce(time, aero));
        }
        
        this.moments.set(this.aerodynamics.moment);
        final double arm = this.aerodynamics.position.x - this.inertia.CGx;
        this.moments.y -= arm*this.aerodynamics.force.z;
        this.moments.z += arm*this.aerodynamics.force.y;
        this.moments.add(this.gnc.getControlMoment());
    }
    
    Vec3 getAngularAcceleration()
    {
        final double invIrr = 1.0/this.inertia.Irr;
        final double artificial_damping = -0.001;
        final Vec3 angularAcceleration = new Vec3(this.moments.x/this.inertia.Ixx,
            this.moments.y*invIrr, this.moments.z*invIrr);
        return Vec3.add(angularAcceleration, Vec3.mult(this.angular_velocity, artificial_damping));
    }
    
    void step(double dt)
    {
        Vec3 acceleration0 = this.coordinateSystem.transposeMult(this.forces);

        double gravity = this.g0*this.inertia.mass;
        
        if(this.position.z < this.launchRailHeight)
        {
            acceleration0.x = 0;
            acceleration0.y = 0;
            moments.scale(0.0);

            if(acceleration0.z < gravity)
            {
                gravity = acceleration0.z;
            }
        }

        acceleration0.z -= gravity;
        acceleration0.scale(dt*0.5/this.inertia.mass);
    
        this.velocity.add(acceleration0);
        this.position.add(Vec3.mult(this.velocity, dt));
        this.velocity.add(acceleration0);
        
        double angleRotation = this.angular_velocity.magnitude();
        if(angleRotation > 1e-10) 
        {
            Vec3 axis = Vec3.mult(this.angular_velocity, 1.0/angleRotation);
            final Quaternion qd0 = Quaternion.fromAxisAngle(axis, angleRotation*dt);
            this.orientation.set(qd0.mult(this.orientation));
        }

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

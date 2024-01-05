package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.*;

/**
 *
 * @author mariu
 */
public class Propulsion extends Action
{
    public final Thruster thruster;
    
    public final Propellant propellant;
    
    public final Vec3 thrustVector = new Vec3(1,0,0);
    
    private Vec3 commandedThrustVector = new Vec3(1,0,0);
    
    private double gimbalY = 0.0;
    
    private double gimbalZ = 0.0;
    
    private double gimbalRate = 0.0;
    
    private double maxGimbalAngle = 0.0;
    
    private double timeOld;
    
    public Propulsion(Thruster thruster, Propellant propellant)
    {
        this.thruster = thruster;
        this.propellant = propellant;
    }
    
    public void setGimbalPerformance(double gimbalRate, double maxGimbalAngle)
    {
        this.gimbalRate = gimbalRate;
        this.maxGimbalAngle = maxGimbalAngle;
    }
    
    public void setCommandedVectorReference(Vec3 commandedThrustVector)
    {
        this.commandedThrustVector = commandedThrustVector;
    }
    
    public void slew(double dt)
    {
        
    }
    
    public void update(double pressure, double time)
    {
        double dt = time - this.timeOld;
        this.thruster.update(pressure, time);
        this.propellant.update(time);
        
        this.slew(dt);
        
        double thrust = this.thruster.getThrust();
        this.force.x(thrust*this.thrustVector.x());
        this.force.y(thrust*this.thrustVector.y());
        this.force.z(thrust*this.thrustVector.z());
        
        this.timeOld = time;
    }
}

package com.mardpop.jrocket.vehicle;

/**
 *
 * @author mariu
 */
public class Propulsion extends Action
{
    public final Thruster thruster;
    
    public final PropellantTank propellant;

    protected PropulsionVectorControl vectorControl = new PropulsionVectorControl();
    
    public Propulsion(Thruster thruster, PropellantTank propellant)
    {
        this.thruster = thruster;
        this.propellant = propellant;
    }
    
    public void setPropulsionVectorControl(PropulsionVectorControl vectorControl)
    {
        this.vectorControl = vectorControl;
    }
    
    public void update(double pressure, double time, double dt)
    {
        this.thruster.update(pressure, time);
        
        this.vectorControl.update(time, dt);
        
        double thrust = this.thruster.getThrust();
        this.force.x(thrust*this.vectorControl.thrustVector.x());
        this.force.y(thrust*this.vectorControl.thrustVector.y());
        this.force.z(thrust*this.vectorControl.thrustVector.z());

        this.propellant.takePropellant(this.thruster.getMassRate()*dt);
    }
}

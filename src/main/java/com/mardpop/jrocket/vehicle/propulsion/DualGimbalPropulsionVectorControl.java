package com.mardpop.jrocket.vehicle.propulsion;

import com.mardpop.jrocket.util.Vec3;

/**
 * AKA Gimbal Ring Rocket
 */
public class DualGimbalPropulsionVectorControl extends PropulsionVectorControl {
        
    /**
     * Primary gimbal
     */
    private double commandedGimbalY = 0.0;

    private double commandedGimbalZ = 0.0;
    
    private double gimbalY = 0.0;
    
    private double gimbalZ = 0.0;
    
    private final double gimbalRate;
    
    private final double maxGimbalAngle;

    private final double minGimbalAngle;

    public DualGimbalPropulsionVectorControl(double gimbalRate, double maxGimbalAngle)
    {
        this.gimbalRate = gimbalRate;
        this.maxGimbalAngle = maxGimbalAngle;
        this.minGimbalAngle = -maxGimbalAngle;
    }

    @Override
    public void setDesiredThrustVector(Vec3 thrust)
    {
        this.commandedGimbalY = Math.asin(thrust.z);
        this.commandedGimbalZ = Math.asin(thrust.y/thrust.x);

        this.commandedGimbalY = Math.clamp(this.commandedGimbalY, this.minGimbalAngle, this.maxGimbalAngle);
        this.commandedGimbalZ = Math.clamp(this.commandedGimbalZ, this.minGimbalAngle, this.maxGimbalAngle);
    }

    @Override
    public void update(double time, double dt)
    {
        double maxGimbalChange = this.gimbalRate*dt;
        double minGimbalChange = -maxGimbalChange;

        double deltaY = this.commandedGimbalY - this.gimbalY;
        double deltaZ = this.commandedGimbalZ - this.gimbalZ;

        deltaY = Math.clamp(deltaY, minGimbalChange, maxGimbalChange);
        deltaZ = Math.clamp(deltaZ, minGimbalChange, maxGimbalChange);

        this.gimbalY += deltaY;
        this.gimbalZ += deltaZ;
    }
}

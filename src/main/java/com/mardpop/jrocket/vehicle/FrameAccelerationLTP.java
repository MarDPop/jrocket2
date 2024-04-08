package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Earth;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class FrameAccelerationLTP 
{
    private static final double EARTH_MU = 3.986004418e14;

    private final Vec3 earthRotatationInFrame;

    private final Vec3 centripetalForce = new Vec3();
    
    private final double g0;
    
    private final double R0;
    
    public FrameAccelerationLTP(double latitude)
    {
        this.earthRotatationInFrame = new Vec3(0, Math.cos(latitude), -Math.sin(latitude));
        this.R0 = Earth.earthRadius(latitude);
        double rEy = this.R0*Math.cos(latitude);
        double rEz = this.R0*Math.sin(latitude);
        this.centripetalForce.y = rEz*this.earthRotatationInFrame.z*this.earthRotatationInFrame.z;
        this.centripetalForce.z = -rEy*this.earthRotatationInFrame.y*this.earthRotatationInFrame.y;
        this.earthRotatationInFrame.scale(-2.0);
        this.g0 = EARTH_MU/(this.R0*this.R0);
    }

    public FrameAccelerationLTP(double latitude, double altitude, double g0)
    {
        this.earthRotatationInFrame = new Vec3(0, Math.cos(latitude), -Math.sin(latitude));
        Vec3 ecef = Earth.geodetic2ecef(latitude, latitude, altitude);
        this.R0 = ecef.magnitude();
        double rEy = this.R0*Math.cos(latitude);
        double rEz = this.R0*Math.sin(latitude);
        this.centripetalForce.y = rEz*this.earthRotatationInFrame.z*this.earthRotatationInFrame.z;
        this.centripetalForce.z = -rEy*this.earthRotatationInFrame.y*this.earthRotatationInFrame.y;
        this.earthRotatationInFrame.scale(-2.0);
        this.g0 = g0;
    }
    
    public Vec3 computeAcceleration(double height, Vec3 velocity)
    {
        double R = R0 + height;
        double f = R0/R;
        Vec3 coriolis = Vec3.cross(this.earthRotatationInFrame, velocity);
        Vec3 acceleration = Vec3.add(coriolis, this.centripetalForce);
        acceleration.z -= g0*f*f;
        return acceleration;
    }
}

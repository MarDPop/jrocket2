package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Earth;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class FrameAccelerationLTP extends FrameAcceleration
{
    private static final double EARTH_MU = 3.986004418e14;

    private final Vec3 earthRotatationInFrame;

    private final Vec3 centripetalAcceleration = new Vec3();
    
    private final double g0;
    
    private final double R0;
    
    public FrameAccelerationLTP(double latitude)
    {
        this.earthRotatationInFrame = new Vec3(0, Math.cos(latitude), -Math.sin(latitude));
        this.R0 = Earth.earthRadius(latitude);
        double rEy = this.R0*Math.cos(latitude);
        double rEz = this.R0*Math.sin(latitude);
        this.centripetalAcceleration.y = rEz*this.earthRotatationInFrame.z*this.earthRotatationInFrame.z;
        this.centripetalAcceleration.z = -rEy*this.earthRotatationInFrame.y*this.earthRotatationInFrame.y;
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
        this.centripetalAcceleration.y = rEz*this.earthRotatationInFrame.z*this.earthRotatationInFrame.z;
        this.centripetalAcceleration.z = -rEy*this.earthRotatationInFrame.y*this.earthRotatationInFrame.y;
        this.earthRotatationInFrame.scale(-2.0);
        this.g0 = g0;
    }
    
    @Override
    public void compute(Vec3 position, Vec3 velocity)
    {
        final double R = R0 + position.z;
        final double f = R0/R;
        this.fromCross(this.earthRotatationInFrame, velocity);
        this.y += this.centripetalAcceleration.y;
        this.z += this.centripetalAcceleration.z;
        this.z -= g0*f*f;
    }
}

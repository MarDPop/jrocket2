package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Earth;
import com.mardpop.jrocket.util.Vec3;

public class FrameAccelerationGeocentricECEF extends FrameAcceleration
{

    public static final double TWICE_EARTH_ROTATION = 2*Earth.ROTATION_RATE;

    public static final double EARTH_ROTATION_SQ = Earth.ROTATION_RATE*Earth.ROTATION_RATE;
    
    public void compute(Vec3 position, Vec3 velocity)
    {
        final double R = position.magnitude();
        final double g = -Earth.MU/(R*R*R);
        final double gt = g + EARTH_ROTATION_SQ;
        this.x = gt*position.x - TWICE_EARTH_ROTATION*velocity.y;
        this.y = gt*position.y + TWICE_EARTH_ROTATION*velocity.x;
        this.z = g*position.z;
    }
}

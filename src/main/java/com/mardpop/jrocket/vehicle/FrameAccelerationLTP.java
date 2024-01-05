package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class FrameAccelerationLTP extends FrameAcceleration 
{
    private final Vec3 earthRotatationInFrame = new Vec3();
    
    private double g0;
    
    private double R0;
    
    public void setWGS84(double latitude, double longitude)
    {
        
    }
    
    public void setGeoid(double latitude, double longitude)
    {
        
    }
    
    public Vec3 getAcceleration(double height, Vec3 velocity)
    {
        double R = R0 + height;
        double f = R0/R;
        this.acceleration.z(g0*f*f);
        return this.acceleration;
    }
}

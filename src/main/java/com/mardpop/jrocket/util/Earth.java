package com.mardpop.jrocket.util;

/**
 *
 * @author mariu
 */
public class Earth 
{
    public static final double EQUATOR_RADIUS =  6378.1370;
    public static final double POLAR_RADIUS = 6356.7523;
    public static final double EQUATOR_RADIUS_SQ = EQUATOR_RADIUS*EQUATOR_RADIUS;
    public static final double POLAR_RADIUS_SQ = POLAR_RADIUS*POLAR_RADIUS;
    
    public static double earthRadius(double latitude)
    {
        double s = Math.sin(latitude);
        s *= s;
        double c = 1.0 - s;
        double ac = EQUATOR_RADIUS_SQ*c;
        double bs = POLAR_RADIUS_SQ*s;
        
        return Math.sqrt((EQUATOR_RADIUS_SQ*ac + POLAR_RADIUS_SQ*bs)/(ac + bs));
    }
}

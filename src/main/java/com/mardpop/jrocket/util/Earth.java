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
    public static final double ECCENTRICITY_SQ = 6.6943799901377997e-3; 
    public static final double MU = 3.986005e14; // Earth's gravitational constant in m^3/s^2
    public static final double ROTATION_RATE = 7.2921151467e-5; // Earth rotation rate in rad/s
    
    public static double earthRadius(double latitude)
    {
        double s = Math.sin(latitude);
        s *= s;
        double c = 1.0 - s;
        double ac = EQUATOR_RADIUS_SQ*c;
        double bs = POLAR_RADIUS_SQ*s;
        
        return Math.sqrt((EQUATOR_RADIUS_SQ*ac + POLAR_RADIUS_SQ*bs)/(ac + bs));
    }

    public static Vec3 geodetic2ecef(double latitude, double longitude, double altitude)
    {
        final double s = Math.sin(latitude);
        final double s2 = s*s;
        
        final double n = EQUATOR_RADIUS/Math.sqrt( 1 - ECCENTRICITY_SQ*s2 );
        final double c = ( n + altitude )*Math.sqrt(1.0 - s2);
        return new Vec3(c*Math.cos( longitude ), c*Math.sin( longitude ),( n*(1 - ECCENTRICITY_SQ) + altitude )*s);    
             //Return x, y, z in ECEF
    }
}

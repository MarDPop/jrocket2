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
        double s = Math.sin(latitude);
        double c = Math.sqrt(1.0 - s*s);
        double n = EQUATOR_RADIUS/Math.sqrt( 1 - ECCENTRICITY_SQ*s*s );
        Vec3 ecef = new Vec3();
        ecef.x = ( n + altitude )*c*Math.cos( longitude );    //ECEF x
        ecef.y = ( n + altitude )*c*Math.sin( longitude );    //ECEF y
        ecef.z = ( n*(1 - ECCENTRICITY_SQ) + altitude )*s;          //ECEF z
        return ecef;     //Return x, y, z in ECEF
    }
}

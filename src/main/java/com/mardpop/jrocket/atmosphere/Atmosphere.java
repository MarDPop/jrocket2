package com.mardpop.jrocket.atmosphere;

/**
 *
 * @author mariu
 */
public class Atmosphere 
{
    public static class Wind 
    {
        public float north = 0.0f;
        public float east = 0.0f;

        public Wind() {}

        public Wind(float north, float east)
        {
            this.north = north;
            this.east = east;
        }
    }
    
    private final Air air;
    
    private final Wind wind;
    
    public static final Air VACUUM = new Air(0,0,1,1);
    
    public static double geometric2geopotential(double z, double R0)
    {
        return (R0*z)/(R0 + z);
    }

    public Atmosphere() 
    {
        this.air = new Air();
        this.wind = new Wind();
    }

    public Atmosphere(Air air, Wind wind)
    {
        this.air = air;
        this.wind = wind;
    }

    public final Air getAir()
    {
        return this.air;
    }

    public final Wind getWind()
    {
        return this.wind;
    }

    protected void updateAir(Air air, double z, double time) {}
    
    protected void updateWind(Wind wind, double z, double time) {}

    public final void update(double z, double time) 
    {
        this.updateAir(air, z, time);
        this.updateWind(wind, z,time);
    }
    
    
}

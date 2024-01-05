package com.mardpop.jrocket.atmosphere;

/**
 *
 * @author mariu
 */
public class Air 
{
    public static final double GAMMA = 1.4;
    
    public static final double RGAS = 8.31446261815324;
    
    public static final double RGAS_DRY = 287.052874;
    
    public static final double MW_DRY = 0.028964917;
    
    double density;
    
    double pressure;
    
    double invSoundSpeed;
    
    double temperature;
    
    public Air(){}
    
    public Air(double density, double pressure, double invSoundSpeed, double temperature)
    {
        this.density = density;
        this.pressure = pressure;
        this.invSoundSpeed = invSoundSpeed;
        this.temperature = temperature;
    }
    
    void set(double[] values)
    {
        this.density = values[0];
        this.pressure = values[1];
        this.invSoundSpeed = values[2];
        this.temperature = values[3];
    }
    
    void copy(Air air)
    {
        this.density = air.density;
        this.pressure = air.pressure;
        this.invSoundSpeed = air.invSoundSpeed;
        this.temperature = air.temperature;
    }
    
    public double getDensity()
    {
        return density;
    }
    
    public double getPressure()
    {
        return pressure;
    }
    
    public double getInvSoundSpeed()
    {
        return invSoundSpeed;
    }
    
    public double getTemperature()
    {
        return temperature;
    }
    
}

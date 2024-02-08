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
    
    public static double isentropicTemperatureRatio(double M, double gamma)
    {
        return 1.0 + (gamma - 1.0)*0.5*M*M;
    }
    
    public static double isentropicPressureRatio(double M, double gamma)
    {
        return Math.pow(isentropicTemperatureRatio(M,gamma), -gamma/(gamma - 1.0));
    }
    
    public static double isentropicDensityRatio(double M, double gamma)
    {
        return Math.pow(isentropicTemperatureRatio(M,gamma), -1.0/(gamma - 1.0));
    }
    
    public static double machFromPressureRatio(double pressure, double totalPressure, double gamma)
    {
        return Math.sqrt(2/(gamma - 1.0)*(Math.pow(totalPressure/pressure, (gamma-1.0)/gamma) - 1.0));
    }
    
    public static double areaRatio(double M, double gamma)
    {
        double g1 = (gamma + 1)*0.5;
        return M*Math.pow(g1/isentropicTemperatureRatio(M,gamma), g1/(gamma - 1.0));
    }
    
    public static double normalizedChokeRate(double Pt, double Tt, double gamma, double MW)
    {
        return Pt*Math.sqrt(gamma*MW/(Air.RGAS*Tt)*Math.pow(2/(gamma + 1.0),(gamma + 1.0)/(gamma - 1.0)));
    }
    
    public static double supersonicMachFromAreaRatio(double Aratio, double gamma)
    {
        double Mlo = 1.001;
        double Mhi = 8.0;
        
        double M = (Mlo + Mhi)*0.5;
        for(int iter = 0; iter < 20; iter++)
        {
            if(areaRatio(M, gamma) < Aratio)
            {
                Mhi = M;
            }
            else
            {
                Mlo = M;
            }
            M = (Mlo + Mhi)*0.5;
        }
        return M;
    }
    
    public static double subsonicMachFromAreaRatio(double Aratio, double gamma)
    {
        double Mlo = 0.0;
        double Mhi = 0.9999;
        
        double M = (Mlo + Mhi)*0.5;
        for(int iter = 0; iter < 20; iter++)
        {
            if(areaRatio(M, gamma) > Aratio)
            {
                Mhi = M;
            }
            else
            {
                Mlo = M;
            }
            M = (Mlo + Mhi)*0.5;
        }
        return M;
    }
    
    public static double normalShockPressureRatio(double M, double gamma)
    {
        return (2*gamma*M*M - (gamma - 1.0))/(gamma + 1.0);
    }
    
    public static double normalShockDensityRatio(double M, double gamma)
    {
        double M2 = M*M;
        return ((gamma + 1)*M2)/((gamma - 1)*M2 + 2.0);
    }
    
    public static double normalShockMachAfter(double M, double gamma)
    {
        return Math.sqrt(((gamma - 1.0)*M*M + 2.0 )/(2*gamma*M*M - 1.0 + gamma));
    }
    
    public static double normalShockTotalPressureRatio(double M, double gamma)
    {
        double ex = 1.0/(gamma - 1.0);
        return Math.pow(normalShockDensityRatio(M,gamma), gamma*ex)*Math.pow(normalShockPressureRatio(M,gamma),-ex);
    }
}

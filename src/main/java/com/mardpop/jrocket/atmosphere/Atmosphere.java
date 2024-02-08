package com.mardpop.jrocket.atmosphere;

import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class Atmosphere 
{
    private double[] heights;
    
    private double[] values;
    
    private double[] dvalues;
    
    private int heightIdx = 0;
    
    public final Air air = new Air();
    
    public final Vec3 wind = new Vec3(0,0,0);
    
    public static final Air VACUUM = new Air(0,0,1,1);
    
    public static double geometric2geopotential(double z, double R0)
    {
        return (R0*z)/(R0 + z);
    }
    
    public void setConstantTemperature(double temperature, double groundPressure, 
        double groundGravity, double heightIncrement, double maxHeight, double R0)
    {
        final int nHeights = (int)(maxHeight / heightIncrement) + 2;
        this.heights = new double[nHeights];
        this.values = new double[nHeights*4];
        this.dvalues = new double[nHeights*4];
        
        final double invHeightInc = 1.0/heightIncrement;
        
        final double RT = Air.RGAS_DRY*temperature;
        final double constant = -groundGravity/RT;
        final double groundDensity = groundPressure/RT;
        final double soundSpeed = Math.sqrt(Air.GAMMA*RT);
        
        for(int i = 0; i < nHeights; i++)
        {
            this.heights[i] = i*heightIncrement;
            int idx = 4*i;
            double geopotentialHeight = geometric2geopotential(this.heights[i], R0);
            double factor = Math.exp(geopotentialHeight*constant);
            this.values[idx] = groundDensity*factor;
            this.values[idx + 1] = groundPressure*factor;
            this.values[idx + 2] = soundSpeed;
            this.values[idx + 3] = temperature;
        }
        
        for(int i = 1; i < nHeights; i++)
        {
            int lo = 4*(i - 1);
            int hi = 4*i;
            this.dvalues[lo] = (this.values[hi] - this.values[lo])*invHeightInc;
            this.dvalues[lo + 1] = (this.values[hi + 1] - this.values[lo + 1])*invHeightInc;
            this.dvalues[lo + 2] = (this.values[hi + 2] - this.values[lo + 2])*invHeightInc;
            this.dvalues[lo + 3] = (this.values[hi + 3] - this.values[lo + 3])*invHeightInc;
        }
    }
    
    public void loadProfile(String file)
    {
        
    }
    
    public void updateWind(double height, double time)
    {
        
    }
    
    public void update(double z, double time)
    {
        if(z >= heights[heights.length-1])
        {
            air.copy(VACUUM);
            wind.zero();
            return;
        }
        
        z = Double.max(0.0, z);
        
        while(z < this.heights[this.heightIdx])
        {
            this.heightIdx--;
        }
        
        while(z > this.heights[this.heightIdx + 1])
        {
            this.heightIdx++;
        }
        
        double delta = z - this.heights[heightIdx];
        
        int idx = heightIdx << 2;
        
        this.air.density = this.values[idx] + this.dvalues[idx]*delta;
        this.air.pressure = this.values[idx + 1] + this.dvalues[idx + 1]*delta;
        this.air.invSoundSpeed = this.values[idx + 2] + this.dvalues[idx + 2]*delta;
        this.air.temperature = this.values[idx + 3] + this.dvalues[idx + 3]*delta;
        
        this.updateWind(z, time);
    }
}

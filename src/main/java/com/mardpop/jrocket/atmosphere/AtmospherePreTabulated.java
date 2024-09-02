package com.mardpop.jrocket.atmosphere;

import com.mardpop.jrocket.util.Physics;

public class AtmospherePreTabulated extends Atmosphere 
{
    private final double[] heights;
    
    private final double[] values;
    
    private final double[] dvalues;
    
    private int heightIdx = 0;

    private final int lastIdx;

    public AtmosphereWindPreTabulated windTable = null;

    public void finishInit()
    {
        this.values[this.lastIdx] = 0.0;
        this.values[this.lastIdx] = 0.0;
        this.values[this.lastIdx] = 100.0;
        this.values[this.lastIdx] = 100.0;
        for(int i = 1; i < this.values.length; i++)
        {
            final double invHeightInc = 1.0/this.heights[i] - 1.0/this.heights[i - 1];
            final int lo = 4*(i - 1);
            final int hi = 4*i;
            this.dvalues[lo] = (this.values[hi] - this.values[lo])*invHeightInc;
            this.dvalues[lo + 1] = (this.values[hi + 1] - this.values[lo + 1])*invHeightInc;
            this.dvalues[lo + 2] = (this.values[hi + 2] - this.values[lo + 2])*invHeightInc;
            this.dvalues[lo + 3] = (this.values[hi + 3] - this.values[lo + 3])*invHeightInc;
        }
    }

    public AtmospherePreTabulated(double temperature, double groundPressure, 
        double groundGravity, double heightIncrement, double maxHeight, double R0)
    {
        this.lastIdx = (int)(maxHeight / heightIncrement) + 1;
        final int nHeights = lastIdx + 1;
        this.heights = new double[nHeights];
        this.values = new double[nHeights*4];
        this.dvalues = new double[nHeights*4];
        
        final double RT = Air.RGAS_DRY*temperature;
        final double constant = -groundGravity/RT;
        final double groundDensity = groundPressure/RT;
        final double invSoundSpeed = 1.0/Math.sqrt(Air.GAMMA*RT);
        
        for(int i = 0; i < nHeights; i++)
        {
            this.heights[i] = i*heightIncrement;
            int idx = 4*i;
            double geopotentialHeight = geometric2geopotential(this.heights[i], R0);
            double factor = Math.exp(geopotentialHeight*constant);
            this.values[idx] = groundDensity*factor;
            this.values[idx + 1] = groundPressure*factor;
            this.values[idx + 2] = invSoundSpeed;
            this.values[idx + 3] = temperature;
        }

        this.finishInit();
    }

    public AtmospherePreTabulated(double groundTemperature, double groundPressure, 
        double groundGravity, double heightIncrement, double maxHeight, double R0, 
        double[] lapseRate, double[] lapseRateAlt)
    {
        this.lastIdx = (int)(maxHeight / heightIncrement) + 1;
        final int nHeights = lastIdx + 1;
        this.heights = new double[nHeights];
        this.values = new double[nHeights*4];
        this.dvalues = new double[nHeights*4];

        this.values[0] = groundPressure/Air.RGAS_DRY*groundTemperature;
        this.values[1] = groundPressure;
        this.values[2] = 1.0/Math.sqrt(Air.GAMMA*Air.RGAS_DRY*groundTemperature);
        this.values[3] = groundTemperature;

        int lapseIdx = 0;
        for(int i = 1; i < this.lastIdx; i++)
        {
            this.heights[i] = i*heightIncrement;
            if(this.heights[i] > lapseRateAlt[lapseIdx] && lapseIdx < lapseRateAlt.length)
            {
                lapseIdx++;
            }

            final int idx = 4*i;

            this.values[idx + 3] = this.values[idx - 1] + lapseRate[idx]*heightIncrement;
            this.values[idx + 2] = 1.0/Math.sqrt(this.values[idx + 3]*Air.RGAS_DRY*Air.GAMMA);

            double factor = 1.0;
            final double geopotentialHeight1 = geometric2geopotential(this.heights[i - 1], R0);
            final double geopotentialHeight2 = geometric2geopotential(this.heights[i], R0);
            final double dH = geopotentialHeight2 - geopotentialHeight1;
            if(Math.abs(lapseRate[lapseIdx]) < 1e-6)
            {
                factor = Math.exp(-Physics.G0*dH/(Air.RGAS_DRY*this.values[idx - 1]));
            }
            else
            {
                factor = Math.pow((this.values[idx + 3]/this.values[idx - 1]), -Physics.G0/(Air.RGAS_DRY*lapseRate[idx]));
            }
            this.values[idx] = this.values[idx - 4]*factor*(this.values[idx - 1]/this.values[idx + 3]);
            this.values[idx + 1] = this.values[idx - 3]*factor;
        }
        
        this.finishInit();
    }

    public AtmospherePreTabulated(double[] heights, double[] values)
    {
        assert(values.length == heights.length*4);

        this.lastIdx = heights.length;
        final int nHeights = lastIdx + 1;

        this.heights = new double[nHeights];
        this.values = new double[nHeights*4];
        this.dvalues = new double[nHeights*4];

        System.arraycopy(heights, 0, this.heights, 0, nHeights);
        System.arraycopy(values, 0, this.values, 0, nHeights*4);

        this.finishInit();
    }

    @Override
    public void updateWind(Wind wind, double z, double time)
    {
        if(this.windTable != null)
        {
            this.windTable.updateWind(wind, z, time);
        }
    }

    @Override
    public void updateAir(Air air, double z, double time)
    {       
        while(this.heightIdx > 0 && z < this.heights[this.heightIdx])
        {
            this.heightIdx--;
        }
        
        while(this.heightIdx < this.lastIdx && z > this.heights[this.heightIdx + 1])
        {
            this.heightIdx++;
        }
        
        double delta = z - this.heights[heightIdx];
        
        int idx = heightIdx << 2;
        
        air.density = this.values[idx] + this.dvalues[idx]*delta;
        air.pressure = this.values[idx + 1] + this.dvalues[idx + 1]*delta;
        air.invSoundSpeed = this.values[idx + 2] + this.dvalues[idx + 2]*delta;
        air.temperature = this.values[idx + 3] + this.dvalues[idx + 3]*delta;
    }
    
}

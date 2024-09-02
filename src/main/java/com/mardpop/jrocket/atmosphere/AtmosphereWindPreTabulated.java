package com.mardpop.jrocket.atmosphere;

public class AtmosphereWindPreTabulated 
{
    private double[] windTimes;

    private double[] invTimes;
    
    private double[] windHeights;

    private double[] invHeights;
    
    private float[][] windNorth;

    private float[][] windEast;
    
    private int windHeightIdx = 0;

    private int windTimeIdx = 0;

    private double finalTime = 0.0;

    private double finalHeight = 0.0;
    
    public AtmosphereWindPreTabulated(String file)
    {
        
    }
    
    public void updateWind(Atmosphere.Wind wind, double height, double time)
    {
        if(height > this.finalHeight || time > this.finalTime)
        {
            wind.north = 0.0f;
            wind.east = 0.0f;
            return;
        }

        while(height < this.windHeights[this.windHeightIdx])
        {
            this.windHeightIdx--;
        }
        
        while(height > this.windHeights[this.windHeightIdx + 1])
        {
            this.windHeightIdx++;
        }

        while(time < this.windTimes[this.windTimeIdx])
        {
            this.windTimeIdx--;
        }
        
        while(time > this.windTimes[this.windTimeIdx + 1])
        {
            this.windTimeIdx++;
        }

        float dT1 = (float)( (time - windTimes[windTimeIdx])*this.invTimes[windTimeIdx]);
        float dH1 = (float)( (height - windHeights[windHeightIdx])*this.invHeights[windHeightIdx]);
        float A1 = (1-dT1)*(1-dH1);
        float A2 = dT1*(1-dH1);
        float A3 = (1-dT1)*dH1;
        float A4 = dT1*dH1;
        
        wind.north = windNorth[windHeightIdx][windTimeIdx]*A1 + windNorth[windHeightIdx + 1][windTimeIdx]*A2 + windNorth[windHeightIdx][windTimeIdx + 1]*A3 + windNorth[windHeightIdx + 1][windTimeIdx + 1]*A4;
        wind.east = windEast[windHeightIdx][windTimeIdx]*A1 + windEast[windHeightIdx + 1][windTimeIdx]*A2 + windEast[windHeightIdx][windTimeIdx + 1]*A3 + windEast[windHeightIdx + 1][windTimeIdx + 1]*A4;
    }
}

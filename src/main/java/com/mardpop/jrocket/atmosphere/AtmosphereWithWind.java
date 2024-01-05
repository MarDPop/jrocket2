package com.mardpop.jrocket.atmosphere;

/**
 *
 * @author mariu
 */
public class AtmosphereWithWind extends Atmosphere
{
    private double[] windTimes;
    
    private double[] windHeights;
    
    private double[][] windValues;
    
    
    
    public void loadWind(String file)
    {
        
    }
    
    @Override
    public void updateWind(double height, double time)
    {
        if(height > windHeights[windHeights.length - 1])
        {
            this.wind.zero();
            return;
        }
        
        
    }
}

package com.mardpop.jrocket.vehicle;

/**
 *
 * @author mariu
 */
public abstract class SolidFuel extends Fuel
{        
    public final double heatingValue;
    
    public final double gasProductGamma;
    
    public final double gasProductMW;
    
    public enum FUELS
    {
        BLACKPOWDER,
        SUCROSE,
        DEXTROSE,
        SORBITOL
    }
    
    SolidFuel(double density, 
            double heatingValue, double gasProductGamma, double gasProductMW)
    {
        super(1,density);
        this.heatingValue = heatingValue;
        this.gasProductGamma = gasProductGamma;
        this.gasProductMW = gasProductMW;
    }
    
    /**
     * To be overridden by different burn rate
     * @param pressure
     * @param temperature
     * @param flowVelocity
     * @return 
     */
    public abstract double burnRate(double pressure, double temperature, double flowVelocity);
}

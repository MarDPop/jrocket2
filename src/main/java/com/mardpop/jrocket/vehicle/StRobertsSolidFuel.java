package com.mardpop.jrocket.vehicle;

/**
 *
 * @author mariu
 */
public class StRobertsSolidFuel extends SolidFuel 
{
    private final double r0;
    
    private final double coefA;
    
    private final double coefN;
    
    public StRobertsSolidFuel(double r0, double coefA, double coefN, double density, 
            double heatingValue, double gasProductGamma, double gasProductMW)
    {
        super(density, heatingValue, gasProductGamma, gasProductMW);
        this.r0 = r0;
        this.coefA = coefA;
        this.coefN = coefN;
    }
    
    public static double stRobertsBurnRate(double rate0, double a, double n, double pressure)
    {
        return rate0 + a*Math.pow(pressure / 101325,n);
    }
    
    @Override
    public double burnRate(double pressure, double temperature, double v)
    {
        return stRobertsBurnRate(this.r0, this.coefA, this.coefN, pressure);
    }
    
    public static StRobertsSolidFuel create(FUELS fuel)
    {
        StRobertsSolidFuel output = new StRobertsSolidFuel(1e-3, 2e-3, 0.3, 1.8, 3e6, 1.3, 0.042);
        switch(fuel)
        {
            case BLACKPOWDER:
                output = new StRobertsSolidFuel(1e-3, 4e-3, 0.4, 1.6, 3e6, 1.25, 0.042);
                break;
            case SUCROSE:
                output = new StRobertsSolidFuel(1e-6, 0.0016891, 0.32, 1.805, 2175126.085, 1.198, 0.0372);
                break;
            case DEXTROSE:
                output = new StRobertsSolidFuel(1e-3, 2e-3, 0.3, 1.824, 3e6, 1.3, 0.042);
                break;
        }
        return output;
    }
}

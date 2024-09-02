package com.mardpop.jrocket.atmosphere;

/**
 * Don't use this for over 10000 meters or so
 */
public class AtmosphereScaleHeightConstantTemp extends Atmosphere 
{
    private final double invScaleHeight;

    private final Air seaLevel;

    /**
     * Faster exponential function accurate enough for most reasonable altitudes
     * @param x
     * @return
     */
    public static double fastExp(double x)
    {
        final double x2 = x*x;
        return 1 + (x + x)/(2 - x + x2/(6.0 + x2*0.1));
    }

    /**
     * Constructor with no wind
     * @param scaleHeight in meters
     * @param temperature in kelvin
     * @param pressure (sea level) in pascals
     */
    public AtmosphereScaleHeightConstantTemp(double scaleHeight, double temperature, double pressure)
    {
        this(scaleHeight, temperature, pressure, 0, 0);
    }

    public AtmosphereScaleHeightConstantTemp(double scaleHeight, double temperature, double pressure, float eastWind, float northWind)
    {
        super(Air.fromTempAndPessure(temperature, pressure), new Atmosphere.Wind(northWind, eastWind) );
        this.invScaleHeight = -1.0/scaleHeight;
        this.seaLevel = Air.fromTempAndPessure(temperature, pressure);
    }

    @Override
    protected final void updateAir(Air air, double z, double time) 
    {
        final double factor = fastExp(z*this.invScaleHeight);
        air.pressure = this.seaLevel.pressure*factor;
        air.density = this.seaLevel.density*factor;
    }
}

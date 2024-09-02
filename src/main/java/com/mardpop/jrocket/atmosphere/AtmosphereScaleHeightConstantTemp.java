package com.mardpop.jrocket.atmosphere;

public class AtmosphereScaleHeightConstantTemp extends Atmosphere 
{
    private final double invScaleHeight;

    private final Air seaLevel;

    public static double fastExp(double x)
    {
        final double x2 = x*x;
        return 1 + (x + x)/(2 - x + x*x/(6.0 + x2*0.1));
    }

    public AtmosphereScaleHeightConstantTemp(double scaleHeight, double temp, double pressure)
    {
        this(scaleHeight, temp, pressure, 0, 0);
    }

    public AtmosphereScaleHeightConstantTemp(double scaleHeight, double temp, double pressure, float eastWind, float northWind)
    {
        super(Air.fromTempAndPessure(temp, pressure), new Atmosphere.Wind(northWind, eastWind) );
        this.invScaleHeight = -1.0/scaleHeight;
        this.seaLevel = Air.fromTempAndPessure(temp, pressure);
    }

    @Override
    protected final void updateAir(Air air, double z, double time) 
    {
        final double factor = fastExp(z*this.invScaleHeight);
        air.pressure = this.seaLevel.pressure*factor;
        air.density = this.seaLevel.density*factor;
    }
}

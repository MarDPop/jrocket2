package com.mardpop.jrocket.vehicle.propulsion;

public class ThrusterPressureISP extends Thruster 
{
    private final double[] pressure;

    private final double[] exitVelocity;

    private final double[] dVdp;

    private final double minPressure;

    private final double maxExitVelocity;

    public ThrusterPressureISP(double[] pressure, double[] exitVelocity, double massRate)
    {
        assert(pressure.length == exitVelocity.length);
        this.pressure = new double[pressure.length];
        this.exitVelocity = new double[pressure.length];
        this.dVdp = new double[pressure.length];
        System.arraycopy(pressure, 0, this.pressure, 0, pressure.length);
        System.arraycopy(exitVelocity, 0, this.exitVelocity, 0, pressure.length);
        for(int i = 1; i < pressure.length; i++)
        {
            this.dVdp[i - 1] = (this.exitVelocity[i] - this.exitVelocity[i - 1])/(this.pressure[i] - this.pressure[i - 1]);
        }
        this.minPressure = pressure[pressure.length - 1];
        this.maxExitVelocity = exitVelocity[exitVelocity.length - 1];
        this.massRate = massRate;
    }

    public double getExitVelocity(double pressure)
    {
        if(pressure > this.pressure[0])
        {
            return this.exitVelocity[0];
        }
        if(pressure < this.minPressure)
        {
            return this.maxExitVelocity;
        }
        int idx = 0;
        while(idx < this.pressure.length)
        {
            if(pressure < this.pressure[idx])
            {
                break;
            }
            idx++;
        }
        return this.exitVelocity[idx] + this.dVdp[idx]*(pressure - this.pressure[idx]);
    }

    public void update(double pressure, double time) 
    {
        this.thrust = this.getExitVelocity(pressure)*this.massRate;
    }
}

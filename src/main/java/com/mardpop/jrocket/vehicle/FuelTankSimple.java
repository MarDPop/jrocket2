package com.mardpop.jrocket.vehicle;

/**
 *
 * @author mariu
 */
public class FuelTankSimple extends FuelTank 
{
    public final double maxFuelMass;
    
    public final double maxI_rr;
    
    public final double maxI_xx;
    
    public final double deltaCG_x;
    
    private final double dI_rr;
    
    private final double dI_xx;
    
    private final double dCG_x;

    public FuelTankSimple(Fuel fuel, double maxFuelMass, double maxI_rr, 
            double maxI_xx, double deltaCG_x)
    {
        super(fuel);
        this.maxFuelMass = maxFuelMass;
        this.maxI_rr = maxI_rr;
        this.maxI_xx = maxI_xx;
        this.deltaCG_x = deltaCG_x;
        
        this.dI_rr = maxI_rr / maxFuelMass;
        this.dI_xx = maxI_xx / maxFuelMass;
        this.dCG_x = deltaCG_x / maxFuelMass;
    }
    
    @Override
    public void setMass(double mass) 
    {
        this.mass = mass;
        this.I_rr = this.dI_rr*mass;
        this.I_xx = this.dI_xx*mass;
        this.centerOfMassChange.z(this.dCG_x*mass);
    }

    @Override
    public void update(double dm) 
    {
        this.setMass(this.mass + dm);
    }
    
}

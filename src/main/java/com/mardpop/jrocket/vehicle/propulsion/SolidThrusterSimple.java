package com.mardpop.jrocket.vehicle.propulsion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author mariu
 */
public class SolidThrusterSimple extends SolidThruster 
{
    private final double[] times;
    
    private final double[] thrusts;
    
    private final double[] massRates;
    
    private final double[] dThrust;
    
    private final double[] dMassRate;
    
    private final double tFinal;
    
    private int tIdx;
    
    public SolidThrusterSimple(double[] times, double[] thrusts, double[] massRates,
            double Irr_full, double Ixx_full)
    {
        this.times = times;
        this.thrusts = thrusts;
        this.massRates = massRates;
        this.dThrust = new double[times.length];
        this.dMassRate = new double[times.length];
        
        this.tFinal = times[times.length - 1];
        double massSum = 0;
        for(int i = 1; i < times.length; i++)
        {
            double dT = this.times[i] - this.times[i-1];
            this.dThrust[i-1] = (this.thrusts[i] - this.thrusts[i-1])/dT;
            this.dMassRate[i-1] = (this.massRates[i] - this.massRates[i-1])/dT;
            massSum += (this.massRates[i-1]*dT);
        }

        this.solidFuel = new PropellantTankSimple(null, massSum, Irr_full, Ixx_full, 0);
    }
    
    public static SolidThrusterSimple load(String filename)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            String line = reader.readLine();
            if(line == null)
            {
                return null;
            }
            line = reader.readLine();
            String[] data = line.split("\\s+");
            double Irr_full = Double.parseDouble(data[0]);
            double Ixx_full = Double.parseDouble(data[1]);
            ArrayList<Double> times = new ArrayList<>();
            ArrayList<Double> massRates = new ArrayList<>();
            ArrayList<Double> thrusts = new ArrayList<>();

            while(line != null)
            {
                line = reader.readLine();
                if(line != null)
                {
                    data = line.split("\\s+");
                    times.add(Double.valueOf(data[0]));
                    massRates.add(Double.valueOf(data[1]));
                    thrusts.add(Double.valueOf(data[2]));
                }
            }
            if(times.size() > 1) 
            {
                double[] aTimes = new double[times.size()];
                double[] aThrusts = new double[times.size()];
                double[] aMassRates = new double[times.size()];
                for(int i = 0; i < times.size(); i++)
                {
                    aTimes[i] = times.get(i);
                    aThrusts[i] = thrusts.get(i);
                    aMassRates[i] = massRates.get(i);
                }
                return new SolidThrusterSimple(aTimes, aThrusts, aMassRates, Irr_full, Ixx_full);
            }
            
        }
        catch(IOException ex)
        {
            // DO nothing
        }
        return null;
    }
    
    @Override
    public void update(double pressure, double time) 
    {
        if(time > this.tFinal)
        {
            this.massRate = 0;
            this.thrust = 0;
            return;
        }
        while(time > this.times[this.tIdx + 1])
        {
            this.tIdx++;
        }
        while(time < this.times[this.tIdx])
        {
            this.tIdx--;
        }
        
        double deltaT = time - this.times[this.tIdx];
        this.massRate = this.massRates[this.tIdx] + deltaT*this.dMassRate[this.tIdx];
        this.thrust = this.thrusts[this.tIdx] + deltaT*this.dMassRate[this.tIdx];
    }
    
}

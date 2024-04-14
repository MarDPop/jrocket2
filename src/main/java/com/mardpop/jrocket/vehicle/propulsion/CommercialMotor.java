package com.mardpop.jrocket.vehicle.propulsion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.mardpop.jrocket.vehicle.InertiaSimple;

public class CommercialMotor 
{
    public final String name;

    private final InertiaSimple inertia_empty = new InertiaSimple();

    private final InertiaSimple inertia_delta = new InertiaSimple();

    private final double[] thrust_curve;

    private final double[] thrust_curve_delta;

    private final double[] mass_curve;

    private final double[] mass_curve_delta;

    private final double[] time_curve;

    public final double time_final;

    public final double radius;

    public final double length;

    public double thrusterXOffset;

    public final int lastIndex;

    public CommercialMotor(String name, ArrayList<Double> thrust_curve, ArrayList<Double> time_curve,
        final double mass_empty, final double mass_propellant, double radius, double length) 
    {
        this.name = name;
        final int nEntry = thrust_curve.size();
        this.lastIndex = nEntry-1;
        this.thrust_curve = new double[nEntry];
        this.mass_curve = new double[nEntry];
        this.thrust_curve_delta = new double[nEntry];
        this.mass_curve_delta = new double[nEntry];
        this.time_curve = new double[nEntry];

        this.time_final = time_curve.get(this.lastIndex);

        this.radius = radius;
        this.length = length;

        double boreRadius = radius*0.3;
        double caseThickness = radius*0.9;

        this.inertia_empty.mass = mass_empty;

        this.inertia_empty.Irr = (caseThickness*caseThickness + radius*radius)*0.5;
        this.inertia_empty.Ixx  = mass_empty/12*(3*(caseThickness*caseThickness 
            + radius*radius) + length*length);

        double Irr_prop = mass_propellant*(caseThickness*caseThickness + boreRadius*boreRadius)*0.5;
        double Ixx_prop  = mass_propellant/12*(3*(caseThickness*caseThickness 
            + boreRadius*boreRadius) + length*length);

        this.inertia_empty.CGx = length*0.5;

        
        InertiaSimple inertia_prop = new InertiaSimple(mass_propellant, Ixx_prop, Irr_prop, length*0.5);
        InertiaSimple inertia_full = new InertiaSimple(inertia_prop, this.inertia_empty);
        
        InertiaSimple delta = new InertiaSimple(mass_propellant,
            (inertia_full.Ixx - inertia_empty.Ixx)/mass_propellant,
            (inertia_full.Irr - inertia_empty.Irr)/mass_propellant,
            0.0);

        this.inertia_delta.copy(delta);

        for (int i = 0; i < nEntry; i++) 
        {
            this.thrust_curve[i] = thrust_curve.get(i);
            this.time_curve[i] = time_curve.get(i);
        }

        for (int i = 1; i < nEntry; i++) 
        {
            this.mass_curve[i] = this.mass_curve[i-1] + 
                (thrust_curve.get(i) + thrust_curve.get(i-1))*0.5*(this.time_curve[i] - this.time_curve[i-1]);
        }

        double factor = mass_propellant/this.mass_curve[nEntry-1];
        for (int i = 0; i < nEntry; i++) 
        {
            this.mass_curve[i] = mass_empty + mass_propellant - this.mass_curve[i]*factor;
        }

        for (int i = 1; i < nEntry; i++) 
        {
            double dt = time_curve.get(i) - time_curve.get(i-1);
            this.thrust_curve_delta[i-1] = (thrust_curve.get(i) - thrust_curve.get(i-1))/dt;
            this.mass_curve_delta[i-1] = (mass_curve[i] - mass_curve[i-1])/dt;
        }
    }

    public double[] getTimes()
    {
        return this.time_curve;
    }

    public double getPropellantMass()
    {
        return this.mass_curve[0] - this.mass_curve[this.lastIndex];
    }

    public double[] getValuesAtTime(double time)
    {
        if(time < this.time_curve[0])
        {
            return this.getValuesAtTime(0.0, 0);
        }
        
        if(time > this.time_curve[this.lastIndex])
        {
            return new double[] {this.thrust_curve[this.lastIndex],
                this.mass_curve[this.lastIndex],
                this.inertia_empty.Ixx,
                this.inertia_empty.Irr,
                this.inertia_empty.CGx};
        }

        int tIdx = 0;
        while(this.time_curve[tIdx + 1] < time)
        {
            tIdx++;
        }

        return getValuesAtTime(time, tIdx);
    }

    public double[] getValuesAtTime(final double time, final int tIdx)
    {
        double[] values = new double[5];
        final double dt = time - this.time_curve[tIdx];
        values[0] = this.thrust_curve[tIdx] + this.thrust_curve_delta[tIdx]*dt;
        values[1] = this.mass_curve[tIdx] + this.mass_curve_delta[tIdx]*dt;
        final double dm = values[1] - this.inertia_empty.mass;
        values[2] = this.inertia_empty.Ixx + this.inertia_delta.Ixx*dm;
        values[3] = this.inertia_empty.Irr + this.inertia_delta.Irr*dm;
        values[4] = this.thrusterXOffset - (this.inertia_empty.CGx + this.inertia_delta.CGx*dm);
        return values;
    }

    public static CommercialMotor loadRASPFile(String filename) throws FileNotFoundException, IOException
    {
        try(BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                if(line.charAt(0) != ';')
                {
                    break;
                }
            }
            String[] split = line.trim().split("\\s+");

            String name = split[0];
            double mmDiameter = Double.parseDouble(split[1]);
            double mmLength = Double.parseDouble(split[2]);
            double kgProp = Double.parseDouble(split[4]);
            double kgFull = Double.parseDouble(split[5]);

            ArrayList<Double> times = new ArrayList<>();
            ArrayList<Double> thrusts = new ArrayList<>();
            
            times.add(0.0);
            thrusts.add(0.0);
            while((line = br.readLine()) != null)
            {
                split = line.trim().split("\\s+");

                times.add(Double.parseDouble(split[0]));
                thrusts.add(Double.parseDouble(split[1]));
            }

            double radius = mmDiameter*0.0005;
            double length = mmLength*0.001;
            return new CommercialMotor(name, thrusts, times, kgFull - kgProp, kgProp, radius, length);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}

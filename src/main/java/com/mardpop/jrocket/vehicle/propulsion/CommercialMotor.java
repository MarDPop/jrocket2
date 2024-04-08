package com.mardpop.jrocket.vehicle.propulsion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.mardpop.jrocket.vehicle.InertiaSimple;

public class CommercialMotor 
{
    private final InertiaSimple inertia_empty = new InertiaSimple();

    private final InertiaSimple inertia_delta = new InertiaSimple();

    private final double[] thrust_curve;

    private final double[] thrust_curve_delta;

    private final double[] mass_curve;

    private final double[] mass_curve_delta;

    private final double[] time_curve;

    public final double radius;

    public final double length;

    public CommercialMotor(ArrayList<Double> thrust_curve, ArrayList<Double> time_curve,
        final double mass_empty, final double mass_propellant, double radius, double length) 
    {
        final int nEntry = thrust_curve.size();
        this.thrust_curve = new double[nEntry];
        this.mass_curve = new double[nEntry];
        this.thrust_curve_delta = new double[nEntry];
        this.mass_curve_delta = new double[nEntry];
        this.time_curve = new double[nEntry];

        this.radius = radius;
        this.length = length;

        double boreRadius = radius*0.3;
        double caseThickness = radius*0.9;

        double Irr_case = (caseThickness*caseThickness + radius*radius)*0.5;
        double Ixx_case  = 1.0/12*(3*(caseThickness*caseThickness 
            + radius*radius) + length*length);

        double Irr_prop = (caseThickness*caseThickness + boreRadius*boreRadius)*0.5;
        double Ixx_prop  = 1.0/12*(3*(caseThickness*caseThickness 
            + boreRadius*boreRadius) + length*length);

        double CGx = length*0.5;

        this.inertia_empty.copy(new InertiaSimple(mass_empty, Ixx_case, Irr_case ,CGx));
        
        InertiaSimple inertia_prop = new InertiaSimple(mass_propellant, Ixx_prop, Irr_prop, CGx);
        InertiaSimple inertia_full = new InertiaSimple(inertia_prop, this.inertia_empty);
        
        InertiaSimple delta = new InertiaSimple(0.0,
            (inertia_full.getIxx() - inertia_empty.getIxx())/mass_propellant,
            (inertia_full.getIrr() - inertia_empty.getIrr())/mass_propellant,
            mass_propellant);
        this.inertia_delta.copy(delta);

        for (int i = 0; i < nEntry; i++) 
        {
            this.thrust_curve[i] = thrust_curve.get(i);
            this.time_curve[i] = time_curve.get(i);
        }

        for (int i = 1; i < nEntry; i++) 
        {
            this.mass_curve[i] = this.mass_curve[i-1] + thrust_curve.get(i);
        }

        double factor = this.mass_curve[nEntry-1]/mass_propellant;
        for (int i = 0; i < nEntry; i++) 
        {
            this.mass_curve[i] = mass_propellant - this.mass_curve[i]*factor;
        }

        for (int i = 1; i < nEntry; i++) 
        {
            double dt = time_curve.get(i) - time_curve.get(i-1);
            this.thrust_curve_delta[i] = (thrust_curve.get(i) - thrust_curve.get(i-1))/dt;
            this.mass_curve_delta[i] = (mass_curve[i] - mass_curve[i-1])/dt;
        }
    }

    public double[] getTimes()
    {
        return this.time_curve;
    }

    public double[] getValuesAtTime(double time, final int tIdx)
    {
        final double dt = time - this.thrust_curve[tIdx];
        double[] values = new double[5];
        values[0] = this.thrust_curve[tIdx] + this.thrust_curve_delta[tIdx]*dt;
        values[1] = this.mass_curve[tIdx] + this.mass_curve_delta[tIdx]*dt;
        final double dm = values[0] - this.inertia_empty.getMass();
        values[2] = this.inertia_empty.getIxx() + this.inertia_delta.getIxx()*dm;
        values[3] = this.inertia_empty.getIrr() + this.inertia_delta.getIrr()*dm;
        values[4] = this.inertia_empty.getCGx() + this.inertia_delta.getCGx()*dm;
        return values;
    }

    public static CommercialMotor loadRASPFile(String filename) throws FileNotFoundException, IOException
    {
        try(BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
            String line;
            while(true)
            {
                line = br.readLine();
                if(line.charAt(0) != ';')
                {
                    break;
                }
            }
            String[] split = line.trim().split("\\s+");

            double mmDiameter = Double.parseDouble(split[1]);
            double mmLength = Double.parseDouble(split[2]);
            double kgProp = Double.parseDouble(split[4]);
            double kgFull = Double.parseDouble(split[5]);

            ArrayList<Double> times = new ArrayList<>();
            ArrayList<Double> thrusts = new ArrayList<>();
            while((line = br.readLine()) != null)
            {
                split = line.trim().split("\\s+");

                times.add(Double.parseDouble(split[0]));
                thrusts.add(Double.parseDouble(split[1]));
            }

            double radius = mmDiameter*0.0005;
            double length = mmLength*0.001;
            return new CommercialMotor(thrusts, times, kgFull - kgProp, kgProp, radius, length);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}

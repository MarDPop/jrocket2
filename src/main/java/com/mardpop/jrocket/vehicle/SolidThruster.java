package com.mardpop.jrocket.vehicle;

import java.util.ArrayList;
import com.mardpop.jrocket.atmosphere.Air;

/**
 *
 * @author mariu
 */
public class SolidThruster extends Thruster 
{
    private final double[] times;
    
    private final double[] performance;
    
    private final double[] fuelInertias;
    
    private final double[] deltaPerformance;
    
    private final double[] deltaInertia;
    
    private int tIdx = 0;
    
    private final double tFinal;
    
    private final double Aexit;
    
    public SolidThruster(double[] times, double[] performance, double[] fuelInertias, double Aexit)
    {
        this.times = times;
        this.performance = performance;
        this.fuelInertias = fuelInertias;
        this.Aexit = Aexit;
        final int n = times.length - 1;
        this.tFinal = times[n];
        this.deltaPerformance = new double[n];
        this.deltaInertia = new double[n];
        for(int i = 1; i < times.length; i++)
        {
            double deltaT = 1.0/(this.times[i] - this.times[i-1]);
            int jhi = i*3;
            int jlo = (i-1)*3;
            this.deltaPerformance[jlo] = (this.performance[jhi] - this.performance[jlo])*deltaT;
            this.deltaPerformance[jlo + 1] = (this.performance[jhi + 1] - this.performance[jlo + 1])*deltaT;
            this.deltaPerformance[jlo + 2] = (this.performance[jhi + 2] - this.performance[jlo + 2])*deltaT;
            
            this.deltaInertia[jlo] = (this.fuelInertias[jhi] - this.fuelInertias[jlo])*deltaT;
            this.deltaInertia[jlo + 1] = (this.fuelInertias[jhi + 1] - this.fuelInertias[jlo + 1])*deltaT;
            this.deltaInertia[jlo + 2] = (this.fuelInertias[jhi + 2] - this.fuelInertias[jlo + 2])*deltaT;
        }
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
        
        int idx = this.tIdx*3;
        
        double deltaT = time - this.times[this.tIdx];
        this.massRate = this.performance[idx] + deltaT*this.deltaPerformance[idx];
        double vExitEffective = this.performance[idx + 1] + deltaT*this.deltaPerformance[idx + 1];
        double pExit = this.performance[idx + 2] + deltaT*this.deltaPerformance[idx + 21];
        
        this.thrust = (pExit - pressure)*Aexit + this.massRate*vExitEffective;
    }
    
    public static SolidThruster create(double throatR, double combustorR, double exitR, double boreR,
        double combustorL, int sections, double sectionGap, SolidFuel fuel, double dischargeCoef, double nozzleEfficiency)
    {
        ArrayList<Double> times = new ArrayList<>();
        ArrayList<Double> massRates = new ArrayList<>();
        ArrayList<Double> vexit = new ArrayList<>();
        ArrayList<Double> pexit = new ArrayList<>();
        ArrayList<Double> mass = new ArrayList<>();
        ArrayList<Double> Ixx = new ArrayList<>();
        ArrayList<Double> Irr = new ArrayList<>();
        
        final double Pambient = 1e5;
        
        final double Pchoke = Math.pow((fuel.gasProductGamma + 1)*0.5, fuel.gasProductGamma/(fuel.gasProductGamma - 1));
        
        final double Athroat = Math.PI*throatR*throatR;
        final double Astar = Athroat*dischargeCoef;
        final double Acombustor = Math.PI*combustorR*combustorR;
        final double Vcone2throat = Math.PI/3.0*combustorR*(combustorR*combustorR + combustorR*throatR + throatR*throatR);
        final double Vempty = Acombustor*combustorL + Vcone2throat;
        final double maxSegmentWidth = combustorL/sections;
        final double Aexit = Math.PI*exitR*exitR;
        
        double segmentWidth = sectionGap;
        double Rbore = boreR;
        double Abore = Math.PI*Rbore*Rbore;
        
        double V = Abore*combustorL + sections*segmentWidth*(Acombustor - Abore) + Vcone2throat;
        
        double Aburn = 2*boreR*Math.PI*combustorL;
        double Rgas = Air.RGAS/fuel.gasProductMW;
        double T = 298;
        double P = 101325;
        double density = P/(Rgas*T);
        double massRate = 0;
        double M = V*density;
        double cv = Rgas/(fuel.gasProductGamma - 1);
        double cp = fuel.gasProductGamma*cv;
        double ratio = (fuel.gasProductGamma - 1)/fuel.gasProductGamma;
        double specificEnergyCombustor = T*cp;
        double E = specificEnergyCombustor*M;
        
        double time = 0;
        double dt = 0.00001;
        while(time < 1000)
        {
            double V0 = V;
            
            double burnRate = fuel.burnRate(P,0,0);
            double dR = burnRate*dt;
            
            Rbore -= dR;
            segmentWidth = Math.min(maxSegmentWidth, segmentWidth + 2*dR);
            Abore = Math.PI*Rbore*Rbore;            
            V = Abore*combustorL + sections*segmentWidth*(Acombustor - Abore) + Vcone2throat;
            
            double dV = V - V0;
            double dM = dV*fuel.density;
            double dE = dM*fuel.heatingValue;
            
            M += dM;
            E += dE;
            
            P = ratio*E/V;
            
            if (V > Vempty)
            {
                break;
            }
            
            time += dt;
        }
        
        
        int n = times.size();
        
        double[] t = new double[n];
        double[] p = new double[n*3];
        double[] m = new double[n*3];
        
        for(int i = 0; i < n; i++)
        {
            t[i] = times.get(i);
            int j = i*3;
            p[j] = massRates.get(i);
            p[j + 1] = vexit.get(i);
            p[j + 2] = pexit.get(i);
            m[j] = mass.get(i);
            m[j + 1] = Irr.get(i);
            m[j + 2] = Ixx.get(i);
        }
        
        return new SolidThruster(t,p,m, Aexit);
    }
    
}

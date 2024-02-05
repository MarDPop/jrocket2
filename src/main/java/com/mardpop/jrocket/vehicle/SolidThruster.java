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
    
    private final double[] fuelIneritias;
    
    public SolidThruster(double[] times, double[] performance, double[] fuelInertias)
    {
        this.times = times;
        this.performance = performance;
        this.fuelIneritias = fuelInertias;
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
            
            double specificEnthalpy = E / M;
            T = specificEnthalpy / cp;
            P = M/V*Rgas*T;
            
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
        
        return new SolidThruster(t,p,m);
    }
    
}

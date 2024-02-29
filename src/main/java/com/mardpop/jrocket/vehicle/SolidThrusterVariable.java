package com.mardpop.jrocket.vehicle;

import java.util.ArrayList;
import com.mardpop.jrocket.atmosphere.Air;
import java.io.*;

/**
 *
 * @author mariu
 */
public class SolidThrusterVariable extends SolidThruster 
{
    private final double[] times;
    
    private final double[] performance;
    
    private final double[] deltaPerformance;
    
    private int tIdx = 0;
    
    private final double tFinal;
    
    private final double areaExit;
    
    private SolidThrusterVariable(PropellantTankInterpolated solidFuel, ArrayList<Double> times, 
                ArrayList<Double> performance, double areaExit)
    {
        this.solidFuel = solidFuel;
        this.times = new double[times.size()];
        for(int i = 0; i < times.size(); i++){this.times[i] = times.get(i);}
        this.performance = new double[3*times.size()];
        for(int i = 0; i < times.size(); i++){this.performance[i] = performance.get(i);}
        
        this.areaExit = areaExit;
        final int n = times.size() - 1;
        this.tFinal = times.get(n);
        this.deltaPerformance = new double[n];
        for(int i = 1; i < this.times.length; i++)
        {
            double deltaT = 1.0/(this.times[i] - this.times[i-1]);
            int jhi = i*3;
            int jlo = (i-1)*3;
            this.deltaPerformance[jlo] = (this.performance[jhi] - this.performance[jlo])*deltaT;
            this.deltaPerformance[jlo + 1] = (this.performance[jhi + 1] - this.performance[jlo + 1])*deltaT;
            this.deltaPerformance[jlo + 2] = (this.performance[jhi + 2] - this.performance[jlo + 2])*deltaT;
        }
    }
    
    public static SolidThrusterVariable load(String filename)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            String line = reader.readLine();
            line = reader.readLine();
            String[] data = line.split("\\s+");
            double A_exit = Double.parseDouble(data[0]);
            int fuelType = Integer.parseInt(data[1]);
            ArrayList<Double> times = new ArrayList<>();
            ArrayList<Double> massRates = new ArrayList<>();
            ArrayList<Double> vExit = new ArrayList<>();
            ArrayList<Double> pExit = new ArrayList<>();
            ArrayList<Double> mass = new ArrayList<>();
            ArrayList<Double> Ixx = new ArrayList<>();
            ArrayList<Double> Irr = new ArrayList<>();
            while(line != null)
            {
                line = reader.readLine();
                if(line != null)
                {
                    data = line.split("\\s+");
                    times.add(Double.valueOf(data[0]));
                    massRates.add(Double.valueOf(data[1]));
                    vExit.add(Double.valueOf(data[2]));
                    pExit.add(Double.valueOf(data[3]));
                    mass.add(Double.valueOf(data[4]));
                    Ixx.add(Double.valueOf(data[5]));
                    Irr.add(Double.valueOf(data[6]));
                }
            }
            if(times.size() > 1) 
            {
                ArrayList<ArrayList<Double>> arrays = getArrays(massRates, vExit, pExit, Irr, Ixx);
                Propellant fuel = new Propellant(fuelType, 1.805);
                PropellantTankInterpolated solidFuel = new PropellantTankInterpolated(fuel,mass, arrays.get(1));
                return new SolidThrusterVariable(solidFuel, times, arrays.get(0),A_exit);
            }
            
        }
        catch(IOException ex)
        {
            // DO nothing
        }
        return null;
    }
    
    public void save(String filename)
    {
        this.solidFuel.setMass(1e300);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false)))
        {
            writer.write("SOLID ROCKET");
            writer.write(Double.toString(this.areaExit));
            
            for(int i = 0; i < this.times.length; i++)
            {
                int idx = i*3;
                if(i > 0)
                {
                    this.solidFuel.takePropellant(this.performance[idx - 3]*(this.times[i] - this.times[i-1]));
                }
                
                Inertia inertia = this.solidFuel.getInertia();
                writer.write(String.format("%8.3f %14.9f %16.2f %14.8f %14.6f %12.6f %12.6f %n", this.times[i], 
                        this.performance[idx], this.performance[idx+1], this.performance[idx+2],
                        inertia.mass, inertia.Ixx, inertia.Iyy));
                
            }
        }
        catch(IOException ex)
        {
            // DO nothing
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
        
        this.thrust = (pExit - pressure)*areaExit + this.massRate*vExitEffective;
    }
    
    public static double[] getExitConditions(double P_total, double T_total, double A_exit,double M_exit_Ideal, double P_exit_RatioIdeal,
            double P_choke, double P_crit, double P_ambient, double cSoundSpeed, SolidFuel fuel)
    {
        final double dMNozzle = (M_exit_Ideal - 1.0)*0.05;
        double[] exitConditions = new double[2];
        if( P_total < P_choke )
        {
            exitConditions[0] = P_ambient;
            double M_exit = Air.machFromPressureRatio(P_ambient, P_total, fuel.gasProductGamma);
            double T_exit = T_total/Air.isentropicTemperatureRatio(M_exit, fuel.gasProductGamma);
            exitConditions[1] = M_exit*Math.sqrt(cSoundSpeed*T_exit);
        }
        else
        {
            if( P_total > P_crit )
            {
                exitConditions[0] = P_total*P_exit_RatioIdeal;
                double T_exit = T_total/Air.isentropicTemperatureRatio(M_exit_Ideal, fuel.gasProductGamma);
                exitConditions[1] = M_exit_Ideal*Math.sqrt(cSoundSpeed*T_exit);
            }
            else
            {
                double minDelta = 1e300;
                double V_exit_found = 0;
                for(double Mshock = 1.0; Mshock < M_exit_Ideal; Mshock += dMNozzle)
                {
                    double areaRatioBefore = Air.areaRatio(Mshock, fuel.gasProductGamma); // Astar1 / A
                    double pTotalAfter = P_total*Air.normalShockPressureRatio(Mshock, fuel.gasProductGamma);
                    double machAfter = Air.normalShockMachAfter(Mshock, fuel.gasProductGamma);
                    double areaRatioAfter = Air.areaRatio(machAfter, fuel.gasProductGamma); // Astar2 / A
                    double A_star2 = areaRatioAfter / areaRatioBefore;
                    double M_exit = Air.subsonicMachFromAreaRatio(A_star2/A_exit, fuel.gasProductGamma);

                    exitConditions[0] = pTotalAfter*Air.isentropicPressureRatio(M_exit, fuel.gasProductGamma);

                    double err = Math.abs(exitConditions[0] - P_ambient);
                    if(err < minDelta)
                    {
                        minDelta = err;
                        double T_exit = T_total/Air.isentropicTemperatureRatio(M_exit, fuel.gasProductGamma);
                        V_exit_found = Math.sqrt(cSoundSpeed*T_exit)*M_exit;
                    }
                }
                exitConditions[1] = V_exit_found; 
            }
        }
        
        return exitConditions;
    }
    
    /**
     *
     * @param R_throat
     * @param R_combustor
     * @param R_exit
     * @param boreR
     * @param combustorL
     * @param sections
     * @param sectionGap
     * @param fuel
     * @param dischargeCoef
     * @param nozzleEfficiency
     * @param P_ambient
     * @return
     */
    public static SolidThrusterVariable create(double R_throat, double R_combustor, double R_exit, double boreR,
        double combustorL, int sections, double sectionGap, SolidFuel fuel, double dischargeCoef, double nozzleEfficiency, double P_ambient)
    {
        ArrayList<Double> times = new ArrayList<>();
        ArrayList<Double> massRates = new ArrayList<>();
        ArrayList<Double> vExit = new ArrayList<>();
        ArrayList<Double> pExit = new ArrayList<>();
        ArrayList<Double> mass = new ArrayList<>();
        ArrayList<Double> Ixx = new ArrayList<>();
        ArrayList<Double> Irr = new ArrayList<>();
        
        final double pChokeRatio = Math.pow((fuel.gasProductGamma + 1)*0.5, fuel.gasProductGamma/(fuel.gasProductGamma - 1));
        final double P_choke = pChokeRatio*P_ambient;
        
        final double A_throat = Math.PI*R_throat*R_throat;
        final double A_star = A_throat*dischargeCoef;
        final double A_combustor = Math.PI*R_combustor*R_combustor;
        final double V_empty = A_combustor*combustorL;
        final double maxSegmentWidth = combustorL/sections;
        final double A_exit = Math.PI*R_exit*R_exit;
        final double A_exit_ratio = A_star/A_exit;
        
        final double Rgas = Air.RGAS/fuel.gasProductMW;
        final double cSoundSpeed = Rgas*fuel.gasProductGamma;
        
        final double M_exit_Ideal = Air.supersonicMachFromAreaRatio(A_exit_ratio, fuel.gasProductGamma);
        final double P_exit_RatioIdeal = Air.isentropicPressureRatio(M_exit_Ideal, fuel.gasProductGamma);
        final double normalShockPressureRatio = Air.normalShockPressureRatio(M_exit_Ideal, fuel.gasProductGamma);

        final double P_crit = P_ambient/(normalShockPressureRatio*P_exit_RatioIdeal);
        final double massFlowChokeRatio = 
                A_star*Math.sqrt(fuel.gasProductGamma*Math.pow((fuel.gasProductGamma + 1)*0.5,(fuel.gasProductGamma + 1.0)/(1.0 - fuel.gasProductGamma)));
        
        double segmentWidth = maxSegmentWidth - sectionGap;
        double R_bore = boreR;
        
        double V_fuel = sections*segmentWidth*(A_combustor - Math.PI*R_bore*R_bore);
        double V = V_empty - V_fuel;
        double T = 298;
        double P = P_ambient;
        double density = P/(Rgas*T);
        double M = V*density;
        double cv = Rgas/(fuel.gasProductGamma - 1);
        double cp = fuel.gasProductGamma*cv;
        double ratio = (fuel.gasProductGamma - 1)/fuel.gasProductGamma;
        double specificEnergyCombustor = T*cp;
        double E = specificEnergyCombustor*M;
        
        double time = 0;
        double dt = 1e-4;
        double timeRecord = time;
        final double timeRecordInterval = 0.1;
        while(time < 1000)
        {
            double V0 = V;
            
            double dR = fuel.burnRate(P,0,0)*dt;
            
            R_bore -= dR;
            segmentWidth -= (2*dR);
            
            V_fuel = sections*segmentWidth*(A_combustor - Math.PI*R_bore*R_bore);
            V = V_empty - V_fuel;
            
            double dV = V - V0;
            double dM_in = dV*fuel.density;
            double dE_in = dM_in*fuel.heatingValue;
            
            double massRate;
            if( P < P_choke )
            {
                double M_exit = Air.machFromPressureRatio(P_ambient, P, fuel.gasProductGamma);
                double T_exit = T/Air.isentropicTemperatureRatio(M_exit, fuel.gasProductGamma);
                double V_exit = M_exit*Math.sqrt(cSoundSpeed*T_exit);
                double rhoExit = P_ambient/(Rgas*T_exit);
                massRate = rhoExit*V_exit*A_exit;
            }
            else
            {
                massRate = massFlowChokeRatio*P*M/V;
            }
            
            double specificEnergy = E/M;
            
            double dM_out = massRate*dt;
            double dE_out = dM_out*specificEnergy;
                        
            M += (dM_in - dM_out);
            E += (dE_in - dE_out);
            
            P = ratio*E/V;
            T = (specificEnergy/cp);
            
            if (V > V_empty)
            {
                break;
            }
            
            if(time >= timeRecord)
            {
                double[] exitConditions = getExitConditions(P, T, A_exit, M_exit_Ideal, P_exit_RatioIdeal,
                    P_choke, P_crit, P_ambient, cSoundSpeed, fuel);
                times.add(time);
                pExit.add(exitConditions[0]);
                vExit.add(exitConditions[1]*nozzleEfficiency);
                massRates.add(massRate);
                
                double massFuel = V_fuel*fuel.density;
                double segmentMass = massFuel/sections;
                double I_xx = 0.5*massFuel*(R_combustor*R_combustor - R_bore*R_bore);
                double I_rr = (I_xx + massFuel*segmentWidth*segmentWidth)/12;
                for(int iSegment = 0; iSegment < sections; iSegment++)
                {
                    double d = sections*maxSegmentWidth + maxSegmentWidth*0.5 - combustorL*0.5;
                    I_rr += segmentMass*d*d;
                }

                mass.add(massFuel);
                Ixx.add(I_xx);
                Irr.add(I_rr);
                
                timeRecord += timeRecordInterval;
            }
            
            time += dt;
        }
        ArrayList<ArrayList<Double>> arrays = getArrays(massRates, vExit, pExit, Irr, Ixx);
        PropellantTankInterpolated solidFuel = new PropellantTankInterpolated(fuel, mass, arrays.get(1));       
        return new SolidThrusterVariable(solidFuel, times, arrays.get(0), A_exit);
    }
    
    /**
     *
     * @param times
     * @param massRates
     * @param vExit
     * @param pExit
     * @param mass
     * @param Ixx
     * @param Irr
     * @return
     */
    private static ArrayList<ArrayList<Double>> getArrays(ArrayList<Double> massRates, ArrayList<Double> vExit,
        ArrayList<Double> pExit, ArrayList<Double> Irr, ArrayList<Double> Ixx)
    {
        final int n = massRates.size();
        
        ArrayList<Double> p = new ArrayList<>();
        ArrayList<Double> I = new ArrayList<>();
        
        for(int i = 0; i < n; i++)
        {
            int j = i*3;
            p.set(j, massRates.get(i));
            p.set(j + 1,vExit.get(i));
            p.set(j + 2,pExit.get(i));

            I.set(j, Irr.get(i));
            I.set(j + 1,Ixx.get(i));
            I.set(j + 2, 0.0);
        }
        ArrayList<ArrayList<Double>> out = new ArrayList<>();
        out.add(p);
        out.add(I);
        return out;
    }
    
}

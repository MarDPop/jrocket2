package com.mardpop.jrocket;

import com.mardpop.jrocket.util.Quaternion;
import com.mardpop.jrocket.util.Vec3;
import com.mardpop.jrocket.vehicle.*;
import com.mardpop.jrocket.vehicle.gnc.SimpleGNC;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.*;

/**
 *
 * @author mariu
 */
public class SimulationSimpleRocket 
{
    private double groundGravity = 9.806;
    
    private double groundPressure = 101325;
    
    private double groundTemperature = 291;
    
    private double latitude = 0.0;
    
    private double longitude = 0.0;
    
    private double altitude = 0.0;
    
    private double timeFinal = 60.0;
    
    private InertiaSimple structureInertia = new InertiaSimple(0.5,1.0,1.0,0.0);
    
    private InertiaSimple fuelInertia = new InertiaSimple(0.5,1.0,1.0,0.0);
    
    private Aerodynamics aerodynamics = new AerodynamicsBallistic(0.5, 0.1);
    
    private Thruster thruster = new Thruster(80,0.1);
    
    private SimpleGNC gnc = new SimpleGNC();
    
    private final ArrayList<Double> times = new ArrayList<>();
    
    private final ArrayList<Double> masses = new ArrayList<>();
    
    private final ArrayList<State> states = new ArrayList<>();
    
    public void setGround(double groundGravity, double groundPressure, double groundTemperature)
    {
        this.groundGravity = groundGravity;
        this.groundPressure = groundPressure;
        this.groundTemperature = groundTemperature + 273.15;
    }
    
    public void setLaunchSite(double latitude, double longitude, double altitude)
    {
        this.longitude = Math.toRadians(longitude);
        this.latitude = Math.toRadians(latitude);
        this.altitude = altitude;
    }
    
    public void setSimulationRunTime(double runTime)
    {
        this.timeFinal = runTime;
    }
    
    public void setStructureInertia(InertiaSimple inertia)
    {
        this.structureInertia = inertia;
    }
    
    public void setFuelInertia(InertiaSimple inertia)
    {
        this.fuelInertia = inertia;
    }
    
    public void load(String filename) throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        JSONObject json = new JSONObject(content);
        JSONObject obj;
        
        if(json.has("Ground"))
        {
            obj = json.getJSONObject("Ground");
            this.setGround(obj.getDouble("Gravity"),
                    obj.getDouble("Pressure"),
                    obj.getDouble("Temperature"));
        }
        
        if(json.has("MaxRunTime"))
        {
            this.setSimulationRunTime(json.getDouble("MaxRunTime"));
        }
        
        if(json.has("Launch"))
        {
            obj = json.getJSONObject("Launch");
            this.setLaunchSite(obj.getDouble("Latitude"),
                    obj.getDouble("Longitude"),
                    obj.getDouble("Altitude"));
        }
        
        if(json.has("InertiaEmpty"))
        {
            obj = json.getJSONObject("InertiaEmpty");
            this.structureInertia = new InertiaSimple(obj.getDouble("Mass"),obj.getDouble("Irr"),
                    obj.getDouble("Ixx"),obj.getDouble("CGx"));
        }
        
        if(json.has("InertiaFuel"))
        {
            obj = json.getJSONObject("InertiaFuel");
            this.fuelInertia = new InertiaSimple(obj.getDouble("Mass"),obj.getDouble("Irr"),
                    obj.getDouble("Ixx"),obj.getDouble("CGx"));
        }
        
        if(json.has("Thruster"))
        {
            obj = json.getJSONObject("Thruster");
            this.thruster = new Thruster(obj.getDouble("Thrust"), obj.getDouble("ISP"));
        }
        
        if(json.has("Aerodynamics"))
        {
            obj = json.getJSONObject("Aerodynamics");
            this.aerodynamics = new AerodynamicsBallistic(obj.getDouble("CD"), obj.getDouble("Area"));
        }
    }
    
    public void run() 
    {
        RocketSimple rocket = new RocketSimple(this.thruster, this.aerodynamics, this.gnc);
        rocket.setGround(this.groundPressure, this.groundTemperature, this.groundGravity, this.latitude);
        rocket.setInertia(this.structureInertia, this.fuelInertia);
        rocket.setLaunchOrientation(0.0, 0.0);
        
        double time = 0;
        double dt = 0.01;
        double timeRecord = 0;
        double dtRecord = 0.5;
        
        this.times.clear();
        this.masses.clear();
        this.states.clear();
        while(time < this.timeFinal)
        {
            if(time >= timeRecord)
            {
                times.add(time);
                masses.add(rocket.getMass());
                states.add(rocket.copy());
                timeRecord += dtRecord;
            }
            
            rocket.push(time, dt);
            time += dt;
        }
    }
    
    public void save(String filename)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false)))
        {
            writer.write(String.format("Launch %12.8f %12.8f %10.2f %n", 
                    Math.toDegrees(this.latitude), Math.toDegrees(this.longitude), this.altitude) );
            
            Vec3 position = new Vec3();
            Quaternion orientation = new Quaternion();
            for(int i = 0; i < this.times.size(); i++)
            {
                State s = this.states.get(i);
                s.getPosition(position);
                s.getOrientation(orientation);
                writer.write(String.format("%8.3f %10.4f %+16.6f %+16.6f %+16.6f %+12.10f %+12.10f %+12.10f %+12.10f %n", 
                        this.times.get(i), this.masses.get(i), position.x(), position.y(), position.z(),
                        orientation.w(), orientation.x(), orientation.y(), orientation.z()));
            }

        }
        catch(IOException ex)
        {
            // DO nothing
        }
    }
}

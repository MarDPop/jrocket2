package com.mardpop.jrocket;

import com.mardpop.jrocket.util.Quaternion;
import com.mardpop.jrocket.util.Vec3;
import com.mardpop.jrocket.vehicle.*;
import com.mardpop.jrocket.vehicle.aerodynamics.Aerodynamics;
import com.mardpop.jrocket.vehicle.aerodynamics.AerodynamicsBallistic;
import com.mardpop.jrocket.vehicle.aerodynamics.AerodynamicsConstantCP;
import com.mardpop.jrocket.vehicle.gnc.SimpleGNC;
import com.mardpop.jrocket.vehicle.propulsion.CommercialMotor;
import com.mardpop.jrocket.vehicle.propulsion.Thruster;

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

    private double pitch = 0.0;
    
    private double heading = 0.0;
    
    private double timeFinal = 600.0;
    
    private InertiaSimple structureInertia = new InertiaSimple(0.5,1.0,1.0,0.0);
    
    private Aerodynamics aerodynamics = new AerodynamicsBallistic(0.5, 0.1);
    
    private CommercialMotor thruster;
    
    private SimpleGNC gnc = new SimpleGNC();
    
    private final ArrayList<Double> times = new ArrayList<>();
    
    private final ArrayList<Double> masses = new ArrayList<>();
    
    private final ArrayList<State> states = new ArrayList<>();
    
    /**
     * Set the ground properties including gravity, pressure, and temperature.
     *
     * @param  groundGravity     the gravity value of the ground
     * @param  groundPressure    the pressure value of the ground
     * @param  groundTemperature the temperature value of the ground in Celsius
     */
    public void setGround(double groundGravity, double groundPressure, double groundTemperature)
    {
        this.groundGravity = groundGravity;
        this.groundPressure = groundPressure;
        this.groundTemperature = groundTemperature + 273.15;
    }
    
    /**
     * Sets the launch site coordinates and altitude.
     *
     * @param  latitude   the latitude of the launch site
     * @param  longitude  the longitude of the launch site
     * @param  altitude   the altitude of the launch site
     */
    public void setLaunchSite(double latitude, double longitude, double altitude)
    {
        this.longitude = Math.toRadians(longitude);
        this.latitude = Math.toRadians(latitude);
        this.altitude = altitude;
    }

    public void setLaunchOrientation(double pitch, double heading)
    {
        this.pitch = Math.toRadians(pitch);
        this.heading = Math.toRadians(heading);
    }
    
    public void setSimulationRunTime(double runTime)
    {
        this.timeFinal = runTime;
    }
    
    public void setStructureInertia(InertiaSimple inertia)
    {
        this.structureInertia = inertia;
    }
    
    public void load(String filename) throws Exception
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
        
        if(json.has("ODE") )
        {
            obj = json.getJSONObject("ODE");
            this.setSimulationRunTime(obj.getDouble("MaxRunTime"));
        }
        
        if(json.has("Launch"))
        {
            obj = json.getJSONObject("Launch");
            this.setLaunchSite(obj.getDouble("Latitude"),
                    obj.getDouble("Longitude"),
                    obj.getDouble("Altitude"));
            this.setLaunchOrientation(obj.getDouble("Pitch"),obj.getDouble("Heading"));
        }

        if(!json.has("Rocket"))
        {
            throw new Exception("Missing Rocket");
        }

        JSONObject rocket = json.getJSONObject("Rocket");
        
        if(rocket.has("InertiaEmpty"))
        {
            obj = rocket.getJSONObject("InertiaEmpty");
            this.structureInertia = new InertiaSimple(obj.getDouble("Mass"),obj.getDouble("Irr"),
                    obj.getDouble("Ixx"),obj.getDouble("CGx"));
        }
        
        if(rocket.has("Thruster"))
        {
            obj = rocket.getJSONObject("Thruster");
            if(obj.has("format"))
            {

            }
            else
            {       
                this.thruster = CommercialMotor.loadRASPFile(obj.getString("file"));
                if(this.thruster == null)
                {
                    throw new Exception("Missing Thruster");
                }
            }
        }
        
        if(rocket.has("Aerodynamics"))
        {
            obj = rocket.getJSONObject("Aerodynamics");
            this.aerodynamics = new AerodynamicsConstantCP(obj.getDouble("CD"),
                    obj.getDouble("CN_alpha"), obj.getDouble("COP_x"), obj.getDouble("Area"));
        }
    }
    
    public void run() 
    {
        RocketSimple rocket = new RocketSimple(this.thruster, this.aerodynamics, this.gnc);
        rocket.setGround(this.groundPressure, this.groundTemperature, this.groundGravity, this.latitude);
        rocket.setInertia(this.structureInertia);
        rocket.setLaunchOrientation(this.pitch, this.heading);
        
        double time = 0;
        double dt = 0.001;
        double timeRecord = 0;
        double dtRecord = 0.5;
        
        final Vec3 position = new Vec3();
        this.times.clear();
        this.masses.clear();
        this.states.clear();

        rocket.init();

        while(time < this.timeFinal)
        {
            if(time >= timeRecord)
            {
                times.add(time);
                masses.add(rocket.getMass());
                states.add(rocket.copy());
                timeRecord += dtRecord;

                rocket.getPosition(position);
                if(position.z < -1.0)
                {
                    break;
                }
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
            Vec3 velocity = new Vec3();
            Quaternion orientation = new Quaternion();
            Vec3 angularVelocity = new Vec3();
            for(int i = 0; i < this.times.size(); i++)
            {
                State s = this.states.get(i);
                s.getPosition(position);
                s.getVelocity(velocity);
                s.getOrientation(orientation);
                s.getAngularVelocity(angularVelocity);
                writer.write(String.format("%8.3f %10.4f %+16.6f %+16.6f %+16.6f %+14.6f %+14.6f %+14.6f %+12.10f %+12.10f %+12.10f %+12.10f %+9.6f %+9.6f %+9.6f %n", 
                        this.times.get(i), this.masses.get(i), position.x, position.y, position.z, velocity.x, velocity.y, velocity.z,
                        orientation.w, orientation.x, orientation.y, orientation.z, angularVelocity.x, angularVelocity.y, angularVelocity.z));
            }

        }
        catch(IOException ex) {} // DO nothing

    }
}

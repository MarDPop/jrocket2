package com.mardpop.jrocket;

import org.json.JSONObject;

import com.mardpop.jrocket.atmosphere.Atmosphere;
import com.mardpop.jrocket.atmosphere.AtmospherePreTabulated;
import com.mardpop.jrocket.atmosphere.AtmosphereScaleHeightConstantTemp;
import com.mardpop.jrocket.atmosphere.AtmosphereWindPreTabulated;
import com.mardpop.jrocket.vehicle.FrameAcceleration;
import com.mardpop.jrocket.vehicle.FrameAccelerationLTP;
import com.mardpop.jrocket.vehicle.Rocket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 *
 * @author mariu
 */
public class Simulation 
{

    Rocket rocket = null;

    Atmosphere atmosphere = null;

    FrameAcceleration frameAcceleration = null;

    public void load(String filename) throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        JSONObject json = new JSONObject(content);
        JSONObject obj;

        if(json.has("RocketFile"))
        {
            this.rocket = Rocket.loadFromFile(json.getString("RocketFile"));
        }
        else
        {
            throw new IOException("Rocket File is required");
        }

        double groundGravity = 9.806;
        if(json.has("GroundGravity"))
        {
            groundGravity = json.getDouble("GroundGravity");
        }
        if(json.has("Atmosphere"))
        {
            obj = json.getJSONObject("Atmosphere");
            if(obj.has("Type"))
            {
                double groundTemperature = 292.15;
                double groundPressure = 100000;

                if(obj.has("GroundTemperature"))
                {
                    groundTemperature = obj.getDouble("GroundTemperature");
                }
                else
                {
                    System.err.println("Ground Temperature not specified, assuming 19 C");
                }

                if(obj.has("GroundPressure"))
                {
                    groundPressure = obj.getDouble("GroundPressure");
                }
                else
                {
                    System.err.println("Ground Pressure not specified, assuming 100bar");
                }

                final int type = obj.getInt("Type");
                if(type == 0)
                {
                    double scaleHeight = 8500;
                    if(obj.has("ScaleHeight"))
                    {
                        scaleHeight = obj.getDouble("ScaleHeight");
                    }
                    atmosphere = new AtmosphereScaleHeightConstantTemp(scaleHeight, groundTemperature, groundPressure);
                }
                else if (type == 1)
                {
                    
                    double maxHeight = 6000;
                    double heightIncrement = 100;
                    double R0 = 6371000;
                    double[] lapseRate = null;
                    double[] lapseRateAlt = null;
                    String windFile = null;
                    if(obj.has("MaxHeight"))
                    {
                        maxHeight = obj.getDouble("MaxHeight");
                    }
                    if(obj.has("HeightIncrement"))
                    {
                        heightIncrement = obj.getDouble("HeightIncrement");
                    }
                    if(obj.has("EarthRadius"))
                    {
                        R0 = obj.getDouble("EarthRadius");
                    }

                    if(obj.has("LapseRate"))
                    {
                        lapseRate = new double[obj.getJSONArray("LapseRate").length()];
                        for(int i = 0; i < lapseRate.length; i++)
                        {
                            lapseRate[i] = obj.getJSONArray("LapseRate").getDouble(i);
                        }
                    }
                    if(obj.has("LapseRateAlt"))
                    {
                        lapseRateAlt = new double[obj.getJSONArray("LapseRateAlt").length()];
                        for(int i = 0; i < lapseRateAlt.length; i++)
                        {
                            lapseRateAlt[i] = obj.getJSONArray("LapseRateAlt").getDouble(i);
                        }
                    }
                    if(obj.has("WindFile"))
                    {
                        windFile = obj.getString("WindFile");
                    }
                    
                    AtmospherePreTabulated atm;
                    if(lapseRate!= null && lapseRateAlt!= null && lapseRate.length == lapseRateAlt.length)
                    {
                        atm = new AtmospherePreTabulated(groundTemperature, groundPressure, 
                            groundGravity, maxHeight, heightIncrement, R0, lapseRate, lapseRateAlt);
                    }
                    else
                    {
                        atm = new AtmospherePreTabulated(groundTemperature, groundPressure, 
                            groundGravity, maxHeight, heightIncrement, R0);
                    }
                    
                    if(windFile != null)
                    {
                        atm.windTable = new AtmosphereWindPreTabulated(windFile);
                    }
                    this.atmosphere = atm;
                }
            }
            else
            {
                throw new IOException("Need to specify atmosphere type");
            }
        }


        if(json.has("FrameAcceleration"))
        {
            obj = json.getJSONObject("FrameAcceleration");
            double latitude = 0;
            double altitude = 0;

            int type = 0;
            if(obj.has("Type"))
            {
                type = obj.getInt("Type");
            }

            if(type == 0)
            {
               frameAcceleration = new FrameAcceleration(); 
               frameAcceleration.z = -groundGravity;
            }  
            else if(type == 1)
            {
                if(obj.has("Latitude"))
                {
                    latitude = obj.getDouble("Latitude");
                }
                else
                {
                    throw new IOException("Latitude required for FrameAccelerationLTP");
                }

                if(obj.has("Altitude"))
                {
                    altitude = obj.getDouble("Altitude");
                }
                frameAcceleration = new FrameAccelerationLTP(latitude, altitude, groundGravity);
            }
        }
    }
    
    public void run()
    {
        
    }
}

package com.mardpop.jrocket;

import org.json.JSONObject;

import com.mardpop.jrocket.atmosphere.Atmosphere;
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

    FrameAccelerationLTP frameAcceleration = null;

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

        if(json.has("Atmosphere"))
        {
            
        }
    }
    
    public void run()
    {
        
    }
}

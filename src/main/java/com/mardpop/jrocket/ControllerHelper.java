package com.mardpop.jrocket;

import org.json.*;

public class ControllerHelper {

    public static JSONObject createSimulationSimpleJson()
    {
        JSONObject json = new JSONObject();

        json.put("Type", "Simple");

        JSONObject ground = new JSONObject();
        ground.put("Gravity", 9.8);
        ground.put("Pressure", 101325.0);
        ground.put("Temperature", 18.0);

        JSONObject launch = new JSONObject();
        launch.put("Latitude", 0);
        launch.put("Longitude", 0);
        launch.put("Altitude", 0);
        launch.put("Pitch", 0);
        launch.put("Heading", 0);

        JSONObject ode = new JSONObject();
        ode.put("MaxRuntime", 300);

        json.put("Ground", ground);
        json.put("Launch", launch);
        json.put("Ode", ode);

        json.put("Rocket", createSimpleRocketJson());

        return json;
    }
    
    public static JSONObject createSimpleRocketJson()
    {
        JSONObject json = new JSONObject();

        JSONObject inertiaEmpty = new JSONObject();
        inertiaEmpty.put("CGx", 0);
        inertiaEmpty.put("Irr", 0.3);
        inertiaEmpty.put("Ixx", 0.1);
        inertiaEmpty.put("Mass", 2);

        JSONObject inertiaFuel = new JSONObject();
        inertiaFuel.put("CGx", 0);
        inertiaFuel.put("Irr", 0.3);
        inertiaFuel.put("Ixx", 0.1);
        inertiaFuel.put("Mass", 14);

        JSONObject thruster = new JSONObject();
        thruster.put("Type", 1);
        thruster.put("Thrust", 1000.0);
        thruster.put("ISP", 100.0);

        JSONObject aerodynamics = new JSONObject();
        aerodynamics.put("CD", 0.7);
        aerodynamics.put("CL_alpha", 0.0);
        aerodynamics.put("CM_alpha", 0.0);
        aerodynamics.put("LiftInducedDrag", 0.0);
        aerodynamics.put("Area", 0.03);

        json.put("InertiaEmpty", inertiaEmpty);
        json.put("InertiaFuel", inertiaFuel);
        json.put("Thruster", thruster);
        json.put("Aerodynamics", aerodynamics);

        json.put("GNC", 0);

        return json;
    }
}

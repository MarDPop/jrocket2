package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Matrix3;
import com.mardpop.jrocket.util.Quaternion;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class Rocket {
    
    final Vec3 position = new Vec3();
    
    final Vec3 velocity  = new Vec3();
    
    final Quaternion orientation = new Quaternion();
    
    final Vec3 angular_velocity = new Vec3();
    
    
    final Inertia inertia = new Inertia();
    
    
    final Vec3 forces = new Vec3();
    
    final Vec3 moments = new Vec3();
    
    
    final Inertia inertia_empty = new Inertia();
    
        
    Matrix3 CS;
    
    
    public final Propulsion propulsion;
    
    public final Aerodynamics aerodynamics;
    
    
    public final Guidance guidance;
    
    public final Navigation navigation;
    
    public final Control control;
    

    Rocket(Propulsion propulsion, Aerodynamics aerodynamics,
            Guidance guidance, Navigation navigation, Control control) 
    {
        this.propulsion = propulsion;
        this.aerodynamics = aerodynamics;
        this.guidance = guidance;
        this.navigation = navigation;
        this.control = control;
    }
    
    void update(Atmosphere atm, double time, double dt)
    {
        this.CS = this.orientation.toRotationMatrix();
        this.inertia.combine(this.inertia_empty, this.propulsion.fuel_inertia);
        
        this.aerodynamics.update(this, atm.air);
        
    }
    
    
    
    
    
}

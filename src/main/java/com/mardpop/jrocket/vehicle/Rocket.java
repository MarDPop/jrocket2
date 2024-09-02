package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.vehicle.propulsion.Propellant;
import com.mardpop.jrocket.vehicle.propulsion.PropellantTank;
import com.mardpop.jrocket.vehicle.propulsion.PropellantTankSimple;
import com.mardpop.jrocket.vehicle.propulsion.Propulsion;
import com.mardpop.jrocket.vehicle.propulsion.SolidThrusterSimple;
import com.mardpop.jrocket.vehicle.propulsion.Thruster;
import com.mardpop.jrocket.vehicle.aerodynamics.Aerodynamics;
import com.mardpop.jrocket.vehicle.aerodynamics.AerodynamicsBallistic;
import com.mardpop.jrocket.vehicle.aerodynamics.AerodynamicsBasicCoefficients;
import com.mardpop.jrocket.vehicle.aerodynamics.AerodynamicsConstantCP;
import com.mardpop.jrocket.atmosphere.Atmosphere;

import com.mardpop.jrocket.atmosphere.AerodynamicQuantities;
import com.mardpop.jrocket.util.Matrix3;
import com.mardpop.jrocket.util.Quaternion;
import com.mardpop.jrocket.util.Util;
import com.mardpop.jrocket.util.Vec3;
import com.mardpop.jrocket.vehicle.gnc.GNC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

/**
 *
 * @author mariu
 */
public class Rocket extends State 
{    
    final Inertia inertia = new Inertia();
    
    
    final Vec3 forces = new Vec3();
    
    final Vec3 moments = new Vec3();
    
    
    final Inertia inertiaEmpty = new Inertia();
    
        
    public final Matrix3 CS = new Matrix3();
    
    public Atmosphere atm;
    
    public final AerodynamicQuantities aero = new AerodynamicQuantities();

    private final Vec3 wind = new Vec3();

    public FrameAcceleration frameAcceleration = new FrameAcceleration();
    
    
    public final Propulsion propulsion;
    
    public final Aerodynamics aerodynamics;

    public GNC gnc;

    public static Rocket loadFromFile(String filename) throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        JSONObject json = new JSONObject(content);
        JSONObject obj;

        Rocket rocket = null;

        Inertia inertiaEmpty;
        if(json.has("InertiaEmpy"))
        {
            obj = json.getJSONObject("InertiaEmpty");

            double mass = obj.getDouble("Mass");
            double ixx = obj.getDouble("Ixx");
            double iyy = obj.getDouble("Iyy");
            double izz = obj.getDouble("Izz");
            double ixy = obj.getDouble("Ixy");
            double ixz = obj.getDouble("Ixz");
            double iyz = obj.getDouble("Iyz");
            double COMx = obj.getDouble("COMx"), COMy = obj.getDouble("COMy"), COMz = obj.getDouble("COMz");
            Vec3 COM = new Vec3(COMx, COMy, COMz);
            inertiaEmpty = new Inertia(mass, ixx, iyy, izz, ixy, ixz, iyz, COM);
        }
        else
        {
            throw new IOException("Missing Empty Inertia");
        }  
        
        Aerodynamics aerodynamics = null;
        Propulsion propulsion = null;
        if(json.has("Propulsion"))
        {
            obj = json.getJSONObject("Propulsion");

            int propulsionType = 0;
            if(obj.has("Type"))
            {
                propulsionType = obj.getInt("Type");
            }
            else
            {
                throw new IOException("Need to specify propulasion type");
            }

            if(propulsionType == 0)
            {
                PropellantTank tank = null;
                Thruster thruster = null;
                Propellant prop = null;
                if(obj.has("Propellant"))
                {
                    prop = new Propellant(obj.getInt("Id"), obj.getDouble("Density"));
                }
                else
                {
                    throw new IOException("Need to set propellant for tank");
                }

                if(obj.has("PropellantTankSimple"))
                {
                    double mass = obj.getDouble("Full Mass");
                    double maxIxx = obj.getDouble("Full Ixx");
                    double maxIrr = obj.getDouble("Full Irr");
                    double COMx = obj.getDouble("Full COM X");
                    tank = new PropellantTankSimple(prop, mass, maxIxx, maxIrr, COMx);
                }

                if(obj.has("Thruster"))
                {
                    double thrust = obj.getDouble("Thrust");
                    double isp = obj.getDouble("ISP");
                    thruster = new Thruster(thrust, isp);
                }

                if(tank == null || thruster == null)
                {
                    throw new IOException("Need to have a tank and thruster for type 1");
                }
                propulsion = new Propulsion(thruster, tank);
            }
            else if(propulsionType == 1)
            {
                SolidThrusterSimple thruster;
                if(obj.has("File"))
                {
                    thruster = SolidThrusterSimple.load(obj.getString("File"));
                }
                else
                {
                    throw new IOException("Need to specificy file");
                }
                propulsion = thruster.toPropulsion();
            }
        }
        else
        {
            throw new IOException("Missing tank");
        }

        if(json.has("Aerodynamics"))
        {
            obj = json.getJSONObject("Aerodynamics");

            int type = 0;
            if(obj.has("Type"))
            {
                type = obj.getInt("Type");
            }
            else
            {
                throw new IOException("Need to specify propulasion type");
            }

            if(type == 0)
            {
                double CD = obj.getDouble("CD");
                double A = obj.getDouble("Area");
                aerodynamics = new AerodynamicsBallistic(CD, A);
            }
            else if(type == 1)
            {
                double CD0 = obj.getDouble("CD0");
                double A = obj.getDouble("Area");
                double CL_alpha = obj.getDouble("CL_alpha");
                double CM_alpha = obj.getDouble("CM_alpha");
                double K = obj.getDouble("K");
                aerodynamics = new AerodynamicsBasicCoefficients(CD0, CL_alpha, CM_alpha, K, A);
            }
            else if(type == 2)
            {
                double CD0 = obj.getDouble("CD0");
                double A = obj.getDouble("Area");
                double CN = obj.getDouble("CN");
                double CPx = obj.getDouble("CPx");
                aerodynamics = new AerodynamicsConstantCP(CD0, CN, CPx, A);
            }
            else
            {
                System.err.println("No aerodynamics found setting null aero");
                aerodynamics = new Aerodynamics();
            }
        }

        rocket = new Rocket(propulsion, aerodynamics);
        return rocket;
    }

    Rocket(Propulsion propulsion, Aerodynamics aerodynamics) 
    {
        this.propulsion = propulsion;
        this.aerodynamics = aerodynamics;
        this.gnc = new GNC(this);
    }
    
    Vec3 getAngularAcceleration()
    {
        Matrix3 I = this.inertia.getMatrix();
        Vec3 Iw = I.mult(this.angular_velocity);
        Vec3 rhs = Vec3.subtract(this.moments, Vec3.cross(this.angular_velocity, Iw));
        I = I.getInverse();
        return I.mult(rhs);
    }

    void updateState(double time)
    {
        this.orientation.setRotationMatrixUnit(this.CS);
        this.inertia.combine(this.inertiaEmpty, this.propulsion.propellant.getInertia());
        
        this.atm.update(this.position.z, time);

        final Atmosphere.Wind wind = this.atm.getWind();

        this.wind.x = this.CS.a00*wind.east + this.CS.a01*wind.north;
        this.wind.y = this.CS.a10*wind.east + this.CS.a11*wind.north;
        this.wind.z = this.CS.a20*wind.east + this.CS.a21*wind.north;
        this.aero.update(this.velocity, this.CS, this.atm.getAir(), this.wind);
        
        this.gnc.update(time);
    }
    
    void updateForces(double time, double dt) 
    {
        this.propulsion.update(this.atm.getAir().getPressure(), time, dt);
        this.aerodynamics.update(aero);
        
        this.forces.set(this.aerodynamics.force);
        this.forces.add(this.propulsion.force);
        
        this.moments.set(this.aerodynamics.getTorque(this.inertia.COM));
        this.moments.add(this.propulsion.getTorque(this.inertia.COM));
    }
    
    void update(double time, double dt)
    {
        double dt_2 = dt*0.5;
        this.updateState(time);
        this.updateForces(time, dt);

        Vec3 position0 = new Vec3(this.position);
        Vec3 velocity0 = new Vec3(this.velocity);
        Quaternion orientation0 = new Quaternion(this.orientation);
        Vec3 angularRate0 = new Vec3(this.angular_velocity);
        
        Vec3 acceleration0 = Vec3.mult(this.forces, 1.0/this.inertia.mass);
        this.frameAcceleration.compute(this.position, this.velocity);
        acceleration0.add(this.frameAcceleration);
        
        Vec3 angularAcceleration0 = this.getAngularAcceleration();

        this.position.add(Vec3.mult(velocity0, dt));
        this.velocity.add(Vec3.mult(acceleration0, dt));
        
        Quaternion qd0 = Util.getQuaternionDelta(this.orientation, this.angular_velocity, dt);
        this.orientation.add(qd0);
        this.angular_velocity.add(Vec3.mult(angularAcceleration0, dt));

        this.updateState(time + dt);
        this.updateForces(time, 0);

        Vec3 acceleration1 = Vec3.mult(this.forces, 1.0/this.inertia.mass);
        this.frameAcceleration.compute(this.position, this.velocity);
        acceleration1.add(this.frameAcceleration);
        
        Vec3 angularAcceleration1 = this.getAngularAcceleration();

        this.position.set(Vec3.add(position0, Vec3.mult(Vec3.add(velocity0, this.velocity), dt_2)));
        this.velocity.set(Vec3.add(velocity0, Vec3.mult(Vec3.add(acceleration0, acceleration1), dt_2)));
        
        Quaternion qd = Util.getQuaternionDelta(this.orientation, Vec3.add(this.angular_velocity, angularRate0), dt_2);
        this.orientation.set(Quaternion.add(orientation0, qd));
        this.angular_velocity.set(Vec3.add(angularRate0, Vec3.mult(Vec3.add(angularAcceleration0, angularAcceleration1), dt_2)));

        this.orientation.normalize();
    }
    
    
    
    
    
}

package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Quaternion;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class State 
{
    protected final Vec3 position = new Vec3();
    
    protected final Vec3 velocity = new Vec3();
    
    protected final Quaternion orientation = new Quaternion();
    
    protected final Vec3 angular_velocity = new Vec3();
    
    public State copy()
    {
        State state = new State();
        state.position.set(this.position);
        state.velocity.set(this.velocity);
        state.orientation.set(this.orientation);
        state.angular_velocity.set(this.angular_velocity);
        return state;
    }
    
    public void getPosition(Vec3 position)
    {
        position.set(this.position);
    }
    
    public void getVelocity(Vec3 velocity)
    {
        velocity.set(this.velocity);
    }
    
    public void getOrientation(Quaternion orientation)
    {
        orientation.set(this.orientation);
    }
    
    public void getAngularVelocity(Vec3 angular_velocity)
    {
        angular_velocity.set(this.angular_velocity);
    }
}

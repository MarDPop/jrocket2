package com.mardpop.jrocket.vehicle;

import com.mardpop.jrocket.util.Matrix3;
import com.mardpop.jrocket.util.Vec3;

/**
 *
 * @author mariu
 */
public class Component
{
    protected final Inertia inertia = new Inertia();
    
    public Inertia getInertia()
    {
        return inertia;
    }    
}

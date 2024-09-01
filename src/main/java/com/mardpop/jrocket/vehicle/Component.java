package com.mardpop.jrocket.vehicle;

/**
 *
 * @author mariu
 */
public class Component
{
    protected final Inertia inertia = new Inertia();
    
    public final Inertia getInertia()
    {
        return new Inertia(inertia);
    }    
}

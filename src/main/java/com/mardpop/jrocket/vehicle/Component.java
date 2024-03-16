package com.mardpop.jrocket.vehicle;

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

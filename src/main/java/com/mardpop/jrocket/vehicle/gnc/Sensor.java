package com.mardpop.jrocket.vehicle.gnc;

/**
 *
 * @author mariu
 */
public class Sensor 
{
    public enum VALUE_TYPE 
    {
        FLAG,
        COUNT,
        FLOAT,
        VECTOR
    }
    
    public final String valueName;
    
    public final VALUE_TYPE valueType;
    
    public Sensor(String name, VALUE_TYPE type)
    {
        this.valueName = name;
        this.valueType = type;
    }
    
}

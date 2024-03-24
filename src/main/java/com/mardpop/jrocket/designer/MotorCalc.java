package com.mardpop.jrocket.designer;

public class MotorCalc 
{

    public static double hoopStressThinWall(double pressure, double radius, double thickness)
    {
        return pressure*radius/thickness;
    }
    
}

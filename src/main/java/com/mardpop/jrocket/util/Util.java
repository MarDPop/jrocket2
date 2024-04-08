package com.mardpop.jrocket.util;

import java.util.List;

/**
 *
 * @author mariu
 */
public class Util 
{
    
    private Util() {
        throw new IllegalStateException("Utility class");
    }

    public static int lowerIndex(double[] values, double value)
    {
        int lo = 0;
        int hi = values.length - 1;
        int mid = (lo + hi) >> 1;
        while(lo != mid)
        {
            if(values[mid] > value)
            {
                lo = mid;
            }
            else
            {
                hi = mid;
            }
            mid = (lo + hi) >> 1; 
        }
        return lo;
    }
    
    public static Quaternion getQuaternionDelta(Quaternion q, Vec3 angular_velocity, double dt)
    {
        dt *= 0.5;
        Quaternion q_dot = new Quaternion();
        q_dot.x = ( angular_velocity.z*q.y - angular_velocity.y*q.z + angular_velocity.x*q.w)*dt;
        q_dot.y = (-angular_velocity.z*q.x + angular_velocity.x*q.z + angular_velocity.y*q.w)*dt;
        q_dot.z = ( angular_velocity.y*q.x - angular_velocity.x*q.y + angular_velocity.z*q.w)*dt;
        q_dot.w = (-angular_velocity.x*q.x - angular_velocity.y*q.y - angular_velocity.z*q.z)*dt;
        return q_dot;
    }
    
    public static void getQuaternionRate(double[] q, double[] angular_velocity, double[] q_dot)
    {
        q_dot[0] = ( angular_velocity[2]*q[1] - angular_velocity[1]*q[2] + angular_velocity[0]*q[3])*0.5;
        q_dot[1] = (-angular_velocity[2]*q[0] + angular_velocity[0]*q[2] + angular_velocity[1]*q[3])*0.5;
        q_dot[2] = ( angular_velocity[1]*q[0] - angular_velocity[0]*q[1] + angular_velocity[2]*q[3])*0.5;
        q_dot[3] = (-angular_velocity[0]*q[0] - angular_velocity[1]*q[1] - angular_velocity[2]*q[2])*0.5;
    }
    
    public static void getQuaternionDelta(double[] q, double[] angular_velocity, double[] q_dot, double dt_half)
    {
        q_dot[0] = ( angular_velocity[2]*q[1] - angular_velocity[1]*q[2] + angular_velocity[0]*q[3])*dt_half;
        q_dot[1] = (-angular_velocity[2]*q[0] + angular_velocity[0]*q[2] + angular_velocity[1]*q[3])*dt_half;
        q_dot[2] = ( angular_velocity[1]*q[0] - angular_velocity[0]*q[1] + angular_velocity[2]*q[3])*dt_half;
        q_dot[3] = (-angular_velocity[0]*q[0] - angular_velocity[1]*q[1] - angular_velocity[2]*q[2])*dt_half;
    }
    
    public static boolean isSorted(double[] arr)
    {
        boolean ascending = true, descending = true;
        for (int i = 1; i < arr.length && (ascending || descending); i++) {
            ascending = ascending && arr[i] >= arr[i-1];
            descending = descending && arr[i] <= arr[i-1];
        }
        return ascending || descending;
    }
    
    public static boolean isSorted(List<Double> arr)
    {
        boolean ascending = true;
        boolean descending = true;
        for (int i = 1; i < arr.size() && (ascending || descending); i++) {
            ascending = ascending && arr.get(i) >= arr.get(i-1);
            descending = descending && arr.get(i) <= arr.get(i-1);
        }
        return ascending || descending;
    }
}

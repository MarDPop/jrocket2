package com.mardpop.jrocket.util;

/**
 *
 * @author mariu
 */
public class Util 
{

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
}

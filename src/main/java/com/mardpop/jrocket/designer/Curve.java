package com.mardpop.jrocket.designer;

import java.util.ArrayList;

public class Curve {
        
    ArrayList<Double> x = new ArrayList<>();

    ArrayList<Double> r = new ArrayList<>();

    public void add(Curve c) 
    {
        x.addAll(c.x);
        r.addAll(c.r);
    }

    static Curve combineCurves(Curve c1, Curve c2)
    {
        Curve c = new Curve();
        c.x.addAll(c1.x);
        c.r.addAll(c1.r);
        c.x.addAll(c2.x);
        c.r.addAll(c2.r);
        return c;
    }

    public void mergeClosePoints(double threshold)
    {
        for(int i = 1; i < x.size(); i++)
        {
            double dx = x.get(i) - x.get(i-1);
            double dr = r.get(i) - r.get(i-1);
            double ds = Math.sqrt(dx*dx + dr*dr);

            if(ds < threshold)
            {
                double avgX = (x.get(i) + x.get(i-1))/2;
                double avgR = (r.get(i) + r.get(i-1))/2;
                x.set(i, avgX);
                r.set(i, avgR);
                x.remove(i+1);
                r.remove(i+1);
            }
        }
    }
}
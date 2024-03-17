package com.mardpop.jrocket.designer;

import java.util.ArrayList;

public class Curve {

    public static class CurvePoint 
    {
        public double x;
        public double r;
        public CurvePoint(double x, double r)
        {
            this.x = x;
            this.r = r;
        }
    }
        
    public ArrayList<CurvePoint> points = new ArrayList<>();

    public void add(Curve c) 
    {
        this.points.addAll(c.points);
    }

    public void mergeClosePoints(double threshold)
    {
        for(int i = 1; i < points.size(); i++)
        {
            CurvePoint p2 = points.get(i-1);
            CurvePoint p1 = points.get(i);
            double dx = p1.x - p2.x;
            double dr = p1.x - p2.x;
            double ds = Math.sqrt(dx*dx + dr*dr);

            if(ds < threshold)
            {
                p2.x = (p2.x + p1.x)/2;
                p2.r = (p2.r + p1.r)/2;

                points.remove(i);
                i--;   
            }
        }
    }
}
package com.mardpop.jrocket.designer;

import java.util.ArrayList;

public class RocketShape 
{
    public static enum NoseConeType
    {
        CONICAL,
        ELLIPTICAL,
        TANGENT_OGIVE,
        SECANT_OGIVE,
        PARABOLIC,
        THREE_FOURTH_POWER,
        HAACK
    }

    public static class RocketShapeParameters
    {
        public double noseConeLength;
        public double noseConeSphericalRadius;
        public double payloadRadius;
        public double payloadLength;
        public double payloadFlangeLength;
        public double tubeRadius;
        public double tubeLength;
        public double tubeFlangeLength;
        public double motorRadius;
        public double motorLength;
        public double finBaseChord;
        public double finTipChord;
        public double finSpan;
        public double finSweep;
        public int numFins;
        public NoseConeType noseConeType;
    }

    Curve body;
    Curve fin;

    RocketShape() {}

    RocketShape(RocketShapeParameters params, double sResolution)
    {
        this.body = generateNoseCone(params.payloadRadius, params.noseConeLength, params.noseConeType,
                params.noseConeSphericalRadius,sResolution);
        double x = params.noseConeLength;
        Curve c = generateFlangedSection(params.payloadRadius, x, 
            params.payloadRadius, x + params.payloadLength, sResolution);
        this.body.add(c);

        x = this.body.x.getLast();
        c = generateFlangedSection(x, params.payloadRadius, x + params.payloadFlangeLength,
            params.tubeRadius, sResolution);
        this.body.add(c);

        x = this.body.x.getLast();
        c = generateFlangedSection(x, params.tubeRadius, x + params.tubeLength,
            params.tubeRadius, sResolution);
        this.body.add(c);
        
        x = this.body.x.getLast();
        c = generateFlangedSection(x, params.tubeRadius, x + params.tubeFlangeLength,
            params.motorRadius, sResolution);
        this.body.add(c);

        x = this.body.x.getLast();
        c = generateFlangedSection(x, params.motorRadius, x + params.motorLength,
            params.motorRadius, sResolution);
        this.body.add(c);

        this.fin = generateFin(params.finBaseChord, params.finTipChord, params.finSpan, params.finSweep,sResolution);
    }

    public static Curve generateConicalNose(double radius, double length, double sphericalRadius, double sResolution)
    {
        assert(sphericalRadius < radius);

        Curve curve = new Curve();

        double x = 0;
        double r = 0;
        double drdx;
        if(sphericalRadius < sResolution) 
        {
            drdx = radius/length;
        }
        else
        {
            final double R_sq = sphericalRadius*sphericalRadius;

            final double d = length - sphericalRadius;
            final double d_sq = d*d;
            final double l_sq = d_sq + radius*radius - R_sq;
            final double a0 = 1.0 + l_sq/R_sq;
            final double a2 = d_sq - l_sq;
            final double c = (-d + Math.sqrt(d_sq - a0*a2))/a0;
            final double xSphere = sphericalRadius - c;
            final double b = c*Math.sqrt(l_sq)/sphericalRadius;
            final double a = radius - b;

            drdx = b/(d + c);
    
            final double angle = Math.acos(c/sphericalRadius);
            final double dTheta = sResolution/sphericalRadius;
            double theta = 0;
            while (theta < angle) 
            {
                x = sphericalRadius*(1 - Math.cos(theta));
                r = sphericalRadius*Math.sin(theta);
                curve.x.add(x);
                curve.r.add(r);
                theta += dTheta;
            }

            curve.x.add(xSphere);
            curve.r.add(a);
        }

        double dx = sResolution/Math.sqrt(1.0 + drdx*drdx);
        double dr = dx*drdx;
        while(x < length)
        {
            curve.x.add(x);
            curve.r.add(r);
            x += dx;
            r += dr;
        }
        curve.x.add(length);
        curve.r.add(radius);
        return curve;
    }

    public static Curve generateEllipticalNose(double radius, double length, double sphericalRadius, double sResolution)
    {
        Curve curve = new Curve();
        double x = 0;
        double r = 0;

        curve.x.add(x);
        curve.r.add(r);

        while(x < length)
        {

            curve.x.add(x);
            curve.r.add(r);
            double dx = 10;
            x += dx;
        }
        curve.x.add(length);
        curve.r.add(radius);


        return curve;
    }

    public static Curve generateNoseCone(double radius, double length, NoseConeType type, 
        double sphericalRadius, double sResolution)
    {
        switch(type)
        {
            case CONICAL:
                return generateConicalNose(radius, length, sphericalRadius, sResolution);
            case ELLIPTICAL:
                return generateEllipticalNose(radius, length, sphericalRadius, sResolution);
            default:
                return generateConicalNose(radius, length, sphericalRadius, sResolution);
        }
    }

    public static Curve generateFlangedSection(double x1, double r1, double x2,
        double r2, double sResolution) 
    {
        Curve curve = new Curve();
        double x = x1;
        double r = r1;
        final double drdx = (r2 - r1)/(x2 - x1);
        final double dx = sResolution/Math.sqrt(1.0 + drdx*drdx);
        final double dr = dx*drdx;
        while(x < x2)
        {
            curve.x.add(x);
            curve.r.add(r);
            x += dx;
            r += dr;
        }
        curve.x.add(x2);
        curve.r.add(r2);
        return curve;
    }

    public static Curve generateFin(double baseChord, double tipChord, double span,
         double sweep, double sResolution)
    {
        Curve curve = new Curve();

        return curve;
    }

    Mesh toMesh() 
    {
        Mesh mesh = new Mesh();

        return mesh;
    }
}

package com.mardpop.jrocket.designer;

import com.mardpop.jrocket.designer.Curve.CurvePoint;

public class RocketShape 
{
    public static enum NoseConeType
    {
        CONICAL,
        ELLIPTICAL,
        TANGENT_OGIVE,
        SECANT_OGIVE,
        PARABOLIC,
        HALF_POWER,
        THREE_FOURTH_POWER,
        VON_KARMAN
    }

    Curve body;

    Curve fin;

    RocketShape() {}

    public RocketShape(RocketParameters params)
    {
        double sResolution = params.payloadTubeRadius*0.05;
        this.body = generateNoseCone(params.payloadTubeRadius, params.noseConeLength, params.noseConeType,
                params.noseConeSphericalRadius,sResolution);
        double x = params.noseConeLength;
        x += params.payloadTubeLength;
        this.body.points.add(new CurvePoint(x, params.payloadTubeRadius));
        x += params.payloadFlangeLength;
        this.body.points.add(new CurvePoint(x, params.tubeRadius));
        x += params.tubeLength;
        this.body.points.add(new CurvePoint(x, params.tubeRadius));
        x += params.tubeFlangeLength;
        this.body.points.add(new CurvePoint(x, params.motorRadius));
        x += params.motorLength;
        this.body.points.add(new CurvePoint(x, params.motorRadius));

        this.fin = generateFin(params.finBaseChord, params.finTipChord, params.finSpan, params.finSweep);

        x -= (params.finBaseChord + params.finChordOffset);
        for(CurvePoint p : fin.points) {
            p.x += x;
            p.r += params.motorRadius;
        }
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
                curve.points.add(new CurvePoint(x,r));
                theta += dTheta;
            }

            curve.points.add(new CurvePoint(xSphere, a));
        }

        double dx = sResolution/Math.sqrt(1.0 + drdx*drdx);
        double dr = dx*drdx;
        while(x < length)
        {
            curve.points.add(new CurvePoint(x, r));
            x += dx;
            r += dr;
        }
        curve.points.add(new CurvePoint(length, radius));
        return curve;
    }

    public static Curve generateEllipticalNose(double radius, double length, double sphericalRadius, double sResolution)
    {
        Curve curve = new Curve();

        double x = 0;
        double r = 0;

        final double a_sq = length*length;
        final double b_sq = radius*radius;
        final double a_b_sq = a_sq/b_sq;
        final double b_a_sq = b_sq/a_sq;

        curve.points.add(new CurvePoint(x, r));

        r = sResolution;
        double x_positive = Math.sqrt(a_sq - r*r*a_b_sq);
        x = length - x_positive;
        while(x < length)
        {
            curve.points.add(new CurvePoint(x, r));

            double radical = Math.sqrt(b_sq - x_positive*x_positive*b_a_sq);
            double drdx = x_positive*b_a_sq/radical;
            double dx = sResolution/Math.sqrt(1.0 + drdx*drdx);
            x_positive -= dx;
            x = length - x_positive;
            r = Math.sqrt(b_sq - x_positive*x_positive*b_a_sq);
        }
        curve.points.add(new CurvePoint(length, radius));


        return curve;
    }

    public static Curve generateSqrtCurve(double radius, double length, double sphericalRadius, double sResolution)
    {
        Curve curve = new Curve();
        curve.points.add(new CurvePoint(0, 0));

        if(sphericalRadius < sResolution) {
            final double a = radius/Math.sqrt(length);
            double x = sResolution/a;
            x *= x;
            curve.points.add(new CurvePoint(x, sResolution));
            while(x < length)
            {
                double drdx = 0.5*a/Math.sqrt(x);
                double dx = sResolution/Math.sqrt(1.0 + drdx*drdx);
                double r = a*Math.sqrt(x);
                curve.points.add(new CurvePoint(x, r));
                x += dx;
            }
            curve.points.add(new CurvePoint(length, radius));
        }

        return curve;
    }

    public static Curve generateTangentOgiveCurve(double radius, double length, double sphericalRadius, double sResolution)
    {
        Curve curve = new Curve();
        curve.points.add(new CurvePoint(0, 0));

        double theta;
        double dTheta;
        double radiusOgive;
        double L;
        if(sphericalRadius < sResolution)
        {
            radiusOgive = (length*length + radius*radius)/(2*radius);
            L = radiusOgive - radius;
            theta = Math.PI*0.5 - Math.acos(L/radiusOgive);
        }
        else
        {
            L = 0.5*(length*length + 2*sphericalRadius*(radius - length) - radius*radius)/(radius - sphericalRadius);
            radiusOgive = L + radius;
            final double THETA_TRANSITION = Math.PI*0.5 - Math.acos(L/radiusOgive);
            // final double D = length - sphericalRadius;
            theta = 0;
            dTheta = sResolution/sphericalRadius;
            while(theta < THETA_TRANSITION)
            {
                curve.points.add(new CurvePoint(sphericalRadius*(1 - Math.cos(theta)), sphericalRadius*Math.sin(theta)));
                theta += dTheta;
            }
    
            
        }
        dTheta = sResolution/radiusOgive;
        while(theta < Math.PI*0.5)
        {
            curve.points.add(new CurvePoint(length - radiusOgive*Math.cos(theta),
                radiusOgive*Math.sin(theta) - L));
            theta += dTheta;
        }

        curve.points.add(new CurvePoint(length, radius));
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
            case HALF_POWER:
                return generateSqrtCurve(radius, length, sphericalRadius, sResolution);
            case TANGENT_OGIVE:
                return generateTangentOgiveCurve(radius, length, sphericalRadius, sResolution);
            default:
                return generateConicalNose(radius, length, sphericalRadius, sResolution);
        }
    }

    public static Curve generateFin(double baseChord, double tipChord, double span,
        double sweep)
    {
        Curve curve = new Curve();
        curve.points.add(new CurvePoint(0, 0));

        double dxdr = Math.tan(sweep);
        double xShift = dxdr*span;
        double midChord = baseChord*0.5;
        double tipMidChord = midChord + xShift;

        curve.points.add(new CurvePoint(tipMidChord - tipChord*0.5, span));
        curve.points.add(new CurvePoint(tipMidChord + tipChord*0.5, span));
        curve.points.add(new CurvePoint(baseChord, 0));

        return curve;
    }

    public Curve getBody()
    {
        return this.body;
    }

    public Curve getFin()
    {
        return this.fin;
    }

    public Mesh toMesh() 
    {
        Mesh mesh = new Mesh();

        return mesh;
    }
}

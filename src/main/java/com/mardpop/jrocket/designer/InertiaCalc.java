package com.mardpop.jrocket.designer;

import com.mardpop.jrocket.designer.Curve.CurvePoint;
import com.mardpop.jrocket.vehicle.InertiaSimple;

public class InertiaCalc
{


    public static double coneVolume(double radius, double height)
    {
        return Math.PI*radius*radius*height/3;
    }

    public static double coneCOM(double height)
    {
        return height/4;
    }

    /**
     * Calculates the second moment of area (Ixx) of a cone. through axis of symmetry.
     *
     * @param  radius  the radius of the cone
     * @param  height  the height of the cone
     * @param  density the density of the material used to make the cone
     * @return         the value of Ixx
     */
    public static double coneIxx(double radius, double height, double density)
    {
        double mass = density*Math.PI*radius*radius*height/3;
        return 0.3*mass*radius*radius;
    }

    /**
     * Calculate the moment of inertia of a cone through center of mass.
     *
     * @param  radius    the radius of the cone's base
     * @param  height    the height of the cone
     * @param  density   the density of the cone material
     * @return           the moment of inertia of the cone
     */
    public static double coneIrr(double radius, double height, double density)
    {
        double mass = density*Math.PI*radius*radius*height/3;
        return 3*mass*(radius*radius/20 + height*height/80);
    }

    /**
     * Calculate the moment of inertia about the x-axis for a tube.
     *
     * @param  innerRadius   the inner radius of the tube
     * @param  outerRadius   the outer radius of the tube
     * @param  length        the length of the tube
     * @param  density       the density of the tube material
     * @return               the moment of inertia about the x-axis
     */
    public static double tubeIxx(double innerRadius, double outerRadius, double length, double density)
    {
        double mass = Math.PI*(outerRadius*outerRadius - innerRadius*innerRadius)*density*length;
        return mass/12*(3*(outerRadius*outerRadius + innerRadius*innerRadius)+ length*length);
    }

    /**
     * Calculate the moment of inertia of a tube.
     *
     * @param  innerRadius  the inner radius of the tube
     * @param  outerRadius  the outer radius of the tube
     * @param  length       the length of the tube
     * @param  density      the density of the tube material
     * @return              the moment of inertia of the tube
     */
    public static double tubeIrr(double innerRadius, double outerRadius,double length, double density)
    {
        double mass = Math.PI*(outerRadius*outerRadius - innerRadius*innerRadius)*density*length;
        return mass*(outerRadius*outerRadius + innerRadius*innerRadius)*0.5;
    }

    public static InertiaSimple tubeInertia(double innerRadius, double outerRadius, double length, double density)
    {
        if(innerRadius > outerRadius)
        {
            double temp = innerRadius;
            innerRadius = outerRadius;
            outerRadius = temp;
        }
        double mass = Math.PI*(outerRadius*outerRadius - innerRadius*innerRadius)*density*length;
        double Irr = mass*(outerRadius*outerRadius + innerRadius*innerRadius)*0.5;
        double Ixx = mass/12*(3*(outerRadius*outerRadius + innerRadius*innerRadius)+ length*length);
        return new InertiaSimple(mass, Ixx, Irr, length*0.5);
    }

    /**
     * Calculate the moment of inertia around the x-axis for a truncated cone.
     *
     * @param  baseOuterRadius    the outer radius of the base
     * @param  topOuterRadius     the outer radius of the top
     * @param  length             the length of the truncated cone
     * @param  density            the density of the material
     * @return                    the moment of inertia around the x-axis
     */
    public static double tuncatedConeIxx(double baseOuterRadius, double topOuterRadius,
        double length, double density)
    {
        double drdh = (baseOuterRadius - topOuterRadius)/length;
        double lengthMissing = topOuterRadius/drdh;
        double Ixx1 = coneIxx(topOuterRadius, lengthMissing, density);
        double Ixx2 = coneIxx(baseOuterRadius, length + lengthMissing, density);
        return Ixx2 - Ixx1;
    }

    public static double tuncatedConeIrr(double baseOuterRadius, double topOuterRadius,
        double length, double density)
    {
        double drdh = (baseOuterRadius - topOuterRadius)/length;
        double lengthMissing = topOuterRadius/drdh;
        double Irr1 = coneIrr(topOuterRadius, lengthMissing, density);
        double Irr2 = coneIrr(baseOuterRadius, length + lengthMissing, density);
        return Irr2 - Irr1;
    }

        /**
     * Calculate the volume of a truncated cone.
     *
     * @param  baseOuterRadius    the outer radius of the base
     * @param  topOuterRadius     the outer radius of the top
     * @param  length             the length of the truncated cone
     * @return                    the calculated volume of the truncated cone
     */
    public static double truncatedConeVolume(double baseOuterRadius, double topOuterRadius, double length)
    {
        return length*Math.PI/3*(baseOuterRadius*baseOuterRadius + topOuterRadius*topOuterRadius 
            + baseOuterRadius*topOuterRadius);
    }

    public static double truncatedConeCOM(double baseOuterRadius, double topOuterRadius, double length)
    {
        double drdh = (baseOuterRadius - topOuterRadius)/length;
        double lengthMissing = topOuterRadius/drdh;
        double V1 = baseOuterRadius*baseOuterRadius*length/3;
        double V2 = topOuterRadius*topOuterRadius*lengthMissing/3;
        double V3 = V1 - V2;

        double cG1 = V1*(length + lengthMissing)*0.25;
        double cG2 = V2*lengthMissing*0.25;
        
        return (cG1 - cG2)/V3;
    }

    public static double thinWalledTruncatedConeIxx(double baseOuterRadius, double topOuterRadius, double wallThickness,
        double length, double density)
    {
        double Ixx1 = tuncatedConeIxx(baseOuterRadius, topOuterRadius, length, density);
        double Ixx2 = tuncatedConeIxx(baseOuterRadius - wallThickness, topOuterRadius - wallThickness, length, density);
        return Ixx1 - Ixx2;
    }

    public static double thinWalledTruncatedConeIrr(double baseOuterRadius, double topOuterRadius, double wallThickness,
        double length, double density)
    {
        double Irr1 = tuncatedConeIrr(baseOuterRadius, topOuterRadius, length, density);
        double Irr2 = tuncatedConeIrr(baseOuterRadius - wallThickness, topOuterRadius - wallThickness, length, density);
        return Irr1 - Irr2;
    }

    public static double thinWalledTruncatedConeCOM(double baseOuterRadius, double topOuterRadius, double wallThickness,
        double length)
    {
        double mass1 = truncatedConeVolume(baseOuterRadius, topOuterRadius, length);
        double mass2 = truncatedConeVolume(baseOuterRadius - wallThickness, topOuterRadius - wallThickness, length);
        double COM1 = truncatedConeCOM(baseOuterRadius, topOuterRadius, length);
        double COM2 = truncatedConeCOM(baseOuterRadius - wallThickness, topOuterRadius - wallThickness, length);
        double mass = mass1 + mass2;
        return (COM1*mass1 - COM2*mass2)/mass;
    }

    public static InertiaSimple thinWalledTruncatedConeInertia(double baseOuterRadius, double topOuterRadius,
        double wallThickness, double length, double density)
    {
        double drdh = (baseOuterRadius - topOuterRadius)/length;
        double lengthMissing = topOuterRadius/drdh;
        double V1 = baseOuterRadius*baseOuterRadius*length/3;
        double V2 = topOuterRadius*topOuterRadius*lengthMissing/3;
        double V3 = V1 - V2;

        double cG1 = V1*(length + lengthMissing)*0.25;
        double cG2 = V2*lengthMissing*0.25;
        
        double CG = (cG1 - cG2)/V3;

        double Ixx = thinWalledTruncatedConeIxx(baseOuterRadius, topOuterRadius, wallThickness, length, density);
        double Irr = thinWalledTruncatedConeIrr(baseOuterRadius, topOuterRadius, wallThickness, length, density);
        return new InertiaSimple(V3*density, Ixx, Irr, CG);
    }

    public static double thinWallMass(Curve curve, double thickness, double density)
    {
        double mass = 0;
        CurvePoint prev = curve.points.get(0);
        for(int i = 1; i < curve.points.size(); i++)
        {
            CurvePoint point = curve.points.get(i);
            double dx = point.x - prev.x;
            double dr = point.r - prev.r;
            double ds = Math.sqrt(dx*dx + dr*dr);
            double dm = density*thickness*ds;
            mass += dm;
            prev = point;
        }
        return  mass;
    }

    public static double thinWallCOM(Curve curve, double thickness, double density)
    {
        double CGx = 0;
        double mass = 0;
        CurvePoint prev = curve.points.get(0);
        for(int i = 1; i < curve.points.size(); i++)
        {
            CurvePoint point = curve.points.get(i);
            double dx = point.x - prev.x;
            double dr = point.r - prev.r;
            double ds = Math.sqrt(dx*dx + dr*dr);
            double dm = density*thickness*ds;
            double xavg = (point.x + prev.x)*0.5;
            CGx += xavg*dm;
            mass += dm;
            prev = point;
        }
        return CGx / mass;
    }

    /**
     * Calculate the second moment of area (Ixx) for a thin-walled structure based on the provided curve, thickness, and density.
     *
     * @param  curve      the curve representing the shape of the structure
     * @param  thickness  the thickness of the structure
     * @param  density    the density of the material
     * @return            the second moment of area (Ixx) for the thin-walled structure
     */
    public static double thinWallIxx(Curve curve, double thickness, double density)
    {
        double Ixx = 0;
        CurvePoint prev = curve.points.get(0);
        for(int i = 1; i < curve.points.size(); i++)
        {
            CurvePoint point = curve.points.get(i);
            double dx = point.x - prev.x;
            double dr = point.r - prev.r;
            double ds = Math.sqrt(dx*dx + dr*dr);
            double I = 0.5*density*thickness*ds*(point.r*point.r + prev.r*prev.r);
            Ixx += I;
            prev = point;
        }
        return Ixx;
    }

    /**
     * Calculate the moment of inertia for a thin-walled curve.
     *
     * @param  curve       the curve for which to calculate the moment of inertia
     * @param  thickness   the thickness of the thin wall
     * @param  density     the density of the material
     * @return             the moment of inertia of the thin-walled curve
     */
    public static double thinWallIrr(Curve curve, double thickness, double density)
    {
        double CGx = 0;
        double mass = 0;
        double[] dm = new double[curve.points.size() - 1];
        double[] xavg = new double[curve.points.size() - 1];
        CurvePoint prev = curve.points.get(0);
        for(int i = 1; i < curve.points.size(); i++)
        {
            CurvePoint point = curve.points.get(i);
            double dx = point.x - prev.x;
            double dr = point.r - prev.r;
            double ds = Math.sqrt(dx*dx + dr*dr);
            dm[i-1] = density*thickness*ds;
            xavg[i-1] = (point.x + prev.x)*0.5;
            CGx += xavg[i-1]*dm[i-1];
            mass += dm[i-1];
            prev = point;
        }

        CGx /= mass;
        double Irr = 0;
        for(int i = 0; i < dm.length; i++)
        {
            double dx = xavg[i] - CGx;
            Irr += dm[i]*dx*dx;
        }

        return Irr;
    }

    public static InertiaSimple thinWallInertia(Curve curve, double thickness, double density)
    {double CGx = 0;
        double mass = 0;
        double[] dm = new double[curve.points.size() - 1];
        double[] xavg = new double[curve.points.size() - 1];
        double[] ravg = new double[curve.points.size() - 1];
        CurvePoint prev = curve.points.get(0);
        for(int i = 1; i < curve.points.size(); i++)
        {
            CurvePoint point = curve.points.get(i);
            final double dx = point.x - prev.x;
            final double dr = point.r - prev.r;
            final double ds = Math.sqrt(dx*dx + dr*dr);
            final int i1 = i-1;
            xavg[i1] = (point.x + prev.x)*0.5;
            ravg[i1] = (point.r + prev.r)*0.5;
            dm[i1] = density*thickness*ds*ravg[i1]*Math.PI*2;
            CGx += xavg[i1]*dm[i1];
            mass += dm[i1];
            prev = point;
        }

        CGx /= mass;
        double Irr = 0;
        double Ixx = 0;
        for(int i = 0; i < dm.length; i++)
        {
            double dx = xavg[i] - CGx;
            Irr += dm[i]*dx*dx;
            Ixx += dm[i]*ravg[i]*ravg[i];
        }

        return new InertiaSimple(mass,Ixx,Irr,CGx);
    }

    public static double[] centerOfAreaPolygon(int[] x, int[] y) 
    {
        double Cx = 0;
        double Cy = 0;
        double A = 0;
        for(int i = 1; i < x.length; i++)
        {
            double dA = x[i-1]*y[i] - x[i]*y[i-1];
            A += dA;
            Cx += (x[i-1] + x[i])*dA;
            Cy += (y[i-1] + y[i])*dA;
        }
        Cx /= 6*A;
        Cy /= 6*A;
        return new double[]{Cx, Cy, A};
    }

    public static double[] secondMomentOfAreaPolygon(int[] x, int[] y)
    {
        double Ixx = 0;
        double Iyy = 0;
        double Ixy = 0;
        for(int i = 1; i < x.length; i++)
        {
            double dA = x[i-1]*y[i] - x[i]*y[i-1];
            double Ax = x[i-1] + x[i];
            double Ay = y[i-1] + y[i];

            Ixx += Ax*Ax*dA;
            Iyy += Ay*Ay*dA;
            Ixy += Ax*Ay*dA;
        }
        Ixx /= 12;
        Iyy /= 12;
        Ixy /= 24;
        return new double[]{Ixx, Iyy, Ixy};
    }

    public static InertiaSimple finInertia(double baseChord, double tipChord, double sweep, double span,
        double initialRadius, double thickness, double density, int number, double xOffsetBase)
    {
        double tmp1 = 2*baseChord + tipChord;
        double tmp2 = baseChord + tipChord;
        double den = 3*tmp2;
        double avgChord = tmp2*0.5;

        double dChorddSpan = Math.tan(sweep);
        double dChord = span*dChorddSpan;

        double f = baseChord/2 - tipChord/2;
        f -= dChord;

        double xCGfin = (f*tmp1 + tmp2*tmp2 - baseChord*tipChord)/den;
        double rCGfin = span*tmp1/den;
        rCGfin += initialRadius;

        double finArea = (baseChord + tipChord)*0.5*span;
        double massFin = finArea*thickness*density;

        double Irr = rCGfin*rCGfin*massFin + span*span*span*avgChord/12;
        double Ixx = avgChord*avgChord*avgChord*span/12;

        return new InertiaSimple(massFin*number, Ixx*number, Irr*number, xOffsetBase + xCGfin);
    }

    public static InertiaSimple computeStructureInertiaFromParams(RocketParameters params, 
        double materialDensity, double materialThickness, 
        double noseConeMaterialDensity, double noseConeThickness,
        double finMaterialDensity, double finThickness)
    {
        Curve noseCone = RocketShape.generateNoseCone(params.payloadTubeRadius, params.noseConeLength, params.noseConeType,
                params.noseConeSphericalRadius, 0.0001);

        InertiaSimple noseInertia = thinWallInertia(noseCone, noseConeThickness, noseConeMaterialDensity);

        InertiaSimple payloadInertia = tubeInertia(params.payloadTubeRadius - materialThickness, params.payloadTubeRadius,
            params.payloadTubeLength, materialDensity);

        payloadInertia.CGx =payloadInertia.CGx + params.noseConeLength;

        double xLoc = params.noseConeLength + params.payloadTubeLength;
        InertiaSimple payloadFlangeInertia;
        if(Math.abs(params.tubeRadius - params.payloadTubeRadius) < 0.0001)
        {
            payloadFlangeInertia = tubeInertia(params.tubeRadius - materialThickness, params.tubeRadius,
                params.payloadFlangeLength, materialDensity);

            payloadFlangeInertia.CGx =xLoc + payloadFlangeInertia.CGx;
        }
        else if(params.tubeRadius > params.payloadTubeRadius)
        {
            payloadFlangeInertia = thinWalledTruncatedConeInertia( params.payloadTubeRadius, params.tubeRadius, materialThickness,
                params.payloadFlangeLength, materialDensity);
            payloadFlangeInertia.CGx =xLoc + payloadFlangeInertia.CGx;
        }
        else
        {
            payloadFlangeInertia = thinWalledTruncatedConeInertia( params.tubeRadius, params.payloadTubeRadius, materialThickness,
                params.payloadFlangeLength, materialDensity);
            
                payloadFlangeInertia.CGx =xLoc + params.payloadFlangeLength -payloadInertia.CGx;
        }

        xLoc += params.payloadFlangeLength;

        InertiaSimple tubeInertia = tubeInertia(params.tubeRadius - materialThickness, params.tubeRadius,
            params.tubeLength, materialDensity);

        tubeInertia.CGx =xLoc + tubeInertia.CGx;

        xLoc += params.tubeLength;
        InertiaSimple tubeFlangeInertia;
        if(Math.abs(params.tubeRadius - params.motorRadius) < 0.0001)
        {
            tubeFlangeInertia = tubeInertia(params.tubeRadius - materialThickness, params.tubeRadius,
                params.tubeFlangeLength, materialDensity);

            tubeFlangeInertia.CGx = xLoc + tubeFlangeInertia.CGx;
        }
        else if(params.tubeRadius > params.motorRadius)
        {
            tubeFlangeInertia = thinWalledTruncatedConeInertia( params.tubeRadius, params.motorRadius, materialThickness,
                params.tubeFlangeLength, materialDensity);

            tubeFlangeInertia.CGx = xLoc + tubeFlangeInertia.CGx;
        }
        else
        {
            tubeFlangeInertia = thinWalledTruncatedConeInertia( params.motorRadius, params.tubeRadius, materialThickness,
                params.tubeFlangeLength, materialDensity);
            tubeFlangeInertia.CGx =xLoc + params.tubeFlangeLength - tubeFlangeInertia.CGx;
        }

        xLoc += params.tubeFlangeLength;
        InertiaSimple motorTube = tubeInertia(params.motorRadius - materialThickness, params.motorRadius,
            params.motorLength, materialDensity);

        motorTube.CGx = xLoc + motorTube.CGx;

        xLoc += params.motorLength;
        InertiaSimple finInertia = finInertia(params.finBaseChord, params.finTipChord, params.finSweep, params.finSpan,
            params.motorRadius, finThickness, finMaterialDensity, params.numFins, params.finChordOffset);

        finInertia.CGx = xLoc - finInertia.CGx;

        double mass = noseInertia.mass + payloadInertia.mass + payloadFlangeInertia.mass +
            tubeInertia.mass + tubeFlangeInertia.mass + motorTube.mass + finInertia.mass;

        double CGx = noseInertia.CGx*noseInertia.mass + payloadInertia.CGx*payloadInertia.mass +
            payloadFlangeInertia.CGx*payloadFlangeInertia.mass +
            tubeInertia.CGx*tubeInertia.mass + tubeFlangeInertia.CGx*tubeFlangeInertia.mass +
            motorTube.CGx*motorTube.mass + finInertia.CGx*finInertia.mass;

        CGx /= mass;

        double Ixx = noseInertia.Ixx + payloadInertia.Ixx + payloadFlangeInertia.Ixx +
            tubeInertia.Ixx + tubeFlangeInertia.Ixx + motorTube.Ixx + finInertia.Ixx;

        double Irr = noseInertia.Irr + payloadInertia.Irr + payloadFlangeInertia.Irr +
            tubeInertia.Irr + tubeFlangeInertia.Irr + motorTube.Irr + finInertia.Irr;

        double dxNose = noseInertia.CGx - CGx;
        double dxPayload = payloadInertia.CGx - CGx;
        double dxPayloadFlange = payloadFlangeInertia.CGx - CGx;
        double dxTube = tubeInertia.CGx - CGx;
        double dxTubeFlange = tubeFlangeInertia.CGx - CGx;
        double dxMotor = motorTube.CGx - CGx;
        double dxFin = finInertia.CGx - CGx;

        Irr += noseInertia.mass*dxNose*dxNose + payloadInertia.mass*dxPayload*dxPayload +
            payloadFlangeInertia.mass*dxPayloadFlange*dxPayloadFlange +
            tubeInertia.mass*dxTube*dxTube + tubeFlangeInertia.mass*dxTubeFlange*dxTubeFlange +
            motorTube.mass*dxMotor*dxMotor + finInertia.mass*dxFin*dxFin;

        return new InertiaSimple(mass, Ixx, Irr, CGx);
    }

    public static InertiaSimple computeEmptyInertiaFromParams(RocketParameters params)
    {
        double materialDensity = 1300;
        double noseConeDensity = 1000;
        double noseConeThickness = 0.001;
        double finDensity = 1000;
        double finThickness = 0.001;

        InertiaSimple structureMass = InertiaCalc.computeStructureInertiaFromParams(params, 
            materialDensity, params.structureThickness, noseConeDensity, noseConeThickness, finDensity, finThickness);

        double payloadIxx = params.payloadMass*((0.25*params.payloadTubeRadius*params.payloadTubeRadius 
            + params.payloadTubeLength*params.payloadTubeLength) + params.payloadCOM*params.payloadCOM);
        double payloadIrr = 0.5*params.payloadMass*params.payloadTubeRadius*params.payloadTubeRadius;

        InertiaSimple payloadInertia = new InertiaSimple(params.payloadMass, payloadIxx, payloadIrr, params.payloadCOM);

        InertiaSimple emptyInertia = new InertiaSimple(structureMass, payloadInertia);

        final double MASS_MARGIN = 1.25;
        return new InertiaSimple(emptyInertia.mass*MASS_MARGIN, emptyInertia.Ixx*MASS_MARGIN,
            emptyInertia.Irr*MASS_MARGIN, emptyInertia.CGx);
    }
}
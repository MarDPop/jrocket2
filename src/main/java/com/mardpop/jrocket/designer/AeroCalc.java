package com.mardpop.jrocket.designer;

public class AeroCalc 
{
    
    static double[] barrowmanMethod(RocketParameters params)
    {
        // Compute CN

        // Compute CN for nose cone
        double noseConeCN = 2.0;

        // Compute CN for payload flange
        double payloadTransitionCN = 8*Math.PI*(params.tubeRadius*params.tubeRadius - 
            params.payloadTubeRadius*params.payloadTubeRadius);
        
        // Compute CN for motor flange
        double motorTransitionCN = 8*Math.PI*(params.motorRadius*params.motorRadius - 
            params.tubeRadius*params.tubeRadius);

        // Compute CN for fins
        final double interferenceF = params.numFins > 4 ? 0.5 : 1.0;
        final double midchordLength = params.finSpan/Math.cos(params.finSweep);
        final double s_d = params.finSpan*0.5/params.motorRadius;
        final double denC = 2*midchordLength/(params.finBaseChord + params.finTipChord);
        double finCN = (1 + interferenceF*params.motorRadius/(params.motorRadius + params.finSpan))*
            ((4*params.numFins*s_d*s_d)/(1 + Math.sqrt(1 + denC*denC)));

        // Compute CP 

        // Compute CP for nose cone
        double noseCP = 0.5;
        switch ( params.noseConeType) {
            case CONICAL:
                noseCP = 0.6666;
                break;
            case ELLIPTICAL:
                noseCP = 0.3333;
                break;
            case TANGENT_OGIVE:
                noseCP = 0.466;
                break;
            case SECANT_OGIVE:
                noseCP = 0.5;
                break;
            case PARABOLIC:
                noseCP = 0.5;
                break;
            case HALF_POWER:
                noseCP = 0.5;
                break;
            case THREE_FOURTH_POWER:
                noseCP = 0.5;
                break;
            case VON_KARMAN:
                noseCP = 0.4;
                break;
            default:
                break;
        }
        noseCP *= params.noseConeLength;

        // Compute CP for conical sections
        // Payload Flange
        double X = params.noseConeLength + params.payloadTubeLength;
        double diameterRatio = params.payloadTubeRadius/params.tubeRadius;
        double payloadTransitionCP = X;
        if(Math.abs(diameterRatio - 1) > 0.001)
        {
            payloadTransitionCP += params.payloadFlangeLength*0.333333333333*
                (1 + (1 - diameterRatio)/(1 - diameterRatio*diameterRatio));
        }

        // Motor Flange
        X = params.noseConeLength + params.payloadTubeLength + params.payloadFlangeLength + params.tubeLength;
        diameterRatio = params.tubeRadius/params.motorRadius;
        double motorTransitionCP = X; 
        if(Math.abs(diameterRatio - 1) > 0.001)
        {
            motorTransitionCP += params.payloadFlangeLength*0.333333333333*
                (1 + (1 - diameterRatio)/(1 - diameterRatio*diameterRatio));
        }

        // Compute CP for fin sections
        X = params.noseConeLength + params.payloadTubeLength + params.payloadFlangeLength + params.tubeLength
            + params.tubeFlangeLength + params.motorLength - params.finBaseChord - params.finChordOffset;

        double chordSum = params.finBaseChord + params.finTipChord;
        double tipLEX = X + params.finBaseChord*0.5 + Math.tan(params.finSweep)*params.finSpan - params.finTipChord*0.5;

        double finCP = X + tipLEX/(3*chordSum)*((params.finBaseChord + 2*params.finTipChord) + 
            0.5*(chordSum*chordSum - (params.finBaseChord*params.finTipChord)));

        return new double[]{noseConeCN, noseCP, payloadTransitionCN, payloadTransitionCP, 
            motorTransitionCN, motorTransitionCP, finCN, finCP};
    }

    public static double[] barrowmanCoef(RocketParameters params)
    {
        final double referenceArea = params.motorRadius*params.motorRadius*Math.PI;

        final double[] barrowmanCoef = barrowmanMethod(params);
        double num = 0;
        double den = 0;
        for(int i = 0; i < 8; i+=2)
        {
            num += barrowmanCoef[i]*barrowmanCoef[i+1];
            den += barrowmanCoef[i];
        }
        // returns [CP, CN, Aref]
        return new double[]{num/den, den, referenceArea};
    }

    public static double dragCoef(RocketParameters params)
    {
        double noseCd = 0.66;
        switch(params.noseConeType)
        {
            case TANGENT_OGIVE:
                noseCd = 0.62;
                break;
            case PARABOLIC:
                noseCd = 0.65;
                break;
            case CONICAL:
                noseCd = 0.75;
                break;
            case ELLIPTICAL:
                noseCd = 0.65;
                break;
            case HALF_POWER:
                noseCd = 0.63;
                break;
            case SECANT_OGIVE:
                noseCd = 0.67;
                break;
            case THREE_FOURTH_POWER:
                noseCd = 0.65;
                break;
            case VON_KARMAN:
                noseCd = 0.65;
                break;
            default:
                break;
        }
        double finArea = (params.finBaseChord + params.finTipChord)*params.finSpan*0.5;
        double finCD = 0.01*params.numFins*finArea/(params.motorRadius*params.motorRadius*Math.PI);

        return noseCd + finCD;
    }
}

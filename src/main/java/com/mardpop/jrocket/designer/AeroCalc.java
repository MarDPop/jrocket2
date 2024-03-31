package com.mardpop.jrocket.designer;

public class AeroCalc 
{
    
    double[] barrowmanMethod(RocketParameters params)
    {
        double noseConeCN = 2.0;

        double payloadTransitionCN = 8*Math.PI*(params.tubeRadius*params.tubeRadius - 
            params.payloadTubeRadius*params.payloadTubeRadius);
        
        double motorTransitionCN = 8*Math.PI*(params.motorRadius*params.motorRadius - 
            params.tubeRadius*params.tubeRadius);

        final double interferenceF = params.numFins > 4 ? 0.5 : 1.0;
        final double midchordLength = params.finSpan/Math.cos(params.finSweep);
        final double s_d = params.finSpan*0.5/params.motorRadius;
        final double denC = 2*midchordLength/(params.finBaseChord + params.finTipChord);
        double finCN = (1 + interferenceF*params.motorRadius/(params.motorRadius + params.finSpan))*
            ((4*params.numFins*s_d*s_d)/(1 + Math.sqrt(1 + denC*denC)));

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

        double X = params.noseConeLength + params.payloadTubeLength;
        double diameterRatio = params.payloadTubeRadius/params.tubeRadius;
        double payloadTransitionCP = X + params.payloadFlangeLength*0.333333333333*
            (1 + (1 - diameterRatio)/(1 - diameterRatio*diameterRatio));

        X = params.noseConeLength + params.payloadTubeLength + params.payloadFlangeLength + params.tubeLength;
        diameterRatio = params.tubeRadius/params.motorRadius;
        double motorTransitionCP = X + params.payloadFlangeLength*0.333333333333*
            (1 + (1 - diameterRatio)/(1 - diameterRatio*diameterRatio));

        X = params.noseConeLength + params.payloadTubeLength + params.payloadFlangeLength + params.tubeLength
            + params.tubeFlangeLength + params.motorLength - params.finBaseChord - params.finChordOffset;

        double chordSum = params.finBaseChord + params.finTipChord;
        double tipLEX = X + params.finBaseChord*0.5 + Math.tan(params.finSweep)*params.finSpan - params.finTipChord*0.5;

        double finCP = X + tipLEX/(3*chordSum)*((params.finBaseChord + 2*params.finTipChord) + 
            0.5*(chordSum*chordSum - (params.finBaseChord*params.finTipChord)));

        return new double[]{noseConeCN, noseCP, payloadTransitionCN, payloadTransitionCP, 
            motorTransitionCN, motorTransitionCP, finCN, finCP};
    }

    public double[] barrowmanCoef(RocketParameters params)
    {
        final double referenceArea = params.motorRadius*params.motorRadius*Math.PI;

        double[] barrowmanCoef = barrowmanMethod(params);
        double num = 0;
        double den = 0;
        for(int i = 0; i < 8; i+=2)
        {
            num += barrowmanCoef[i]*barrowmanCoef[i+1];
            den += barrowmanCoef[i];
        }

        return new double[]{num/den, den/referenceArea};
    }
}

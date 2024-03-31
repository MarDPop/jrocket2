package com.mardpop.jrocket.util;

public class TableD 
{
    private double[] data;

    private double[] delta;

    private double[] x;

    private double finalX;

    private int finalIdx;

    final int NCOLS;
    
    public TableD(int NCOLS)
    {
       this.NCOLS = NCOLS; 
    }

    public void add(double x, double[] data)
    {
        if(data == null || data.length != NCOLS)
            throw new IllegalArgumentException("data must be an array of length " + NCOLS);

        if(this.x == null)
        {
            this.x = new double[]{x};
            this.data = data;
            this.finalIdx = 0;
            this.finalX = x;
            return;
        }
        double[] newX = new double[this.x.length + 1];
        double[] newData = new double[this.data.length + NCOLS];

        int index = 0;
        while(index < this.x.length && this.x[index] < x)
            index++;

        newX[index] = x;
        System.arraycopy(data, 0, newData, index*NCOLS, NCOLS);

        System.arraycopy(this.x, 0, newX, 0, index);
        System.arraycopy(this.data, 0, newData, 0, index*NCOLS);

        if(index < this.x.length)
        {
            System.arraycopy(this.x, index, newX, index+1, this.x.length - index);
            System.arraycopy(this.data, index*NCOLS, newData, (index + 1)*NCOLS, this.data.length - index*NCOLS);
        }

        this.x = newX;
        this.data = newData;

        this.finalIdx = this.x.length - 1;
        this.finalX = this.x[this.finalIdx];

        computeDeltas();
    }

    private void computeDeltas()
    {
        this.delta = new double[this.data.length];

        for(int i = 1; i < this.x.length; i++)   
        {
            double invDx = 1.0/(this.x[i] - this.x[i-1]);
            int idx = (i - 1)*NCOLS;
            for(int j = 0; j < NCOLS; j++)
            {
                this.delta[idx + j] = (this.data[idx + j + NCOLS] - this.data[idx + j])*invDx;
            }
        }
    }

    public double[] get(double x)
    {
        double[] output = new double[NCOLS];
        if(x > this.finalX)
        {
            System.arraycopy(this.data, this.finalIdx*NCOLS, output, 0, NCOLS);
        }
        else
        {
            int idx = Util.lowerIndex(this.x, x);
            final double dx = x - this.x[idx];
            idx*=NCOLS;
            for(int i = 0; i < NCOLS; i++)
            {
                output[i] = this.data[idx + i] + this.delta[idx + i]*dx;
            }
        }

        return output;

    }
}

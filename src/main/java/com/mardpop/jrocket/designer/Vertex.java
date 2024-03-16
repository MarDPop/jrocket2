package com.mardpop.jrocket.designer;

import java.util.ArrayList;

public class Vertex 
{
    public Vertex() {}

    public Vertex(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    double x;

    double y;

    double z;

    ArrayList<Edge> lines = new ArrayList<>();


}

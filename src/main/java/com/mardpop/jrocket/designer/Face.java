package com.mardpop.jrocket.designer;

public class Face 
{
    
    final Edge[] edges = new Edge[3];

    final Vertex[] vertices = new Vertex[3];

    public Face() {}

    public Face(Vertex v1, Vertex v2, Vertex v3)
    {
        this.vertices[0] = v1;
        this.vertices[1] = v2;
        this.vertices[2] = v3;
    }
}

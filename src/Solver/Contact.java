package Solver;

import Objects.Physical.Physical;
import Vector.Vec2;

public class Contact {
    public Physical a;
    public Physical b;
    public Vec2[] contact;
    public Vec2 normal;
    public double penetration;
}

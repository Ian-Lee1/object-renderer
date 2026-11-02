package Solver;

import Objects.Physical.Physical;
import Vector.Vec2;

public class Contact {
    public Physical a;
    public Physical b;
    public boolean isTouching;
    public Vec2 point;
    public Vec2 normal;
    public double penetration;
}

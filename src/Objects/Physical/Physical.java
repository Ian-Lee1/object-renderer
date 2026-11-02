package Objects.Physical;

import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;

public abstract class Physical {
    public Vec2 origin;
    public Vec2 vel;
    public Vec2 acc;

    public boolean movable = false;

    public double ang;
    public double angVel;

    public double inverseMass = 0.0; //default is unmovable and has 1/infinite "mass"
    public double inertia;

    public int colorIdx;

    public abstract void draw(Graphics g, Viewport vp);
}

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

    public double mass = 1.0;
    public double inertia;

    public int colorIdx;

    public abstract void draw(Graphics g, Viewport vp);
}

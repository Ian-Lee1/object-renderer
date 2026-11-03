package Objects.Physical;

import Objects.Viewport;
import Vector.Vec2;
import jdk.jshell.spi.ExecutionControl;

import java.awt.*;
import java.util.HashMap;

public abstract class Physical {
    public HashMap<String, Double> attributes;
    //would be useful for particle life or other simulations

    public Vec2 origin;
    public Vec2 vel;
    public Vec2 acc;

    public boolean movable = false;

    public double ang;
    public double angVel;

    public double inverseMass = 0.0; //default is unmovable and has 1/infinite "mass"

    public int colorIdx;

    public static Physical parsePhysical(String parameters) {
        if (parameters.toLowerCase().startsWith("pill") || parameters.toLowerCase().startsWith("poly")) {
            return new Pill(parameters.substring(4) + " ");
        }
        throw new IllegalArgumentException("Cannot create Unsupported type " + parameters.substring(0, parameters.indexOf(' ')));
    }

    public abstract void initFromString(String parameters); //init the object from a string. If a required parameter is not found, raise an exception. If it is optional, fill in with default values.

    public abstract void applyRotate(double stepScale); //apply angVel to ang, rotating the object origin around the centre of mass.

    public abstract Vec2 getCenterOfMass();

    public abstract double getInertia();

    public abstract void draw(Graphics g, Viewport vp);
}

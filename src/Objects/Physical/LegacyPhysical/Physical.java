package Objects.Physical.LegacyPhysical;

import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;

public abstract class Physical {
    boolean movable = false;
    Vec2 origin;
    Vec2 vel = new Vec2(0,0);
    Vec2 acc = new Vec2(0,0);
    double density = 1; //Density should be inverse. so the closer to 0, the denser the object it.

    public Physical(Vec2 origin){
        this.origin = origin;
    }

    public boolean getMovable(){
        return movable;
    }
    public void setMovable(boolean movable){
        this.movable = movable;
    }

    public Vec2 getOrigin(){
        return origin;
    }

    public Vec2 getVel(){
        return vel;
    }

    public Vec2 getAcc(){
        return acc;
    }

    public double getDensity(){
        return density;
    }

    public void setOrigin(Vec2 origin){
        this.origin = origin;
    }

    public void setVel(Vec2 vel){
        this.vel = vel;
    }

    public void setAcc(Vec2 acc){
        this.acc = acc;
    }

    public void setDensity(double den){
        this.density = den;
    }


    public abstract double minDist(Vec2 pt); //obj's min distance to a point
    public abstract Vec2 closest(Vec2 pt); //the closest point on the obj to the given point
    public abstract Vec2 closestNormal(Vec2 pt); //the surface normal of the closest point to the given point
    public abstract Vec2[] definingPoints(); // get an array of "defining points" to check for min distance

    public abstract double getMass();
    abstract public void draw(Graphics g, Viewport vp);
}

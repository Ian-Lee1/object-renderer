package Objects.Physical;

import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;

public class Polygon extends Physical{
    public Vec2[] vertices;

    public Polygon(Vec2 origin, double ang, Vec2[] verticies, int ind){
        this.origin = origin;
        this.ang = ang;
        this.vertices = verticies;
        this.colorIdx = ind;
        this.vel = new Vec2(0, 0);
        this.force = new Vec2(0, 0);
    }

    public Polygon(String cmdParameters){
        changeFromString(cmdParameters);
    }

    @Override
    public double getBoundLeft() {
        return 0;
    }

    @Override
    public double getBoundRight() {
        return 0;
    }

    @Override
    public double getBoundTop() {
        return 0;
    }

    @Override
    public double getBoundBottom() {
        return 0;
    }

    @Override
    public void changeFromString(String parameters) {

    }

    @Override
    public void applyRotate(double stepScale) {

    }

    @Override
    public Vec2 getCenterOfMass() {
        return null;
    }

    @Override
    public double getInertia() {
        return 0;
    }

    @Override
    public void draw(Graphics g, Viewport viewport) {
        g.setColor(viewport.getColor(colorIdx));
        double radians = Math.toRadians(ang);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        int[] xs = new int[vertices.length];
        int[] ys = new int[vertices.length];

        for (int i = 0; i < vertices.length; i++) {
            Vec2 v = vertices[i];

            double rotatedX = v.getX() * cos - v.getY() * sin;
            double rotatedY = v.getX() * sin + v.getY() * cos;

            double worldX = origin.getX() + rotatedX;
            double worldY = origin.getY() + rotatedY;

            Vec2 screenPos = viewport.worldToScreen(g, new Vec2(worldX, worldY));
            xs[i] = (int) (screenPos.getX() + g.getClipBounds().getWidth()/2);
            ys[i] = (int)-(screenPos.getY() + g.getClipBounds().getWidth()/2);
        }

        g.fillPolygon(xs, ys, vertices.length);
    }

    @Override
    public Physical clone() {
        return null;
    }

}

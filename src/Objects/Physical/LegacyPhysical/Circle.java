package Objects.Physical.LegacyPhysical;

import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;

import static java.lang.Math.pow;

public class Circle extends Physical{
    private double size;
    private int colorIdx; //with many particles, it would not make sense to have a lot of duplicated Color objects.

    public Circle(Vec2 origin, double size, int ci){
        super(origin);
        this.size = size;
        this.colorIdx = ci;
    }

    public void setSize(double s){
        this.size = s;
    }
    public double getSize(){
        return size;
    }

    public void setColor(int ci){
        this.colorIdx = ci;
    }

    @Override
    public double minDist(Vec2 pt) {
        return Math.sqrt(origin.distSq(pt)) - size;
    }

    @Override
    public Vec2 closest(Vec2 pt) {
        return pt.sub(origin).toUnit().mul(size).add(origin);
    }

    @Override
    public Vec2 closestNormal(Vec2 pt) {
        return pt.sub(origin);
    }

    @Override
    public Vec2[] definingPoints() {
        return new Vec2[]{origin};
    }

    @Override
    public double getMass() {
        return Math.PI * pow(size, 2) * density;
    }

    @Override
    public void draw(Graphics g, Viewport viewport) {
        Color color = viewport.colors.get(colorIdx%viewport.colors.size());

        Rectangle bounds = g.getClipBounds();

        int screenWidth = bounds.width;
        int screenHeight = bounds.height;

        // pixels per world unit
        double scale = screenHeight / viewport.zoom;

        // world -> screen
        double screenX = (origin.getX() - viewport.origin.getX()) * scale + screenWidth/2;
        double screenY = -(origin.getY() - viewport.origin.getY()) * scale + screenHeight/2;

        double boundingPixels = size * 2.0 * scale;
        g.setColor(color.darker().darker());
        g.fillOval(
                (int) (screenX - boundingPixels / 2.0)-2,
                (int) (screenY - boundingPixels / 2.0)-2,
                (int) (boundingPixels)+4,
                (int) (boundingPixels)+4
        );
        g.setColor(color);
        g.fillOval(
                (int) (screenX - boundingPixels / 2.0),
                (int) (screenY - boundingPixels / 2.0),
                (int) (boundingPixels),
                (int) (boundingPixels)
        );
    }
}
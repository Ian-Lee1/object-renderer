package Objects.Physical;

import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;

public class Line extends Physical{
    private Vec2 end;
    private int colorIdx;

    public Line(Vec2 start, Vec2 end, int colorIdx){
        super(start);
        super.density = 0;
        this.end = end;
        this.colorIdx = colorIdx;
    }

    public int getColorIdx() {
        return colorIdx;
    }

    public Vec2 getEnd() {
        return end;
    }

    public Vec2 getStart() {
        return origin;
    }

    public void setColorIdx(int colorIdx) {
        this.colorIdx = colorIdx;
    }

    public void setEnd(Vec2 end) {
        this.end = end;
    }

    public void setStart(Vec2 start) {
        this.origin = start;
    }

    @Override
    public double minDist(Vec2 pt) {
        return pt.sub(closest(pt)).length();
    }

    @Override
    public Vec2 closest(Vec2 pt) {
        Vec2 wall = origin.sub(end);
        Vec2 ap = pt.sub(end);
        double t = ap.dot(wall) / wall.dot(wall);
        t = Math.clamp(t, 0, 1);
        return end.add(wall.mul(t));
    }

    @Override
    public Vec2 closestNormal(Vec2 pt) {
        Vec2 nor1 = new Vec2(origin.sub(end).getY(),-origin.sub(end).getX()).toUnit();
        Vec2 nor2 = new Vec2(-origin.sub(end).getY(),origin.sub(end).getX()).toUnit();

        if (nor1.distSq(pt) < nor2.distSq(pt)){
            return nor1;
        }
        return nor2;
    }

    @Override
    public Vec2[] definingPoints() {
        return new Vec2[]{origin, end};
    }

    @Override
    public double getMass() {
        return Double.POSITIVE_INFINITY;
    }

    public void draw(Graphics g, Viewport viewport){
        Color color = viewport.colors.get(colorIdx%viewport.colors.size());

        Rectangle bounds = g.getClipBounds();

        int screenWidth = bounds.width;
        int screenHeight = bounds.height;

        // pixels per world unit
        double scale = screenHeight / viewport.zoom;

        // world -> screen
        int screenX1 = (int)( (origin.getX() - viewport.origin.getX()) * scale + screenWidth/2);
        int screenY1 = (int)(-(origin.getY() - viewport.origin.getY()) * scale + screenHeight/2);
        int screenX2 = (int)( (end.getX() - viewport.origin.getX()) * scale + screenWidth/2);
        int screenY2 = (int)(-(end.getY() - viewport.origin.getY()) * scale + screenHeight/2);
        g.setColor(color);
        g.drawLine(screenX1, screenY1, screenX2, screenY2);
    }
}

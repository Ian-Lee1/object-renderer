package Objects;

import Vector.Vec2;

import java.awt.*;
import java.util.ArrayList;

public class Viewport{
    public Vec2 origin;
    public Vec2 vel;
    public double zoom;
    public boolean antialiasing;

    public ArrayList<Color> colors;

    public Viewport(){
        origin = new Vec2(0, 0);
        vel = new Vec2(0, 0);
        zoom = 0;
        colors = new ArrayList<>();
        antialiasing = false;
    }

    public Color getColor(int i){
        if (colors.size() == 0){
            return Color.RED;
        }
        return colors.get(i % colors.size());
    }

    public Vec2 screenToWorld(int w, int h, double x, double y){
        double dx = x-w/2;
        double dy = y-h/2;
        dx /= h/zoom;
        dy /= h/zoom;
        return new Vec2(dx, dy).add(origin);
    }

    public double worldToScreenScale(Graphics g){
        return g.getClipBounds().height / zoom;
    }
    public Vec2 worldToScreen(Graphics g, Vec2 pos){
        return (pos.sub(origin)).mul(worldToScreenScale(g));
    }
}

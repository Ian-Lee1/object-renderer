package Objects;

import Vector.Vec2;

import java.awt.*;
import java.util.ArrayList;

public class Viewport{
    public Vec2 origin;
    public Vec2 vel;
    public double zoom;

    public ArrayList<Color> colors;

    public Viewport(){
        origin = new Vec2(0, 0);
        vel = new Vec2(0, 0);
        zoom = 0;
        colors = new ArrayList<>();
    }

    public Color getColor(int i){
        if (colors.size() == 0){
            return Color.RED;
        }
        return colors.get(i % colors.size());
    }
    public double worldToScreenScale(Graphics g){
        return g.getClipBounds().height / zoom;
    }
    public Vec2 worldToScreen(Graphics g, Vec2 pos){
        return (pos.sub(origin)).mul(worldToScreenScale(g));
    }
}

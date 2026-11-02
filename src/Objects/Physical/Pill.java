package Objects.Physical;

import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;

public class Pill extends Physical{
    public double size;
    public double length; //length extending according to angle from origin

    public Pill(Vec2 origin, double ang, double size, double length, int ind){
        this.origin = origin;
        this.ang = ang;
        this.size = size;
        this.length = length;
        this.colorIdx = ind;
        this.vel = new Vec2(0, 0);
        this.acc = new Vec2(0, 0);
    }

    @Override
    public void draw(Graphics g, Viewport viewport) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(viewport.colors.get(colorIdx));
        double scale = viewport.worldToScreenScale(g);

        double radians = Math.toRadians(ang);

        Vec2 end = origin.add(
                new Vec2(
                        Math.cos(radians) * length,
                        Math.sin(radians) * length
                )
        );

        Vec2 screenPos = viewport.worldToScreen(g, origin);
        int x1 = (int) (screenPos.getX() + g.getClipBounds().getWidth()/2);
        int y1 = (int)-(screenPos.getY() - g.getClipBounds().getHeight()/2);

        screenPos = viewport.worldToScreen(g, end);
        int x2 = (int) (screenPos.getX() + g.getClipBounds().getWidth()/2);
        int y2 = (int)-(screenPos.getY() - g.getClipBounds().getHeight()/2);

        float thickness = (float)(size * 2 * scale);

        Stroke stroke = g2.getStroke();

        g2.setStroke(new BasicStroke(
                thickness,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        g2.drawLine(x1, y1, x2, y2);

        g2.setStroke(stroke);
    }
}

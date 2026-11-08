package Objects.tracer;

import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;

import static java.lang.Math.pow;

public class CubicCurveTracer implements Tracer{
    @Override
    public void draw(Graphics2D g2d, Viewport vp, ArrayList<Vec2> points, int colorIdx) {
        if (points.size() > 1) {


            g2d.setColor(vp.getColor(colorIdx).darker().brighter().brighter());
            //double scale = vp.worldToScreenScale(g2d);
            for (int i = 0; i < points.size(); i++) {

                Vec2 lastLast = vp.worldToScreen(g2d, points.get(Math.clamp(i - 2, 0, points.size()-1)));
                Vec2 last = vp.worldToScreen(g2d, points.get(Math.clamp(i - 1, 0, points.size()-1)));
                Vec2 curr = vp.worldToScreen(g2d, points.get(i));
                Vec2 next = vp.worldToScreen(g2d, points.get(Math.clamp(i + 1, 0, points.size()-1)));
                double segLen = last.sub(curr).length();
                Vec2 cPoint1 = calcControl(lastLast, last, curr, -segLen / 3.5);
                Vec2 cPoint2 = calcControl(last, curr, next, segLen / 3.5);

                CubicCurve2D.Double cubicCurve = new CubicCurve2D.Double(
                        last.getX(), last.getY(),
                        cPoint1.getX(), cPoint1.getY(),
                        cPoint2.getX(), cPoint2.getY(),
                        curr.getX(), curr.getY()
                );

                float size = 1 ;//(int) Math.clamp((
                     //   2.0 / (pow(vp.zoom, 2)/1000 + 1)

              //  ), 1.0, 4.0);
                g2d.setStroke(new BasicStroke(size));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.draw(cubicCurve);

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            }
        }
    }

    private Vec2 calcControl(Vec2 last, Vec2 curr, Vec2 next, double scale){
        Vec2 dir = last.sub(next).toUnit();
        return curr.add(dir.mul(scale));
    }
    }



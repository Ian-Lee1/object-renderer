package Objects.Forces;

import Objects.Physical.Physical;
import Objects.Physical.Pill;
import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.pow;

public class Attractors extends Force {
    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes) {
        int attractors = 0;
        if(attributes.containsKey("a_point_amt")) {
            attractors = (int)attributes.get("a_point_amt").doubleValue();
        }

        //ASSUME ONLY PILL IS PASSED IN.

        if (obj instanceof Pill){
            Vec2 start = null, end = null;
            if(((Pill) obj).length > 0) {
                double radians = Math.toRadians(obj.ang);
                start = obj.origin.sub(new Vec2(
                                Math.cos(radians) * ((Pill) obj).length / 2,
                                Math.sin(radians) * ((Pill) obj).length / 2
                        )
                );
                end = obj.origin.add(
                        new Vec2(
                                Math.cos(radians) * ((Pill) obj).length / 2,
                                Math.sin(radians) * ((Pill) obj).length / 2
                        )
                );
            }

        for ( int i  = 1 ; i < attractors + 1; i++) {
            if (attributes.containsKey("a_" + i + "_x") && attributes.containsKey("a_" + i + "_y") && attributes.containsKey("a_" + i + "_g")) {
                if(((Pill) obj).length > 0) {
                    Vec2 sAcc = (new Vec2(attributes.get("a_" + i + "_x"), attributes.get("a_" + i + "_y")).sub(start));
                    Vec2 eAcc = (new Vec2(attributes.get("a_" + i + "_x"), attributes.get("a_" + i + "_y")).sub(end));

                    double torqueImpulseA = (start.sub(obj.origin).getX() * sAcc.getY()) - (start.sub(obj.origin).getY() * sAcc.getX());
                    double torqueImpulseB = (end.sub(obj.origin).getX() * eAcc.getY()) - (end.sub(obj.origin).getY() * eAcc.getX());

                    obj.angVel = obj.angVel + torqueImpulseA + torqueImpulseB;
                }
                Vec2 dir = (new Vec2(attributes.get("a_" + i + "_x"), attributes.get("a_" + i + "_y")).sub(obj.getCenterOfMass()));

                obj.acc = obj.acc.add(dir.mul(attributes.get("a_" + i + "_g") * 1 / Math.max(0.01, dir.lengthSq())));
            }
        }
        }
    }

    @Override
    public void draw(Graphics g, Viewport vp, HashMap<String, Double> attributes) {
        Graphics2D g2 = (Graphics2D)g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int attractors = 0;
        if(attributes.containsKey("a_point_amt")) {
            attractors = (int)attributes.get("a_point_amt").doubleValue();
        }
        int size = (int)Math.clamp((6.0 / (pow(vp.zoom, 2)/1000 + 1)), 0, 6);
        for ( int i  = 1 ; i < attractors + 1; i++) {
            if (attributes.containsKey("a_" + i + "_x") && attributes.containsKey("a_" + i + "_y") && attributes.containsKey("a_" + i + "_g")) {
                Color color = new Color(40, 70, 135);
                if (attributes.get("a_" + i + "_g") < 0){
                    color = new Color(155, 20, 40);
                }
                Vec2 pos = vp.worldToScreen(g, new Vec2(attributes.get("a_" + i + "_x"), attributes.get("a_" + i + "_y")));
                //pos = pos.add(new Vec2( g.getClipBounds().getWidth()/2, g.getClipBounds().getHeight()/2));
                g2.setColor(color.brighter().brighter());
                g2.fillOval((int)pos.getX() -(size+2), (int)pos.getY()-(size+2), (size+2)*2, (size+2)*2);
                g2.setColor(color);
                g2.fillOval((int)pos.getX() -size, (int)pos.getY()-size, size*2, size*2);
               // g2.setColor(new Color(40, 70, 135));
               // g2.setFont(new Font("SansSerif", Font.BOLD, (25 * 72 / 96)));
               // g2.drawString(attributes.get("a_" + i + "_g").toString(), (int) pos.getX() - 12, (int) pos.getY() + 6);
            }
        }
    }
}

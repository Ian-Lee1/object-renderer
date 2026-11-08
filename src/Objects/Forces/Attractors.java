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
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes, double stepScale) {
        int attractors = 0;
        if(attributes.containsKey("a_point_amt")) {
            attractors = (int)attributes.get("a_point_amt").doubleValue();
        }

        //ASSUME ONLY PILL IS PASSED IN.
        if (obj instanceof Pill){
            Vec2 start = obj.origin, end = obj.origin;
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
                Vec2 dir = (new Vec2(attributes.get("a_" + i + "_x"), attributes.get("a_" + i + "_y")).sub(obj.getCenterOfMass()));
                if(((Pill) obj).length > 0) { //for each point on object (2 for pill), sum the torque, then add to angvel.
                    Vec2 sDir = (new Vec2(attributes.get("a_" + i + "_x"), attributes.get("a_" + i + "_y")).sub(start));
                    Vec2 eDir = (new Vec2(attributes.get("a_" + i + "_x"), attributes.get("a_" + i + "_y")).sub(end));

                    Vec2 rA = start.sub(obj.origin);
                    Vec2 rB = end.sub(obj.origin);

                    double distToSSq = sDir.lengthSq();
                    double distToESq = eDir.lengthSq();

                    sDir = sDir.toUnit();
                    eDir = eDir.toUnit();

                    //multiplying mass in since it affects drag.
                    Vec2 sFor = sDir.mul(attributes.get("a_" + i + "_g") / Math.max(0.1, distToSSq)).mul(1/ obj.inverseMass);
                    Vec2 eFor = eDir.mul(attributes.get("a_" + i + "_g") / Math.max(0.1, distToESq)).mul(1/ obj.inverseMass);

                    double torque = rA.cross(sFor) + rB.cross(eFor);
                    double angularAccel = torque / obj.getInertia();
                    obj.angVel += angularAccel * stepScale;
                }
                obj.force = obj.force.add(dir.mul(attributes.get("a_" + i + "_g") / Math.max(0.1, dir.lengthSq())).mul(1/obj.inverseMass));
            }
        }
        }
    }

    public void specialProcess(HashMap<String, Double> attributes, double stepScale){ //move attractors around attractors.
        int attractors = 0;
        if(attributes.containsKey("a_point_amt")) {
            attractors = (int)attributes.get("a_point_amt").doubleValue();
        }

        for ( int i  = 1 ; i < attractors + 1; i++) {
            if (attributes.containsKey("a_" + i + "_movable") && attributes.get("a_" + i + "_movable") > 0){
                if (!attributes.containsKey("a_" + i + "_velX") || !attributes.containsKey("a_" + i + "_velY")){
                    attributes.put("a_" + i + "_velX", 0.0);
                    attributes.put("a_" + i + "_velY", 0.0);
                }
                Vec2 acc = new Vec2(0,0);
                for ( int j  = 1 ; j < attractors + 1; j++) {
                    if (i != j) {
                        Vec2 dir = new Vec2( attributes.get("a_" + j + "_x") - attributes.get("a_" + i + "_x"),
                                attributes.get("a_" + j + "_y") - attributes.get("a_" + i + "_y"));
                        acc = acc.add(dir.toUnit().mul(attributes.get("a_" + j + "_g")/ Math.max(3, dir.lengthSq())));
                    }
                }
                Vec2 toOrigin = new Vec2(-attributes.get("a_" + i + "_x"), -attributes.get("a_" + i + "_y"));
                acc = acc.add(toOrigin.mul(toOrigin.length()*0.0001));
                attributes.put("a_" + i + "_velX", attributes.get("a_" + i + "_velX")+acc.getX() * stepScale);
                attributes.put("a_" + i + "_velY", attributes.get("a_" + i + "_velY")+acc.getY() * stepScale);

                attributes.put("a_" + i + "_x", attributes.get("a_" + i + "_x") + attributes.get("a_" + i + "_velX") * stepScale);
                attributes.put("a_" + i + "_y", attributes.get("a_" + i + "_y") + attributes.get("a_" + i + "_velY") * stepScale);
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

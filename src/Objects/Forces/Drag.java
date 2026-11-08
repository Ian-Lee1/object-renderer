package Objects.Forces;

import Objects.Physical.Physical;
import Objects.Physical.Pill;
import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.pow;
public class Drag extends Force{
    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes, double stepScale) {
        if (attributes.containsKey("drag")){ //TODO implement better
            Vec2 velDir = obj.vel.toUnit();
            Vec2 drag = velDir.mul(-attributes.get("drag")/100 * obj.vel.lengthSq()).mul(1/ obj.inverseMass);
            if (obj instanceof Pill) {
                Vec2 axis = Vec2.radToVec(Math.toRadians(obj.ang));

                double exposure = Math.abs(axis.cross(velDir));
                double area = 2 * ((Pill) obj).size + exposure * ((Pill)obj).length;
                drag = velDir.mul(-attributes.get("drag")/100 * area * obj.vel.lengthSq()).mul(1/ obj.inverseMass);
            }

            obj.force = (obj.force.add(drag));
            double torqueDrag = -attributes.get("drag")/1000 * obj.angVel * Math.abs(obj.angVel);
            obj.angVel += torqueDrag * stepScale;
        }
    }

    @Override
    public void draw(Graphics g, Viewport vp, HashMap<String, Double> attributes) {
        //nothing to draw
    }
}

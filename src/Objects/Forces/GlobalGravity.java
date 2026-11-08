package Objects.Forces;

import Objects.Physical.Physical;
import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GlobalGravity extends Force {
    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes, double stepScale) {
        if (attributes.containsKey("gravity")) {
                obj.force = obj.force.add(new Vec2(0, -attributes.get("gravity")).mul(1/obj.inverseMass));
        }
    }


    @Override
    public void draw(Graphics g, Viewport vp, HashMap<String, Double> attributes) {
        //nothing to draw
    }
}

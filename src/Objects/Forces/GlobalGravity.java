package Objects.Forces;

import Objects.Physical.Physical;
import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GlobalGravity extends Force {
    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes) {
        if (attributes.containsKey("ggravity")) {
                obj.acc = obj.acc.add(new Vec2(0, attributes.get("ggravity")));
        }
    }

    @Override
    public void draw(Graphics g, Viewport vp, HashMap<String, Double> attributes) {
        //nothing to draw
    }
}

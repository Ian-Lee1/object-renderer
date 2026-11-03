package Objects.Forces;

import Objects.Physical.Physical;
import Vector.Vec2;

import java.util.ArrayList;
import java.util.HashMap;

public class CenterGravity extends Force {
    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes) {
        if (attributes.containsKey("g_center_x") && attributes.containsKey("g_center_y") && attributes.containsKey("gravity")){
            Vec2 dir = (new Vec2(attributes.get("g_center_x"), attributes.get("g_center_y")).sub(obj.getCenterOfMass()));
            obj.acc = obj.acc.add(dir).mul(attributes.get("gravity") * 1/Math.max(0.5,dir.lengthSq()));
        }
    }
}

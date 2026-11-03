package Objects.Forces;

import Objects.Physical.Physical;
import Vector.Vec2;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalGravity extends Force {
    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes) {
        if (attributes.containsKey("gravity")) {
            obj.acc = obj.acc.add(new Vec2(0, attributes.get("gravity")));
        }
    }
}

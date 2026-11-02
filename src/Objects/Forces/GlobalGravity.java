package Objects.Forces;

import Objects.Physical.Physical;
import Vector.Vec2;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalGravity implements Force {
    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes) {
        if (attributes.containsKey("gravity")) {
            obj.setAcc(new Vec2(0, attributes.get("gravity")));
        }
    }
}

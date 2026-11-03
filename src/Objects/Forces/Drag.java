package Objects.Forces;

import Constants.Constants;
import Objects.Physical.Physical;

import java.util.ArrayList;
import java.util.HashMap;

public class Drag extends Force{
    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes) {
        if (attributes.containsKey("drag")){
            obj.acc = (obj.acc.sub(obj.vel.mul(Constants.DRAG)));
        }
    }
}

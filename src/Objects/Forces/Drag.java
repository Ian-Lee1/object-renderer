package Objects.Forces;

import Constants.Constants;
import Objects.Physical.Physical;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.pow;
public class Drag extends Force{
    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes) {
        if (attributes.containsKey("drag")){
            obj.acc = (obj.acc.sub(obj.vel.mul(attributes.get("drag")/ obj.getInertia()).mul( -1/(obj.vel.lengthSq()+1) + 1 )   ));
            obj.angVel -= obj.angVel*attributes.get("drag")*0.005/(pow(obj.getInertia(),2)+1);
        }
    }
}

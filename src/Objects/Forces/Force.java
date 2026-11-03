package Objects.Forces;

import Objects.Physical.Physical;
import Objects.Physical.Pill;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Force {
    public abstract void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes);
        //all objects may not always be used. Mutual gravity or partical life or normal force may use it, but
        //things such as drag would not.

    public static Force parseForce(String parameters) {
        if (parameters.toLowerCase().startsWith("ggravity")) {
            return new GlobalGravity();
        } else if (parameters.toLowerCase().startsWith("drag")) {
            return new Drag();
        }
        throw new IllegalArgumentException("Cannot create Unsupported type " + parameters.substring(0, parameters.indexOf(' ')));
    }

}

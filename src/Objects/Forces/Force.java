package Objects.Forces;

import Objects.Physical.Physical;

import java.util.ArrayList;
import java.util.HashMap;

public interface Force {
    void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes);
        //all objects may not always be used. Mutual gravity or partical life or normal force may use it, but
        //things such as drag would not.
}

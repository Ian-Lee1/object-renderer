package Objects.Forces;

import Objects.Physical.Physical;
import Solver.LegacySolver.Solver;

import java.util.ArrayList;
import java.util.HashMap;

public class Normal extends Force{
    Solver solver;
    public Normal(Solver solver){
        this.solver = solver;
    }

    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes) {
        for (Object obj2 : objects) {
            //solver.isTouching(obj, obj2) returns true if objects are colliding
        }
    }
}

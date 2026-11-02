package Objects.Forces;

import Constants.Constants;
import Objects.Physical.Physical;

import java.util.ArrayList;
import java.util.HashMap;

public class Drag implements Force{
    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes) {
        if (attributes.containsKey("drag")){
            obj.setAcc(obj.getAcc().sub(obj.getVel().mul(Constants.DRAG)));
        }
    }
}

package Solver;

import Objects.Physical.Physical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Solver {
    public void resolve(List<Physical> objects, double stepScale, HashMap<String, Double> attributes); //resolve collisions, detecting them and applying impulses to objects as neccesary.
}

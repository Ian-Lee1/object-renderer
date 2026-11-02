package Solver;

import Objects.Physical.Physical;
import Vector.Vec2;

import java.util.ArrayList;

public interface Solver {
    record Touching(boolean touching, Vec2 closest) {}

    Touching isTouching(Physical obj, Physical obj2);
    void resolveCollisions(ArrayList<Physical> objects);

}

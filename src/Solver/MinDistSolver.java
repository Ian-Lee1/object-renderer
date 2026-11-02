package Solver;

import Objects.Physical.Physical;
import Vector.Vec2;

import java.util.ArrayList;

import static Constants.Constants.BOUNCYNESS;

public class MinDistSolver implements Solver{
    double margin = 1e-3;
    int maxIter = 10;
    public MinDistSolver(){}
    public MinDistSolver(double margin, int maxIter){
        this.margin = margin;
        this.maxIter = maxIter;
    }

    @Override
    public Touching isTouching(Physical obj, Physical obj2) {
        Vec2 closestPoint1 = obj.definingPoints()[0];
        Vec2 closestPoint2 = obj2.definingPoints()[0];
        Vec2 lastClosestPoint = new Vec2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        for (int i = 0; i < maxIter; i++){
            closestPoint1 = obj.closest(closestPoint2);  //closest point of obj to last closest
            if (obj2.minDist(closestPoint1) < margin){ //is the closest point on obj inside of obj2?
                return new Touching(true, closestPoint1);
            }
            closestPoint2 = obj2.closest(closestPoint1); //repeat but for other object
            if (obj.minDist(closestPoint2) < margin){
                return new Touching(true, closestPoint2);
            }
            if (lastClosestPoint.distSq(closestPoint1) < 0.0001){ // is the closest point moving or has it been found already?
                return new Touching(false, closestPoint1); //has been found, but is not in objects.
            }
            lastClosestPoint = closestPoint1;
        }
        return new Touching(false, closestPoint1);
    }

    @Override
    public void resolveCollisions(ArrayList<Physical> objects) {
        for (Physical obj : objects){
            for (Physical obj2 : objects){
                if (obj != obj2 && obj.getMovable()){
                    Touching touching = isTouching(obj, obj2);
                    if (touching.touching()) {
                        Vec2 normal = obj2.closestNormal(touching.closest());
                        Vec2 rv = obj.getVel().sub(obj2.getVel());
                        //in normal
                        double velAlongNormal = rv.dot(normal);

                        if (velAlongNormal < 0) { //is moving together
                            double j = -(1 + BOUNCYNESS) * velAlongNormal;
                            j /= (obj.getMass()) + (obj2.getMass());
                            Vec2 impulse = normal.mul(j);
                            obj.setVel(obj.getVel().add(impulse.mul(obj.getMass())));
                            obj2.setVel(obj2.getVel().sub(impulse.mul(obj2.getMass())));
                        }

                    }
                }
            }
        }
    }
}

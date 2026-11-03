package Solver.LegacySolver;

import Objects.Physical.LegacyPhysical.Circle;
import Objects.Physical.LegacyPhysical.Line;
import Objects.Physical.LegacyPhysical.Physical;
import Vector.Vec2;

import java.util.ArrayList;

import static Constants.Constants.*;
import static java.lang.Math.pow;

public class CircleCollisions implements Solver {
    @Override
    public void resolveCollisions(ArrayList<Physical> objects) {
        for (Physical obj : objects) {
            if (obj instanceof Circle) {
                resolveObjCollisions((Circle) obj, objects);
            }
        }
    }

    public Touching isTouching(Physical obj, Physical obj2) {
        if (obj instanceof Circle) {
            return new Touching(isTouching((Circle)obj, obj2), new Vec2(0, 0));
        }
        return  new Touching(false,new Vec2(0, 0));
    }

    private boolean isTouching(Circle obj, Physical obj2) {
        if (obj2 instanceof Circle) {
            return obj.getOrigin().distSq(obj2.getOrigin()) < pow(obj.getSize() + ((Circle) obj2).getSize(), 2);
        } else if (obj2 instanceof Line) {
            //calculate circle and line collision
            Vec2 wall = obj2.getOrigin().sub(((Line) obj2).getEnd());
            Vec2 ap = obj.getOrigin().sub(((Line) obj2).getEnd());
            double t = ap.dot(wall) / wall.dot(wall);
            t = Math.clamp(t, 0, 1);
            Vec2 closest = ((Line) obj2).getEnd().add(wall.mul(t));
            Vec2 delta = obj.getOrigin().sub(closest);
            double distance = delta.length();
            return distance < obj.getSize();
        }
        throw new IllegalArgumentException("Unsupported type for collision detection");
    }

    private void resolveObjCollisions(Circle obj, ArrayList<Physical> objects) {
        for (Physical obj2 : objects) {
            if (obj != obj2) { //objects are distinct
                Vec2 normal = new Vec2(0, 0);
                double m1 = Math.PI * pow(obj.getSize(), 2) * obj.getDensity();
                double m2 = 0;

                if (isTouching(obj, obj2)) {
                    if (obj2 instanceof Circle) {
                        normal = obj.getOrigin().sub(obj2.getOrigin()).toUnit();
                        //density is raised to ^-1 power when initilizing.
                        m2 = Math.PI * pow(((Circle) obj2).getSize(), 2) * obj2.getDensity();
                    } else if (obj2 instanceof Line) {
                        Vec2 wall = obj2.getOrigin().sub(((Line) obj2).getEnd());
                        Vec2 ap = obj.getOrigin().sub(((Line) obj2).getEnd());
                        double t = ap.dot(wall) / wall.dot(wall);
                        t = Math.clamp(t, 0, 1);
                        Vec2 closest = ((Line) obj2).getEnd().add(wall.mul(t));
                        Vec2 delta = obj.getOrigin().sub(closest);
                        normal = delta.toUnit();
                    }

                        //=========APPLY IMPULSE==========
                        //reletive velocity
                        Vec2 rv = obj.getVel().sub(obj2.getVel());
                        //in normal
                        double velAlongNormal = rv.dot(normal);

                        if (velAlongNormal < 0) { //is moving together
                            double j = -(1 + 0.99) * velAlongNormal;
                            j /= (m1) + (m2);
                            Vec2 impulse = normal.mul(j);
                            obj.setVel(obj.getVel().add(impulse.mul(m1)));
                            obj2.setVel(obj2.getVel().sub(impulse.mul(m2)));
                        }

                }
            }
        }
    }
}

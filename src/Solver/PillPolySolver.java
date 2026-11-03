package Solver;

import Objects.Physical.Physical;
import Objects.Physical.Pill;
import Objects.Physical.Polygon;
import Vector.Vec2;

import java.util.HashMap;
import java.util.List;

import static java.lang.Math.pow;

public class PillPolySolver implements Solver{

    @Override
    public void resolve(List<Physical> objects, double stepScale, HashMap<String, Double> attributes) {
        for (int i = objects.size()-1 ; i >= 0 ; i--){

            Physical obj = objects.get(i);
            for (int j = i-1; j >=0 ; j--){
                Physical obj2 = objects.get(j);
                if (obj.movable || obj2.movable){
                    Contact contact = collide(obj, obj2);
                    if (contact.penetration >= 0){
                        applyImpulse(contact, stepScale, attributes);
                    }
                }

            }
        }
    }

    private Contact PolyPolyContact(Polygon obj, Polygon obj2){
        //TODO
        return new Contact();
    }
    private Contact PillPillContact(Pill obj, Pill obj2){
        Vec2 objLineStart, objLineEnd;
        Vec2 obj2LineStart, obj2LineEnd;

        objLineStart = obj.origin;
        objLineEnd = obj.origin;
        if (obj.length > 0) {
            objLineEnd = obj.origin.add(Vec2.radToVec(Math.toRadians(obj.ang)).mul(obj.length));
        }

        obj2LineStart = obj2.origin;
        obj2LineEnd = obj2.origin;
        if (obj2.length > 0) {
            obj2LineEnd = obj2.origin.add(Vec2.radToVec(Math.toRadians(obj2.ang)).mul(obj2.length));
        }
        Points result;
        if (obj.length > 0 && obj2.length > 0) {
            result = computNearestPoints(objLineStart, objLineEnd, obj2LineStart, obj2LineEnd);
        } else if (obj.length == 0 && obj2.length == 0) {
            result = new Points(obj.origin, obj2.origin, false);
        } else if (obj.length == 0 && obj2.length > 0) {
            result = new Points(obj.origin, nearestPointToPt(obj2LineStart, obj2LineEnd, obj.origin), false);
        } else { //obj.length > 0 && obj2.length == 0
            result = new Points(nearestPointToPt(objLineStart, objLineEnd, obj2.origin), obj2.origin, false);
        }

        Vec2 delta = result.a.sub(result.b);
        double dist = delta.length();
        double penetration = obj.size + obj2.size - dist;
        Vec2 normal ;
        if (dist == 0){
            normal = obj.origin.sub(obj2.origin).toUnit();
        } else { normal= delta.toUnit();}

        //Midpoint
        Vec2 contactPt = result.a.add(result.b).mul(0.5);
        Contact contact = new Contact();
        contact.a = obj;
        contact.b = obj2;
        contact.penetration = penetration;
        contact.normal = normal;

        if (!result.parallel){
            contact.contact = new Vec2[]{contactPt};
        } else { //pills are parallel, fill in contact manifold with the shorter lines.
            if (objLineEnd.distSq(objLineStart) > obj2LineEnd.distSq(obj2LineStart)){
                contact.contact = new Vec2[]{obj2LineStart, obj2LineEnd};
            } else { //TODO debug on wether directly taking the line points are ok or if we need to figure out exact contact points
                contact.contact = new Vec2[]{objLineStart, objLineEnd};
            }
        }
        return contact;
    }

    /*public void nearestPointsTest(Vec2 p0,Vec2 p1, Vec2 q0, Vec2 q1){
        Points result = computNearestPoints(p0, p1, q0, q1);
        System.out.println(result.a);System.out.println(result.b);System.out.println(result.parallel);
    }*/
    private record Points(Vec2 a, Vec2 b, boolean parallel){};

    private Vec2 nearestPointToPt(Vec2 p0,Vec2 p1, Vec2 pt) {
        Vec2 line = p0.sub(p1);
        Vec2 ap = pt.sub(p1);
        double t = ap.dot(line) / line.dot(line);
        t = Math.clamp(t, 0, 1);
        return p1.add(line.mul(t));
    }

    private Points computNearestPoints(Vec2 p0,Vec2 p1, Vec2 q0, Vec2 q1) {
        boolean parallel = false;
        double ux = p1.getX() - p0.getX();
        double uy = p1.getY() - p0.getY();
        double vx = q1.getX() - q0.getX();
        double vy = q1.getY() - q0.getY();
        double wx = p0.getX() - q0.getX();
        double wy = p0.getY() - q0.getY();

        double a = ux * ux + uy * uy; // Dot product of u with itself
        double b = ux * vx + uy * vy; // Dot product of u with v
        double c = vx * vx + vy * vy; // Dot product of v with itself
        double d = ux * wx + uy * wy; // Dot product of u with w
        double e = vx * wx + vy * wy; // Dot product of v with w

        double denom = a * c - b * b; // Determinant
        double sNumerator, sDenominator = denom;
        double tNumerator, tDenominator = denom;

        // Check if segments are parallel or degenerate
        final double EPSILON = 1e-9;
        if (denom < EPSILON) {
            // Segments are parallel, fix s = 0 and solve for t
            parallel = true;
            sNumerator = 0.0;
            sDenominator = 1.0;
            tNumerator = e;
            tDenominator = c;
        } else {
            // Get the closest points on the infinite lines
            sNumerator = (b * e - c * d);
            tNumerator = (a * e - b * d);

            // Clamp s to the segment range [0, 1]
            if (sNumerator < 0.0) {
                sNumerator = 0.0;
                tNumerator = e;
                tDenominator = c;
            } else if (sNumerator > sDenominator) {
                sNumerator = sDenominator;
                tNumerator = e + b;
                tDenominator = c;
            }
        }
        // Clamp t to the segment range [0, 1]
        if (tNumerator < 0.0) {
            tNumerator = 0.0;
            // Recompute s if t was clamped to 0
            if (-d < 0.0) {
                sNumerator = 0.0;
            } else if (-d > a) {
                sNumerator = sDenominator;
            } else {
                sNumerator = -d;
                sDenominator = a;
            }
        } else if (tNumerator > tDenominator) {
            tNumerator = tDenominator;
            // Recompute s if t was clamped to 1
            if ((-d + b) < 0.0) {
                sNumerator = 0.0;
            } else if ((-d + b) > a) {
                sNumerator = sDenominator;
            } else {
                sNumerator = (-d + b);
                sDenominator = a;
            }
        }
        // Calculate final parametric parameters s and t
        double s = (Math.abs(sNumerator) < EPSILON ? 0.0 : sNumerator / sDenominator);
        double t = (Math.abs(tNumerator) < EPSILON ? 0.0 : tNumerator / tDenominator);

        // Map s and t back to 2D coordinates
        Vec2 closestS1 = new Vec2(p0.getX() + s * ux, p0.getY() + s * uy);
        Vec2 closestS2 = new Vec2(q0.getX() + t * vx, q0.getY() + t * vy);
        return new Points(closestS1, closestS2, parallel);
    }
    
    private Contact PillPolyContact(Pill obj, Polygon obj2){
        //TODO
        return new Contact();
    }

    private Contact collide(Physical obj, Physical obj2) {
        if (obj instanceof Polygon) {
            if (obj2 instanceof Polygon) {
                return PolyPolyContact((Polygon) obj, (Polygon) obj2);
            } else if (obj2 instanceof Pill) {
                return PillPolyContact((Pill) obj2, (Polygon) obj);
            } else {
                throw new IllegalArgumentException("Solver does not support type " + obj2.getClass().getName());
            }
        } else if (obj instanceof Pill) {
            if (obj2 instanceof Polygon) {
                return PillPolyContact((Pill) obj, (Polygon) obj2);
            } else if (obj2 instanceof Pill) {
                return PillPillContact((Pill) obj2, (Pill) obj);
            } else {
                throw new IllegalArgumentException("Solver does not support type " + obj2.getClass().getName());
            }
        } else {
            throw new IllegalArgumentException("Solver does not support type " + obj.getClass().getName());
        }
    }

    private void applyImpulse(Contact contact, double stepScale, HashMap<String, Double> attributes){
        double bouncyness = 0.999;
        if (attributes.containsKey("bouncyness")){
            bouncyness = Math.clamp(attributes.get("bouncyness"), 0, 0.999);
        }

        Physical obj2 = contact.a; //moving
        Physical obj = contact.b; //wall

        Vec2 rA = contact.contact[0].sub(obj.getCenterOfMass());
        Vec2 rB = contact.contact[0].sub(obj2.getCenterOfMass());

        // Contact point velocities
        Vec2 vContactA = obj.vel.add(new Vec2(-Math.toRadians(obj.angVel) * rA.getY(), Math.toRadians(obj.angVel) * rA.getX()));
        Vec2 vContactB = obj2.vel.add(new Vec2(-Math.toRadians(obj2.angVel) * rB.getY(), Math.toRadians(obj2.angVel) * rB.getX()));

        // Relative velocity along the normal
        Vec2 vRel = vContactB.sub(vContactA);
        double velAlongNormal = vRel.dot(contact.normal);

        if (velAlongNormal < 0) { //is moving together
            double rAn = (rA.getX() * contact.normal.getY()) - (rA.getY() * contact.normal.getX());
            double rBn = (rB.getX() * contact.normal.getY()) - (rB.getY() * contact.normal.getX());

            double invMassA = !obj.movable ? 0 : obj.inverseMass;
            double invMassB = !obj2.movable ? 0 : obj2.inverseMass;
            double invInertiaA = !obj.movable ? 0 : 1.0 / obj.getInertia();
            double invInertiaB = !obj2.movable ? 0 : 1.0 / obj2.getInertia();

            double denom = invMassA + invMassB +
                    (rAn * rAn * invInertiaA) +
                    (rBn * rBn * invInertiaB);

            double j = -(1 + bouncyness) * velAlongNormal / denom;

            //apply velocity impulse
            Vec2 impulse = contact.normal.mul(j);

            if (obj.movable) {
                obj.vel = obj.vel.sub(impulse.mul(invMassA));
                // 2D cross product: rA x (-impulse) -> -(rA.x * impulse.y - rA.y * impulse.x)
                double torqueImpulseA = (rA.getX() * impulse.getY()) - (rA.getY() * impulse.getX());
                obj.angVel -= Math.toDegrees(torqueImpulseA * invInertiaA);
            }

            if (obj2.movable) {
                obj2.vel = obj2.vel.add(impulse.mul(invMassB));
                // 2D cross product: rB x impulse
                double torqueImpulseB = (rB.getX() * impulse.getY()) - (rB.getY() * impulse.getX());
                obj2.angVel += Math.toDegrees(torqueImpulseB * invInertiaB);
            }

            
        }

        //prevent merging
        double totalInvMass = obj.inverseMass + obj2.inverseMass;

        if(obj.movable){
            obj.origin = obj.origin.sub(contact.normal.mul(
                    contact.penetration * obj.inverseMass / totalInvMass));
       }
        if(obj2.movable){
            obj2.origin = obj2.origin.add(contact.normal.mul(
                    contact.penetration * obj2.inverseMass / totalInvMass
            ));
        }
    }
}

package Solver;

import Objects.Physical.Physical;
import Objects.Physical.Pill;
import Objects.Physical.Polygon;
import Vector.Vec2;

import java.util.List;

import static Constants.Constants.BOUNCYNESS;

public class PillPolySolver implements Solver{

    @Override
    public void resolve(List<Physical> objects) {
        for (int i = 0; i < objects.size() ; i++){
            for (int j = i+1; j < objects.size() ; j++){
                Physical obj = objects.get(i);
                Physical obj2 = objects.get(j);
                if (obj.movable || obj2.movable){
                    Contact contact = collide(obj, obj2);
                    if (contact.penetration >= 0){
                        applyImpulse(contact);
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
        objLineEnd   = obj.origin.add(Vec2.radToVec(Math.toRadians(obj.ang)).mul(obj.length));

        obj2LineStart = obj2.origin;
        obj2LineEnd   = obj2.origin.add(Vec2.radToVec(Math.toRadians(obj2.ang)).mul(obj2.length));

        Points result = computNearestPoints(objLineStart, objLineEnd, obj2LineStart, obj2LineEnd);
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

    private void applyImpulse(Contact contact){
        Physical obj = contact.a;
        Physical obj2 = contact.b;
        //=========APPLY IMPULSE==========
        //reletive velocity
        Vec2 rv = obj.vel.sub(obj2.vel);
        //in normal
        double velAlongNormal = rv.dot(contact.normal);

        if (velAlongNormal < 0) { //is moving together
            double j = -(1 + BOUNCYNESS) * velAlongNormal;
            j /= (obj.inverseMass) + (obj2.inverseMass);
            Vec2 impulse = contact.normal.mul(j);
            obj.vel = (obj.vel.add(impulse.mul(obj.inverseMass)));
            obj2.vel = (obj2.vel.sub(impulse.mul(obj2.inverseMass)));
        }
    }
}

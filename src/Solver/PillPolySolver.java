package Solver;

import Objects.Physical.Physical;
import Objects.Physical.Pill;
import Objects.Physical.Polygon;

import java.util.List;

public class PillPolySolver implements Solver{

    @Override
    public void resolve(List<Physical> objects) {
        for (Physical obj : objects){
            for (Physical obj2 : objects){
                if (obj != obj2){
                    Contact contact = collide(obj, obj2);
                    if (contact.isTouching){
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
        //TODO
        return new Contact();
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
        //TODO
    }
}

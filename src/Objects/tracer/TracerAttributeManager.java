package Objects.tracer;

import Objects.Physical.Physical;
import Vector.Vec2;

import java.util.ArrayList;

public class TracerAttributeManager { //manages the tracer attributes in the given object.

    public static int counter = 0;

   public static void setTracerLength(Physical obj, int l) {
        obj.attributes.put("t_l", (double) l);
        obj.attributes.forEach((key, value) -> trimObjTracer(obj, key));
    } //how many points are stored. If 0, no trace will be drawn.

    public static void setTracerfrequency(Physical obj, int f) {
        obj.attributes.put("t_f", (double) f);
    } //how often points are stored. If 0, no trace will be drawn.

    static void trimObjTracer(Physical obj, String key) {
        if (key.startsWith("t_p_")) { //a tracer point, in format of t_p_[num]_x/y
            if (Integer.parseInt(key.substring(4, key.lastIndexOf('_'))) > obj.attributes.get("t_l")) { //
                obj.attributes.remove(key);
            }
        }

    }

    public static void updateTracerPoints(Physical obj){
       if (obj.attributes.containsKey("t_f") && obj.attributes.containsKey("t_l")) {
           //Standard Update
           if (counter % obj.attributes.get("t_f") == 0) {
               //if storing 5 points, move from 3->4, 2->3 1->2, 0->1, origin->0
               for (int i = (int) obj.attributes.get("t_l").doubleValue() - 2; i >= 0; i--) {
                   if (obj.attributes.containsKey("t_p_" + i + "_x")) {
                       obj.attributes.put("t_p_" + (i + 1) + "_x", obj.attributes.get("t_p_" + i + "_x"));
                       obj.attributes.put("t_p_" + (i + 1) + "_y", obj.attributes.get("t_p_" + i + "_y"));
                   }
               }
               obj.attributes.put("t_p_0_x", obj.getCenterOfMass().getX());
               obj.attributes.put("t_p_0_y", obj.getCenterOfMass().getY());
           }
       }
    }

    public static ArrayList<Vec2> getPoints(Physical obj){
        ArrayList<Vec2> points = new ArrayList<>();
        points.add(obj.getCenterOfMass());
        if (obj.attributes.containsKey("t_l")) {
            for (int i = 0; i < obj.attributes.get("t_l"); i++) {
                if (obj.attributes.containsKey("t_p_" + i + "_x")) {
                    points.add(new Vec2(obj.attributes.get("t_p_" + i + "_x"), obj.attributes.get("t_p_" + i + "_y")));
                } else {
                    break;
                }
            }
        }
        return points;
    }
}

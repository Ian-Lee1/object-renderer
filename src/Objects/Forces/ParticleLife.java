package Objects.Forces;

import Objects.Physical.Physical;
import Objects.Physical.Pill;
import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.*;

public class ParticleLife extends Force{
    //Particle life forces described by arrays.
    //arrays represented as a single array, x,y,z, -> index with maths
    //for each type, describe its attractedness to each type, at each dist level, scaled by a dist scale of that type.
    // t1[distscale, t1[1,2,3,4,5],[1,2,3,4,5], t2[1,2,3,4,5],[1,2,3,4,5], t3[1,2,3,4,5],[1,2,3,4,5] ...]
    ArrayList<Double> attractionData = new ArrayList<>();
    int types = 0;
    int distss = 0;
    double maxDist = 10.0;
    double attScale = 10.0;

    private void randomize(HashMap<String, Double> attributes){
        if (attributes.containsKey("pl_typeAmt") && attributes.containsKey("pl_distAmt")&& attributes.containsKey("pl_maxDist") && attributes.containsKey("pl_attScale")){
            attractionData.clear();
            types = (int)attributes.get("pl_typeAmt").doubleValue();
            distss = (int)attributes.get("pl_distAmt").doubleValue();
            maxDist = attributes.get("pl_maxDist");
            attScale = attributes.get("pl_attScale");

            //for each type: dist scale,  lists of attr at each distance for each type.
            int capacity = types * (1 + types * distss);

            attractionData.ensureCapacity(capacity);

            int i = 0;
            while (i < capacity){
                double value = Math.random()*2 - 1;
                attractionData.add(copySign((abs(value) + 0.1)/1.1, value));//random value from [-1, -0.1] [0.1, 1)
                i++;
            }
        }

    }

    private double getAttraction(int type1, int type2, double dist){
            int offsetTo1 = (type1 % types) * (1 + types * distss); //offset to the start of data block for type 1
            int offsetTo2 = (type2 % types) * distss;

        double distScale = abs(attractionData.get(offsetTo1 % attractionData.size()));

        dist *= distScale;

            int distIndex = min((int)floor( dist / (maxDist/distss)), distss - 1); //get which index to use based on the distance

            int offset = offsetTo1 + 1 + offsetTo2 + distIndex;


            double att1 = attractionData.get(offset % attractionData.size());
            double att2 = attractionData.get((offset+1) % attractionData.size());
            double scale = min(dist / (maxDist/distss),maxDist) - distIndex;
            double attraction = (att2 - att1) * min(scale,1) + att1;

        return attraction / pow(dist,2);
    }

    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes, double stepScape) {
        if (attributes.containsKey("pl_command")) {
            double cmd = attributes.get("pl_command");
            if (cmd != 0) {
                attributes.put("pl_command", 0.0); //reset to indicate that command has been processed
                if (cmd == 1) {
                    randomize(attributes);
                }
            }
        }
        if (obj.attributes.containsKey("type")) {
            Vec2 start = obj.origin, end = obj.origin;
            if (((Pill) obj).length > 0) {
                double radians = Math.toRadians(obj.ang);
                start = obj.origin.sub(new Vec2(
                                Math.cos(radians) * ((Pill) obj).length / 2,
                                Math.sin(radians) * ((Pill) obj).length / 2
                        )
                );
                end = obj.origin.add(
                        new Vec2(
                                Math.cos(radians) * ((Pill) obj).length / 2,
                                Math.sin(radians) * ((Pill) obj).length / 2
                        )
                );
            }
            Vec2 acc;

            if (!attractionData.isEmpty() && obj instanceof Pill) {
                for (Physical obj2 : objects) {
                    if (obj2 != obj && obj2.attributes.containsKey("type")) {

                        //for each attraction point of obj, calculate acceleration for each other attraction point of all objects.
                        Vec2 start2 = obj.origin, end2 = obj.origin;
                        if (((Pill) obj2).length > 0) {
                            double radians = Math.toRadians(obj.ang);
                            start2 = obj.origin.sub(new Vec2(
                                            Math.cos(radians) * ((Pill) obj).length / 2,
                                            Math.sin(radians) * ((Pill) obj).length / 2
                                    )
                            );
                            end2 = obj.origin.add(
                                    new Vec2(
                                            Math.cos(radians) * ((Pill) obj).length / 2,
                                            Math.sin(radians) * ((Pill) obj).length / 2
                                    )
                            );
                        }

                        //s to s1
                        acc = computeAcceleration(start, start2, (int)obj.attributes.get("type").doubleValue(), (int)obj2.attributes.get("type").doubleValue());
                        if (((Pill) obj).length > 0){
                            //s to s1 rot
                            obj.angVel += computeAngAcceleration(start, obj.origin, start2, (int)obj.attributes.get("type").doubleValue(), (int)obj2.attributes.get("type").doubleValue()) * stepScape;
                            //e to s1
                            acc = acc.add(computeAcceleration(end, start2, (int)obj.attributes.get("type").doubleValue() + 1, (int)obj2.attributes.get("type").doubleValue()));
                            obj.angVel += computeAngAcceleration(end, obj.origin, start2, (int)obj.attributes.get("type").doubleValue() + 1, (int)obj2.attributes.get("type").doubleValue());
                        }

                        if (((Pill) obj2).length > 0) {
                            //s to e2
                            acc = computeAcceleration(start, end2, (int)obj.attributes.get("type").doubleValue(), (int)obj2.attributes.get("type").doubleValue() + 1);
                            if (((Pill) obj).length > 0){
                                //s to e2 rot
                                obj.angVel += computeAngAcceleration(start, obj.origin, end2, (int)obj.attributes.get("type").doubleValue(), (int)obj2.attributes.get("type").doubleValue() + 1) * stepScape;
                                //e to e2
                                acc = acc.add(computeAcceleration(end2, end2, (int)obj.attributes.get("type").doubleValue() + 1, (int)obj2.attributes.get("type").doubleValue() + 1));
                                obj.angVel += computeAngAcceleration(end, obj.origin, end2, (int)obj.attributes.get("type").doubleValue() + 1, (int)obj2.attributes.get("type").doubleValue() + 1);
                            }

                        }
                        obj.force = obj.force.add(acc.mul(1/obj.inverseMass));
                    }
                }
            }
        }
    }
    private Vec2 computeAcceleration(Vec2 p1, Vec2 p2, int t1, int t2){
//TODO
        return null;
    }

    private double computeAngAcceleration(Vec2 p1, Vec2 origin, Vec2 p2, int t1, int t2){
//TODO
        return 0.0;
    }

    @Override
    public void draw(Graphics g, Viewport vp, HashMap<String, Double> attributes) {
        //Force has nothing to draw based on global attributes
    }
}

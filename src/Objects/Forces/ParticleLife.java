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
    private Graphics g = null;
    private Viewport vp = null;
    private final ArrayList<ParticleLifeInteractions> interactions= new ArrayList<>();

    static final int NUMRULES = 1;

    private void randomize(HashMap<String, Double> attributes, ArrayList<Physical> objects){
        if (attributes.containsKey("pl_typeAmt") && attributes.containsKey("pl_distAmt")&& attributes.containsKey("pl_maxDist") && attributes.containsKey("pl_attMax") && attributes.containsKey("pl_attMin")) {
        for (int r = 0 ; r < NUMRULES; r++){
            ParticleLifeInteractions interaction = new ParticleLifeInteractions((int)attributes.get("pl_typeAmt").doubleValue(),(int)attributes.get("pl_distAmt").doubleValue(), (int)attributes.get("pl_maxDist").doubleValue() , attributes.get("pl_attMax"), attributes.get("pl_attMin"));
            interactions.add(interaction);
        }

            for (Physical obj : objects) {
                obj.attributes.put("type", floor(Math.random() * attributes.get("pl_typeAmt")));
                obj.attributes.put("ruleset", floor(Math.random() * NUMRULES)); //clamp(floor((obj.origin.getY() + 1000) / 667), 0, 2));//
                if(obj instanceof Pill) {
                    obj.attributes.put("type", floor((((Pill) obj).size * 8 % attributes.get("pl_typeAmt"))));
                }
                obj.colorIdx = (int)obj.attributes.get("type").doubleValue();
            }
        }
    }




    private double remap (double inMin , double inMax , double outMin , double outMax , double val){
        return min(outMax, max(outMin, ((val - inMin) * (outMax - outMin) / (inMax - inMin) + outMin)));
    }

    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes, double stepScape) {
        //Individual objects do not use this function.
    }

    private HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> constructSpactialHash(ArrayList<Physical> objects, double cellSize){
        //data structure of square cells of length cellSize. each cell will have their own list of objects.
        HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash = new HashMap<>();
        for (Physical obj : objects){
            int x = (int)floor(obj.origin.getX() / cellSize);
            int y = (int)floor(obj.origin.getY() / cellSize);
            if(hash.containsKey(x)){
                HashMap<Integer, ArrayList<Physical>> yHash = hash.get(x);
                if(yHash.containsKey(y)) {
                    yHash.get(y).add(obj);
                } else{
                    ArrayList<Physical> objList = new ArrayList<>();
                    objList.add(obj);
                    yHash.put(y, objList);
                }
            }else{
                ArrayList<Physical> objList = new ArrayList<>();
                objList.add(obj);
                HashMap<Integer, ArrayList<Physical>> yHash = new HashMap<>();
                yHash.put(y, objList);
                hash.put(x, yHash);
            }
        }
        return hash;
    }

    private ArrayList<Physical> getInRange(HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash, Physical obj, double cellSize){
        //Based on the given object's location and the cellSize, return the list of objects that is in range of the given object.
        int x = (int)floor(obj.origin.getX() / cellSize);
        int y = (int)floor(obj.origin.getY() / cellSize);
        ArrayList<Physical> inRange = new ArrayList<>();
        for (int i = -1; i < 2; i++){
            if (hash.containsKey(x + i)) {
                for (int j = -1; j < 2; j++) {
                    if (hash.get(x + i).containsKey(y + j)) {
                        inRange.addAll(hash.get(x + i).get(y + j));
                    }
                }
            }
        }

        return inRange;
    }

    private void removeObj(HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash, Physical obj, double cellSize){
        //remove the given object from the hash.
        int x = (int)floor(obj.origin.getX() / cellSize);
        int y = (int)floor(obj.origin.getY() / cellSize);
            if (hash.containsKey(x)) {
                    if (hash.get(x).containsKey(y)) {
                        hash.get(x).get(y).remove(obj);
                    }
                }
            }

    public void processAll(ArrayList<Physical> objects, HashMap<String, Double> attributes, double stepScape) {
        if (attributes.containsKey("pl_command")) {
            double cmd = attributes.get("pl_command");
            if (cmd != 0) {
                attributes.put("pl_command", 0.0); //reset to indicate that command has been processed
                if (cmd == 1) {
                    randomize(attributes, objects);
                }
            }
        }

        double maxDist = 0;
        for(ParticleLifeInteractions interactions : interactions){
            if (interactions.maxDist > maxDist){
                maxDist = interactions.maxDist;
            }
        }
        HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash = constructSpactialHash(objects, maxDist);

        for (int i = 0 ; i < objects.size(); i++){
        Physical obj = objects.get(i);

        if (obj.attributes.containsKey("type") && obj.attributes.containsKey("ruleset")) {
            Vec2 start = obj.origin;
            /*if (((Pill) obj).length > 0) {
                double radians = Math.toRadians(obj.ang);
                start = obj.origin.sub(new Vec2(
                                Math.cos(radians) * ((Pill) obj).length / 2,
                                Math.sin(radians) * ((Pill) obj).length / 2
                        )
                );
                /*end = obj.origin.add(
                        new Vec2(
                                Math.cos(radians) * ((Pill) obj).length / 2,
                                Math.sin(radians) * ((Pill) obj).length / 2
                        )
                );
            }*/
            Vec2 force, force2;

            if (interactions.size() > 0){
                ArrayList<Physical> inRange = getInRange(hash, obj, maxDist);
                for (Physical obj2 : inRange) {
                    if (obj != obj2 && obj2.attributes.containsKey("ruleset") && obj2.attributes.containsKey("type")) {
                        ParticleLifeInteractions interaction = interactions.get((int)obj.attributes.get("ruleset").doubleValue());
                        if (obj.origin.sub(obj2.origin).lengthSq() < pow(interaction.maxDist,2)) {
                            //for each attraction point of obj, calculate acceleration for each other attraction point of all objects.
                            Vec2 start2 = obj2.origin;/*, end2 = obj2.origin;
                        if (((Pill) obj2).length > 0) {
                            double radians = Math.toRadians(obj2.ang);
                            start2 = obj2.origin.sub(new Vec2(
                                            Math.cos(radians) * ((Pill) obj2).length / 2,
                                            Math.sin(radians) * ((Pill) obj2).length / 2
                                    )
                            );
                            end2 = obj2.origin.add(
                                    new Vec2(
                                            Math.cos(radians) * ((Pill) obj2).length / 2,
                                            Math.sin(radians) * ((Pill) obj2).length / 2
                                    )
                            );
                        }*/

                            //s to s1
                            force = computeForce(interaction, start, start2, (int) obj.attributes.get("type").doubleValue(), (int) obj2.attributes.get("type").doubleValue()).mul(1 / obj.inverseMass);
                            force2 = computeForce(interaction, start2, start, (int) obj2.attributes.get("type").doubleValue(), (int) obj.attributes.get("type").doubleValue()).mul(1 / obj2.inverseMass);
                            obj.force = obj.force.add(force.mul(((Pill) obj).size * 0.1 + 0.9));
                            obj2.force = obj2.force.add(force2.mul(((Pill) obj2).size * 0.1 + 0.9));
                            //Proper Pill processing is disabled for now, borthersome and does not result in anything more interesting.
                        /*if (((Pill) obj).length > 0) {
                            //s to s1 rot
                            obj.angVel += computeAngAcceleration(start, obj, start2, force) * stepScape;
                            //e to s1
                            force = force.add(computeForce(end, start2, (int) obj.attributes.get("type").doubleValue() + 1, (int) obj2.attributes.get("type").doubleValue())).mul(1 / obj.inverseMass);
                            obj.force = obj.force.add(force.mul(((Pill) obj).size * 0.1 + 0.9));
                            obj.angVel += computeAngAcceleration(end, obj, start2, force) * stepScape;
                        }

                        if (((Pill) obj2).length > 0) {
                            //s to e2
                            force = computeForce(start, end2, (int) obj.attributes.get("type").doubleValue(), (int) obj2.attributes.get("type").doubleValue() + 1).mul(1 / obj.inverseMass);
                            obj.force = obj.force.add(force.mul(((Pill) obj).size * 0.1 + 0.9));
                            if (((Pill) obj).length > 0) {
                                //s to e2 rot
                                obj.angVel += computeAngAcceleration(start, obj, end2, force) * stepScape;
                                //e to e2
                                force = force.add(computeForce(end, end2, (int) obj.attributes.get("type").doubleValue() + 1, (int) obj2.attributes.get("type").doubleValue() + 1)).mul(1 / obj.inverseMass);
                                obj.force = obj.force.add(force.mul(((Pill) obj).size * 0.1 + 0.9));
                                obj.angVel += computeAngAcceleration(end, obj, end2, force) * stepScape;
                            }
                        }*/

                            //DRAWING FORCE LINES:
                            //FOR EACH OBJECT PAIR, IF abs FORCE IS ABOVE THREASHOLD (near attMax?) draw line between them. oclor based on sign.
                            //fade based on strength and distence.
                            if (g != null && vp != null && attributes.containsKey("pl_opacity") && attributes.get("pl_opacity") > 0) {
                                Graphics2D g2 = (Graphics2D) g;
                                if (vp.antialiasing) {
                                    g2.setRenderingHint(
                                            RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_ON
                                    );
                                } else {
                                    g2.setRenderingHint(
                                            RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_OFF
                                    );
                                }
                                double att1 = interaction.getAttraction((int) obj.attributes.get("type").doubleValue(), (int) obj2.attributes.get("type").doubleValue(), obj.origin.sub(obj2.origin).lengthSq());
                                double att2 = interaction.getAttraction((int) obj2.attributes.get("type").doubleValue(), (int) obj.attributes.get("type").doubleValue(), obj.origin.sub(obj2.origin).lengthSq());
                                double len = obj.origin.sub(obj2.origin).lengthSq();

                                //Conditions: strength of attractions, distance between points.
                                double attScore = remap(interaction.maxDist * 0.5, interaction.maxDist, 0, 1, max(abs(att1), abs(att2)));
                                double closeScore = remap(pow(maxDist, 2) * 0.02, pow(maxDist, 2) * 0.05, 0, 1, len);
                                double farScore = 1 - remap(pow(maxDist, 2) * 0.05, pow(maxDist, 2), 0, 1, len);
                                double score = attScore * closeScore * farScore;

                                if (score > 0.3) {
                                    Vec2 coords = vp.worldToScreen(g, obj.origin);
                                    Vec2 coords2 = vp.worldToScreen(g, obj2.origin);
                                    Stroke stroke = g2.getStroke();

                                    int r = (int) remap(-abs(att1) - abs(att2), 0, 0, 255, att1 + att2);
                                    int b = 255 - (int) remap(0, abs(att1) + abs(att2), 0, 255, att1 + att2);
                                    int a = (int) remap(pow(0.3, 1.5), 1, 0, attributes.get("pl_opacity"), pow(score, 1.5));
                                    if (a > 20) {
                                        g2.setStroke(new BasicStroke(
                                                (int) (((((Pill) obj).size / 3) * vp.worldToScreenScale(g)) * (score - 0.2)),
                                                BasicStroke.CAP_ROUND,
                                                BasicStroke.JOIN_ROUND
                                        ));
                                        g.setColor(new Color(r, 0, b, a));
                                        g2.drawLine((int) coords.getX(), (int) coords.getY(), (int) coords2.getX(), (int) coords2.getY());
                                        g2.setStroke(stroke);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
            obj.force = obj.force.add(new Vec2(0,0).sub(obj.origin).mul(0.02)).mul(1 / obj.inverseMass);
            obj.force = obj.force.add(new Vec2(obj.origin.getY(), -obj.origin.getX()).mul(0.000000001).mul(obj.origin.lengthSq())).mul(1 / obj.inverseMass);
            removeObj(hash, obj, maxDist);
        }
    }
    private Vec2 computeForce(ParticleLifeInteractions interaction, Vec2 p1, Vec2 p2, int t1, int t2){
        //a positive attraction attracts.
        return p2.sub(p1).toUnit().mul(interaction.getAttraction(t1, t2, p2.sub(p1).lengthSq()));
    }

    @Override
    public void draw(Graphics g, Viewport vp, HashMap<String, Double> attributes) {
        //Force has nothing to draw based on global attributes
        this.g = g;
        this.vp = vp;
    }
}

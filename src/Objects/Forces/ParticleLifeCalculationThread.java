package Objects.Forces;

import Objects.Physical.Physical;
import Objects.Physical.Pill;
import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.*;
import static java.lang.Math.floor;

public class ParticleLifeCalculationThread extends Thread{
    Graphics gr;
    Viewport vp;
    ArrayList<Physical> objects;
    HashMap<String, Double> attributes;
    HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash;
    double maxDist;
    double opacity;
    ArrayList<ParticleLifeInteractions> interactions;

    public int start;
    public int end;

    public ParticleLifeCalculationThread(Graphics gr, Viewport vp, ArrayList<Physical> objects, HashMap<String, Double> attributes, HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash, double maxDist, double opacity, ArrayList<ParticleLifeInteractions> interactions){
        this.gr = gr;
        this.vp = vp;
        this.objects = objects;
        this.attributes = attributes;
        this.hash = hash;
        this.maxDist = maxDist;
        this.opacity = opacity;
        this.interactions = interactions;
    }

    public void run() {
        for (int i = start; i < end ; i++){
            Physical obj = objects.get(i);
            if (obj.attributes.containsKey("type") && obj.attributes.containsKey("ruleset")) {
                Vec2 start = obj.origin;
                Vec2 force, force2;

                if (interactions.size() > 0){
                    ArrayList<Physical> inRange = getInRange(hash, obj, maxDist);
                    for (Physical obj2 : inRange) {
                        if (obj != obj2 && obj2.attributes.containsKey("ruleset") && obj2.attributes.containsKey("type")) {
                            ParticleLifeInteractions interaction = interactions.get((int)obj.attributes.get("ruleset").doubleValue());
                            if (obj.origin.sub(obj2.origin).lengthSq() < pow(interaction.maxDist,2)) {
                                //for each attraction point of obj, calculate acceleration for each other attraction point of all objects.
                                Vec2 start2 = obj2.origin;
                                //s to s1
                                force = computeForce(interaction, start, start2, (int) obj.attributes.get("type").doubleValue(), (int) obj2.attributes.get("type").doubleValue()).mul(1 / obj.inverseMass);
                                //force2 = computeForce(interaction, start2, start, (int) obj2.attributes.get("type").doubleValue(), (int) obj.attributes.get("type").doubleValue()).mul(1 / obj2.inverseMass);
                                obj.force = obj.force.add(force.mul(((Pill) obj).size * 0.1 + 0.5));
                                //obj2.force = obj2.force.add(force2.mul(((Pill) obj2).size * 0.1 + 0.5));
                                //Proper Pill processing is disabled for now, borthersome and does not result in anything more interesting.

                                //DRAWING FORCE LINES:
                                //FOR EACH OBJECT PAIR, IF abs FORCE IS ABOVE THREASHOLD (near attMax?) draw line between them. oclor based on sign.
                                //fade based on strength and distence.
                                /*if (gr != null && vp != null && opacity > 0) {
                                    Stroke stroke = new BasicStroke(
                                            (float) ((((Pill) obj).size / 3) * vp.worldToScreenScale(gr)),
                                            BasicStroke.CAP_ROUND,
                                            BasicStroke.JOIN_ROUND
                                    );
                                    Graphics2D g2 = (Graphics2D) gr;
                                    g2.setStroke(stroke);
                                    g2.setRenderingHint(
                                            RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_OFF);


                                    Vec2 coords = vp.worldToScreen(gr, obj.origin);
                                    Vec2 coords2 = vp.worldToScreen(gr, obj2.origin);

                                    //Color attColor = computeAttractionData(interactions, obj, obj2, maxDist, attributes);
                                    Color attColor = computeAttractionData(interaction.magnitudeMax, interaction.getAttraction((int)obj.attributes.get("type").doubleValue(), (int)obj2.attributes.get("type").doubleValue(), max(0.01,obj2.origin.sub(obj.origin).lengthSq())), obj.origin.sub(obj2.origin).length(), maxDist);
                                    if (attColor.getGreen() > 0) {
                                        gr.setColor(attColor);
                                        g2.drawLine((int) coords.getX(), (int) coords.getY(), (int) coords2.getX(), (int) coords2.getY());
                                    }
                                }*/
                            }
                        }
                    }
                }
            }
            obj.force = obj.force.add(new Vec2(0,0).sub(obj.origin).mul(0.02)).mul(1 / obj.inverseMass);
            obj.force = obj.force.add(new Vec2(obj.origin.getY(), -obj.origin.getX()).mul(0.000000001).mul(obj.origin.lengthSq())).mul(1 / obj.inverseMass);
            //removeObj(hash, obj, maxDist);
        }
    }


    //TODO improve / fix
    /*private Color computeAttractionData(ParticleLifeInteractions interaction, Physical obj, Physical obj2, double maxDist,  HashMap<String, Double> attributes){
        double att1 = interaction.getAttraction((int) obj.attributes.get("type").doubleValue(), (int) obj2.attributes.get("type").doubleValue(), obj.origin.sub(obj2.origin).lengthSq());
        //double att2 = interactions.get((int) obj2.attributes.get("ruleset").doubleValue()).getAttraction((int) obj2.attributes.get("type").doubleValue(), (int) obj.attributes.get("type").doubleValue(), obj.origin.sub(obj2.origin).lengthSq());
        double len = obj.origin.sub(obj2.origin).lengthSq();
        double closeness= 1 - remap(pow(maxDist, 2) * 0.05, pow(maxDist, 2), 0, 1, len);
        double farness= remap(0, pow(maxDist, 2)* 0.05, 0, 1, len);

        double limitedMax = interactions.get((int) obj.attributes.get("ruleset").doubleValue()).magnitudeMax;
        int r = (int) remap(- limitedMax, 0, 0, 255, att1);
        int b = 255 - (int) remap(0,  limitedMax, 0, 255, att1);

        //int a = (int) remap(pow(0.3, 1.7), 1, 0, opacity, pow(closeness, 1.7));
        int g = (int)remap(limitedMax * 0.7, limitedMax, 0, 255, abs(att1));
        int a = 100;//(int)(closeness * farness);
        //a = (g > 150) ? (int)opacity : 0;

        return new Color(r,g,b,a);
    }*/
    private Vec2 computeForce(ParticleLifeInteractions interaction, Vec2 p1, Vec2 p2, int t1, int t2){
        //a positive attraction attracts.
        return p2.sub(p1).toUnit().mul(interaction.getAttraction(t1, t2, max(0.01,p2.sub(p1).lengthSq())));
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



}

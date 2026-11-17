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

    double[][][][][] interactions;
    /* state change given by       +/- state same bonus: 0 if states are different, 0.1 if states are the same.
                        POSITIVES ATTRACT, NEGATIVE REPEL.
                            PH    PL    NH    NL
                        PH  0.1+1 0.2   0.4-1   0.3      //if they are the same, more likely to attract each other.
                        PL  0.2 0       0.4+1   0.4-1
                        NH  0.4  0.3+1  0.1   0.2-1        //if they are the same, more likely to attract each other.
                        NL  0.4 0.4+1   0.2-1 0.0        //if they are the same, more likely to start attracting.

                         */

    double[][] transitionProbs = {
            {0.2, 0.1, 0.5, 0.2},
            {0.3, 0.0, 0.3, 0.4},
            {0.5, 0.2, 0.2, 0.1},
            {0.3, 0.4, 0.3, 0.0},
    };
    double[][] transitionOnSame = {
            {0.3, 0.3, 0.2, 0.2},
            {0.3, 0.0, 0.4, 0.3},
            {0.3, 0.3, 0.2, 0.2},
            {0.4, 0.3, 0.3, 0.0},
    };
    final int numRules = 1;

    //type i to type j, point, dist/scale.

    int types = 0;
    int distss = 0;
    double maxDist = 10.0;
    double attMax = 10.0;
    double attMin = 0.0;

    private void randomize(HashMap<String, Double> attributes, ArrayList<Physical> objects){
        if (attributes.containsKey("pl_typeAmt") && attributes.containsKey("pl_distAmt")&& attributes.containsKey("pl_maxDist") && attributes.containsKey("pl_attMax") && attributes.containsKey("pl_attMin")) {
            types = (int) attributes.get("pl_typeAmt").doubleValue();
            distss = (int) attributes.get("pl_distAmt").doubleValue();
            maxDist = attributes.get("pl_maxDist");
            attMax = attributes.get("pl_attMax");
            attMin = attributes.get("pl_attMin");
        interactions = new double[numRules][types + 1][types + 1][distss + 1][2];
        //TODO create a new "pl interactions class that handles and compares rules. move randomisation and dist to att calculations into class.
        for (int r = 0 ; r < numRules; r++){
            for (int i = 0; i < types + 1; i++) {
                System.out.println("======================");
                for (int j = 0; j < types + 1; j++) {
                    //distances
                    System.out.println(i + " to " + j);

                    interactions[r][i][j][0][0] = Math.random() * maxDist * 0.15 + maxDist * 0.05;
                    System.out.println(interactions[r][i][j][0][0]);

                    interactions[r][i][j][distss][0] = maxDist * 0.85 + Math.random() * maxDist * 0.15;

                    double increment = (interactions[r][i][j][distss][0] - interactions[r][i][j][0][0]) / distss;
                    for (int k = 1; k < distss; k++) {
                        interactions[r][i][j][k][0] = interactions[r][i][j][k - 1][0] + increment;
                        System.out.println(interactions[r][i][j][k][0]);
                    }
                    System.out.println(interactions[r][i][j][distss][0]);
                    char[] state = {'P', 'H'};

                    if (i != j) {
                        changeState(state, false);
                        changeState(state, false);
                        changeState(state, false);
                    }

                    for (int k = 0; k < distss; k++) { //probablities.
                        //interactions[i][j][k][1] = copySign(Math.random() * (attMax - attMin) + attMin, (i == j ? Math.random() < 0.4 : Math.random() < 0.4) ? -1 : 1); //TODO set up state system instead of relying on pure randomness
                        //same types should *usually* attract when near oneanother, and repulse when further away
                        //different types should *usually* repulse when near oneanother, and attract when further away

                        //High: magnitude is near the max attraction. opposite for low.
                        interactions[r][i][j][k][1] =
                                copySign(
                                        (state[1] == 'H') ?
                                                Math.random() * (attMax - attMin) / 2 + (attMax + attMin) / 2 :
                                                Math.random() * (attMax - attMin) / 2 + attMin,
                                        (state[0] == 'N') ? -1 : 1);
                        changeState(state, i == j);
                        System.out.println(interactions[r][i][j][k][1]);
                        interactions[r][i][j][k][0] = pow(interactions[r][i][j][k][0], 2);

                    }

                }
            }
        }
          //maxDist = pow(maxDist,2);

            for (Physical obj : objects) {
                obj.attributes.put("type", floor(Math.random() * types));
                obj.attributes.put("ruleset", floor(Math.random() * numRules)); //clamp(floor((obj.origin.getY() + 1000) / 667), 0, 2));//
                if(obj instanceof Pill) {
                    obj.attributes.put("type", floor((((Pill) obj).size * 8 % types)));
                    obj.attributes.put("colorIdx", floor((((Pill) obj).size * 8 % types)));
                }
                obj.colorIdx = (int)obj.attributes.get("type").doubleValue();
            }
        }
    }

    private void changeState(char[] state, Boolean useSame){
        double[][] probs = transitionProbs;
        if(useSame){
            probs = transitionOnSame;
        }

        int ind = (state[0] == 'P') ? 0:2;
        ind += (state[1] == 'H') ? 0:1;

        double prob = Math.random();
        double comp = 0;
        for (int i = 0; i < 4; i++){
            comp += probs[ind][i];

            if (prob < comp){
                state[0] = (i < 2)?'P':'N';
                state[1] = (i % 2 == 0)?'H':'L';
                break;
            }
        }
    }

    private double getAttraction(int type1, int type2, double dist, int rule){
        //double attraction = 0;
        if (dist < interactions[rule][type1][type2][0][0]){
            return remap (0, interactions[rule][type1][type2][0][0], -5000, interactions[rule][type1][type2][0][1], dist);
        }

        for (int i = 1 ; i < distss + 1 ; i++){
            if (dist < interactions[rule][type1][type2][i][0]){
                return remap (interactions[rule][type1][type2][i-1][0], interactions[rule][type1][type2][i][0], interactions[rule][type1][type2][i-1][1], interactions[rule][type1][type2][i][1], dist);
            }
        }
            return remap (interactions[rule][type1][type2][distss][0], pow(maxDist,2), interactions[rule][type1][type2][distss][1], 0, dist);
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

    private ArrayList<Physical> getInRange(HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash, Physical obj){
        //Based on the given object's location and the maxDist, return the list of objects that is in range of the given object.
        int x = (int)floor(obj.origin.getX() / maxDist);
        int y = (int)floor(obj.origin.getY() / maxDist);
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

    private void removeObj(HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash, Physical obj){
        //remove the given object from the hash.
        int x = (int)floor(obj.origin.getX() / maxDist);
        int y = (int)floor(obj.origin.getY() / maxDist);
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

        HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash = constructSpactialHash(objects, maxDist);

        for (int i = 0 ; i < objects.size(); i++){
        Physical obj = objects.get(i);

        if (obj.attributes.containsKey("type")) {
            Vec2 start = obj.origin;//, end = obj.origin;
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

            if (interactions.length > 0){// && obj instanceof Pill) {
                ArrayList<Physical> inRange = getInRange(hash, obj);
                for (Physical obj2 : inRange) {
                    //Physical obj2 = objects.get(j);
                    if (obj != obj2 && obj2.attributes.containsKey("ruleset") && obj2.attributes.containsKey("type") && obj.origin.sub(obj2.origin).lengthSq() < pow(maxDist,2)) {

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
                        force = computeForce(start, start2, (int) obj.attributes.get("type").doubleValue(), (int) obj2.attributes.get("type").doubleValue(), (int)obj.attributes.get("ruleset").doubleValue()).mul(1 / obj.inverseMass);
                        force2 = computeForce(start2, start, (int) obj2.attributes.get("type").doubleValue(), (int) obj.attributes.get("type").doubleValue(), (int)obj2.attributes.get("ruleset").doubleValue()).mul(1 / obj2.inverseMass);
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
                        if (g!=null && vp!=null && attributes.containsKey("pl_opacity") && attributes.get("pl_opacity") > 0) {
                            Graphics2D g2 = (Graphics2D) g;

                            double att1 = getAttraction((int)obj.attributes.get("type").doubleValue(), (int)obj2.attributes.get("type").doubleValue(), obj.origin.sub(obj2.origin).lengthSq(), (int)obj.attributes.get("ruleset").doubleValue());
                            double att2 = getAttraction((int)obj2.attributes.get("type").doubleValue(), (int)obj.attributes.get("type").doubleValue(), obj.origin.sub(obj2.origin).lengthSq(), (int)obj2.attributes.get("ruleset").doubleValue());
                            double len = obj.origin.sub(obj2.origin).lengthSq();

                            //Conditions: strength of attractions, distance between points.
                            double attScore = remap(attMax*0.5, attMax, 0, 1, max(abs(att1), abs(att2)));
                            double closeScore = remap(pow(maxDist,2)*0.02, pow(maxDist,2)*0.05, 0, 1, len);
                            double farScore = 1-remap(pow(maxDist,2)*0.05, pow(maxDist,2), 0, 1, len);
                            double score = attScore * closeScore * farScore;

                            if (score > 0.3){
                                Vec2 coords = vp.worldToScreen(g, obj.origin);
                                Vec2 coords2 = vp.worldToScreen(g, obj2.origin);
                                Stroke stroke = g2.getStroke();

                                int r = (int)remap(-abs(att1) -abs(att2), 0, 0, 255, att1+att2);
                                int b = 255-(int)remap(0, abs(att1) + abs(att2), 0, 255, att1+att2);
                                int a = (int)remap(0.3, 1, 0, attributes.get("pl_opacity"), score);
                                g2.setStroke(new BasicStroke(
                                        (int) (((Pill) obj).size/3 * vp.worldToScreenScale(g) * score - 0.2),
                                        BasicStroke.CAP_ROUND,
                                        BasicStroke.JOIN_ROUND
                                ));
                                g.setColor(new Color(r, 0, b, a));
                                g2.drawLine((int)coords.getX(), (int)coords.getY(), (int)coords2.getX(), (int)coords2.getY());
                                g2.setStroke(stroke);

                            }
                        }

                    }
                }
            }
        }

            obj.force = obj.force.add(new Vec2(0,0).sub(obj.origin).mul(0.05));
            removeObj(hash, obj);
        }
    }
    private Vec2 computeForce(Vec2 p1, Vec2 p2, int t1, int t2, int rule){
        //a positive attraction attracts.
        return p2.sub(p1).toUnit().mul(getAttraction(t1, t2, p2.sub(p1).lengthSq(), rule));
    }

    private double computeAngAcceleration(Vec2 p1, Physical obj, Vec2 p2, Vec2 force){
        Vec2 rA = p1.sub(obj.origin);
        double torque = rA.cross(force);
        return torque / obj.getInertia();

    }

    @Override
    public void draw(Graphics g, Viewport vp, HashMap<String, Double> attributes) {
        //Force has nothing to draw based on global attributes
        this.g = g;
        this.vp = vp;
    }
}

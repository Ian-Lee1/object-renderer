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
    private Graphics gr = null;
    private Viewport vp = null;
    private final ArrayList<ParticleLifeInteractions> interactions= new ArrayList<>();
    private ArrayList<Physical> objects;
    private int opacity = 0;
    private int threadCount = 10;
    private ArrayList<Physical> selected = new ArrayList<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash;
    double hashSize;
    double maxDist = 0;
    double maxAtt = 0;
    static final int NUMRULES = 1;

    public void randomize(HashMap<String, Double> attributes, ArrayList<Physical> objects){
        if (attributes.containsKey("pl_typeAmt") && attributes.containsKey("pl_distAmt")&& attributes.containsKey("pl_maxDist") && attributes.containsKey("pl_attMax") && attributes.containsKey("pl_attMin")) {
        for (int r = 0 ; r < NUMRULES; r++){
            ParticleLifeInteractions interaction = new ParticleLifeInteractions((int)attributes.get("pl_typeAmt").doubleValue(),(int)attributes.get("pl_distAmt").doubleValue(), (int)attributes.get("pl_maxDist").doubleValue() , attributes.get("pl_attMax"), attributes.get("pl_attMin"));
            interactions.add(interaction);
        }

            for (Physical obj : objects) {
                obj.attributes.put("type", floor(Math.random() * attributes.get("pl_typeAmt")));
                ((Pill)obj).size = obj.attributes.get("type")*0.5+1;
                obj.attributes.put("ruleset", floor(Math.random() * NUMRULES)); //clamp(floor((obj.origin.getY() + 1000) / 667), 0, 2));//
                /*if(obj instanceof Pill) {
                    obj.attributes.put("type", floor((((Pill) obj).size * 8 % attributes.get("pl_typeAmt"))));
                }*/
                obj.colorIdx = (int)obj.attributes.get("type").doubleValue();
            }
        }
    }


    @Override
    public void applyForces(Physical obj, ArrayList<Physical> objects, HashMap<String, Double> attributes, double stepScape) {
        //Individual objects do not use this function.
    }

    private void constructSpactialHash(ArrayList<Physical> objects, double cellSize){
        //data structure of square cells of length cellSize. each cell will have their own list of objects.
        hash = new HashMap<>();
        hashSize = cellSize;
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
    }



    public void processAll(ArrayList<Physical> objects, HashMap<String, Double> attributes, double stepScape) {
        this.objects = objects;
        double maxDist = 0;
        double maxAtt = 0;
        for(ParticleLifeInteractions interactions : interactions){
            if (interactions.maxDist > maxDist){
                maxDist = interactions.maxDist;
            }
            if (interactions.magnitudeMax > maxAtt){
                maxAtt = interactions.magnitudeMax;
            }
        }
        constructSpactialHash(objects, maxDist);

        ArrayList<ParticleLifeCalculationThread> threads = new ArrayList<>(threadCount);
        for (int i = 0 ; i < threadCount; i++){
            ParticleLifeCalculationThread t= new ParticleLifeCalculationThread(gr, vp, objects, attributes, hash, maxDist, opacity, interactions);
            t.start = i * objects.size()/threadCount;
            t.end = (i+1) * objects.size()/threadCount;
            threads.add(t);
            t.start();
        }

        try {
            for (ParticleLifeCalculationThread t : threads){
                t.join();
            }
        } catch (InterruptedException e) {
            System.out.println("Thread failure: " + e.getMessage());
        }


//Drawing forces

    }

    private Color computeAttractionData(Physical obj, Physical obj2, double maxDist, double maxAtt){

        ParticleLifeInteractions interaction1 = interactions.get((int)obj.attributes.get("ruleset").doubleValue());
        ParticleLifeInteractions interaction2 = interactions.get((int)obj2.attributes.get("ruleset").doubleValue());
        double att1 = interaction1.getAttraction((int)obj.attributes.get("type").doubleValue(), (int)obj2.attributes.get("type").doubleValue(), obj.origin.sub(obj2.origin).lengthSq());
        double att2 = interaction2.getAttraction((int)obj2.attributes.get("type").doubleValue(), (int)obj.attributes.get("type").doubleValue(), obj.origin.sub(obj2.origin).lengthSq());
        double dist = obj.origin.sub(obj2.origin).length();
        double closeness = remap(0, maxDist * 0.06, 0, 1, dist);
        double farness = 1 - remap(maxDist * 0.04, maxDist, 0, 1, dist);

        int r = (int) remap(-maxAtt, 0, 0, 255, min(att1,att2));
        int g = (int)remap(0, maxAtt, 0, 255, abs(att1) + abs(att2)); //(int)remap(0.65, 1, 150, 255, (pow((closeness*farness),1)));
        int b = 255 - (int) remap(0, maxAtt, 0, 255, max(att1, att2));
        int a = (int)(pow((closeness * farness),3)*opacity);
        g = g > 70? g : 0;
        //a = g > 200? g : a;
return new Color(r,g,b,a);
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

    private double remap (double inMin , double inMax , double outMin , double outMax , double val){
        return min(outMax, max(outMin, ((val - inMin) * (outMax - outMin) / (inMax - inMin) + outMin)));
    }

    @Override
    public void draw(Graphics g, Viewport vp, HashMap<String, Double> attributes) {
        //Force has nothing to draw based on global attributes
        this.gr = g;
        this.vp = vp;

        if(objects == null){
            return;
        }
        HashMap<Vec2, ArrayList<Vec2>> screenHash = HashMap.newHashMap(128);

        for(ParticleLifeInteractions interactions : interactions){
            if (interactions.maxDist > maxDist){
                maxDist = interactions.maxDist;
            }
            if (interactions.magnitudeMax > maxAtt){
                maxAtt = interactions.magnitudeMax;
            }
        }
        constructSpactialHash(objects, maxDist);
        if (vp != null && gr !=null && opacity > 0 && hash != null) {
            Stroke stroke = new BasicStroke(
                    (float) (0.1 * vp.worldToScreenScale(gr)),
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            );
            Graphics2D g2d = (Graphics2D) gr;
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setStroke(stroke);
            //color line


            boolean drawLine;
            //get a list of all visible points
            ArrayList<Physical> visible = new ArrayList<>();
            Pill cameraPos = new Pill(vp.origin, 0, 0.1, 0.1, 0);
            objects.add(cameraPos);
            for (Physical obj : objects) {

                if (vp.contains(obj.origin, gr)) {
                    visible.add(obj);/*
                     */
                }
            }
            objects.remove(cameraPos);


            //per first point in all vpoints:
            while(visible.size() > 0) {
                Physical obj = visible.getFirst();
                //use spactial hash to get all points/nearby points in that area of that screen.
                ArrayList<Physical> section = getInRange(hash, obj, maxDist);
                int cellX = (int)floor(obj.origin.getX() / maxDist);
                int cellY = (int)floor(obj.origin.getY() / maxDist);

                //per first points in that area of screen (spactial hash list)
                while(section.size() > 0) {
                    Physical sectionObj = section.getFirst();
                    Vec2 coords = vp.worldToScreen(gr, sectionObj.origin);
                    Vec2 snapPixles = new Vec2(15, 15);
                    Vec2 snappedCoords = (coords.add(snapPixles.mul(0.5)).mulSep(1/15.0, 1/15.0));
                    snappedCoords = new Vec2(floor(snappedCoords.getX()), floor(snappedCoords.getY())).mulSep(snapPixles.getX(), snapPixles.getY());


                    //combute pair attraction data with each point and nearby points
                    for(Physical sectionObj2 : section) {
                        if (sectionObj != sectionObj2) {
                            Vec2 coords2 = vp.worldToScreen(gr, sectionObj2.origin);
                            double length = sectionObj.origin.sub(sectionObj2.origin).lengthSq();
                            Vec2 snappedCoords2 = (coords2.add(snapPixles.mul(0.5)).mulSep(1/15.0, 1/15.0));
                            snappedCoords2 = new Vec2(floor(snappedCoords2.getX()), floor(snappedCoords2.getY())).mulSep(snapPixles.getX(), snapPixles.getY());


                            if(length < pow(maxDist,2)) {


                                drawLine = false;
                                if (!screenHash.containsKey(snappedCoords)) {
                                    ArrayList<Vec2> ends = new ArrayList<>(1);
                                    ends.add(snappedCoords2);
                                    screenHash.put(snappedCoords, ends);
                                    drawLine = true;
                                } else if (!screenHash.get(snappedCoords).contains(snappedCoords2)) {
                                    screenHash.get(snappedCoords).add(snappedCoords2);
                                    drawLine = true;
                                }

                                if(drawLine){
                                    //GET ATT COLOR
                                    Color attData = computeAttractionData(sectionObj, sectionObj2, maxDist, maxAtt);
                                    if(attData.getAlpha() > opacity * 0.2) {
                                        gr.setColor(attData);
                                        g2d.drawLine((int) coords.getX(), (int) coords.getY(), (int) coords2.getX(), (int) coords2.getY());
                                    }
                                }
                            }
                        }
                    }

                    if (selected.contains(sectionObj)){
                        g.setColor(new Color(50,255,40));
                        double scale = vp.worldToScreenScale(g);
                        double size = ((Pill)sectionObj).size * scale;
                        g.fillOval((int)(coords.getX() - size - 6), (int)(coords.getY() - size - 6), (int)(size*2 + 12),(int)(size*2 + 12));
                    }


                    //remove all points in that hash cell from the spactial hash and visible points list. all lines connected to it must've been drawn already.
                    int currCellX = (int)floor(sectionObj.origin.getX() / maxDist);
                    int currCellY = (int)floor(sectionObj.origin.getY() / maxDist);
                    if(currCellX == cellX && currCellY == cellY){
                        visible.remove(sectionObj);
                        removeObj(hash, sectionObj, maxDist);
                    }
                    section.remove(sectionObj);
                }
                visible.remove(obj);
            }
        }

    }

    private Physical fromHash(ArrayList<Physical> objects, double hash){
        for(Physical obj : objects){
            if (obj instanceof Pill && ((Pill) obj).attributes.containsKey("hash")){
                if(((Pill) obj).attributes.get("hash") == hash){
                    return obj;
                }
            }
        }
        return null;
    }

    public void command(String command){
        try{
            int temp;
        String[] cmd = command.split(" ");
            Physical obj;
            switch(cmd[0]){
            case ("opacity"):
                opacity = Integer.parseInt(cmd[1]);
                break;
            case ("select"):
                temp = opacity;
                opacity = 255;
                //select the structure group based on the given hash
                constructSpactialHash(objects, maxDist);
                obj = fromHash(objects, Double.parseDouble(cmd[1]));
                recursiveSelect(obj, 25);
                opacity = temp;
                break;
            case ("add"):
                //add the particle with the given hash
                obj = fromHash(objects, Double.parseDouble(cmd[1]));
                if (obj != null && !selected.contains(obj)) {
                    selected.add(obj);
                }
                break;
            case ("unselect"):
                temp = opacity;
                opacity = 255;
                constructSpactialHash(objects, maxDist);
                obj = fromHash(objects, Double.parseDouble(cmd[1]));
                recursiveUnSelect(obj, 25);
                opacity = temp;
                break;
            case("remove"):
                //remove the given hash.
                selected.remove(fromHash(objects, Double.parseDouble(cmd[1])));
                break;
            case("reset_selection"):
                //clear all selected items
                selected.clear();
                break;
            case("save"):
                //save a specific rule, or the selected strucutre(s)
                break;
            case("load"):
                //load the given file into the simulation.
        }
    } catch (Exception e){
            System.out.println("Particle Life command formatting error. "+ e.getMessage());
        }
        }


        public void recursiveSelect(Physical obj, int count){
        if (count == 0){
            return;
        }
        selected.add(obj);
        ArrayList<Physical> nearby = getInRange(hash, obj, hashSize);
        for(Physical obj2 : nearby){
            if(!selected.contains(obj2)){
                Color data = computeAttractionData(obj, obj2, maxDist, maxAtt);
                if (data.getGreen() > 130 && data.getAlpha() > 50){
                    recursiveSelect(obj2, count -1);
                }
            }
        }
        }

    public void recursiveUnSelect(Physical obj, int count){
        if (count == 0){
            return;
        }
        selected.remove(obj);
        ArrayList<Physical> nearby = getInRange(hash, obj, hashSize);
        for(Physical obj2 : nearby){
            if(selected.contains(obj2)){
                Color data = computeAttractionData(obj, obj2, maxDist, maxAtt);
                if (data.getGreen() > 130 && data.getAlpha() > 50){
                    recursiveUnSelect(obj2, count -1);
                }
            }
        }
    }
}

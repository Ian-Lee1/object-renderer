package Objects.Forces;

import Objects.Physical.Physical;
import Objects.Physical.Pill;
import Objects.Viewport;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.*;

public class ParticleLife extends Force{
    private Graphics gr = null;
    private Viewport vp = null;
    private final ArrayList<ParticleLifeInteractions> interactions= new ArrayList<>();

    private int opacity = 0;
    private int threadCount = 10;

    static final int NUMRULES = 2;

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



    public void processAll(ArrayList<Physical> objects, HashMap<String, Double> attributes, double stepScape) {

        double maxDist = 0;
        for(ParticleLifeInteractions interactions : interactions){
            if (interactions.maxDist > maxDist){
                maxDist = interactions.maxDist;
            }
        }
        HashMap<Integer, HashMap<Integer, ArrayList<Physical>>> hash = constructSpactialHash(objects, maxDist);

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


    }





    @Override
    public void draw(Graphics g, Viewport vp, HashMap<String, Double> attributes) {
        //Force has nothing to draw based on global attributes
        this.gr = g;
        this.vp = vp;
    }

    public void command(String command){
        try{
        String[] cmd = command.split(" ");

        switch(cmd[0]){
            case ("opacity"):
                opacity = Integer.parseInt(cmd[1]);
        }
    } catch (Exception e){
            System.out.println("Particle Life command formatting error. "+ e.getMessage());
        }
        }
}

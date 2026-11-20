package Objects.Forces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static java.lang.Math.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class ParticleLifeInteractions {
    //A class representing a set of interaction rules for particle life.
    //Stored as a 4D array of doubles
    // [from type][type][point #][distence/magnitude]

    public int types;
    public int points;
    public double maxDist;
    public double magnitudeMax;
    public double magnitudeMin;
    public double[][][][] interactions;

    //used by randomize to determine the magnitude of attraction points.
    /* state change given by       +/- state same bonus: 0 if states are different, 0.1 if states are the same.
                        POSITIVES ATTRACT, NEGATIVE REPEL.
                            PH    PL    NH    NL
                        PH  0.1+1 0.2   0.4-1   0.3      //if they are the same, more likely to attract each other.
                        PL  0.2 0       0.4+1   0.4-1
                        NH  0.4  0.3+1  0.1   0.2-1        //if they are the same, more likely to attract each other.
                        NL  0.4 0.4+1   0.2-1 0.0        //if they are the same, more likely to start attracting.

{
            {0.1,0.2,0.5,0.2},
            {0.1,0.0,0.6,0.1},
            {0.5,0.2,0.1,0.2},
            {0.8,0.1,0.1,0.0},
    };

                         */
    public double[][] transitionProbs = {
            {0.25,0.25,0.25,0.25},
            {0.25,0.25,0.25,0.25},
            {0.25,0.25,0.25,0.25},
            {0.25,0.25,0.25,0.25},
    };
    public double[][] transitionOnSame = {
            {0.6, 0.0, 0.2, 0.2},
            {0.0, 0.0, 0.7, 0.3},
            {0.3, 0.3, 0.2, 0.2},
            {0.7, 0.3, 0.0, 0.0},
    };

    public ParticleLifeInteractions(){}

    public ParticleLifeInteractions(int types, int points, double maxDist, double max, double min){
        this.types = types;
        this.points = points;
        this.maxDist = maxDist;
        magnitudeMax = max;
        magnitudeMin = min;
        randomize();
    }
    public ParticleLifeInteractions(String filepath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filepath);
        ParticleLifeInteractions temp = mapper.readValue(file, ParticleLifeInteractions.class);
        this.maxDist = temp.maxDist;
        this.types = temp.types;
        this.magnitudeMax = temp.magnitudeMax;
        this.magnitudeMin = temp.magnitudeMin;
        this.points = temp.points;
        this.interactions = temp.interactions;
        this.transitionProbs = temp.transitionProbs;
        this.transitionOnSame = temp.transitionOnSame;
    }

    public void randomize() {
        interactions = new double[types][types][points][2];
        for (int i = 0; i < types; i++)
            for (int j = 0; j < types; j++) {
                //distances
                interactions[i][j][0][0] = Math.random() * maxDist * 0.15 + maxDist * 0.05;

                interactions[i][j][points - 1][0] = maxDist * 0.85 + Math.random() * maxDist * 0.15;

                double increment = (interactions[i][j][points - 1][0] - interactions[i][j][0][0]) / points;
                for (int k = 1; k < points - 1; k++) {
                    interactions[i][j][k][0] = interactions[i][j][k - 1][0] + increment;
                }
                char[] state = {'P', 'H'};

                if (i != j) {
                    changeState(state, false);
                    changeState(state, false);
                    changeState(state, false);
                }

                for (int k = 0; k < points; k++) { //probablities.
                    interactions[i][j][k][1] =
                            copySign(
                                    (state[1] == 'H') ?
                                            Math.random() * (magnitudeMax - magnitudeMin) / 2 + (magnitudeMax + magnitudeMin) / 2 :
                                            Math.random() * (magnitudeMax - magnitudeMin) / 2 + magnitudeMin,
                                    (state[0] == 'N') ? -1 : 1);
                    changeState(state, i == j);

                    interactions[i][j][k][0] = pow(interactions[i][j][k][0], 2);

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


    public double getAttraction(int type1, int type2, double dist){
        type1 = type1 % types;
        type2 = type2 % types;
        if (dist < interactions[type1][type2][0][0]){
            return remap (0, interactions[type1][type2][0][0], -magnitudeMax, interactions[type1][type2][0][1], dist);
        }

        for (int i = 1 ; i < points - 1; i++){
            if (dist < interactions[type1][type2][i][0]){
                return remap (interactions[type1][type2][i-1][0], interactions[type1][type2][i][0], interactions[type1][type2][i-1][1], interactions[type1][type2][i][1], dist);
            }
        }
        return remap (interactions[type1][type2][points - 1][0], pow(maxDist,2), interactions[type1][type2][points - 1][1], 0, dist);
    }


    private double remap (double inMin , double inMax , double outMin , double outMax , double val){
        return min(outMax, max(outMin, ((val - inMin) * (outMax - outMin) / (inMax - inMin) + outMin)));
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof ParticleLifeInteractions){
            ParticleLifeInteractions other = (ParticleLifeInteractions) o;
            if(other.magnitudeMin == magnitudeMin && other.magnitudeMax == magnitudeMax && other.types == types && other.maxDist == maxDist && other.points == points){
                return Arrays.deepEquals(interactions, other.interactions);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        // 4. Always override hashCode() when overriding equals()
        return Objects.hash(types, points, maxDist, magnitudeMax, magnitudeMin, Arrays.deepHashCode(interactions));
    }


    public void toJson(String folderpath, int ruleNum) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        File folder = new File(folderpath);
        File file = new File(folderpath + "/r_" + ruleNum + ".json");
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }
        mapper.writeValue(file, this);
    }


    public void fromJson(String filepath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filepath);
        ParticleLifeInteractions temp = mapper.readValue(file, ParticleLifeInteractions.class);
        this.maxDist = temp.maxDist;
        this.types = temp.types;
        this.magnitudeMax = temp.magnitudeMax;
        this.magnitudeMin = temp.magnitudeMin;
        this.points = temp.points;
        this.interactions = temp.interactions;
        this.transitionProbs = temp.transitionProbs;
        this.transitionOnSame = temp.transitionOnSame;
    }
    
}

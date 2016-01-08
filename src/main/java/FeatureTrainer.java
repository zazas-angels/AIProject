import java.util.ArrayList;

/**
 * Created by guri on 1/8/16.
 */
public class FeatureTrainer {


    private  Action scissores;
    private  Action net;
    private  Action well;

    public FeatureTrainer(){

    }
    public void train(ArrayList<Integer> feature,HandFigureTypes real){
        Action realAction = realActionByType(real);
        int bestScore =  scissores.score(feature);
        Action bestAction = scissores;
        int netScore = net.score(feature);
        int wellScore = well.score(feature);
        if(bestScore<=netScore){
            bestScore=netScore;
            bestAction=net;
        }
        if(bestScore<=wellScore){
            bestScore=wellScore;
            bestAction=well;
        }
        if(bestAction!=realAction){
            realAction.increaseWeights(feature);
            bestAction.decreaseWeights(feature);
        }


    }

    private Action realActionByType(HandFigureTypes real) {
        switch(real){
            case SCISSORS:
                return scissores;

            case NET:
                return net;

            case WELL:
                return well;
        }
        return null;
    }

}

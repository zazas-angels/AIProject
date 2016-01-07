import java.util.ArrayList;

/**
 * Created by guri on 1/8/16.
 */
public class Action {

    private  ArrayList<Integer> weights;
    public Action( ArrayList<Integer> weights){
        this.weights=weights;
    }
    int score(ArrayList<Integer> feature){
        int res=0;
        for(int i=0 ; i< weights.size(); i++){
           res+= weights.get(i)*feature.get(i);
        }
        return res;
    }
    void increaseWeights(ArrayList<Integer> feature){
        for(int i=0 ; i< weights.size(); i++){
            weights.set(i,weights.get(i) + feature.get(i));
        }
    }
    void decreaseWeights(ArrayList<Integer> feature){
        for(int i=0 ; i< weights.size(); i++){
            weights.set(i,weights.get(i) - feature.get(i));
        }
    }
}

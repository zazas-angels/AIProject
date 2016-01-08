import java.io.FileNotFoundException;
import java.util.ArrayList;

public class FeatureTrainer {


    public static final int FEATURES_COUNT = 9;
    private static final int SECONDS_TO_TRAIN = 300;
    private Action scissors;
    private Action net;
    private Action well;

    public FeatureTrainer() {
        scissors = new Action(FileUtils.getWeights(HandFigureTypes.SCISSORS));
        net = new Action(FileUtils.getWeights(HandFigureTypes.NET));
        well = new Action(FileUtils.getWeights(HandFigureTypes.WELL));

    }

    public void train(ArrayList<Integer> feature, HandFigureTypes real) {
        Action realAction = realActionByType(real);
        int bestScore = scissors.score(feature);
        Action bestAction = scissors;
        int netScore = net.score(feature);
        int wellScore = well.score(feature);
        if (bestScore <= netScore) {
            bestScore = netScore;
            bestAction = net;
        }
        if (bestScore <= wellScore) {
            bestScore = wellScore;
            bestAction = well;
        }
        if (bestAction != realAction) {
            realAction.increaseWeights(feature);
            bestAction.decreaseWeights(feature);
        }
    }

    public HandFigureTypes getResult(ArrayList<Integer> feature) {
        int bestScore = scissors.score(feature);
        Action bestAction = scissors;
//        System.out.println("scissors = " + bestScore);
        int netScore = net.score(feature);
        int wellScore = well.score(feature);
//        System.out.println("net = " + netScore);
//        System.out.println("well = " + wellScore);
        if (bestScore <= netScore) {
            bestScore = netScore;
            bestAction = net;
        }
        if (bestScore <= wellScore) {
            bestAction = well;
        }
        if (bestAction == net)
            return HandFigureTypes.NET;
        if (bestAction == well)
            return HandFigureTypes.WELL;
        if (bestAction == scissors)
            return HandFigureTypes.SCISSORS;

        return null;
    }

    private Action realActionByType(HandFigureTypes real) {
        switch (real) {
            case SCISSORS:
                return scissors;
            case NET:
                return net;
            case WELL:
                return well;
        }
        return null;
    }

    public void doneTraining() throws FileNotFoundException {
        FileUtils.setWeights(HandFigureTypes.SCISSORS, scissors.getWeights());
        FileUtils.setWeights(HandFigureTypes.NET, net.getWeights());
        FileUtils.setWeights(HandFigureTypes.WELL, well.getWeights());
    }

    public void trainWeights() {
        ArrayList<ArrayList<Integer>> netFeaturesList = TrainingData.getTrainingData(HandFigureTypes.NET);
        ArrayList<ArrayList<Integer>> wellFeaturesList = TrainingData.getTrainingData(HandFigureTypes.WELL);
        ArrayList<ArrayList<Integer>> scissorsFeaturesList = TrainingData.getTrainingData(HandFigureTypes.SCISSORS);
        long start = System.currentTimeMillis();
        long end = start + SECONDS_TO_TRAIN*1000; // 60 seconds * 1000 ms/sec
        while (System.currentTimeMillis() < end) {
            for (ArrayList<Integer> arrayList : netFeaturesList) {
                train(arrayList, HandFigureTypes.NET);
            }
            for (ArrayList<Integer> arrayList : wellFeaturesList) {
                train(arrayList, HandFigureTypes.WELL);
            }
            for (ArrayList<Integer> arrayList : scissorsFeaturesList) {
                train(arrayList, HandFigureTypes.SCISSORS);
            }
        }

        try {
            doneTraining();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FeatureTrainer trainer = new FeatureTrainer();
        trainer.trainWeights();
    }
}

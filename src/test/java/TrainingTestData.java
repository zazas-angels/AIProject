import java.util.ArrayList;

public class TrainingTestData {


    public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> lists = TrainingData.getTrainingData(HandFigureTypes.NET);
        for (ArrayList<Integer> list : lists) {
            System.out.println(list);
        }
    }
}

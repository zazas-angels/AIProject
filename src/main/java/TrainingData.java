
import com.leapmotion.leap.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class TrainingData {

    private static HandFigureTypes TYPE = HandFigureTypes.NET;

    public static void main(String[] args) {
        TrainingListener listener = new TrainingListener();
        Controller controller = new Controller();
        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }


    public static ArrayList<ArrayList<Integer>> getTrainingData(HandFigureTypes type){
        Scanner scanner = FileUtils.getReaderFromTrainingDate(type);
        if(scanner == null)
            return null;

        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        while (scanner.hasNext()){
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (int i = 0; i < FeatureTrainer.FEATURES_COUNT; i++) {
                arrayList.add(scanner.nextInt());
            }
            result.add(arrayList);
            scanner.nextLine();
        }
        return result;
    }


    private static class TrainingListener extends Listener{
        private PrintWriter writer;
        public void onInit(Controller controller) {
            System.out.println("Initialized");
            try {
                writer = FileUtils.getWriterInTrainingFile(TYPE);
            } catch (FileNotFoundException e) {
                System.out.println("can't open file");
            }
        }

        public void onConnect(Controller controller) {
            System.out.println("Connected");
            controller.enableGesture(Gesture.Type.TYPE_SWIPE);
            controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
            controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
            controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
        }

        public void onDisconnect(Controller controller) {
        }

        public void onExit(Controller controller) {
            writer.close();
            System.out.println("Exited");
        }


        public boolean fillFeatures(Frame frame, ArrayList<Integer> features){
            if( frame.hands().count()!=1)
                return false;
            features.add(boolToInt(FeatureEvaluator.thumbAndIndexFingersMakeCircle(frame)));
            features.add(boolToInt(FeatureEvaluator.thumbMakesCircleWithRingOrPinky(frame)));

            for(Hand hand : frame.hands()) {
                if (hand.fingers().count()!=5){
                    return false;
                }
                features.add(FeatureEvaluator.countCroachedFingers(hand,features));
            }
            return true;
        }

        private Integer boolToInt(boolean b) {
            if(b)
                return 1;
            return 0;
        }

        public void onFrame(Controller controller) {
            // Get the most recent frame and report some basic information
            Frame frame = controller.frame();
            ArrayList<Integer> features = new ArrayList<Integer>();
            if(fillFeatures(frame,features)){
                features.add(0, 1);
                System.out.println(features);
                writeInFile(features);
            }
        }

        private void writeInFile(ArrayList<Integer> features) {
            for (Integer integer : features) {
                writer.print(integer);
                writer.print(" ");
            }
            writer.print("\n");
        }
    }
}

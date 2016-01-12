/******************************************************************************
 * \
 * Copyright (C) 2012-2013 Leap Motion, Inc. All rights reserved.               *
 * Leap Motion proprietary and confidential. Not for distribution.              *
 * Use subject to the terms of the Leap Motion SDK Agreement available at       *
 * https://developer.leapmotion.com/sdk_agreement, or another agreement         *
 * between Leap Motion and you, your company or other organization.             *
 * \
 ******************************************************************************/

import com.leapmotion.leap.*;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

class SampleListener extends Listener {
    private FeatureTrainer featureTrainer;
    private int countNet = 0;
    private int countWell = 0;
    private int countScissors = 0;
    private boolean countingEnabled = false;

    public void onInit(Controller controller) {
        System.out.println("Initialized");
        featureTrainer = new FeatureTrainer();
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
        try {
            featureTrainer.doneTraining();
            System.out.println("updated weights");
        } catch (FileNotFoundException e) {
            System.out.println("Can't update weights");
        }
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }


    public boolean fillFeatures(Frame frame, ArrayList<Integer> features) {
        if (frame.hands().count() != 1)
            return false;
        features.add(boolToInt(FeatureEvaluator.thumbAndIndexFingersMakeCircle(frame)));
        features.add(boolToInt(FeatureEvaluator.thumbMakesCircleWithRingOrPinky(frame)));

        for (Hand hand : frame.hands()) {
            if (hand.fingers().count() != 5) {
                return false;
            }
            features.add(FeatureEvaluator.countCroachedFingers(hand, features));
        }
        return true;
    }

    private Integer boolToInt(boolean b) {
        if (b)
            return 1;
        return 0;
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        ArrayList<Integer> features = new ArrayList<Integer>();
        if (fillFeatures(frame, features)) {
//            System.out.println(features);
            features.add(0, 1);
//            featureTrainer.train(features, HandFigureTypes.WELL);
            HandFigureTypes type = featureTrainer.getResult(features);
            if (countingEnabled) {
                switch (type) {
                    case NET:
                        countNet++;
                        break;
                    case WELL:
                        countWell++;
                        break;
                    case SCISSORS:
                        countScissors++;
                        break;
                }
            }
//            System.out.println(type);
        }


    }

    public void resetCurrentFigures() {
        countNet = 0;
        countWell = 0;
        countScissors = 0;
    }

    public HandFigureTypes getCurrentFigure() {
        HandFigureTypes type = HandFigureTypes.NET;
        int best = countNet;
        if (best < countWell) {
            type = HandFigureTypes.WELL;
            best = countWell;
        }
        if (best < countScissors) {
            type = HandFigureTypes.SCISSORS;
        }
        return type;
    }

    public void disableCounting() {
        countingEnabled = false;
    }

    public void enableCounting() {
        countingEnabled = true;
    }
}

class Sample {
    private static final int SECONDS_TO_CHOOSE_FIGURE = 2;
    private static final int WAIT_TIME = 2;
    private static final int TIME_TO_WAIT_BEFORE_MOVE = 1;

    public static void main(String[] args) throws SerialPortException {
        GUITest guiTest = new GUITest();
        SampleListener listener = new SampleListener();
        Controller controller = new Controller();

        SerialPort serialPort = new SerialPort("COM3");
        try {
            serialPort.openPort();//Open serial port
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        while (true) {
            try {
                Thread.sleep(WAIT_TIME * 1000);
                guiTest.prepareForMove();
                Thread.sleep(WAIT_TIME * 1000);
                System.out.println("your move.. choose your move");
                guiTest.takeYourHandIn();
                System.out.println("take hand in");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listener.resetCurrentFigures();
            listener.enableCounting();
            try {
                Thread.sleep(SECONDS_TO_CHOOSE_FIGURE * 1000);
                guiTest.takeYourHandOut();
                System.out.println("take hand out");
                listener.disableCounting();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            guiTest.chosen(listener.getCurrentFigure());
            System.out.println("you have chosen  " + listener.getCurrentFigure());
            switch (listener.getCurrentFigure()){
                case NET:
                    serialPort.writeBytes("2\n".getBytes());
                    break;
                case WELL:
                    serialPort.writeBytes("5\n".getBytes());
                    break;
                case SCISSORS:
                    serialPort.writeBytes("8\n".getBytes());
                    break;
            }


            try {
                Thread.sleep(WAIT_TIME * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // Keep this process running until Enter is pressed
//        System.out.println("Press Enter to quit...");
//        try {
//            System.in.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Remove the sample listener when done
//        controller.removeListener(listener);
    }


}

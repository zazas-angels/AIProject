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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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

    public boolean detectedFigure() {
        return (countScissors + countWell + countNet) > 40;
    }
}

class Sample {
    private static final int SECONDS_TO_CHOOSE_FIGURE = 2;
    private static final int WAIT_TIME = 2;
    private static final int TIME_TO_WAIT_BEFORE_MOVE = 1;
    private static int netQuantity = 1;
    private static int scissorsQuantity = 1;
    private static int wellQuantity = 1;

    public static HandFigureTypes oppositeFigure(HandFigureTypes figureType) {
        switch (figureType) {
            case NET:
                return HandFigureTypes.SCISSORS;
            case WELL:
                return HandFigureTypes.NET;
            case SCISSORS:
                return HandFigureTypes.WELL;
        }
        return null;
    }

    static Random random = new Random();

    public static HandFigureTypes getOppositeFigure() {
        double sum = netQuantity + scissorsQuantity + wellQuantity;
        double netPercentage = netQuantity / sum;
        double scissorsPercentage = scissorsQuantity / sum;
        double rand = random.nextDouble();

        if (rand < netPercentage) {
            return oppositeFigure(HandFigureTypes.NET);
        } else if (rand < netPercentage + scissorsPercentage) {
            return oppositeFigure(HandFigureTypes.SCISSORS);
        } else return oppositeFigure(HandFigureTypes.WELL);
    }


    public static void main(String[] args) throws SerialPortException, FileNotFoundException {
        GUI gui = new GUI();
        SampleListener listener = new SampleListener();
        Controller controller = new Controller();
        readQuantities();
        getOppositeFigure();
        SerialPort serialPort = new SerialPort("COM3");
        try {
            serialPort.openPort();//Open serial port
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
        } catch (Exception e) {
//            e.printStackTrace();
        }

        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        while (true) {
            System.out.println(netQuantity);
            System.out.println(scissorsQuantity);
            System.out.println(wellQuantity);
            try {
                Thread.sleep(WAIT_TIME * 1000);
                gui.prepareForMove();
                Thread.sleep(TIME_TO_WAIT_BEFORE_MOVE * 1000);
                System.out.println("your move.. choose your move");
                gui.takeYourHandIn();
                System.out.println("take hand in");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listener.resetCurrentFigures();
            listener.enableCounting();
            HandFigureTypes type = null;
            try {
                Thread.sleep((SECONDS_TO_CHOOSE_FIGURE -1) * 1000);
                type = getOppositeFigure();
                switch(type) {
                    case NET:
                        netQuantity++;
                        serialPort.writeBytes("2\n".getBytes());
                        break;
                    case WELL:
                        wellQuantity++;
                        serialPort.writeBytes("5\n".getBytes());
                        break;
                    case SCISSORS:
                        scissorsQuantity++;
                        serialPort.writeBytes("8\n".getBytes());
                        break;
                }
                Thread.sleep(1000);
                gui.takeYourHandOut();
                System.out.println("take hand out");
                listener.disableCounting();
//                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (listener.detectedFigure()) {
                gui.chosen(listener.getCurrentFigure(), type);
                updateQuantities();
            } else {
                gui.noneChosen();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // Remove the sample listener when done
//        controller.removeListener(listener);
    }

    private static void updateQuantities() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new File("D:\\dev\\AIProject\\src\\main\\resources\\figureTypesQuantity"));
        writer.print(netQuantity);
        writer.print(" ");
        writer.print(scissorsQuantity);
        writer.print(" ");
        writer.print(wellQuantity);
        writer.print(" ");
        writer.close();

    }

    private static void readQuantities() {
        try {
            Scanner scan = new Scanner(new File("D:\\dev\\AIProject\\src\\main\\resources\\figureTypesQuantity"));
            netQuantity = scan.nextInt();
            scissorsQuantity = scan.nextInt();
            wellQuantity = scan.nextInt();
            if (netQuantity == 0)
                netQuantity++;
            if (scissorsQuantity == 0)
                scissorsQuantity++;
            if (wellQuantity == 0)
                wellQuantity++;
        } catch (Exception e) {
        }
    }


}

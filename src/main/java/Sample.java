/******************************************************************************\
* Copyright (C) 2012-2013 Leap Motion, Inc. All rights reserved.               *
* Leap Motion proprietary and confidential. Not for distribution.              *
* Use subject to the terms of the Leap Motion SDK Agreement available at       *
* https://developer.leapmotion.com/sdk_agreement, or another agreement         *
* between Leap Motion and you, your company or other organization.             *
\******************************************************************************/

import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;

class SampleListener extends Listener {
    public void onInit(Controller controller) {
        System.out.println("Initialized");
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
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }


    public boolean fillFeatures(Frame frame, ArrayList<Integer> features){
        if( frame.hands().count()!=1)
            return false;
        features.add(boolToInt( FeatureEvaluator.thumbAndIndexFingersMakeCircle(frame)));
        features.add(boolToInt( FeatureEvaluator.thumbMakesCircleWithRingOrPinky(frame)));

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
            System.out.println(features);
        }


    }

}

class Sample {
    public static void main(String[] args) {
        SampleListener listener = new SampleListener();
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


}

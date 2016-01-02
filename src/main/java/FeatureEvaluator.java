import com.leapmotion.leap.*;

public class FeatureEvaluator {



    public static boolean thumbAndIndexFingersMakeCircle(Frame frame){
        if(frame == null) return false;
        Finger thumbFinger = getFinger(frame, Finger.Type.TYPE_THUMB);
        Finger indexFinger = getFinger(frame, Finger.Type.TYPE_INDEX);
        return doTwoFingersMakeCircle(thumbFinger, indexFinger);
    }

    private static Finger getFinger(Frame frame, Finger.Type type) {
        if(frame == null || frame.hands().isEmpty() || type == null) return null;
        FingerList fingers = frame.fingers();
        for (Finger finger : fingers) {
            if(finger.type() == type)
                return finger;
        }
        return null;
    }

    private static double distanceBetweenTwoPoints(Vector a, Vector b){
        return Math.abs(Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2) + Math.pow(a.getZ() - b.getZ(), 2)));
    }

    private static boolean doTwoFingersMakeCircle(Finger first, Finger second){
        if(first == null || second == null) return false;
        double distanceBetweenLastBones = distanceBetweenTwoPoints(first.bone(Bone.Type.TYPE_DISTAL).center(), first.bone(Bone.Type.TYPE_INTERMEDIATE).center());
        double distanceBetweenFingerEdges = distanceBetweenTwoPoints(first.bone(Bone.Type.TYPE_DISTAL).center(), second.bone(Bone.Type.TYPE_DISTAL).center());
        return distanceBetweenFingerEdges <= distanceBetweenLastBones;
    }

    public static boolean thumbMakesCircleWithRingOrPinky(Frame frame){
        if(frame == null) return false;
        boolean result = false;
        Finger thumbFinger = getFinger(frame, Finger.Type.TYPE_THUMB);
        Finger ringFinger = getFinger(frame, Finger.Type.TYPE_RING);
        if(doTwoFingersMakeCircle(thumbFinger, ringFinger))
            return true;
        Finger pinkyFinger = getFinger(frame, Finger.Type.TYPE_PINKY);
        return doTwoFingersMakeCircle(thumbFinger, pinkyFinger);
    }
}

import com.leapmotion.leap.*;

import java.util.ArrayList;

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
    public static int countCroachedFingers(Hand hand, ArrayList<Integer> features){
        int count = 0;
        for (Finger finger : hand.fingers()) {
            Vector v1 = new Vector();
            Vector v2 = new Vector();
            //Get Bones
            for (Bone.Type boneType : Bone.Type.values()) {
                Bone bone = finger.bone(boneType);
                if(finger.type() == Finger.Type.TYPE_THUMB){
                    if(bone.type()==Bone.Type.TYPE_PROXIMAL){
                        v1= bone.direction();
                    }
                }else{
                    if(bone.type() == Bone.Type.TYPE_METACARPAL){
                        v1= bone.direction();
                    }
                }

                if(bone.type() == Bone.Type.TYPE_DISTAL){
                    v2= bone.direction();
                }
            }
            int croachLevelRes = crouchLevel(v1,v2);
            if( croachLevelRes>5){
                count++;
            }
            features.add(croachLevelRes);
            features.add(FeatureEvaluator.fingerStraightLevel(finger));


            //System.out.println("crouch level == " + FeatureEvaluator.crouchLevel(v1, v2));

           // System.out.println("finger straight" + FeatureEvaluator.fingerStraightLevel(finger));

        }
        return count;
    }
    public static int fingerStraightLevel(Finger finger){
        //Get Bones
        Vector proximal= new Vector();
        Vector intermidiate =  new Vector();
        Vector distal= new Vector();
        for (Bone.Type boneType : Bone.Type.values()) {
            Bone bone = finger.bone(boneType);
            if(bone.type() == Bone.Type.TYPE_PROXIMAL){
                proximal= bone.direction();
            }
            if(bone.type() == Bone.Type.TYPE_INTERMEDIATE){
                intermidiate= bone.direction();
            }
            if(bone.type() == Bone.Type.TYPE_DISTAL){
                distal= bone.direction();
            }

        }

        return crouchLevel(proximal,intermidiate)+crouchLevel(intermidiate,distal);
    }
    public static int crouchLevel(Vector v1, Vector v2) {
        v1 = v1.normalized();
        v2 = v2.normalized();
        float res = v1.get(0) * v2.get(0)+ v1.get(1) * v2.get(1) + v1.get(2) * v2.get(2);
        res+=1;
        res/=2;
        res *=10;
        return  10-(int)res;
    }
}

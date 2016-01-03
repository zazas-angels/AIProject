import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FileUtils {
    public static ArrayList<Integer> getWeights(HandFigureTypes type) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try {
            Scanner scan = new Scanner(new File("D:\\dev\\AIProject\\src\\main\\resources\\"+ type.name()));
            while (scan.hasNext()) {
                list.add(scan.nextInt());
            }
        } catch (FileNotFoundException e) {
            return null;
        }
        return list;
    }


    public static void setWeights(HandFigureTypes type, ArrayList<Integer> list) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new File("D:\\dev\\AIProject\\src\\main\\resources\\"+ type.name()));
        for (Integer integer : list) {
            writer.print(integer);
            writer.print(" ");
        }
        writer.close();
    }

}

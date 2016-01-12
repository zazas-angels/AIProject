import com.leapmotion.leap.Frame;

import javax.swing.*;
import java.awt.*;

public class GUITest {
    private JFrame frame;
    private JPanel panel;
    private JLabel label;


    public GUITest(){
        frame = new JFrame("JEIRANI");
        frame.setSize(800, 800);
//        frame.setLayout(new FlowLayout());
        label = new JLabel("Prepare to move...");
        label.setFont(new Font(label.getName(), Font.PLAIN, 80));
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void takeYourHandIn() {
        label.setText("<html><font color='green'>take your hand in</font></html>");
    }

    public void takeYourHandOut() {
        label.setText("<html><font color='red'>take your hand out</font></html>");
    }

    public void prepareForMove() {
        label.setText("<html><font color='#CCC054'>prepare for move...</font></html>");
    }

    public void chosen(HandFigureTypes currentFigure) {
        label.setText("<html>Detected: <font color='#5D88CF'> "+currentFigure +"</font></html>");
    }
}

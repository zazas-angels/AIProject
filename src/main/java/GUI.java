import javax.swing.*;
import java.awt.*;

public class GUI {
    private static final String PATH_TO_FRODO = "D:\\dev\\AIProject\\src\\main\\resources\\frodo.png";
    private static final String PATH_TO_FRODO_WINS = "D:\\dev\\AIProject\\src\\main\\resources\\frodoWins.gif";
    private static final String PATH_TO_FRODO_LOSES = "D:\\dev\\AIProject\\src\\main\\resources\\frodoLoses.gif";
    private static final String PATH_TO_NET_IMAGE = "D:\\dev\\AIProject\\src\\main\\resources\\netImage.png";
    private static final String PATH_TO_WELL_IMAGE = "D:\\dev\\AIProject\\src\\main\\resources\\wellImage.png";
    private static final String PATH_TO_SCISSORS_IMAGE = "D:\\dev\\AIProject\\src\\main\\resources\\scissorsImage.png";
    private JFrame frame;
    private JLabel frodoImage;
    private JPanel centerPanel;
    private JLabel infoLabel;
    private int computerPoints;
    private int playerPoints;
    JLabel computerPointsLabel;
    JLabel playerPointsLabel;
    JLabel winnerLabel;

    public void init() {
        computerPoints = 0;
        playerPoints = 0;
        frame = new JFrame("JEIRANI");
        frame.setSize(1200, 800);
        infoLabel = new JLabel("Prepare to move...");

        frame.setLayout(new BorderLayout());
        winnerLabel = new JLabel("");
        winnerLabel.setFont(new Font(winnerLabel.getName(), Font.PLAIN, 60));

        frame.add(getNorthPanel(), BorderLayout.NORTH);
        infoLabel.setFont(new Font(infoLabel.getName(), Font.PLAIN, 80));

        frame.add(winnerLabel, BorderLayout.SOUTH);
        frame.add(getCenterPanel(), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel getNorthPanel() {
        JPanel panel = new JPanel();
        computerPointsLabel = new JLabel("       0    COMPUTER");
        computerPointsLabel.setFont(new Font(computerPointsLabel.getName(), Font.PLAIN, 30));
        playerPointsLabel = new JLabel("PLAYER    0         :");
        playerPointsLabel.setFont(new Font(playerPointsLabel.getName(), Font.PLAIN, 30));
        panel.setLayout(new FlowLayout());
        panel.add(playerPointsLabel);
        panel.add(computerPointsLabel);
        return panel;
    }

    public void takeYourHandIn() {
        infoLabel.setText("<html><font color='green'>take your hand in</font></html>");
    }

    public void takeYourHandOut() {
        infoLabel.setText("<html><font color='red'>take your hand out</font></html>");
    }

    public void prepareForMove() {
        infoLabel.setText("<html><font color='#CCC054'>prepare for move...</font></html>");
    }

    public Player chosen(HandFigureTypes currentFigure, HandFigureTypes opposite) {
        Player winner = HandFigureTypes.getWinner(currentFigure, opposite);
        infoLabel.setText("");
        addImages(currentFigure, opposite);
        switch (winner){
            case HUMAN:
                playerPoints++;
                onPlayerWin();
                break;
            case COMPUTER:
                computerPoints++;
                onComputerWin();
                break;
            default:
                winnerLabel.setText("<html><font color='#5D88CF'>DRAW </font></html>");
                timeout(3);
                winnerLabel.setText("");
        }
        removeImages();
        infoLabel.setText("<html>Detected: <font color='#5D88CF'> "+currentFigure +"</font> opposite " +opposite+"</html>");
        return winner;
    }

    private void timeout(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private JLabel computerFigureImage;
    private JLabel playerFigureImage;

    private void removeImages() {
        centerPanel.remove(computerFigureImage);
        centerPanel.remove(playerFigureImage);
    }

    private void addImages(HandFigureTypes playerFigure, HandFigureTypes computerFigure) {
        String playerChosen = "";
        switch (playerFigure){
            case NET:
                playerChosen = PATH_TO_NET_IMAGE;
                break;
            case WELL:
                playerChosen = PATH_TO_WELL_IMAGE;
                break;
            case SCISSORS:
                playerChosen = PATH_TO_SCISSORS_IMAGE;
                break;
        }
        ImageIcon imageIcon = new ImageIcon(playerChosen);
        playerFigureImage= new JLabel(imageIcon);
        centerPanel.add(playerFigureImage, BorderLayout.WEST);

        String computerChosen = "";
        switch (computerFigure){
            case NET:
                computerChosen = PATH_TO_NET_IMAGE;
                break;
            case WELL:
                computerChosen = PATH_TO_WELL_IMAGE;
                break;
            case SCISSORS:
                computerChosen = PATH_TO_SCISSORS_IMAGE;
                break;
        }
        ImageIcon imageIcon1 = new ImageIcon(computerChosen);
        computerFigureImage = new JLabel(imageIcon1);
        centerPanel.add(computerFigureImage, BorderLayout.CENTER);
    }

    private void onPlayerWin() {
        playerPointsLabel.setText("     PLAYER    "+playerPoints+"         :");
        winnerLabel.setText("<html><font color='red'>     PLAYER WINS</font></html>");
        winnerLabel.setFont(new Font(winnerLabel.getName(), Font.PLAIN, 60));
        frodoImage.setIcon(new ImageIcon(PATH_TO_FRODO_LOSES));
        timeout(4);
        frodoImage.setIcon(new ImageIcon(PATH_TO_FRODO));
        winnerLabel.setText("");
    }

    private void onComputerWin() {
        computerPointsLabel.setText("       " + computerPoints + "    COMPUTER");
        winnerLabel.setText("<html><font color='red'>     COMPUTER WINS</font></html>");
        winnerLabel.setFont(new Font(winnerLabel.getName(), Font.PLAIN, 60));
        frodoImage.setIcon(new ImageIcon(PATH_TO_FRODO_WINS));

        timeout(4);
        frodoImage.setIcon(new ImageIcon(PATH_TO_FRODO));
        winnerLabel.setText("");
    }

    public void noneChosen() {
        infoLabel.setText("<html><font color='#5D88CF'> can't detect figure</font></html>");
    }

    private JPanel getCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon(PATH_TO_FRODO);
        frodoImage = new JLabel(imageIcon);

        panel.add(infoLabel, BorderLayout.CENTER);
        panel.add(frodoImage, BorderLayout.EAST);

        centerPanel = panel;
        return panel;
    }

    public void preGame(){
//Custom button text
        Object[] options = {"start game"};
        int n = JOptionPane.showOptionDialog(frame,
                "play JAIran with frodo, press button to start game...",
                "JAIran",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        //Custom button text
//        JDialog dialog = new JDialog();
//        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    public void playAgain(){
        Object[] options = {"start game"};
        JOptionPane.showOptionDialog(frame,
                "do you want to play again?",
                "JAIran",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    public void resetPoints() {
        computerPoints = 0;
        playerPoints = 0;
        computerPointsLabel.setText("       0    COMPUTER");
//        computerPointsLabel.setFont(new Font(computerPointsLabel.getName(), Font.PLAIN, 30));
        playerPointsLabel.setText("PLAYER    0         :");
    }

    public void setWinner() {
        if(computerPoints > playerPoints){
            computerPointsLabel.setText("Frodo wins!!!");
            playerPointsLabel.setText("");
        } else {
            computerPointsLabel.setText("Congratulation, You are winner!");
            playerPointsLabel.setText("");
        }
    }
}

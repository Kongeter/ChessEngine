package ChessUI;

import ChessEngine.Move;

import javax.swing.*;
import java.util.List;


public class MainFrame extends JFrame {
    private JPanel MainPanel;
    private BoardComponent boardComponent1;
    private JPanel Field;
    float scale;

    public MainFrame(float scale) {
        this.scale = scale;
        setContentPane(MainPanel);
        setSize((int)(1200*scale), (int)(1000*scale));
        setLocationRelativeTo(null);
        setVisible(true);
        setTitle("SchachBot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void drawField(byte[]FeldNeu){
        boardComponent1.ByteArrayLoadPosition(FeldNeu);
    }

    public Move getPlayerMove(List< Move > Moves){
        return boardComponent1.MakeMove(Moves);
    }




    private void createUIComponents() {
        boardComponent1 = new BoardComponent(scale);
    }
}

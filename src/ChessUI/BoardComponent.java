package ChessUI;



import ChessEngine.BinaryHelper;
import ChessEngine.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class BoardComponent extends JPanel {

    char[][] feld = new char[8][8];
    List <ChessPiece> Pieces =  new CopyOnWriteArrayList<ChessPiece>();

    boolean MakeMove = false, StartPicked = false; //wenn getPlayerMove= 1 dann userinput abwarten
    int Startx =-1, Starty = -1, Endx = -1, Endy =-1;

    List <Move> PossibleTargets =  new ArrayList<Move>();

    List<Move> Moves;

    int pixelSizeFigures;
    float scale;

    BoardComponent(float scale){
        this.scale = scale;
        pixelSizeFigures = (int) (scale * 100);
        final Optional<ChessPiece>[] focus = new Optional[]{Optional.empty()};
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(MakeMove) {
                    focus[0].ifPresent(piece -> piece.SetRawPos(e.getX() - (int)(scale * 50), e.getY() - (int)(scale * 50)));
                    repaint();
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {}
        });
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {
                if(MakeMove) {
                    int mx = e.getX();
                    int my = e.getY();
                    focus[0] = Pieces.stream().filter(p -> (p.getX() < mx && p.getX() > mx - pixelSizeFigures && p.getY() < my && p.getY() > my - pixelSizeFigures)).findFirst();
                    focus[0].ifPresent(piece -> {
                        StartPicked = true;
                        Startx = piece.getGameX();
                        Starty = piece.getGameY();
                        SetPossibleTargets(Startx,Starty);
                    });
                    repaint();
                }

            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(MakeMove) {
                    focus[0].ifPresent(piece -> {
                        if(PossibleTargets.stream().anyMatch(m -> m.getEndFieldX() == e.getX()/ pixelSizeFigures && m.getEndFieldY() == e.getY()/ pixelSizeFigures)){
                            Pieces.remove(Pieces.stream()
                                    .filter(p -> p.getGameX() == e.getX()/ pixelSizeFigures && p.getGameY() == e.getY()/ pixelSizeFigures)
                                    .findFirst()
                                    .orElse(null));


                            moveMade = PossibleTargets.stream().
                                    filter(m -> m.getEndFieldX() == e.getX()/ pixelSizeFigures && m.getEndFieldY() == e.getY()/ pixelSizeFigures).
                                    findFirst().orElse(null);



                            piece.SetFieldPos(e.getX()/ pixelSizeFigures,e.getY()/ pixelSizeFigures);

                            Endx = e.getX()/ pixelSizeFigures;
                            Endy = e.getY()/ pixelSizeFigures;

                            MakeMove = false;

                        }
                        else{
                            piece.SetFieldPos(Startx,Starty);
                            StartPicked = false;
                            Startx = -1;
                            Starty = -1;
                        }
                        PossibleTargets =  new ArrayList<Move>();
                    }
                    );
                }
                focus[0] = Optional.empty();
                repaint();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    private void SetPossibleTargets(int startX, int startY){
        List <Move> result =  new ArrayList<Move>();
        for(Move m : Moves){
            if(m.getStartFieldX() == startX && m.getStartFieldY() == startY){
                result.add(m);
            }
        }
        PossibleTargets = result;
    }

    Move moveMade;
    public Move MakeMove(List<Move> Moves){
        this.Moves = Moves;
        MakeMove = true;
        StartPicked = false;
        while (MakeMove)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        Startx = -1;
        Starty = -1;
        repaint();
        return moveMade;
    }
    public void ByteArrayLoadPosition(byte[] FieldInt){

        char[][] AusgabeFeld = new char[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                char PieceLetter = BinaryHelper.binToChar(FieldInt[(x+2)+(12*(y+2))]);
                if(PieceLetter != 'e'){
                    AusgabeFeld[x][y] = PieceLetter;
                }
            }
        }
        loadPosition(AusgabeFeld);
    }

    public void FENloadPosition(String FENcode){
        char[][] AusgabeFeld = new char[8][8];
        String[] FENCodeNurFeld = FENcode.split(" ");
        String[] FENCodeZeile = FENCodeNurFeld[0].split("/");
        for (int y = 0; y < 8; y++) {
            int curr = 0;
            for (int x = 0; x < 8; x++) {
                char singeChar = FENCodeZeile[y].charAt(curr);
                if(Character.isDigit(singeChar)){
                    x += ((int) singeChar);
                }
                else {
                    AusgabeFeld[x][y] = singeChar;
                }
                curr ++;
            }
        }
        loadPosition(AusgabeFeld);
    }
    private void loadPosition(char[][] FeldNeu){
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                //if (feld[x][y] != FeldNeu[x][y]) {
                    if(FeldNeu[x][y] == '\u0000'){
                        changeSingleField(x,y,'e');
                    }
                    else {
                        changeSingleField(x,y,FeldNeu[x][y]);
                    }
                //}
                //TODO wieder an machen
            }
        }
        feld = FeldNeu;
        repaint();
    }

    private void changeSingleField(int x, int y, char c){
        Pieces.remove(Pieces.stream()
                        .filter(p -> p.getGameX() == x && p.getGameY() == y)
                        .findFirst()
                        .orElse(null));
        if(c != 'e' && c != ' ') { //c ist 'e' für empty
            ChessPiece newPiece = new ChessPiece(x, y, c, pixelSizeFigures);
            Pieces.add(newPiece);
        }
    }


    public void paint(Graphics g) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int finalX = x;
                int finalY = y;

                if((x+y) % 2 == 0) {
                    if(PossibleTargets.stream().anyMatch(move->move.getStartFieldX()==Startx && move.getStartFieldY()==Starty && move.getEndFieldX() == finalX && move.getEndFieldY() == finalY))
                    {
                        g.setColor(new Color(38, 194, 65));
                    }
                    else {
                        g.setColor(new Color(255, 222 ,173));
                    }

                }
                else {
                    if(PossibleTargets.stream().anyMatch(move->move.getStartFieldX()==Startx && move.getStartFieldY()==Starty && move.getEndFieldX() == finalX && move.getEndFieldY() == finalY))
                    {
                        g.setColor(new Color(12, 112, 15));
                    }
                    else {
                        g.setColor(new Color(205, 104 ,57));
                    }

                }
                if (x == Startx && y == Starty){
                    g.setColor(new Color(5, 23, 2));
                }
                g.fillRect(x * pixelSizeFigures, y * pixelSizeFigures, pixelSizeFigures, pixelSizeFigures);
            }
        }

        Pieces.forEach(p -> g.drawImage(p.getIcon(), p.getX(), p.getY(), pixelSizeFigures, pixelSizeFigures, this));

//        PossibleTargets.forEach(m -> {
//            g.setColor(new Color(12, 77, 12));
//            int size = (int)(50*scale);
//            g.fillOval( m.getEndFieldX()* pixelSizeFigures +(pixelSizeFigures -size)/2, m.getEndFieldY()* pixelSizeFigures +(pixelSizeFigures -size)/2, size,size);
//        });
    };
}

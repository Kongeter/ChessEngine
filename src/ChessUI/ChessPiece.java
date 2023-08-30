package ChessUI;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ChessPiece {
    int x;
    int y;
    int gx;// x and y from 0-7
    int gy;
    char piece;
    BufferedImage icon;
    int pixelSizeFigures;

    ChessPiece(int x, int y, char Piece, int pixelSizeFigures) {
        this.pixelSizeFigures = pixelSizeFigures;
        this.x = x * pixelSizeFigures;
        this.y = y * pixelSizeFigures;
        this.gy = y;
        this.gx = x;

        this.piece = Piece;
        SetIcon(Piece);
    }

    private void SetIcon(char Piece) {
        String colorFolder = Character.isUpperCase(Piece) ? "white" : "black";
        String path = "./src/ChessUI/Figuren/" + colorFolder + "/" + Piece + ".png";
        try {
            icon = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void SetFieldPos(int x, int y) {
        this.x = (x) * pixelSizeFigures;
        this.y = (y) * pixelSizeFigures;
        this.gy = y;
        this.gx = x;
    }

    void SetRawPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int getY() {
        return y;
    }

    int getX() {
        return x;
    }

    int getGameX() {
        return gx;
    }

    int getGameY() {
        return gy;
    }

    BufferedImage getIcon() {
        return icon;
    }

}

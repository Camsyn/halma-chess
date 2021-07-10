package game.sourcecode;


import javafx.scene.shape.Circle;

import javax.imageio.ImageIO;
import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Pieces implements Serializable {
    ArrayList<Piece> pieces = new ArrayList<>();
    ArrayList<Point> winArea = new ArrayList<>();
    int turn;
    private transient Image pieceImage;

    public Pieces() {
    }

    public void setPieceImage(File file) throws IOException {
        pieceImage = ImageIO.read(file);
    }

    public Pieces(int turn, boolean isFourPlayers, Board board) throws IOException {
        this.turn = turn;
        if (isFourPlayers)
            for (int a = 0; a != 13; a++)
                pieces.add(new Piece(turn));
        else
            for (int a = 0; a != 19; a++)
                pieces.add((new Piece(turn)));
        pieceImage = ImageIO.read(new File("resource\\picture\\" + Constant.COLOR[turn] + "棋子.png"));
        piecesInitialize(board);
    }

    private void piecesInitialize(Board board) {
        if (turn == 0)
            for (int a = 0; a != pieces.size(); a++) {
                pieces.get(a).set1(Board.indexToCoordinate(new Point(Constant.INIT_PIECES_LOCATIONS_X[a], Constant.INIT_PIECES_LOCATIONS_Y[a])));
                board.changeSituationOfBoard(pieces.get(a).toIndexPoint(), turn);
                winArea.add(Board.indexToCoordinate(new Point(15 - Constant.INIT_PIECES_LOCATIONS_X[a], 15 - Constant.INIT_PIECES_LOCATIONS_Y[a])));
            }
        if (turn == 1)
            for (int a = 0; a != pieces.size(); a++) {
                pieces.get(a).set1(Board.indexToCoordinate(new Point(15 - Constant.INIT_PIECES_LOCATIONS_X[a], Constant.INIT_PIECES_LOCATIONS_Y[a])));
                board.changeSituationOfBoard(pieces.get(a).toIndexPoint(), turn);
                winArea.add(Board.indexToCoordinate(new Point(Constant.INIT_PIECES_LOCATIONS_X[a], 15 - Constant.INIT_PIECES_LOCATIONS_Y[a])));
            }
        if (turn == 2)
            for (int a = 0; a != pieces.size(); a++) {
                pieces.get(a).set1(Board.indexToCoordinate(new Point(15 - Constant.INIT_PIECES_LOCATIONS_X[a], 15 - Constant.INIT_PIECES_LOCATIONS_Y[a])));
                board.changeSituationOfBoard(pieces.get(a).toIndexPoint(), turn);
                winArea.add(Board.indexToCoordinate(new Point(Constant.INIT_PIECES_LOCATIONS_X[a], Constant.INIT_PIECES_LOCATIONS_Y[a])));
            }
        if (turn == 3)
            for (int a = 0; a != pieces.size(); a++) {
                pieces.get(a).set1(Board.indexToCoordinate(new Point(Constant.INIT_PIECES_LOCATIONS_X[a], 15 - Constant.INIT_PIECES_LOCATIONS_Y[a])));
                board.changeSituationOfBoard(pieces.get(a).toIndexPoint(), turn);
                winArea.add(Board.indexToCoordinate(new Point(15 - Constant.INIT_PIECES_LOCATIONS_X[a], Constant.INIT_PIECES_LOCATIONS_Y[a])));
            }

    }

    public Piece getPieceFromIndex(int a, int b) {
        Point indexPoint = new Point(a, b);
        for (Piece piece : pieces)
            if (piece.toIndexPoint().equals(indexPoint))
                return piece;
        return null;
    }

    public boolean isGoalArea(Point point) {
        for (int i = 0; i != winArea.size(); i++)
            if (point.equals(winArea.get(i)))
                return true;
        return false;
    }

    public boolean isReachGoalArea(int a) {
        Piece piece = pieces.get(a);
        Point point = piece.toPoint();
        for (int i = 0; i != winArea.size(); i++) {
            piece.reachGoalArea=false;
            if (point.equals(winArea.get(i))) {
                piece.reachGoalArea = true;
                break;
            }
        }
        return piece.reachGoalArea;
    }

    @Override
    public String toString() {
        StringBuilder piecePoisition = new StringBuilder();
        boolean flag = false;
        for (int a = 0; a != 16; a++) {
            for (int b = 0; b != 16; b++) {
                for (Piece piece : pieces)
                    if (new Point(a, b).equals(piece.toIndexPoint())) {
                        piecePoisition.append(turn + "  ");
                        flag = true;
                        break;
                    }
                if (!flag)
                    piecePoisition.append("   ");
            }
            piecePoisition.append("\n");
        }
        return piecePoisition.toString();
    }

    public void draw(Graphics g) {
        Color initColor = g.getColor();
        g.setColor(Constant.colorInTurn.get(turn));
        for (int a = 0; a != pieces.size(); a++)
            g.drawImage(pieceImage, pieces.get(a).x + pieces.get(a).Xdrift, pieces.get(a).y + pieces.get(a).Ydrift,
                    pieces.get(a).WIDTH, pieces.get(a).HEIGHT, null);
        g.setColor(initColor);
    }
}

class Piece extends GameObject implements Serializable {
    int turn;
    int WIDTH = 3 * Grid.WIDTH / 5, HEIGHT = 3 * Grid.HEIGTH / 5;
    int Xdrift = (Grid.WIDTH - WIDTH) / 2, Ydrift = (Grid.HEIGTH - HEIGHT) / 2;
    Image pieceImg;

    static class PieceJudgeArea extends Circle implements Serializable {
        public PieceJudgeArea(double centerX, double centerY, double radius) {
            super(centerX, centerY, radius);
        }
    }

    PieceJudgeArea area = new PieceJudgeArea((double) x + (double) Grid.WIDTH / 2, (double) y + (double) Grid.HEIGTH / 2, (double) HEIGHT / 2);
    boolean isChosen = false;
    boolean reachGoalArea = false;

    public Piece(int turn) {
        this.turn = turn;
    }

    public Piece() {
    }

    public void set1(Point point) {
        x = point.x;
        y = point.y;
        area = new PieceJudgeArea((double) x + (double) Grid.WIDTH / 2, (double) y + (double) Grid.HEIGTH / 2, (double) HEIGHT / 2);
    }

    public void set(Point point, Board board) {
        board.setVoidLocationOfBoard(Board.getIndexPoint(new Point(x, y)));
        x = point.x;
        y = point.y;
        board.changeSituationOfBoard(Board.getIndexPoint(point), turn);
        area = new PieceJudgeArea((double) x + (double) Grid.WIDTH / 2, (double) y + (double) Grid.HEIGTH / 2, (double) HEIGHT / 2);
    }

    public void ifClickedThenChange() {
        isChosen = !isChosen;
        if (isChosen) {
            WIDTH = Grid.WIDTH;
            HEIGHT = Grid.HEIGTH;
            Xdrift = (Grid.WIDTH - WIDTH) / 2;
            Ydrift = (Grid.HEIGTH - HEIGHT) / 2;
            area = new PieceJudgeArea((double) x + (double) Grid.WIDTH / 2, (double) y + (double) Grid.HEIGTH / 2, (double) HEIGHT / 2);
        } else {
            WIDTH = 3 * Grid.WIDTH / 5;
            HEIGHT = 3 * Grid.HEIGTH / 5;
            Xdrift = (Grid.WIDTH - WIDTH) / 2;
            Ydrift = (Grid.HEIGTH - HEIGHT) / 2;
            area = new PieceJudgeArea((double) x + (double) Grid.WIDTH / 2, (double) y + (double) Grid.HEIGTH / 2, (double) HEIGHT / 2);
        }

    }

    public Point toIndexPoint() {
        return new Point(Board.getIndexPoint(new Point(x, y)));
    }

    public Point toPoint() {
        return new Point(x, y);
    }

    public void setImage() {
        try {
            pieceImg = ImageIO.read(new File("resource\\picture\\" + Constant.COLOR[turn] + "棋子.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g, Point point) {
        g.drawImage(pieceImg, point.x + Xdrift, point.y + Ydrift, WIDTH, HEIGHT, null);
    }
}

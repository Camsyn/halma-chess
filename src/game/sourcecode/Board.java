package game.sourcecode;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {

    private static final ArrayList<Grid> grids = new ArrayList<>();
    private int[][] situationOfBoard = new int[16][16];
    private Point[] lastPostion = new Point[4];
    private Point[] currentPosition = new Point[4];

    static {
        for (int a = 0; a != 16; a++)
            for (int b = 0; b != 16; b++) {
                grids.add(new Grid());
                grids.get(a * 16 + b).setX(Constant.BOARD_COORDINATE_X + b * Constant.GRID_WIDTH);
                grids.get(a * 16 + b).setY(Constant.BOARD_COORDINATE_Y + a * Constant.GRID_HEIGHT);
            }
    }

    public void setBoard(int row, int col, int turn) {
        situationOfBoard[row][col] = turn;
    }

    public void changeSituationOfBoard(Point indexPoint, int turn) {
        situationOfBoard[indexPoint.x][indexPoint.y] = turn;
    }

    public void setVoidLocationOfBoard(Point indexPoint) {
        situationOfBoard[indexPoint.x][indexPoint.y] = 4;
    }

    private boolean isValidPoint(Point indexPoint) {
        if (indexPoint.x < 0 || indexPoint.x > 15 || indexPoint.y < 0 || indexPoint.y > 15)
            return false;
        return situationOfBoard[indexPoint.x][indexPoint.y] == 4;
    }

    public ArrayList<Point> returnNextIndexPosition(Piece piece) {
        ArrayList<Point> nextIndexPosition = new ArrayList<>();
        Point accessiblePoint, indexPoint = piece.toIndexPoint();
        for (int row = -1; row != 2; row++)
            for (int column = -1; column != 2; column++) {
                accessiblePoint = new Point(indexPoint.x + row, indexPoint.y + column);
                if (isValidPoint(accessiblePoint))
                    nextIndexPosition.add(accessiblePoint);
                else {
                    accessiblePoint.x += row;
                    accessiblePoint.y += column;
                    if (isValidPoint(accessiblePoint)) {
                        nextIndexPosition.add(accessiblePoint);
                        continueJump(nextIndexPosition, accessiblePoint, new Point(accessiblePoint.x - row, accessiblePoint.y - column));
                    }
                }
            }
        return nextIndexPosition;
    }

    public static int[][] clone(int[][] board){
        int[][] newBoard=board.clone();
        for(int a=0;a!=board.length;a++){
            newBoard[a]=board[a].clone();
        }
        return newBoard;
    }

    private void continueJump(ArrayList<Point> nextIndexPosition, Point indexPoint, Point banJumpPoint) {
        for (int row = -1; row != 2; row++)
            for (int column = -1; column != 2; column++) {
                Point accessiblePoint = new Point(indexPoint.x + row, indexPoint.y + column);
                if ((!accessiblePoint.equals(banJumpPoint)) && !isValidPoint(accessiblePoint)) {
                    accessiblePoint.x += row;
                    accessiblePoint.y += column;
                    if (isValidPoint(accessiblePoint)) {
                        boolean newIndex = true;
                        for (int a = 0; a != nextIndexPosition.size(); a++)
                            if (nextIndexPosition.get(a).equals(accessiblePoint))
                                newIndex = false;
                        if (newIndex) {
                            nextIndexPosition.add(accessiblePoint);
                            continueJump(nextIndexPosition, accessiblePoint, new Point(accessiblePoint.x - row, accessiblePoint.y - column));
                        }
                    }
                }
            }
    }

    public static void drawTips(Graphics g, ArrayList<Point> points) {
        Color initColor = g.getColor();

        int xDrift = Grid.WIDTH / 4, yDrift = Grid.HEIGTH / 4;
        g.setColor(Color.LIGHT_GRAY);
        for (int a = 0; a != points.size(); a++)
            g.fillRect(points.get(a).y * Grid.WIDTH + Constant.BOARD_COORDINATE_X + xDrift,
                    points.get(a).x * Grid.HEIGTH + Constant.BOARD_COORDINATE_Y + yDrift,
                    Grid.WIDTH / 2, Grid.HEIGTH / 2);
        g.setColor(initColor);
    }

    public Board() {
        for (int a = 0; a != 16; a++)
            for (int b = 0; b != 16; b++)
                situationOfBoard[a][b] = 4;
        for (int a = 0; a != 4; a++) {
            lastPostion[a] = new Point(0, 0);
            currentPosition[a] = new Point(0, 0);
        }
    }

    public static Point getIndexPoint(Point point) {
        int x = (point.y - Constant.BOARD_COORDINATE_Y) / Grid.HEIGTH;
        int y = (point.x - Constant.BOARD_COORDINATE_X) / Grid.WIDTH;
        return new Point(x, y);
    }

    public static int COORDINATEToIndexY(int x) {
        return (x - Constant.BOARD_COORDINATE_X) / Grid.WIDTH;
    }

    public int[][] getSituationOfBoard() {
        return situationOfBoard;
    }

    public void setSituationOfBoard(int[][] situationOfBoard) {
        this.situationOfBoard = situationOfBoard.clone();
        for (int a = 0; a != 16; a++)
            this.situationOfBoard[a] = situationOfBoard[a].clone();
    }

    public static int COORDINATEToIndexX(int y) {
        return (y - Constant.BOARD_COORDINATE_Y) / Grid.HEIGTH;
    }

    public static int indexToCoordinateX(int y) {
        return y * Grid.WIDTH + Constant.BOARD_COORDINATE_X;
    }

    public static int indexToCoordinateY(int x) {
        return x * Grid.HEIGTH + Constant.BOARD_COORDINATE_Y;
    }

    public static Point indexToCoordinate(Point point) {
        return new Point(indexToCoordinateX(point.y), indexToCoordinateY(point.x));
    }

    public static Grid getGrid(int x, int y) {
        return grids.get(x * 16 + y);
    }

    public static Grid getGridByIndex(Point point) {
        return grids.get(16 * point.x + point.y);
    }

    public static Grid getGrid(Point point) {
        return getGridByIndex(getIndexPoint(point));
    }

    public static Point getGridCOORDINATE(Point point) {
        return new Point(getGrid(point).x, getGrid(point).y);
    }

    public static Point getGridCOORDINATEByIndex(Point point) {
        return new Point(getGridByIndex(point).x, getGridByIndex(point).y);
    }


    public Point[] getLastPostion() {
        return lastPostion;
    }

    public Point[] getCurrentPosition() {
        return currentPosition;
    }

    public void setTipPosition(Point lastPostion, Point currentPosition, int turn) {
        this.lastPostion[turn] = lastPostion;
        this.currentPosition[turn] = currentPosition;
    }

    private void drawHollowSquareFrame(Graphics g, Point position, int turn) {
        Color initColor = g.getColor();
        g.setColor(Constant.colorInTurn.get(turn));
        if (turn != 0) {
            g.fillRect(position.x, position.y, Grid.WIDTH, Grid.HEIGTH / 10);
            g.fillRect(position.x, position.y, Grid.WIDTH / 10, Grid.HEIGTH);
            g.fillRect(position.x + Grid.WIDTH - Grid.WIDTH / 10, position.y, Grid.WIDTH / 10, Grid.HEIGTH);
            g.fillRect(position.x, position.y + 9 * Grid.HEIGTH / 10, Grid.WIDTH, Grid.HEIGTH / 10);
        } else {
            g.setColor(Color.lightGray);
            g.fillRect(position.x, position.y, Grid.WIDTH, Grid.HEIGTH / 10);
            g.fillRect(position.x, position.y, Grid.WIDTH / 10, Grid.HEIGTH);
            g.fillRect(position.x + Grid.WIDTH - Grid.WIDTH / 10, position.y, Grid.WIDTH / 10, Grid.HEIGTH);
            g.fillRect(position.x, position.y + 9 * Grid.HEIGTH / 10, Grid.WIDTH, Grid.HEIGTH / 10);
        }
        g.setColor(initColor);
    }

    public void drawMoveTipAfterMoving(Graphics g) {
        for (int a = 0; a != 4; a++)
            if (!lastPostion[a].equals(new Point(0, 0))) {
                drawHollowSquareFrame(g, lastPostion[a], a);
                drawHollowSquareFrame(g, currentPosition[a], a);
            }
    }

    public static void drawPlane(Graphics g) {
        Color initColor = g.getColor();
        for (int a = 0; a != 16; a++)
            for (int b = 0; b != 16; b++) {
                if ((a + b) % 2 == 0)
                    g.setColor(Color.gray);
                else
                    g.setColor(Color.darkGray);
                g.fillRect(grids.get(16 * a + b).getX(), grids.get(16 * a + b).getY(), Grid.WIDTH, Grid.HEIGTH);
            }
        g.setColor(initColor);
    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder();
        for (int a = 0; a != 16; a++) {
            for (int b = 0; b != 16; b++)
                board.append(situationOfBoard[a][b] + "  ");
            board.append("\n");
        }
        board.append("\n");
        return board.toString();
    }
}

class Grid extends GameObject implements Serializable {
    static int WIDTH = Constant.GRID_WIDTH;
    static int HEIGTH = Constant.GRID_HEIGHT;

    public Point getPoint() {
        return new Point(this.x, this.y);
    }

    public Rectangle validArea() {
        return new Rectangle(this.x, this.y, WIDTH, HEIGTH);
    }

    public boolean judgeClick(Point point) {
        return new Rectangle(this.x, this.y, WIDTH, HEIGTH).contains(point);
    }

}

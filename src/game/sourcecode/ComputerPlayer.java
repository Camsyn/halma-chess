package game.sourcecode;

import java.awt.*;
import java.util.*;

public class ComputerPlayer {
    static boolean isFourPlayer;
    int turn;
    private int searchDepth = 4;
    Board board;
    Pieces player;
    transient LinkedList<GameNode> searchSeries = new LinkedList<>();
    GameNode finalDepthData = null;
    /*GameData[] possibleDataAfterSteps = new GameData[]{null, null, null, null, null};
    int order = 0;*/
    transient GameNode gameNode;
    static double initialDistance = 0;
    /*启用深度优先算法DFS的开关（空间复杂度低），若否，则采用广度优先算法（BFS）（空间复杂度高）*/
    static boolean DFS = true;
    int goalXinTotal;
    int goalYinTotal;

    class GameNode {
        private Point[] pieces = new Point[ComputerPlayer.isFourPlayer ? 13 : 19];
        private int[][] situationOfBoard;
        double distance;
        int depth;
        Point specialPoint1, specialPoint2;
        GameNode parent;

        public GameNode(int depth, GameNode parent, int[][] board) {
            this.depth = depth;
            this.parent = parent;
            if (parent == null) {
                this.situationOfBoard = board.clone();
                for (int a = 0; a != 16; a++)
                    this.situationOfBoard[a] = board[a].clone();
            } else {
                this.situationOfBoard = parent.situationOfBoard.clone();
                for (int a = 0; a != 16; a++)
                    this.situationOfBoard[a] = parent.situationOfBoard[a].clone();
            }

            switch (turn) {
                case 0:
                    goalXinTotal = isFourPlayer ? 179 : 255;
                    goalYinTotal = isFourPlayer ? 179 : 255;
                    break;
                case 1:
                    goalYinTotal = isFourPlayer ? 179 : 255;
                    goalXinTotal = isFourPlayer ? 16 : 30;
                    break;
                case 2:
                    goalXinTotal = isFourPlayer ? 16 : 30;
                    goalYinTotal = isFourPlayer ? 16 : 30;
                    break;
                case 3:
                    goalYinTotal = isFourPlayer ? 16 : 30;
                    goalXinTotal = isFourPlayer ? 179 : 255;
                    break;
                default:
                    break;
            }
            if (isFourPlayer) {
                switch (turn) {
                    case 0:
                        specialPoint1 = new Point(15, 11);
                        specialPoint2 = new Point(11, 15);
                        break;
                    case 1:
                        specialPoint1 = new Point(0, 11);
                        specialPoint2 = new Point(4, 15);
                        break;
                    case 2:
                        specialPoint1 = new Point(4, 0);
                        specialPoint2 = new Point(0, 4);
                        break;
                    case 3:
                        specialPoint1 = new Point(11, 0);
                        specialPoint2 = new Point(15, 4);
                        break;
                    default:
                        break;
                }
            } else {
                switch (turn) {
                    case 0:
                        specialPoint1 = new Point(15, 10);
                        specialPoint2 = new Point(10, 15);
                        break;
                    case 2:
                        specialPoint1 = new Point(5, 0);
                        specialPoint2 = new Point(0, 5);
                        break;
                    default:
                        break;
                }
            }
        }

        private boolean isValidPoint(Point indexPoint) {
            if (indexPoint.x < 0 || indexPoint.x > 15 || indexPoint.y < 0 || indexPoint.y > 15)
                return false;
            return situationOfBoard[indexPoint.x][indexPoint.y] == 4;
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

        public ArrayList<Point> returnnextIndexPosition(Point piece) {
            ArrayList<Point> nextIndexPosition = new ArrayList<>();
            Point accessiblePoint;
            for (int row = -1; row != 2; row++)
                for (int column = -1; column != 2; column++) {
                    accessiblePoint = new Point(piece.x + row, piece.y + column);
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

        public void setDistanceAndPieces() {
            int c = 0;
            distance = 0;
            int XinTotal = 0, YinTotal = 0;
            for (int a = 0; a != 16; a++) {
                for (int b = 0; b != 16; b++) {
                    if (situationOfBoard[a][b] == turn) {
                        XinTotal += a;
                        YinTotal += b;
                        pieces[c++] = new Point(a, b);
                        if (specialPoint1.equals(new Point(a, b)) || specialPoint2.equals(new Point(a, b)))
                            distance += 0.5;
                    }
                }
                if (c == (ComputerPlayer.isFourPlayer ? 13 : 19))
                    break;
            }
            if (turn == 0 || turn == 2)
                distance += Math.abs(XinTotal + YinTotal - goalXinTotal - goalXinTotal);
            else
                distance += Math.abs(XinTotal - goalXinTotal + goalYinTotal - YinTotal);
            switch (turn) {
                case 0:
                    goalXinTotal = isFourPlayer ? 179 : 255;
                    goalYinTotal = isFourPlayer ? 179 : 255;
                    break;
                case 1:
                    goalXinTotal = isFourPlayer ? 179 : 255;
                    goalYinTotal = isFourPlayer ? 16 : 30;
                    break;
                case 2:
                    goalXinTotal = isFourPlayer ? 16 : 30;
                    goalYinTotal = isFourPlayer ? 16 : 30;
                    break;
                case 3:
                    goalXinTotal = isFourPlayer ? 16 : 30;
                    goalYinTotal = isFourPlayer ? 179 : 255;
                    break;
                default:
                    break;
            }
            if (isFourPlayer) {
                switch (turn) {
                    case 0:
                        specialPoint1 = new Point(15, 11);
                        specialPoint2 = new Point(11, 15);
                        break;
                    case 1:
                        specialPoint1 = new Point(0, 11);
                        specialPoint2 = new Point(4, 15);
                        break;
                    case 2:
                        specialPoint1 = new Point(4, 0);
                        specialPoint2 = new Point(0, 4);
                        break;
                    case 3:
                        specialPoint1 = new Point(11, 0);
                        specialPoint2 = new Point(15, 4);
                        break;
                    default:
                        break;
                }
            } else {
                switch (turn) {
                    case 0:
                        specialPoint1 = new Point(15, 10);
                        specialPoint2 = new Point(10, 15);
                        break;
                    case 1:
                        specialPoint1 = new Point(0, 10);
                        specialPoint2 = new Point(5, 15);
                        break;
                    case 2:
                        specialPoint1 = new Point(5, 0);
                        specialPoint2 = new Point(0, 5);
                        break;
                    case 3:
                        specialPoint1 = new Point(10, 0);
                        specialPoint2 = new Point(15, 5);
                        break;
                    default:
                        break;
                }
            }
        }

        public GameNode returnNewNode(Point point, Point nextPosition) {
            GameNode newBoard = new GameNode(depth + 1, this, this.situationOfBoard);
            newBoard.situationOfBoard[point.x][point.y] = 4;
            newBoard.situationOfBoard[nextPosition.x][nextPosition.y] = turn;

            if (newBoard.depth != searchDepth)
                newBoard.setDistanceAndPieces();
            else
                newBoard.setDistance();
            return newBoard;
        }

        /* WFS*/
        /*private void generateNodesFromOnePiece(Point piece){
            ArrayList<Point> nextPositions=returnnextIndexPosition(piece);
            GameData newGameData;
            for(int a=0;a!=nextPositions.size();a++)
            {
                newGameData=returnNewNode(piece,nextPositions.get(a))*//*.forecastOtherPlays()*//*;

            }
        }*/
        /* DFS*/
        private void generateNodesFromOnePiece(Point piece) {
            ArrayList<Point> nextPositions = returnnextIndexPosition(piece);
            for (int a = 0; a != nextPositions.size(); a++) {
                GameNode newGameNode = returnNewNode(piece, nextPositions.get(a))/*.forecastOtherPlays()*/;
                if (DFS) {
                    if (searchDepth == 1) {
                        if (newGameNode.distance <= finalDepthData.distance && newGameNode.depth == searchDepth)
                            finalDepthData = newGameNode;
                    } else {
                        if (newGameNode.distance < finalDepthData.distance && newGameNode.depth == searchDepth) {
                            finalDepthData = newGameNode;
                            /*if (searchDepth == 3 && !isFourPlayer) {
                                order = order == 5 ? 0 : order + 1;
                                possibleDataAfterSteps[order] = newGameData;
                            }*/
                        }
                        if (newGameNode.distance < ComputerPlayer.initialDistance + 3 && newGameNode.depth < searchDepth) {
                            searchSeries.add(0, newGameNode);
                            if (finalDepthData.depth > newGameNode.depth && newGameNode.distance <= finalDepthData.distance) {
                                finalDepthData = newGameNode;
                                /*if (searchDepth == 3 && !isFourPlayer) {
                                    order = order == 5 ? 0 : order + 1;
                                    possibleDataAfterSteps[order] = newGameData;
                                }*/
                            }
                        }
                    }
                } else {
                    if (searchDepth == 1) {
                        if (newGameNode.distance <= searchSeries.get(searchSeries.size() - 1).distance && newGameNode.depth == searchDepth) {
                            searchSeries.remove(searchSeries.size() - 1);
                            searchSeries.add(newGameNode);
                        }
                    } else {
                        if (newGameNode.distance < searchSeries.get(searchSeries.size() - 1).distance && newGameNode.depth == searchDepth) {
                            searchSeries.remove(searchSeries.size() - 1);
                            searchSeries.add(newGameNode);
                            /*if (searchDepth == 3 && !isFourPlayer) {
                                order = order == 5 ? 0 : order + 1;
                                possibleDataAfterSteps[order] = newGameData;
                            }*/
                        }
                        if (newGameNode.distance < ComputerPlayer.initialDistance + 3 && newGameNode.depth < searchDepth) {
                            searchSeries.add(newGameNode);
                            /*if (searchDepth == 3 && !isFourPlayer) {
                                order = order == 5 ? 0 : order + 1;
                                possibleDataAfterSteps[order] = newGameData;
                            }*/
                        }
                    }
                }
            }
        }

        private void generateNodesUsedInFinal(Point piece) {
            ArrayList<Point> nextPositions = returnnextIndexPosition(piece);
            GameNode newGameNode;
            for (int a = 0; a != nextPositions.size(); a++) {
                newGameNode = returnNewNode(piece, nextPositions.get(a))/*.forecastOtherPlays()*/;
                if (DFS) {
                    if (newGameNode.distance < finalDepthData.distance && newGameNode.depth == searchDepth)
                        finalDepthData = newGameNode;
                    if (newGameNode.distance < ComputerPlayer.initialDistance + 1 && newGameNode.depth < searchDepth) {
                        searchSeries.add(0, newGameNode);
                        if (finalDepthData.depth > newGameNode.depth)
                            finalDepthData = newGameNode.distance <= finalDepthData.distance ? newGameNode : finalDepthData;
                        else
                            finalDepthData = newGameNode.distance < finalDepthData.distance ? newGameNode : finalDepthData;
                    }
                } else {
                    if (searchDepth == 1) {
                        if (newGameNode.distance <= searchSeries.get(searchSeries.size() - 1).distance && newGameNode.depth == searchDepth) {
                            searchSeries.remove(searchSeries.size() - 1);
                            searchSeries.add(newGameNode);
                        }
                    } else {
                        if (newGameNode.distance < searchSeries.get(searchSeries.size() - 1).distance && newGameNode.depth == searchDepth) {
                            searchSeries.remove(searchSeries.size() - 1);
                            searchSeries.add(newGameNode);
                        }
                        if (newGameNode.distance < ComputerPlayer.initialDistance + 3 && newGameNode.depth < searchDepth)
                            searchSeries.add(newGameNode);
                    }
                }
            }
        }

        /*    private void generateNodesFromOnePiece(Point piece,ArrayList<GameData> gameDatas){
                ArrayList<Point> nextPositions=returnnextIndexPosition(piece);
                for(int a=0;a!=nextPositions.size();a++)
                    gameDatas.add(returnNewNode(piece,nextPositions.get(a)));
            }*/
        private void turnChange() {
            if (ComputerPlayer.isFourPlayer)
                turn = turn == 3 ? 0 : turn + 1;
            else
                turn = turn == 2 ? 0 : 2;
        }

        private void setPieces() {
            int c = 0;
            for (int a = 0; a != 16; a++) {
                for (int b = 0; b != 16; b++)
                    if (situationOfBoard[a][b] == turn)
                        pieces[c++] = new Point(a, b);
                if (c == (ComputerPlayer.isFourPlayer ? 13 : 19))
                    break;
            }

        }

        /*private GameData forecastOtherPlays(){
            if(isFourPlayer)
                for(int a=0;a!=3;a++)
                {
                    turnChange();
                    setPieces();
                    generateNodes(temporarySeries);
                    temporarySeries.sort(Comparator.comparingInt(b -> b.distance));
                    this.setSituationOfBoard(temporarySeries.get(a).situationOfBoard);
                    temporarySeries=new ArrayList<>();
                }
            else
            {
                turnChange();
                setPieces();
                generateNodes(temporarySeries);
                temporarySeries.sort(Comparator.comparingInt(b -> b.distance));
                this.setSituationOfBoard(temporarySeries.get(0).situationOfBoard);
                temporarySeries=new ArrayList<>();
            }
            turnChange();
            setPieces();
            return this;
        }*/
        public void generateNodes() {
            for (int a = 0; a != pieces.length; a++)
                generateNodesFromOnePiece(pieces[a]);
            this.pieces = null;
            if (this.depth > 1)
                this.situationOfBoard = null;
        }

        public void generateUsedInFinal() {
            for (int a = 0; a != pieces.length; a++)
                generateNodesUsedInFinal(pieces[a]);
            this.pieces = null;
            if (this.depth > 1)
                this.situationOfBoard = null;
        }

        /*   public void generateNodes(ArrayList<GameData> gameDatas){
               test=true;
               for(int a=0;a!=pieces.length;a++)
                   generateNodesFromOnePiece(pieces[a],gameDatas);
           }

           public void setSituationOfBoard(int[][] situationOfBoard) {
               this.situationOfBoard = situationOfBoard;
           }*/
        public void setDistance() {
            distance = 0;
            int XinTotal = 0, YinTotal = 0;
            for (int a = 0; a != 16; a++) {
                for (int b = 0; b != 16; b++) {
                    if (situationOfBoard[a][b] == turn) {
                        XinTotal += a;
                        YinTotal += b;
                        if (specialPoint1.equals(new Point(a, b)) || specialPoint2.equals(new Point(a, b)))
                            distance += 0.5;
                    }
                }
            }
            if (turn == 0 || turn == 2)
                distance += Math.abs(XinTotal + YinTotal - goalXinTotal - goalXinTotal);
            else
                distance += Math.abs(XinTotal - goalXinTotal + goalYinTotal - YinTotal);
        }
    }

    public ComputerPlayer(boolean isFourPlayer){
        ComputerPlayer.isFourPlayer=isFourPlayer;
    }

    public ComputerPlayer(boolean isFourPlayer, int turn, Board board, Pieces player) {
        ComputerPlayer.isFourPlayer = isFourPlayer;
        this.turn = turn;
        this.player = player;
        this.board = board;
        this.gameNode = new GameNode(0, null, board.getSituationOfBoard());
    }

    public ComputerPlayer(boolean isFourPlayer, int turn, Board board) {
        ComputerPlayer.isFourPlayer = isFourPlayer;
        this.turn = turn;
        this.board = board;
        this.gameNode = new GameNode(0, null, board.getSituationOfBoard());
    }

    public void update(Board board, Pieces player) {
        this.board = board;
        this.player = player;
        this.gameNode = new GameNode(0, null, board.getSituationOfBoard());
        this.gameNode.setDistanceAndPieces();
        ComputerPlayer.initialDistance = this.gameNode.distance;
        searchSeries.add(this.gameNode);
    }

    public void update(Board board, Pieces player, int searchDepth) {
        this.searchDepth = searchDepth;
        this.board = board;
        this.player = player;
        this.gameNode = new GameNode(0, null, board.getSituationOfBoard());
        this.gameNode.setDistanceAndPieces();
        finalDepthData = this.gameNode;
        /*   possibleDataAfterSteps[0] = finalDepthData;*/
        ComputerPlayer.initialDistance = this.gameNode.distance;
        searchSeries.add(this.gameNode);
    }

    public void helpOneStep(int turn,Board board,Pieces player){
        this.turn=turn;
        this.searchDepth = 2;
        this.board = board;
        this.player = player;
        this.gameNode = new GameNode(0, null, board.getSituationOfBoard());
        this.gameNode.setDistanceAndPieces();
        finalDepthData = this.gameNode;
        /*   possibleDataAfterSteps[0] = finalDepthData;*/
        ComputerPlayer.initialDistance = this.gameNode.distance;
        searchSeries.add(this.gameNode);
        this.run();
    }
    public void helpOneStep(int turn,Board board,Pieces player, int grade){
        this.turn=turn;
        this.searchDepth = grade;
        this.board = board;
        this.player = player;
        this.gameNode = new GameNode(0, null, board.getSituationOfBoard());
        this.gameNode.setDistanceAndPieces();
        finalDepthData = this.gameNode;
        /*   possibleDataAfterSteps[0] = finalDepthData;*/
        ComputerPlayer.initialDistance = this.gameNode.distance;
        searchSeries.add(this.gameNode);
        this.run();
    }

    private GameNode ifFinalPeriodBeforeWin() {
        searchDepth = 3;
        boolean originalMode=DFS;
        DFS = false;
        GameNode gameNodeSearched = null;
        while (!searchSeries.isEmpty() && searchSeries.get(0).distance != 0) {
            gameNodeSearched = searchSeries.get(0);
            gameNodeSearched.generateUsedInFinal();
            searchSeries.remove(gameNodeSearched);
        }
        GameNode root=searchSeries.get(0);
        while (root.depth > 1)
            root = root.parent;
        DFS=originalMode;
        return root;
    }

    /*  private GameData correctBestStep() {
          if (!isFourPlayer) {
              turn = turn == 2 ? 0 : 2;
              searchDepth = 1;
              double cost = 0;
              GameData result = null;
              for (int a = 0; a != 5; a++) {
                  if (possibleDataAfterSteps[a] != null) {
                      while (possibleDataAfterSteps[a].depth > 2)
                          possibleDataAfterSteps[a] = possibleDataAfterSteps[a].parent;
                      searchSeries = new ArrayList<>();
                      System.out.print(possibleDataAfterSteps[a].depth);
                      searchSeries.add(possibleDataAfterSteps[a]);
                      possibleDataAfterSteps[a].setDistance();
                      double medium = possibleDataAfterSteps[a].distance - generateBestMove().distance;
                      if (cost <= medium) {
                          cost = medium;
                          result = possibleDataAfterSteps[a];
                      }
                  }
              }
              return result;
          }
          return null;
      }
  */
    private GameNode generateBestMove() {
        GameNode root;
        int depth = 1;
        if (DFS) {
            GameNode gameNodeSearched = null;
            while (!searchSeries.isEmpty()) {
                gameNodeSearched = searchSeries.get(0);
                gameNodeSearched.generateNodes();
                searchSeries.remove(gameNodeSearched);
                if (depth == 1 && !searchSeries.isEmpty()) {
                    depth++;
                    searchSeries.sort(Comparator.comparingDouble(o -> o.distance));
                    gameNode = searchSeries.get(0);
                }
            }
            System.gc();
            if (finalDepthData.distance <= 0)
                root = gameNode;
            else {
                root = finalDepthData;
                while (root.depth > 1)
                    root = root.parent;
            }
        } else {
            while (searchSeries.get(0).depth != searchDepth) {
                searchSeries.get(0).generateNodes();
                if (searchSeries.get(0).depth != searchDepth) {
                    searchSeries.remove(0);
                }
                if (depth == 1 && !searchSeries.isEmpty()) {
                    depth++;
                    searchSeries.sort(Comparator.comparingDouble(o -> o.distance));
                    gameNode = searchSeries.get(0);
                }
            }
            System.gc();
            searchSeries.sort(Comparator.comparingDouble(b -> b.distance));
            if (searchSeries.isEmpty() || searchSeries.get(0).distance == 0)
                root = gameNode;
            else {
                root = searchSeries.get(0);
                while (root.depth > 1)
                    root = root.parent;
            }
        }
        /*困难模式*/
       /* if (searchDepth == 3 && DFS) {
            GameData medium = correctBestStep();
            root = medium == null ? root : medium;
        }*/
        return root;
    }

    public void run() {
        if (!searchSeries.isEmpty()) {
            GameNode root;
            if (searchSeries.get(0).distance > 10) {
                DFS = true;
                root = generateBestMove();
            } else {
                if (searchSeries.get(0).distance == 0.5) {
                    root = ifFinalPeriodBeforeWin();
                } else {
                    DFS = false;
                    root = generateBestMove();
                }
            }
            Piece goalPiece = null;
            Point goalPosition = null;
            for (int a = 0; a != 16; a++)
                for (int b = 0; b != 16; b++) {
                    if (board.getSituationOfBoard()[a][b] == player.turn && root.situationOfBoard[a][b] == 4)
                        goalPiece = player.getPieceFromIndex(a, b);
                    if (board.getSituationOfBoard()[a][b] == 4 && root.situationOfBoard[a][b] == player.turn)
                        goalPosition = Board.indexToCoordinate(new Point(a, b));
                }
            if (goalPiece != null && goalPosition != null) {
                board.setTipPosition(goalPiece.toPoint(), goalPosition, turn);
                goalPiece.set(goalPosition, board);
            }
            /*root.setDistance();
            System.out.println("距离:"+root.distance);*/
            searchSeries = new LinkedList<>();
            /*temporarySeries=new ArrayList<>();*/
        }
    }
}

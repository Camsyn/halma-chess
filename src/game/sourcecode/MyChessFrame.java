package game.sourcecode;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;

import javafx.geometry.Point2D;

import javax.imageio.ImageIO;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.LockSupport;

public class MyChessFrame extends JFrame implements Serializable {
    private static final long serialVersionUID = 1;
    private static final int EACH_RECORD_LENGTH = 875;

    private KeyBoard key;

    private final ArrayList<Pieces> players = new ArrayList<>();
    private transient ArrayList<ComputerPlayer> computers = new ArrayList<>();
    private static ComputerPlayer helper = null;
    private Board board = new Board();

    private ArrayList<Point> nextIndexPosition = new ArrayList<>();
    private final StringBuilder recordBoardEachStep = new StringBuilder();

    private double rewindSpeed = 1;

    int audioPlayIndex = 0;
    static Clip BGM;
    static AudioInputStream audioStream;
    int BGM_index;
    static AudioClip soundOfPieceMove;
    static AudioClip commonButtonSound;
    static AudioClip initialButtonSound;
    static BufferedImage myGameBackground;
    static BufferedImage textFrame;
    static ImageIcon dynamicBG = new ImageIcon("resource\\picture\\gif\\Ori_rain.gif");
    private String myBackgroundPath;
    static HashMap<ButtonName, BufferedImage> buttonImages = new HashMap<>();

    static {
        //System.getPropoty（“user.dir”）返回根目录
        try {
            textFrame = ImageIO.read(new File("resource\\picture\\框 (2).png"));
            soundOfPieceMove = Applet.newAudioClip(new File("resource\\sound\\pieceMove.WAV").toURI().toURL());
            commonButtonSound = Applet.newAudioClip(new File("resource\\sound\\commonButtonSound.wav").toURI().toURL());
            initialButtonSound = Applet.newAudioClip(new File("resource\\sound\\initialButtonSound.wav").toURI().toURL());
            buttonImages.put(ButtonName.BIG_BUTTON_STATUS_1, ImageIO.read(new File("resource\\picture\\button_icon\\BigButton_1.png")));
            buttonImages.put(ButtonName.BIG_BUTTON_STATUS_2, ImageIO.read(new File("resource\\picture\\button_icon\\BigButton_2.png")));
            buttonImages.put(ButtonName.BIG_BUTTON_STATUS_3, ImageIO.read(new File("resource\\picture\\button_icon\\BigButton_3.png")));
            buttonImages.put(ButtonName.SMALL_BUTTON_STATUS_1, ImageIO.read(new File("resource\\picture\\button_icon\\SmallButton_1.png")));
            buttonImages.put(ButtonName.SMALL_BUTTON_STATUS_2, ImageIO.read(new File("resource\\picture\\button_icon\\SmallButton_2.png")));
            buttonImages.put(ButtonName.SMALL_BUTTON_STATUS_3, ImageIO.read(new File("resource\\picture\\button_icon\\SmallButton_3.png")));
            buttonImages.put(ButtonName.NEXT_BGM_1, ImageIO.read(new File("resource\\picture\\button_icon\\Next-Button (1).png")));
            buttonImages.put(ButtonName.NEXT_BGM_2, ImageIO.read(new File("resource\\picture\\button_icon\\Next-Button (2).png")));
            buttonImages.put(ButtonName.NEXT_BGM_3, ImageIO.read(new File("resource\\picture\\button_icon\\Next-Button (3).png")));
            buttonImages.put(ButtonName.SOUND_ON, ImageIO.read(new File("resource\\picture\\button_icon\\Sound (Filled).png")));
            buttonImages.put(ButtonName.SOUND_ON_1, ImageIO.read(new File("resource\\picture\\button_icon\\Sound (Filled)_1.png")));
            buttonImages.put(ButtonName.SOUND_OFF, ImageIO.read(new File("resource\\picture\\button_icon\\Mute (Filled).png")));
            buttonImages.put(ButtonName.SOUND_OFF_1, ImageIO.read(new File("resource\\picture\\button_icon\\Mute (Filled)_1.png")));
            buttonImages.put(ButtonName.SOUND_UP_1, ImageIO.read(new File("resource\\picture\\button_icon\\音量UP_1.png")));
            buttonImages.put(ButtonName.SOUND_UP_2, ImageIO.read(new File("resource\\picture\\button_icon\\音量UP_2.png")));
            buttonImages.put(ButtonName.SOUND_UP_3, ImageIO.read(new File("resource\\picture\\button_icon\\音量UP_3.png")));
            buttonImages.put(ButtonName.SOUND_DOWN_1, ImageIO.read(new File("resource\\picture\\button_icon\\音量DOWN_1.png")));
            buttonImages.put(ButtonName.SOUND_DOWN_2, ImageIO.read(new File("resource\\picture\\button_icon\\音量DOWN_2.png")));
            buttonImages.put(ButtonName.SOUND_DOWN_3, ImageIO.read(new File("resource\\picture\\button_icon\\音量DOWN_3.png")));
            buttonImages.put(ButtonName.SWITCH_BUTTON_ON, ImageIO.read(new File("resource\\picture\\button_icon\\按钮_开启.png")));
            buttonImages.put(ButtonName.SWITCH_BUTTON_OFF, ImageIO.read(new File("resource\\picture\\button_icon\\按钮_关闭.png")));
            buttonImages.put(ButtonName.REPEAL_1, ImageIO.read(new File("resource\\picture\\button_icon\\返回 (1).png")));
            buttonImages.put(ButtonName.REPEAL_2, ImageIO.read(new File("resource\\picture\\button_icon\\返回 (2).png")));
            buttonImages.put(ButtonName.REPEAL_3, ImageIO.read(new File("resource\\picture\\button_icon\\返回 (3).png")));
            buttonImages.put(ButtonName.APPROVE_1, ImageIO.read(new File("resource\\picture\\button_icon\\确认 (1).png")));
            buttonImages.put(ButtonName.APPROVE_2, ImageIO.read(new File("resource\\picture\\button_icon\\确认 (2).png")));
            buttonImages.put(ButtonName.APPROVE_3, ImageIO.read(new File("resource\\picture\\button_icon\\确认 (3).png")));
            buttonImages.put(ButtonName.JOIN, ImageIO.read(new File("resource\\picture\\button_icon\\join.png")));
            buttonImages.put(ButtonName.TEXTFRAME, ImageIO.read(new File("resource\\picture\\textFrame.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean startGame = false;
    boolean isChooseMode = false;
    boolean isFourPlayers = false;
    boolean continueGame = true;
    boolean mousePressLock = false;
    boolean launchTips = false;
    boolean hideTips = false;
    boolean isSaveGame = false;
    boolean isLoadGame = false;
    boolean isComputerTurn = false;
    boolean canRewind = false;
    boolean aliveFlag = true;
    boolean isNextStep = false;
    boolean isUpdateRewindSpeed = false;
    boolean isPlayBGM = true;
    boolean audioPlayOn = true;
    boolean isLastStep = false;
    boolean isClearIcon = false;
    boolean isPlayDynamicBackground = false;
    boolean isHost = true;
    private boolean isLink;
    int hintLevel = 1;

    Date date = new Date();//记录游戏时间
    ArrayList<Integer> winOrder = new ArrayList<>();
    int turn = 0;
    int orderOfPieceClicked = -1;
    int period = 0;
    int BGM_choice = 0;
    int moveIntoButton = -1;
    int choice = 0;/*双人对战：1   四人对战：2    人机：3     读档：4   更换BGM：5  开关BGM: 6
                    撤销：11   存档：12    返回菜单：13    开关提示：14   提示等级:15   代行: 16
                   存档按钮：21~27
                      人机模式选择按钮 31~37
                       */

    int[] modeChoose = new int[7];
    int stepNum = 0;
    int clickNum = 0;
    int lastClickNum;
    long timeInMili = 0;
    transient JButton join;
    transient Semaphore lock = new Semaphore(0);
    Point cursorPress = new Point();
    Point cursorRelease = new Point();
    transient Mouse mouse;
    transient Control control;
    transient AudioPlayDetect audio;

    private int myturn = -1;
    private Subject host;
    private Listener guest;
    private boolean isWaitForChoose;


    class MyButton extends JButton {
        boolean canDraw = false;

        public MyButton() {
        }

        public MyButton(Icon icon) {
            super(icon);
        }

        public MyButton(String text) {
            super(text);
        }

        public MyButton(Action a) {
            super(a);
        }

        public MyButton(String text, Icon icon) {
            super(text, icon);
        }

        @Override
        public void repaint() {
            if (canDraw)
                super.repaint();
        }

    }

    transient MyButton changeBG;
    transient MyButton audioSwitchButton;
    transient MyButton clearIconButton;
    transient MyButton switchBgFormation;

    public MyChessFrame() {
        initialButtonSound.play();
        this.setLayout(null);
        this.setFocusable(true);//使Frame可以使用方法强制获得焦点，以启用Frame自身的监听

        File[] bgFiles = new File("resource\\picture\\background").listFiles();
        if (bgFiles != null) {
            myBackgroundPath = bgFiles[(int) (bgFiles.length * Math.random())].getPath();
        }
        try {
            myGameBackground = ImageIO.read(new File(myBackgroundPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*载入游戏的方法*/
    /*添加窗口，添加鼠标响应，等待并初始化棋盘*/
    public void initFrame() throws InterruptedException, IOException {
        setSize(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setTitle("Halma Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mouse = new Mouse();
        addMouseListener(mouse);
        addMouseMotionListener(new MouseMotion());
        control = new Control();
        audio = new AudioPlayDetect();
        while (!startGame && continueGame)
            Thread.sleep(10);
        if (!continueGame || !aliveFlag)
            return;
        if (isFourPlayers) {
            players.add(new Pieces(0, isFourPlayers, board));
            players.add(new Pieces(1, isFourPlayers, board));
            players.add(new Pieces(2, isFourPlayers, board));
            players.add(new Pieces(3, isFourPlayers, board));
            turn = 0;
        } else {
            players.add(new Pieces(0, isFourPlayers, board));
            players.add(new Pieces());
            players.add(new Pieces(2, isFourPlayers, board));
            turn = 0;
        }
        helper = new ComputerPlayer(isFourPlayers);
        repaint();
        new StatusDetect();
        recordBoard();
        lock = new Semaphore(0);
        new ComputerOperation();
    }

    /*加载电脑*/
    private void loadComputers() {
        computers = new ArrayList<>();
        if (!isFourPlayers) {
            if (modeChoose[0] == 2)
                computers.add(new ComputerPlayer(false, 0, board, players.get(0)));
            if (modeChoose[2] == 2)
                computers.add(new ComputerPlayer(false, 2, board, players.get(2)));
        } else {
            for (int a = 0; a != 4; a++)
                if (modeChoose[a] == 2)
                    computers.add(new ComputerPlayer(true, a, board, players.get(a)));
        }
    }

    /*读档后的初始化*/
    private void loadThenInitialize() throws IOException {
        setBounds(Constant.INIT_WINDOW_COORDINATE_X, Constant.INIT_WINDOW_COORDINATE_Y, Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
        for (int a = 0; a != players.size(); a++) {
            players.get(a).setPieceImage(new File("resource\\picture\\" + Constant.COLOR[a] + "棋子.png"));
            for (int b = 0; b != players.get(a).pieces.size(); b++)
                players.get(a).pieces.get(b).set1(players.get(a).pieces.get(b).toPoint());
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        mouse = new Mouse();
        addMouseListener(mouse);
        addMouseMotionListener(new MouseMotion());
        loadComputers();
        helper = new ComputerPlayer(isFourPlayers);
        control = new Control();
        new StatusDetect();
        audio = new AudioPlayDetect();
        new ComputerOperation();
        setVisible(true);
        repaint();
    }


    /*以下为绘制游戏画面的方法*/
    /*最开始的图形显示，即各种按钮显示*/
    private void initializeThenDraw(Graphics g) {
        Color initColor = g.getColor();
        Font initFont = g.getFont();

        if (choice != 1 && moveIntoButton != 1)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_1), 555, 250, 370, 50, null);
        else if (choice == 1)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_3), 555, 250, 370, 50, null);
        else
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_2), 555, 250, 370, 50, null);
        if (choice != 2 && moveIntoButton != 2)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_1), 555, 350, 370, 50, null);
        else if (choice == 2)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_3), 555, 350, 370, 50, null);
        else
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_2), 555, 350, 370, 50, null);
        if (choice != 3 && moveIntoButton != 3)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_1), 555, 450, 370, 50, null);
        else if (choice == 3)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_3), 555, 450, 370, 50, null);
        else
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_2), 555, 450, 370, 50, null);
        if (choice != 4 && moveIntoButton != 4)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_1), 555, 550, 370, 50, null);
        else if (choice == 4)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_3), 555, 550, 370, 50, null);
        else
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_2), 555, 550, 370, 50, null);

        g.setColor(Color.black);
        g.setFont(new Font("宋体", Font.BOLD, 40));
        g.drawString("双人对战", 650, 290);
        g.drawString("四人对战", 650, 390);
        g.drawString("更多模式", 650, 490);
        g.drawString("读档", 690, 590);

        g.setColor(initColor);
        g.setFont(initFont);
    }

    /*在开始界面选择人机对战后，进入的选择界面的绘制*/
    private void chooseModeDraw(Graphics g) {
        Color initColor = g.getColor();
        Font initFont = g.getFont();
        Board.drawPlane(g);
        if (choice != 35 && moveIntoButton != 35)
            g.drawImage(buttonImages.get(ButtonName.APPROVE_1), 1240, 180, 250, 80, null);
        else if (choice == 35)
            g.drawImage(buttonImages.get(ButtonName.APPROVE_3), 1240, 180, 250, 80, null);
        else
            g.drawImage(buttonImages.get(ButtonName.APPROVE_2), 1240, 180, 250, 80, null);
        if (choice != 36 && moveIntoButton != 36)
            g.drawImage(buttonImages.get(ButtonName.REPEAL_1), 1320, 80, 80, 80, null);
        else if (choice == 36)
            g.drawImage(buttonImages.get(ButtonName.REPEAL_3), 1320, 80, 80, 80, null);
        else
            g.drawImage(buttonImages.get(ButtonName.REPEAL_2), 1320, 80, 80, 80, null);
        String computerDegree = "";
        switch (modeChoose[6]) {
            case 0:
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_1), 1100, 500, 300, 100, null);
                computerDegree = "简 单";
                break;
            case 1:
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_2), 1100, 500, 300, 100, null);
                computerDegree = "普 通";
                break;
            case 2:
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_3), 1100, 500, 300, 100, null);
                computerDegree = "困 难";
                break;
            default:
                break;
        }
        g.drawImage(buttonImages.get(ButtonName.TEXTFRAME), 1000, 80, 200, 100, null);
        g.setColor(Color.black);
        g.setFont(new Font("宋体", Font.BOLD, 60));
        g.drawString(isHost ? "房主" : "客人", 1043, 143);
        g.drawString(computerDegree, 1170, 567);
        g.setFont(new Font("宋体", Font.BOLD, 30));
        g.setColor(Color.white);
        g.drawString("点击以切换难度", 1150, 650);
        g.setFont(new Font("宋体", Font.BOLD, 40));
        g.drawString("请点击棋盘四角以选择模式", 930, 350);
        g.drawString("多次点击以切换角色", 980, 410);
        g.setFont(new Font("宋体", Font.BOLD, 120));
        if (modeChoose[0] != 0) {
            g.setColor(Color.black);
            g.drawString(modeChoose[0] == 1 ? "玩家" : "电脑", Constant.BOARD_COORDINATE_X + 20, Constant.BOARD_COORDINATE_Y + 140);
        }
        if (modeChoose[1] != 0) {
            g.setColor(Color.red);
            g.drawString(modeChoose[1] == 1 ? "玩家" : "电脑", Constant.BOARD_COORDINATE_X + 20, Constant.BOARD_COORDINATE_Y + 16 * Grid.HEIGTH - 70);
        }
        if (modeChoose[2] != 0) {
            g.setColor(Color.white);
            g.drawString(modeChoose[2] == 1 ? "玩家" : "电脑", Constant.BOARD_COORDINATE_X + 16 * Grid.WIDTH - 280, Constant.BOARD_COORDINATE_Y + 16 * Grid.HEIGTH - 70);
        }
        if (modeChoose[3] != 0) {
            g.setColor(Color.green);
            g.drawString(modeChoose[3] == 1 ? "玩家" : "电脑", Constant.BOARD_COORDINATE_X + 16 * Grid.WIDTH - 280, Constant.BOARD_COORDINATE_Y + 140);
        }
        g.setColor(initColor);
        g.setFont(initFont);
    }

    /*开始游戏后，游戏内容的绘制*/
    private void gameDraw(Graphics g) {
        Color initColor = g.getColor();
        Font initFont = g.getFont();
        Board.drawPlane(g);
        for (int a = 0; a != players.size(); a++)
            players.get(a).draw(g);
        if (!hideTips) {
            board.drawMoveTipAfterMoving(g);
            if (launchTips && (hintLevel == 1 || hintLevel == 2))
                Board.drawTips(g, nextIndexPosition);
        }
        g.setColor(Color.white);
        g.setFont(new Font("宋体", Font.BOLD, 50));
        g.drawString(isFourPlayers ? "四人战" : "双人战", 1100, 90);
        g.setFont(new Font("宋体", Font.BOLD, 30));
        g.drawString(date.toString(), 970, 140);
        g.drawString("当前行动方：" + Constant.COLOR[turn], 1070, 185);
        g.drawString("游戏时长：" + timeInMili / 1000 + " s", 1100, 230);
        g.drawString("回合数：" + stepNum / (isFourPlayers ? 4 : 2) + " rev", 1130, 275);
        if (continueGame) {
            if (choice != 16 && moveIntoButton != 16)
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_1), 1100, 380, 200, 50, null);
            else if (choice == 16)
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_3), 1100, 380, 200, 50, null);
            else
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_2), 1100, 380, 200, 50, null);
            if (choice != 11 && moveIntoButton != 11)
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_1), 1100, 460, 200, 50, null);
            else if (choice == 11)
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_3), 1100, 460, 200, 50, null);
            else
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_2), 1100, 460, 200, 50, null);
            if (choice != 12 && moveIntoButton != 12)
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_1), 1100, 540, 200, 50, null);
            else if (choice == 12)
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_3), 1100, 540, 200, 50, null);
            else
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_2), 1100, 540, 200, 50, null);
            if (choice != 13 && moveIntoButton != 13)
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_1), 1100, 620, 200, 50, null);
            else if (choice == 13)
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_3), 1100, 620, 200, 50, null);
            else
                g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_2), 1100, 620, 200, 50, null);
            switch (hintLevel) {
                case 0:
                    g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_1), 1100, 700, 200, 50, null);
                    break;
                case 1:
                    g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_2), 1100, 700, 200, 50, null);
                    break;
                case 2:
                    g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_3), 1100, 700, 200, 50, null);
                    break;
                default:
                    break;
            }
            if (hideTips)
                g.drawImage(buttonImages.get(ButtonName.SWITCH_BUTTON_OFF), 1200, 675, 100, 100, null);
            else
                g.drawImage(buttonImages.get(ButtonName.SWITCH_BUTTON_ON), 1200, 675, 100, 100, null);
            g.setColor(Color.black);
            g.drawString("代步", 1170, 415);
            g.drawString("撤销", 1170, 495);
            g.drawString("保存", 1170, 575);
            g.drawString("返回菜单", 1140, 655);
            switch (hintLevel) {
                case 0:
                    g.drawString("路径", 1130, 735);
                    break;
                case 1:
                    g.drawString("提示", 1130, 735);
                    ;
                    break;
                case 2:
                    g.drawString("全知", 1130, 735);
                    break;
                default:
                    break;
            }
            g.drawImage(textFrame, 920, 40, 560, 250, null);
            g.setColor(Color.white);
            g.setFont(new Font("宋体", Font.BOLD, 30));
            g.drawString("拖拽  点击", 1000, 910);
            g.setFont(new Font("宋体", Font.PLAIN, 30));
            g.setColor(Color.lightGray);
            g.drawString("或    操纵棋子", 1070, 910);
        }
        g.setColor(initColor);
        g.setFont(initFont);
    }

    /*存/读档时，各小窗口的游戏信息提示*/
    private static void drawInformationInPanels(Graphics g) throws FileNotFoundException {
        Color initColor = g.getColor();
        Font initFont = g.getFont();
        File file;
        Scanner in;
        String[] originalDate;
        String saveDate;
        String currentPlayer;
        int gameTime;
        for (int n = 1; n != 7; n++) {
            file = new File("resource\\save\\ToGenerateBoardPicture\\save" + n);
            in = new Scanner(file);
            if (in.hasNext()) {
                originalDate = in.nextLine().split(" ");
                saveDate = originalDate[5] + " " + originalDate[1] + originalDate[2] + " " + originalDate[3];

                g.setColor(Color.black);
                g.setFont(new Font("宋体", Font.BOLD, 20));
                g.drawString(saveDate, Constant.RECTANGLE_ARRAY_LIST.get(n - 1).x + 10, Constant.RECTANGLE_ARRAY_LIST.get(n - 1).y + Constant.RECTANGLE_ARRAY_LIST.get(n - 1).height - 7);
                gameTime = in.nextInt();
                currentPlayer = in.next();
                g.drawString("时长:" + gameTime / 1000 + "s", Constant.RECTANGLE_ARRAY_LIST.get(n - 1).x + 230, Constant.RECTANGLE_ARRAY_LIST.get(n - 1).y + Constant.RECTANGLE_ARRAY_LIST.get(n - 1).height - 7);
                g.drawString(currentPlayer, Constant.RECTANGLE_ARRAY_LIST.get(n - 1).x + 340, Constant.RECTANGLE_ARRAY_LIST.get(n - 1).y + Constant.RECTANGLE_ARRAY_LIST.get(n - 1).height - 7);
                g.drawString(in.next(), Constant.RECTANGLE_ARRAY_LIST.get(n - 1).x + 375, Constant.RECTANGLE_ARRAY_LIST.get(n - 1).y + Constant.RECTANGLE_ARRAY_LIST.get(n - 1).height - 7);
                for (int b = 0; b != 16; b++)
                    for (int a = 0; a != 16; a++) {
                        if ((a + b) % 2 == 0)
                            g.setColor(Color.gray);
                        else
                            g.setColor(Color.darkGray);
                        g.fillRect(Constant.RECTANGLE_ARRAY_LIST.get(n - 1).x + a * 25, Constant.RECTANGLE_ARRAY_LIST.get(n - 1).y + b * 25, 25, 25);
                        int piece = in.nextInt();
                        if (piece != 4) {
                            g.setColor(Constant.colorInTurn.get(piece));
                            g.fillOval(Constant.RECTANGLE_ARRAY_LIST.get(n - 1).x + a * 25 + 5,
                                    Constant.RECTANGLE_ARRAY_LIST.get(n - 1).y + b * 25 + 5,
                                    15, 15);
                        }
                    }
            }

            in.close();
        }
        g.setColor(initColor);
        g.setFont(initFont);
    }

    /*存/读档界面的绘制*/
    private void saveOrLoadThenDraw(Graphics g) throws FileNotFoundException {
        Color initColor = g.getColor();
        Font initFont = g.getFont();
        g.setColor(choice == 21 ? Color.black : Color.lightGray);
        g.fillRect(20, 50, 400, 430);
        g.setColor(choice == 22 ? Color.black : Color.lightGray);
        g.fillRect(470, 50, 400, 430);
        g.setColor(choice == 23 ? Color.black : Color.lightGray);
        g.fillRect(920, 50, 400, 430);
        g.setColor(choice == 24 ? Color.black : Color.lightGray);
        g.fillRect(20, 490, 400, 430);
        g.setColor(choice == 25 ? Color.black : Color.lightGray);
        g.fillRect(470, 490, 400, 430);
        g.setColor(choice == 26 ? Color.black : Color.lightGray);
        g.fillRect(920, 490, 400, 430);
        if (choice != 27 && moveIntoButton != 27)
            g.drawImage(buttonImages.get(ButtonName.REPEAL_1), 1380, 80, 80, 80, null);
        else if (choice == 27)
            g.drawImage(buttonImages.get(ButtonName.REPEAL_3), 1380, 80, 80, 80, null);
        else
            g.drawImage(buttonImages.get(ButtonName.REPEAL_2), 1380, 80, 80, 80, null);
        drawInformationInPanels(g);
        g.setColor(initColor);
        g.setFont(initFont);
    }

    /*游戏胜利后的绘制*/
    private void winThenDraw(Graphics g) {
        g.setColor(Color.white);
        try {
            if (!isFourPlayers && winOrder.size() == 1) {
                g.drawImage(ImageIO.read(new File("resource\\picture\\gold.png")), 450, 190, 100, 100, null);
                g.setFont(new Font("宋体", Font.BOLD, 100));
                g.drawString(Constant.COLOR[winOrder.get(0)], 580, 280);
            }
            if (isFourPlayers && winOrder.size() == 3) {
                g.drawImage(ImageIO.read(new File("resource\\picture\\gold.png")), 450, 190, 100, 100, null);
                g.drawImage(ImageIO.read(new File("resource\\picture\\silver.png")), 450, 645, 100, 100, null);
                g.drawImage(ImageIO.read(new File("resource\\picture\\brass.png")), 450, 790, 100, 100, null);
                g.setFont(new Font("宋体", Font.BOLD, 100));
                g.drawString(Constant.COLOR[winOrder.get(0)], 580, 280);
                g.drawString(Constant.COLOR[winOrder.get(1)], 580, 735);
                g.drawString(Constant.COLOR[winOrder.get(2)], 580, 880);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (choice != 2 && moveIntoButton != 2)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_1), 555, 350, 370, 50, null);
        else if (choice == 2)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_3), 555, 350, 370, 50, null);
        else
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_2), 555, 350, 370, 50, null);
        if (choice != 3 && moveIntoButton != 3)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_1), 555, 450, 370, 50, null);
        else if (choice == 3)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_3), 555, 450, 370, 50, null);
        else
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_2), 555, 450, 370, 50, null);
        if (choice != 4 && moveIntoButton != 4)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_1), 555, 550, 370, 50, null);
        else if (choice == 4)
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_3), 555, 550, 370, 50, null);
        else
            g.drawImage(buttonImages.get(ButtonName.BIG_BUTTON_STATUS_2), 555, 550, 370, 50, null);
        g.setColor(Color.black);
        g.setFont(new Font("宋体", Font.BOLD, 40));
        g.drawString("回放", 690, 390);
        g.drawString("返回菜单", 650, 490);
        g.drawString("退出游戏", 650, 590);
    }

    /*回放的绘制*/
    private void rewindThenDraw(Graphics g) {
        Color initColor = g.getColor();
        Font initFont = g.getFont();
        Board.drawPlane(g);
        board.drawMoveTipAfterMoving(g);
        for (int a = 0; a != players.size(); a++)
            players.get(a).draw(g);
        if (choice != 11 && moveIntoButton != 11)
            g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_1), 1100, 460, 200, 50, null);
        else if (choice == 11)
            g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_3), 1100, 460, 200, 50, null);
        else
            g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_2), 1100, 460, 200, 50, null);
        if (choice != 12 && moveIntoButton != 12)
            g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_1), 1100, 540, 200, 50, null);
        else if (choice == 12)
            g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_3), 1100, 540, 200, 50, null);
        else
            g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_2), 1100, 540, 200, 50, null);
        if (!isUpdateRewindSpeed && moveIntoButton != 13)
            g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_1), 1100, 620, 200, 50, null);
        else if (isUpdateRewindSpeed)
            g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_3), 1100, 620, 200, 50, null);
        else
            g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_2), 1100, 620, 200, 50, null);
        g.drawImage(buttonImages.get(ButtonName.SMALL_BUTTON_STATUS_1), 1100, 700, 200, 50, null);
        if (!hideTips)
            g.drawImage(buttonImages.get(ButtonName.SWITCH_BUTTON_OFF), 1200, 675, 100, 100, null);
        else
            g.drawImage(buttonImages.get(ButtonName.SWITCH_BUTTON_ON), 1200, 675, 100, 100, null);
        if (choice != 27)
            g.drawImage(buttonImages.get(ButtonName.REPEAL_1), 1380, 80, 80, 80, null);
        else
            g.drawImage(buttonImages.get(ButtonName.REPEAL_3), 1380, 80, 80, 80, null);
        g.setColor(Color.black);
        g.setFont(new Font("宋体", Font.BOLD, 30));
        g.drawString("前进", 1170, 495);
        g.drawString("回撤", 1170, 575);
        g.drawString("自动", 1130, 735);
        g.setColor(isUpdateRewindSpeed ? Color.white : Color.black);
        g.drawString("倍率:" + rewindSpeed + "X", 1115, 655);
        g.setColor(Color.white);
        g.setFont(new Font("宋体", Font.BOLD, 50));
        g.drawString(isFourPlayers ? "四人战" : "双人战", 1100, 100);
        g.setFont(new Font("宋体", Font.BOLD, 30));
        g.drawString("当前行动方：" + Constant.COLOR[turn], 1070, 220);
        g.drawString("回合数：" + stepNum / (isFourPlayers ? 4 : 2) + " rev", 1130, 320);
        g.setColor(initColor);
        g.setFont(initFont);
    }

    private void audioButtonDraw(Graphics g) {
        Color initColor = g.getColor();
        Font initFont = g.getFont();
        if (BGM_choice != 1 && moveIntoButton != -1)
            g.drawImage(buttonImages.get(ButtonName.NEXT_BGM_1), 1097, 790, 70, 70, null);
        else if (BGM_choice == 1)
            g.drawImage(buttonImages.get(ButtonName.NEXT_BGM_3), 1097, 790, 70, 70, null);
        else
            g.drawImage(buttonImages.get(ButtonName.NEXT_BGM_2), 1097, 790, 70, 70, null);
        if (isPlayBGM) {
            if (moveIntoButton != -2)
                g.drawImage(buttonImages.get(ButtonName.SOUND_ON), 1180, 790, 70, 70, null);
            else
                g.drawImage(buttonImages.get(ButtonName.SOUND_ON_1), 1180, 790, 70, 70, null);
        } else {
            if (moveIntoButton != -2)
                g.drawImage(buttonImages.get(ButtonName.SOUND_OFF), 1180, 790, 70, 70, null);
            else
                g.drawImage(buttonImages.get(ButtonName.SOUND_OFF_1), 1180, 790, 70, 70, null);
        }
        g.setColor(Color.black);
        g.drawOval(1183, 792, 64, 64);
        if (BGM_choice != 3 && moveIntoButton != -3)
            g.drawImage(buttonImages.get(ButtonName.SOUND_UP_1), 1247, 769, 60, 60, null);
        else if (BGM_choice == 3)
            g.drawImage(buttonImages.get(ButtonName.SOUND_UP_3), 1247, 769, 60, 60, null);
        else
            g.drawImage(buttonImages.get(ButtonName.SOUND_UP_2), 1247, 769, 60, 60, null);
        if (BGM_choice != 4 && moveIntoButton != -4)
            g.drawImage(buttonImages.get(ButtonName.SOUND_DOWN_1), 1247, 819, 60, 60, null);
        else if (BGM_choice == 4)
            g.drawImage(buttonImages.get(ButtonName.SOUND_DOWN_3), 1247, 819, 60, 60, null);
        else
            g.drawImage(buttonImages.get(ButtonName.SOUND_DOWN_2), 1247, 819, 60, 60, null);
        g.setColor(initColor);
        g.setFont(initFont);
    }

    /*双缓冲技术，解决屏闪问题*/
    /*不双缓冲为什么闪烁？因为每一次repaint时，程序会擦除当前窗体的一切信息，使窗体显现背景色：白色，然后进行重新绘制，而这一闪而过的
     * 白色背景就构成了我们所谓的闪烁，且重绘频率越高，闪烁频率越高
     * 双缓冲：在内存中创建一块不可见的与当前窗体大小一样的空白图片，在该图片上重绘图像，然后直接将该图片绘制在当前窗体上（直接覆盖），
     * 这样就避免了对窗体内容的擦除，就避免了窗体显现白色背景色，从而避免了闪烁
     * 问：为什么不直接在窗体上进行覆盖式repaint？因为重绘的过程是离散的，一个一个地绘画会使得新旧图案同时存在于窗体上，导致混乱，尤其
     * 刷新频率高时。而双缓冲一次性将一整张图片画在整个窗体上，避免了绘画的异步和新旧的共存*/
    transient BufferedImage offScreenImage = null;
    int a = 0;

    /*调用以上各种绘制方法的总绘制控制方法
     * 不采用重写update方法是因为JFrame不似Frame，在repaint后不通过调用update方法来调用paint方法，而是直接调用paint*/
    private void paintInPicture(Graphics g) {
        Color initColor = g.getColor();
        Font initFont = g.getFont();
        //super.paint(g);//刷新画布
        if (isPlayDynamicBackground) {
            g.drawImage(dynamicBG.getImage(), Constant.INIT_GAME_WINDOW_COORDINATE_X, Constant.INIT_GAME_WINDOW_COORDINATE_Y,
                    Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT, this);//观察者指定为this，即让窗口观察imageIcon，若其有变化，则调用paint
        } else {
            g.drawImage(myGameBackground, Constant.INIT_GAME_WINDOW_COORDINATE_X, Constant.INIT_GAME_WINDOW_COORDINATE_Y,
                    Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT, null);
        }/*以下为对调用哪个绘制方法的具体限制*/
        if (!isClearIcon) {
            audioButtonDraw(g);
            if (!startGame && continueGame && !isSaveGame && !isLoadGame && !isChooseMode)
                initializeThenDraw(g);
            if (startGame && !isSaveGame && !isLoadGame && !canRewind) {
                gameDraw(g);
            }
            if (!continueGame && !canRewind)
                winThenDraw(g);
            if (canRewind)
                rewindThenDraw(g);
            if (isSaveGame || isLoadGame) {
                try {
                    saveOrLoadThenDraw(g);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (isChooseMode) {
                chooseModeDraw(g);
            }
        }

        g.setColor(initColor);
        g.setFont(initFont);
    }

    @Override
    public void paint(Graphics g) {
        if (offScreenImage == null)
            offScreenImage = new BufferedImage(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);//获得图片对象，该对象为空白对象，但大小于Frame窗体一致
        Graphics gOff = offScreenImage.getGraphics();//提取空白图片对象的画笔
        paintInPicture(gOff);//用图片的画笔按照画窗体的画法画图片-->等同于保存了这一帧窗体的截图，改画笔不是窗口画笔，是图片画笔，等价于在内存中作图，速度快
        g.drawImage(offScreenImage, 0, 0, null);//用窗体的画笔把刚刚的截图画在窗体上
      /*  if(startGame){
            try(FileOutputStream fos=new FileOutputStream("C:\\Users\\86171\\Desktop\\cut.jpg")){
                ImageIO.write(offScreenImage,"jpg",fos);
            }catch (Exception e){}
        }*/
        changeBG.repaint();
        clearIconButton.repaint();
        audioSwitchButton.repaint();
        switchBgFormation.repaint();
    }




    /*以下为工具方法，执行具体游戏判断*/

    /*按固定长度输出某个String，用于规范化存储每一步的棋局*/
    private String outputNumberInContentLength(int num, int lenghth) {
        int a = (num + "").length();
        if (a >= lenghth)
            return num + "";
        else {
            StringBuilder content = new StringBuilder();
            content.append(num);
            for (int n = 0; n != lenghth - a; n++)
                content.append(" ");
            return content.toString();
        }
    }

    /*每下一步，然后记录棋局信息*/
    private void recordBoard() {
        recordBoardEachStep.append(turn).append(" ").append(outputNumberInContentLength(stepNum, 5)).append(" ")
                .append(outputNumberInContentLength(board.getLastPostion()[0].x, 4)).append(" ")
                .append(outputNumberInContentLength(board.getLastPostion()[0].y, 4)).append(" ")
                .append(outputNumberInContentLength(board.getCurrentPosition()[0].x, 4)).append(" ")
                .append(outputNumberInContentLength(board.getCurrentPosition()[0].y, 4)).append(" ")
                .append(outputNumberInContentLength(board.getLastPostion()[1].x, 4)).append(" ")
                .append(outputNumberInContentLength(board.getLastPostion()[1].y, 4)).append(" ")
                .append(outputNumberInContentLength(board.getCurrentPosition()[1].x, 4)).append(" ")
                .append(outputNumberInContentLength(board.getCurrentPosition()[1].y, 4)).append(" ")
                .append(outputNumberInContentLength(board.getLastPostion()[2].x, 4)).append(" ")
                .append(outputNumberInContentLength(board.getLastPostion()[2].y, 4)).append(" ")
                .append(outputNumberInContentLength(board.getCurrentPosition()[2].x, 4)).append(" ")
                .append(outputNumberInContentLength(board.getCurrentPosition()[2].y, 4)).append(" ")
                .append(outputNumberInContentLength(board.getLastPostion()[3].x, 4)).append(" ")
                .append(outputNumberInContentLength(board.getLastPostion()[3].y, 4)).append(" ")
                .append(outputNumberInContentLength(board.getCurrentPosition()[3].x, 4)).append(" ")
                .append(outputNumberInContentLength(board.getCurrentPosition()[3].y, 4)).append(" ")
                .append(" ").append(board).append("~");
    }

    /*改变当前行动方*/
    public void turnChange() {
        if (isFourPlayers) {
            turn = turn == 3 ? 0 : turn + 1;
            if (winOrder.contains(turn)) {
                stepNum--;
                turnChange();
            }
        } else
            turn = turn == 2 ? 0 : 2;
        stepNum++;
    }


    /*检测是否胜利，若胜利改变游戏状态*/
    public boolean ifVictory() {
        boolean victory = true;
        for (int a = 0; a != players.get(turn).pieces.size(); a++)
            if (!players.get(turn).pieces.get(a).reachGoalArea) {
                victory = false;
                break;
            }
        if (victory) {
            if (!winOrder.contains(turn))
                winOrder.add(turn);
            if (!isFourPlayers)
                continueGame = false;
            if (isFourPlayers && winOrder.size() == 3)
                continueGame = false;
            repaint();
        }
        return victory;
    }

    /*由新的二维数组的信息，移动一方的棋子*/
    public void movePieceFromTwoDiffBoard(int[][] formerBoard, int[][] latterBoard, Pieces play) {
        Piece goalPiece = null;
        Point goalPosition = null;
        for (int a = 0; a != 16; a++)
            for (int b = 0; b != 16; b++) {
                if (formerBoard[a][b] == play.turn && latterBoard[a][b] == 4)
                    goalPiece = play.getPieceFromIndex(a, b);
                if (formerBoard[a][b] == 4 && latterBoard[a][b] == play.turn)
                    goalPosition = Board.indexToCoordinate(new Point(a, b));
            }
        if (goalPiece != null) {
            goalPiece.set(goalPosition, board);
        }
        for (int a = 0; a != play.pieces.size(); a++) {
            play.isReachGoalArea(a);
        }

    }

    /*由新的二维数组，移动多方的棋子*/
    public void movePiecesFromTwoDiffBoard(int[][] formerBoard, int[][] latterBoard, ArrayList<Pieces> players) {
        Piece[] goalPieces = new Piece[players.size()];
        Point[] goalPositions = new Point[players.size()];
        for (int a = 0; a != 16; a++)
            for (int b = 0; b != 16; b++) {
                for (Pieces play : players) {
                    if (formerBoard[a][b] == play.turn && latterBoard[a][b] == 4) {
                        goalPieces[play.turn] = play.getPieceFromIndex(a, b);
                        break;
                    }
                    if (formerBoard[a][b] == 4 && latterBoard[a][b] == play.turn) {
                        goalPositions[play.turn] = Board.indexToCoordinate(new Point(a, b));
                        break;
                    }

                }
            }
        for (int a = 0; a != players.size(); a++)
            if (goalPieces[a] != null && goalPositions[a] != null)
                goalPieces[a].set(goalPositions[a], board);
    }

    private void nextPositionCorrect(Point point) {
        if (players.get(turn).isGoalArea(point)) {
            ArrayList<Point> nextPositionCorrect = new ArrayList<>();
            for (int a = 0; a != nextIndexPosition.size(); a++)
                if (players.get(turn).isGoalArea(Board.indexToCoordinate(nextIndexPosition.get(a))))
                    nextPositionCorrect.add(nextIndexPosition.get(a));
            nextIndexPosition = nextPositionCorrect;
        }
    }

    /*键盘类，用于启动键盘监听和对键盘输入信息的操作*/
    class KeyBoard extends KeyAdapter {
        StringBuilder speed = new StringBuilder();
        char ch;

        @Override
        public void keyReleased(KeyEvent e) {
            if (canRewind && isUpdateRewindSpeed) {
                ch = e.getKeyChar();
                if (Character.isDigit(ch) || (speed.length() != 0 && ch == '.' && speed.indexOf(".") == -1))
                    speed.append(ch);
                if (speed.length() != 0)
                    rewindSpeed = Double.parseDouble(speed.toString());
                if (rewindSpeed > 100)
                    rewindSpeed = 100;
                if (rewindSpeed < 0)
                    rewindSpeed = 0;
                if (ch == '\n') {
                    isUpdateRewindSpeed = false;
                    MyChessFrame.this.updateRewindSpeed();
                    audioPlayIndex = 3;
                }
                repaint();
            } else {
                ch = '\0';
                speed = new StringBuilder();
            }
        }
    }

    private boolean isWaitForRelease = false;


    private transient Thread t;

    /*鼠标类，用于启动鼠标监听和对鼠标输入信息的操作*/
    class Mouse extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (!mousePressLock) {
                t = Thread.currentThread();
                clickNum++;
                isWaitForChoose = false;
                cursorPress.setLocation(e.getX(), e.getY());
                isWaitForRelease = false;
                lock.release(2);
                if (period == 0 && startGame && !isChooseMode)
                    period++;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            cursorRelease.setLocation(e.getX(), e.getY());
            while (!isWaitForRelease) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            LockSupport.unpark(control);
            LockSupport.unpark(audio);

        }

    }

    class MouseMotion extends MouseMotionAdapter {
        boolean flag = false;
        int hashcode = -1;

        @Override
        public void mouseMoved(MouseEvent e) {
            if (startGame && continueGame && Constant.BOARD_RECT.contains(e.getPoint())) {
                if (hintLevel == 2 && orderOfPieceClicked == -1) {
                    for (int i = 0; i != players.get(turn).pieces.size(); i++) {
                        if (players.get(turn).pieces.get(i).area.contains(e.getX(), e.getY())) {
                            launchTips = true;
                            flag = true;
                            if (hashcode != players.get(turn).pieces.get(i).hashCode()) {
                                nextIndexPosition = board.returnNextIndexPosition(players.get(turn).pieces.get(i));
                                nextPositionCorrect(players.get(turn).pieces.get(i).toPoint());
                                MyChessFrame.this.repaint();
                                hashcode = players.get(turn).pieces.get(i).hashCode();
                            }
                        }
                    }
                    if (!flag) {
                        if (launchTips) {
                            launchTips = false;
                            MyChessFrame.this.repaint();
                        }
                    }
                    flag = false;
                }
            } else {
                hashcode = -1;
                if (launchTips && orderOfPieceClicked == -1) {
                    launchTips = false;
                    MyChessFrame.this.repaint();
                }
                if (Constant.REPEAL_BUTTON.contains(e.getPoint())) {
                    if (moveIntoButton != 11 && (startGame)) {
                        moveIntoButton = 11;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.SAVE_GAME_BUTTON.contains(e.getPoint())) {
                    if (moveIntoButton != 12 && startGame) {
                        moveIntoButton = 12;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.RETURN_MENU_BUTTON.contains(e.getPoint())) {
                    if (moveIntoButton != 13 && startGame) {
                        moveIntoButton = 13;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.HELP_ONESTEP_BUTTON.contains(e.getPoint())) {
                    if (moveIntoButton != 16 && startGame) {
                        moveIntoButton = 16;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.TWO_PLAYER_BUTTON.contains(e.getPoint())) {
                    if (moveIntoButton != 1 && ((!continueGame && !canRewind) || !startGame)) {
                        moveIntoButton = 1;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.FOUR_PLAYER_BUTTON.contains(e.getPoint())) {
                    if (moveIntoButton != 2 && ((!continueGame && !canRewind) || !startGame)) {
                        moveIntoButton = 2;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.AI_BUTTON.contains(e.getPoint())) {
                    if (moveIntoButton != 3 && ((!continueGame && !canRewind) || !startGame)) {
                        moveIntoButton = 3;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.LOAD_BUTTON.contains(e.getPoint())) {
                    if (moveIntoButton != 4 && ((!continueGame && !canRewind) || !startGame)) {
                        moveIntoButton = 4;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.NEXT_BGM_BUTTON.contains(e.getPoint())) {
                    if (moveIntoButton != -1) {
                        moveIntoButton = -1;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.SWITCH_BGM_BUTTON.contains(e.getX(), e.getY())) {
                    if (moveIntoButton != -2) {
                        moveIntoButton = -2;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.BGM_UP_BUTTON.contains(e.getX(), e.getY())) {
                    if (moveIntoButton != -3) {
                        moveIntoButton = -3;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.BGM_DOWN_BUTTON.contains(e.getPoint().x, e.getPoint().y)) {
                    if (moveIntoButton != -4) {
                        moveIntoButton = -4;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.RECTANGLE_ARRAY_LIST.get(6).contains(e.getPoint())) {
                    if (moveIntoButton != 27 && (isSaveGame || isLoadGame)) {
                        moveIntoButton = 27;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.CHOOSE_MODE_BUTTONS[5].contains(e.getX(), e.getY())) {
                    if (moveIntoButton != 36 && isChooseMode) {
                        moveIntoButton = 36;
                        MyChessFrame.this.repaint();
                    }
                } else if (Constant.CHOOSE_MODE_BUTTONS[4].contains(e.getX(), e.getY())) {
                    if (moveIntoButton != 35 && isChooseMode) {
                        moveIntoButton = 35;
                        MyChessFrame.this.repaint();
                    }
                } else {
                    if (moveIntoButton != 0) {
                        moveIntoButton = 0;
                        MyChessFrame.this.repaint();
                    }
                }
            }
        }
    }

    /*新线程：操作类，用于对游戏内容进行操作，其中包含实现各种操作的具体方法*/
    class Control extends Thread {
        Rewind rewind = null;

        Control() {
            changeBG = new MyButton("易图");
            changeBG.setBounds(1400, 850, 70, 30);
            changeBG.addActionListener(e -> {
                initialButtonSound.play();
                changeBG.repaint();
                File[] bgFiles1 = new File(isPlayDynamicBackground ? "resource\\picture\\gif" : "resource\\picture\\background").listFiles();
                if (bgFiles1 != null) {
                    String path;
                    while (myBackgroundPath.equals(path = bgFiles1[(int) (bgFiles1.length * Math.random())].getPath()) && bgFiles1.length > 1)
                        ;
                    myBackgroundPath = path;
                }
                try {
                    if (isPlayDynamicBackground)
                        dynamicBG = new ImageIcon(myBackgroundPath);
                    else
                        myGameBackground = ImageIO.read(new File(myBackgroundPath));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                MyChessFrame.super.repaint();
                MyChessFrame.this.requestFocus();//使Frame获得焦点，以使得Frame自身的键盘监听生效
            });
            changeBG.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((MyButton) e.getSource()).canDraw = true;
                    MyChessFrame.this.repaint();
                    MyChessFrame.this.requestFocus();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((MyButton) e.getSource()).canDraw = false;
                    MyChessFrame.this.repaint();
                    MyChessFrame.this.requestFocus();
                }
            });
            audioSwitchButton = new MyButton("音效");
            audioSwitchButton.setBounds(1310, 850, 70, 30);
            audioSwitchButton.addActionListener(e -> {
                initialButtonSound.play();
                MyChessFrame.this.requestFocus();
                audioPlayOn = !audioPlayOn;
            });
            audioSwitchButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((MyButton) e.getSource()).canDraw = true;
                    MyChessFrame.this.repaint();
                    MyChessFrame.this.requestFocus();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((MyButton) e.getSource()).canDraw = false;
                    MyChessFrame.this.repaint();
                    MyChessFrame.this.requestFocus();
                }
            });
            clearIconButton = new MyButton("清屏");
            clearIconButton.setBounds(1220, 850, 70, 30);
            clearIconButton.addActionListener(e -> {
                initialButtonSound.play();
                isClearIcon = !isClearIcon;
                if (isClearIcon)
                    ((MyButton) e.getSource()).setText("恢复");
                else
                    ((MyButton) e.getSource()).setText("清屏");
                MyChessFrame.this.repaint();
                MyChessFrame.this.requestFocus();
            });
            clearIconButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((MyButton) e.getSource()).canDraw = true;
                    MyChessFrame.this.repaint();
                    MyChessFrame.this.requestFocus();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((MyButton) e.getSource()).canDraw = false;
                    MyChessFrame.this.repaint();
                    MyChessFrame.this.requestFocus();
                }
            });
            switchBgFormation = new MyButton("静态");
            switchBgFormation.setBounds(1130, 850, 70, 30);
            switchBgFormation.addActionListener(e -> {
                initialButtonSound.play();
                isPlayDynamicBackground = !isPlayDynamicBackground;
                if (isPlayDynamicBackground) {
                    myBackgroundPath = "resource\\picture\\gif\\Ori_rain.gif";
                    ((MyButton) e.getSource()).setText("动态");
                } else {
                    myBackgroundPath = "resource\\picture\\background\\illust_78408638_20191226_134758.png";
                    ((MyButton) e.getSource()).setText("静态");
                }
                MyChessFrame.this.repaint();
                MyChessFrame.this.requestFocus();
            });
            switchBgFormation.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((MyButton) e.getSource()).canDraw = true;
                    MyChessFrame.this.repaint();
                    MyChessFrame.this.requestFocus();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((MyButton) e.getSource()).canDraw = false;
                    MyChessFrame.this.repaint();
                    MyChessFrame.this.requestFocus();
                }
            });
            MyChessFrame.this.add(changeBG);
            MyChessFrame.this.add(audioSwitchButton);
            MyChessFrame.this.add(clearIconButton);
            MyChessFrame.this.add(switchBgFormation);
            MyChessFrame.this.setVisible(true);
            this.start();
        }

        /*实现撤销方法*/
        private void repeal() {
            if (isComputerTurn) {
                isComputerTurn = false;
                while (!isComputerTurn) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (recordBoardEachStep.length() > 794)
                recordBoardEachStep.delete(recordBoardEachStep.length() - EACH_RECORD_LENGTH, recordBoardEachStep.length());
            String data = recordBoardEachStep.toString().split("~")[recordBoardEachStep.toString().split("~").length - 1];
            Scanner in = new Scanner(data);
            if (in.hasNext()) {
                orderOfPieceClicked = -1;
                turn = in.nextInt();
                stepNum = in.nextInt();
                for (int a = 0; a != 4; a++)
                    board.setTipPosition(new Point(in.nextInt(), in.nextInt()), new Point(in.nextInt(), in.nextInt()), a);
                int[][] goalBoard = new int[16][16];
                for (int a = 0; a != 16; a++)
                    for (int b = 0; b != 16; b++) {
                        goalBoard[a][b] = in.nextInt();
                    }
                movePiecesFromTwoDiffBoard(board.getSituationOfBoard(), goalBoard, players);
                repaint();
            }

            /*解决四人模式中，有一方已经锁定了胜利状态，而出现撤销的情况*/
            if (isFourPlayers) {
                if (computers.isEmpty()) {
                    for (int a = 0; a != players.get(turn).pieces.size(); a++)
                        players.get(turn).isReachGoalArea(a);
                    if (!ifVictory() && winOrder.contains(turn))
                        winOrder.remove(turn);
                } else {
                    int num = turn;
                    do {
                        for (int a = 0; a != players.get(turn).pieces.size(); a++)
                            players.get(turn).isReachGoalArea(a);
                        if (!ifVictory() && winOrder.contains(turn)) {
                            winOrder.remove(turn);
                        }
                        turn = turn == 3 ? 0 : turn + 1;
                    } while (num != turn);
                }
            }
            in.close();
        }

        /*实现读档的方法*/
        private void load(int num) {
            File file = new File("resource\\save\\save" + num);
            if (file.length() != 0) {
                FileInputStream fin = null;
                ObjectInputStream gameIn = null;
                try {
                    fin = new FileInputStream(file);
                    gameIn = new ObjectInputStream(fin);
                    MyChessFrame myChessFrame = (MyChessFrame) gameIn.readObject();
                    MyChessFrame.this.continueGame = false;
                    MyChessFrame.this.aliveFlag = false;
                    MyChessFrame.this.setVisible(false);
                    MyChessFrame.this.removeMouseListener(mouse);
                    try {
                        if (myChessFrame.isPlayDynamicBackground) {
                            MyChessFrame.dynamicBG = new ImageIcon(myChessFrame.myBackgroundPath);
                        } else
                            myChessFrame.myGameBackground = ImageIO.read(new File(myChessFrame.myBackgroundPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myChessFrame.repaint();
                    myChessFrame.loadThenInitialize();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (fin != null) {
                        try {
                            fin.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (gameIn != null) {
                        try {
                            gameIn.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /*实现存档的方法，这里采用序列化对象的方式存档*/
        private void save(int num) {
            PrintStream out = null;
            File file = null;
            FileOutputStream fout = null;
            ObjectOutputStream gameOut = null;
            try {
                out = new PrintStream(new File("resource\\save\\ToGenerateBoardPicture\\save" + num));
                out.print(date.toString() + "\n");
                out.print(MyChessFrame.this.timeInMili + "\n");
                out.print(Constant.COLOR[MyChessFrame.this.turn].split("色")[0] + "\n");
                out.print(computers.isEmpty() ? "人\n" : "机\n");
                out.print(board);
                file = new File("resource\\save\\save" + num);
                fout = new FileOutputStream(file);
                gameOut = new ObjectOutputStream(fout);

                MyChessFrame.this.remove(changeBG);
                MyChessFrame.this.remove(clearIconButton);
                MyChessFrame.this.remove(audioSwitchButton);
                MyChessFrame.this.remove(switchBgFormation);
                MyChessFrame.this.isSaveGame = false;
                gameOut.writeObject(MyChessFrame.this);
                MyChessFrame.this.isSaveGame = true;
                MyChessFrame.this.add(changeBG);
                MyChessFrame.this.add(clearIconButton);
                MyChessFrame.this.add(audioSwitchButton);
                MyChessFrame.this.add(switchBgFormation);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (gameOut != null) {
                        gameOut.close();
                    }
                    if (fout != null) {
                        fout.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            repaint();
        }

        /*实现回放的方法*/
        private void rewind() {
            if (rewind != null) {
                while (rewind.isAlive()) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            canRewind = true;
            rewind = new Rewind();
        }

        /*实现返回菜单的方法*/
        private void returnMenu() throws InterruptedException, IOException {
            /*彻底关闭旧的MyChessGame对象*/
            MyChessFrame.this.continueGame = false;
            MyChessFrame.this.aliveFlag = false;
            MyChessFrame.this.removeMouseListener(mouse);
            /*启用新的对象*/
            MyChessFrame newGame = new MyChessFrame();
            MyChessFrame.this.dispose();
            newGame.initFrame();
        }


        private int size = 0;

        /*以下为选择方法，即对不同按钮具体意义的实现*/
        /*更多模式的选择*/
        private void chooseMode() throws InterruptedException {
            if (null != host && size < host.size()) {
                size++;
                host.setData(modeChoose);
                isLink = true;
                if (size == 1) {
                    new Link();
                }
            }
            if (null != host && host.size() == 0) {
                isLink = false;
                size = 0;
            }

            if (clickNum > lastClickNum) {
                isWaitForChoose = true;
                lastClickNum = clickNum;
                lock.acquire();
                isWaitForRelease = true;
                if (startGame) {
                    return;
                }
                if (period == 1)
                    period = 0;
                int order = 0;
                for (; order != 7; order++) {
                    if (Constant.CHOOSE_MODE_BUTTONS[order].contains(cursorPress)) {
                        choice = 31 + order;
                        repaint();
                        break;
                    }
                }
                LockSupport.park();
                choice = 0;

                repaint();
                if (order != 7)
                    if (Constant.CHOOSE_MODE_BUTTONS[order].contains(cursorRelease)) {
                        if (order < 4 || order == 6 || order == 5)
                            audioPlayIndex = 3;
                        else
                            audioPlayIndex = 2;

                        if (!isLink) {
                            modeChoose[order] = modeChoose[order] == 2 ? 0 : modeChoose[order] + 1;
                            if (modeChoose[order] == 1) {
                                myturn = order;
                            }
                        } else {
                            if (order < 4) {
                                if (modeChoose[order] == 0) {
                                    if (!isHost) {
                                        if (myturn >= 0) {
                                            modeChoose[myturn] = 0;
                                        }
                                        myturn = order;
                                        modeChoose[order]++;
                                        guest.update(modeChoose);
                                    } else {
                                        if (myturn == -1) {
                                            modeChoose[order]++;
                                            myturn = order;
                                        } else {
                                            modeChoose[order] = 2;
                                        }
                                        host.setData(modeChoose);
                                    }
                                } else if (modeChoose[order] == 1 && myturn == order) {
                                    modeChoose[order] = 0;
                                    if (isHost) {
                                        host.setData(modeChoose);
                                    } else {
                                        guest.update(modeChoose);
                                    }
                                    myturn = -1;
                                } else if (modeChoose[order] == 2 && isHost) {
                                    modeChoose[order] = 0;
                                    host.setData(modeChoose);
                                }
                            } else {
                                if (isHost) {
                                    modeChoose[order] = modeChoose[order] == 2 ? 0 : modeChoose[order] + 1;
                                } else {
                                    if (order != 4 && order != 6) {
                                        modeChoose[order] = modeChoose[order] == 2 ? 0 : modeChoose[order] + 1;
                                    }
                                }
                                if (isHost) {
                                    host.setData(modeChoose);
                                }
                            }
                        }
                    }
                if (modeChoose[5] != 0) {
                    if (isLink) {
                        try {
                            if (guest != null) {
                                modeChoose[myturn] = 0;
                                modeChoose[5] = 0;
                                myturn = -1;
                                guest.update(modeChoose);
                                guest.close();
                                guest = null;
                                isHost = true;
                                isLink = false;
                                for (int a = 0; a != 5; a++)
                                    modeChoose[a] = 0;
                            }
                            if (null != host) {
                                for (int a = 0; a != 5; a++)
                                    modeChoose[a] = 0;
                                modeChoose[5] = 1;
                                myturn = -1;
                                host.setData(modeChoose);
                                host.close();
                                isLink = false;
                                host = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    for (int a = 0; a != 6; a++)
                        modeChoose[a] = 0;
                    isChooseMode = false;
                }

                if (modeChoose[4] != 0) {
                    if (judgeModeValid() == 2) {
                        audioPlayIndex = 2;
                        isFourPlayers = false;
                        isChooseMode = false;
                        startGame = true;
                        if (modeChoose[0] == 2)
                            computers.add(new ComputerPlayer(false, 0, board));
                        if (modeChoose[2] == 2)
                            computers.add(new ComputerPlayer(false, 2, board));
                    } else if (judgeModeValid() == 0) {
                        audioPlayIndex = 2;
                        isFourPlayers = true;
                        startGame = true;
                        isChooseMode = false;
                        for (int a = 0; a != 4; a++)
                            if (modeChoose[a] == 2)
                                computers.add(new ComputerPlayer(true, a, board));
                    } else {
                        modeChoose[4] = 0;
                    }
                    MyChessFrame.this.remove(join);
                }
                isWaitForChoose = false;
            }
        }

        JFrame joinFrame = null;

        /*开始菜单的选择*/
        private void initialChoose() throws InterruptedException {
            if (clickNum > lastClickNum) {
                lastClickNum = clickNum;
                lock.acquire();
                isWaitForRelease = true;
                if (Constant.TWO_PLAYER_BUTTON.contains(cursorPress))
                    choice = 1;
                if (Constant.FOUR_PLAYER_BUTTON.contains(cursorPress))
                    choice = 2;
                if (Constant.AI_BUTTON.contains(cursorPress))
                    choice = 3;
                if (Constant.LOAD_BUTTON.contains(cursorPress))
                    choice = 4;
                repaint();
                LockSupport.park();
                switch (choice) {
                    case 1:
                        if (Constant.TWO_PLAYER_BUTTON.contains(cursorRelease)) {
                            audioPlayIndex = 2;
                            isFourPlayers = false;
                            startGame = true;
                        }
                        choice = 0;
                        break;
                    case 2:
                        if (Constant.FOUR_PLAYER_BUTTON.contains((cursorRelease))) {
                            audioPlayIndex = 2;
                            isFourPlayers = true;
                            startGame = true;
                        }
                        choice = 0;
                        break;
                    case 3:
                        if (Constant.AI_BUTTON.contains(cursorRelease)) {

                            audioPlayIndex = 2;
                            isChooseMode = true;
                            if (null == host) {
                                host = new Subject();
                            }
                            join = new JButton();
                            join.setText("加入房间");
                            join.setBounds(1000, 150, 150, 50);
                            join.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (joinFrame == null) {
                                        joinFrame = new JFrame("加入房间");
                                        joinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                        JTextField field = new JTextField();
                                        joinFrame.add(field);
                                        JButton match = new JButton("匹配");
                                        match.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                try {
                                                    guest = new Listener(field.getText());
                                                    isHost = false;
                                                    isLink = true;
                                                    new Link();
                                                    host.close();
                                                    host = null;
                                                    joinFrame.dispose();
                                                    joinFrame = null;
                                                    MyChessFrame.this.repaint();
                                                } catch (Exception exception) {
                                                    return;
                                                }
                                            }
                                        });
                                        joinFrame.add(match);
                                        joinFrame.setLayout(new GridLayout());
                                        joinFrame.setLocationRelativeTo(null);
                                        joinFrame.pack();
                                        joinFrame.setVisible(true);
                                    } else {
                                        joinFrame.setFocusable(true);
                                        joinFrame.requestFocus();
                                    }
                                }
                            });
                            MyChessFrame.this.add(join);
                        }
                        choice = 0;
                        break;
                    case 4:
                        if (Constant.LOAD_BUTTON.contains(cursorRelease)) {
                            audioPlayIndex = 2;
                            isLoadGame = true;
                        }
                        choice = 0;
                        break;
                    default:
                        choice = 0;
                }
                repaint();
            }
        }

        /*游戏进行中的选择*/
        private void chooseInGame() throws InterruptedException, IOException {
            if (clickNum > lastClickNum) {
                lastClickNum = clickNum;
                if (period == 1)
                    period = 0;
                if (Constant.REPEAL_BUTTON.contains(cursorPress))
                    choice = 11;
                if (Constant.SAVE_GAME_BUTTON.contains(cursorPress))
                    choice = 12;
                if (Constant.RETURN_MENU_BUTTON.contains(cursorPress))
                    choice = 13;
                if (Constant.SWITCH_TIPS_BUTTON.contains(cursorPress))
                    choice = 14;
                if (Constant.HINT_LEVEL_BUTTON.contains(cursorPress) && !Constant.SWITCH_TIPS_BUTTON.contains(cursorPress))
                    choice = 15;
                if (Constant.HELP_ONESTEP_BUTTON.contains(cursorPress))
                    choice = 16;
                repaint();
                LockSupport.park();
                switch (choice) {
                    case 11:
                        if (Constant.REPEAL_BUTTON.contains(cursorRelease) && stepNum != 0
                                && orderOfPieceClicked == -1 && !isLink) {
                            audioPlayIndex = 3;
                            repeal();
                        }
                        choice = 0;
                        repaint();
                        break;
                    case 12:
                        if (Constant.SAVE_GAME_BUTTON.contains(cursorRelease) && !isLink) {
                            audioPlayIndex = 3;
                            isSaveGame = true;
                        }
                        choice = 0;
                        repaint();
                        break;
                    case 13:
                        if (Constant.RETURN_MENU_BUTTON.contains((cursorRelease)) && !isLink) {
                            audioPlayIndex = 3;
                            returnMenu();
                        }
                        choice = 0;
                        repaint();
                        break;
                    case 14:
                        if (Constant.SWITCH_TIPS_BUTTON.contains(cursorRelease)) {
                            audioPlayIndex = 3;
                            hideTips = !hideTips;
                            choice = 0;
                            repaint();
                        }
                        break;
                    case 15:
                        if (Constant.HINT_LEVEL_BUTTON.contains(cursorRelease) && !Constant.SWITCH_TIPS_BUTTON.contains(cursorRelease)) {
                            hintLevel = hintLevel == 2 ? 0 : hintLevel + 1;
                            repaint();
                        }
                        break;
                    case 16:
                        if (Constant.HELP_ONESTEP_BUTTON.contains(cursorRelease)) {
                            if (isLink && turn == myturn) {
                                helper.helpOneStep(turn, board, players.get(turn));
                                if (isHost) {
                                    host.setData(Board.clone(board.getSituationOfBoard()));
                                } else {
                                    guest.update(Board.clone(board.getSituationOfBoard()));
                                }
                                for (int a = 0; a != players.get(turn).pieces.size(); a++)
                                    players.get(turn).isReachGoalArea(a);
                                launchTips = false;
                                audioPlayIndex = 1;
                                repaint();
                                period = 0;
                                if (orderOfPieceClicked != -1)
                                    players.get(turn).pieces.get(orderOfPieceClicked).ifClickedThenChange();
                                ifVictory();
                                turnChange();
                                orderOfPieceClicked = -1;
                                recordBoard();
                            }
                            if (!isLink) {
                                helper.helpOneStep(turn, board, players.get(turn));
                                for (int a = 0; a != players.get(turn).pieces.size(); a++)
                                    players.get(turn).isReachGoalArea(a);
                                launchTips = false;
                                audioPlayIndex = 1;
                                repaint();
                                period = 0;
                                if (orderOfPieceClicked != -1)
                                    players.get(turn).pieces.get(orderOfPieceClicked).ifClickedThenChange();
                                ifVictory();
                                turnChange();
                                orderOfPieceClicked = -1;
                                recordBoard();
                            }
                        }
                        choice = 0;
                        repaint();
                        break;
                    default:
                        choice = 0;
                        repaint();
                }
            }
        }


        /*存档或读档时的选择*/
        private void chooseInSaveOrLoad() throws InterruptedException, IOException, ClassNotFoundException {
            if (clickNum > lastClickNum) {
                lastClickNum = clickNum;
                lock.acquire();
                isWaitForRelease = true;
                if (period == 1)
                    period = 0;
                int a = 0;
                for (; a != Constant.RECTANGLE_ARRAY_LIST.size(); a++)
                    if (Constant.RECTANGLE_ARRAY_LIST.get(a).contains(cursorPress)) {
                        choice = 21 + a;
                        break;
                    }
                repaint();
                LockSupport.park();

                choice = 0;
                repaint();
                if (a == 7)
                    return;

                if (Constant.RECTANGLE_ARRAY_LIST.get(a).contains(cursorRelease)) {
                    if (a == 6) {
                        audioPlayIndex = 3;
                        isSaveGame = false;
                        isLoadGame = false;
                    } else if (isLoadGame) {
                        audioPlayIndex = 2;
                        load(++a);
                    } else {
                        audioPlayIndex = 2;
                        save(++a);
                    }
                }
            }
        }

        /*游戏结束后的选择*/
        private void gameOverThenChoose() {
            if (clickNum > lastClickNum) {
                lastClickNum = clickNum;
                try {
                    lock.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isWaitForRelease = true;
                if (Constant.FOUR_PLAYER_BUTTON.contains(cursorPress))
                    choice = 2;
                if (Constant.AI_BUTTON.contains(cursorPress))
                    choice = 3;
                if (Constant.LOAD_BUTTON.contains(cursorPress))
                    choice = 4;
                repaint();
                LockSupport.park();


                switch (choice) {
                    case 2:
                        if (Constant.FOUR_PLAYER_BUTTON.contains((cursorRelease))
                                && ((isFourPlayers && computers.size() != 4) || (!isFourPlayers && computers.size() != 2))) {
                            audioPlayIndex = 2;
                            rewind();
                        }
                        choice = 0;
                        break;
                    case 3:
                        if (Constant.AI_BUTTON.contains(cursorRelease)) {
                            audioPlayIndex = 2;
                            try {
                                try {
                                    returnMenu();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        choice = 0;
                        break;
                    case 4:
                        if (Constant.LOAD_BUTTON.contains(cursorRelease)) {
                            audioPlayIndex = 2;
                            System.exit(0);
                        }
                        choice = 0;
                        break;
                    default:
                        choice = 0;
                }
                repaint();
            }
        }

        /*回放棋局时的选择*/
        private void rewindThenChoose() {
            if (clickNum > lastClickNum) {
                lastClickNum = clickNum;
                try {
                    lock.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isWaitForRelease = true;
                if (period == 1)
                    period = 0;
                /*此处的按钮功能都不如它们的名字所示，仅仅是复用了游戏当中的各种按钮，用以实现不同的功能
                 * 这些按钮实则由上往下依次是
                 * 前进一步
                 * 后退一步
                 * 启用自动播放速率修改
                 * 自动播放开关
                 * 返回上个界面*/
                if (Constant.REPEAL_BUTTON.contains(cursorPress))
                    choice = 11;
                if (Constant.SAVE_GAME_BUTTON.contains(cursorPress))
                    choice = 12;
                if (Constant.RETURN_MENU_BUTTON.contains(cursorPress))
                    choice = 13;
                if (Constant.SWITCH_TIPS_BUTTON.contains(cursorPress))
                    choice = 14;
                if (Constant.RECTANGLE_ARRAY_LIST.get(6).contains(cursorPress))
                    choice = 27;
                repaint();
                LockSupport.park();
                switch (choice) {
                    case 11:
                        if (Constant.REPEAL_BUTTON.contains(cursorRelease)) {
                            audioPlayIndex = 3;
                            isNextStep = true;
                        }
                        choice = 0;
                        repaint();
                        break;
                    case 12:
                        if (Constant.SAVE_GAME_BUTTON.contains(cursorRelease)) {
                            audioPlayIndex = 3;
                            isLastStep = true;
                        }
                        choice = 0;
                        repaint();
                        break;
                    case 13:
                        if (Constant.RETURN_MENU_BUTTON.contains((cursorRelease))) {
                            isUpdateRewindSpeed = !isUpdateRewindSpeed;
                            updateRewindSpeed();
                            audioPlayIndex = 3;
                        }
                        choice = 0;
                        repaint();
                        break;
                    case 14:
                        if (Constant.SWITCH_TIPS_BUTTON.contains(cursorRelease)) {
                            audioPlayIndex = 3;
                            /*此处的hideTips并非是隐藏提示开关，仅仅复用了这个变量，用以表示是否自动播放*/
                            hideTips = !hideTips;
                            isNextStep = true;
                            choice = 0;
                            repaint();
                        }
                        break;
                    case 27:
                        if (Constant.RECTANGLE_ARRAY_LIST.get(6).contains(cursorRelease)) {
                            audioPlayIndex = 3;
                            isNextStep = true;
                            canRewind = false;
                        }
                        choice = 0;
                        repaint();
                        break;
                    default:
                        choice = 0;
                        repaint();
                        break;
                }
            }
        }


        /*若抵达到目标区域，则对下一步可行区域进行修正*/


        /*根据当前选择的棋子，输入棋局坐标，移动棋子*/
        private void pieceMove(Point indexPoint) {
            board.setTipPosition(players.get(turn).pieces.get(orderOfPieceClicked).toPoint(), Board.indexToCoordinate(indexPoint), turn);
            players.get(turn).pieces.get(orderOfPieceClicked).set(Board.indexToCoordinate(indexPoint), board);
            players.get(turn).pieces.get(orderOfPieceClicked).ifClickedThenChange();
            players.get(turn).isReachGoalArea(orderOfPieceClicked);
            launchTips = false;
            audioPlayIndex = 1;
            repaint();
            period = 0;
            ifVictory();
            turnChange();
            orderOfPieceClicked = -1;
            recordBoard();
        }

        /*对以上方法的具体控制与调用*/
        @Override
        public void run() {
            while (!startGame && continueGame) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isLoadGame && !isChooseMode) {
                    try {
                        initialChoose();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (isChooseMode && !isLoadGame) {
                    try {
                        chooseMode();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        chooseInSaveOrLoad();
                    } catch (InterruptedException | IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            while (continueGame) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isSaveGame && !isLink) {
                    try {
                        lock.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isWaitForRelease = true;
                    if (Constant.BOARD_RECT.contains(cursorPress) && !isComputerTurn) {
                        if (period == 1) {
                            for (int a = 0; a != players.get(turn).pieces.size(); a++)
                                if (players.get(turn).pieces.get(a).area.contains(new Point2D(cursorPress.x, cursorPress.y))) {
                                    nextIndexPosition = board.returnNextIndexPosition(players.get(turn).pieces.get(a));
                                    nextPositionCorrect(players.get(turn).pieces.get(a).toPoint());
                                    if (nextIndexPosition.isEmpty()) {
                                        period = 0;
                                        break;
                                    }
                                    players.get(turn).pieces.get(a).ifClickedThenChange();
                                    launchTips = true;
                                    repaint();
                                    orderOfPieceClicked = a;

                                    break;
                                }

                            //等待鼠标松开
                            LockSupport.park();

                            if (orderOfPieceClicked != -1) {
                                Point indexPoint = Board.getIndexPoint(cursorRelease);
                                if (indexPoint.equals(players.get(turn).pieces.get(orderOfPieceClicked).toIndexPoint()))
                                    period = 2;
                                else {
                                    boolean ismove = false;
                                    for (int a = 0; a != nextIndexPosition.size(); a++)
                                        if (indexPoint.equals(nextIndexPosition.get(a))) {
                                            pieceMove(indexPoint);
                                            ismove = true;
                                            break;
                                        }
                                    if (!ismove) {
                                        players.get(turn).pieces.get(orderOfPieceClicked).ifClickedThenChange();
                                        launchTips = false;
                                        orderOfPieceClicked = -1;
                                        repaint();
                                        period = 0;
                                    }
                                }
                            }
                        }
                        if (period == 2)//第二种移动方法
                        {
                            //等待鼠标再次按下
                            try {
                                lock.acquire();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            isWaitForRelease = true;
                            Point indexPoint = Board.getIndexPoint(cursorPress);

                            //等待鼠标松开
                            LockSupport.park();

                            if (indexPoint.equals(Board.getIndexPoint(cursorRelease)))//若 松开的坐标和按下的坐标一致
                            {
                                if (indexPoint.equals(players.get(turn).pieces.get(orderOfPieceClicked).toIndexPoint()))//取消选定
                                {
                                    players.get(turn).pieces.get(orderOfPieceClicked).ifClickedThenChange();
                                    launchTips = false;
                                    repaint();
                                    orderOfPieceClicked = -1;
                                    period = 0;
                                    continue;
                                }
                                boolean ismove = false;
                                for (int a = 0; a != nextIndexPosition.size(); a++)
                                    if (indexPoint.equals(nextIndexPosition.get(a))) {
                                        pieceMove(indexPoint);
                                        ismove = true;
                                        break;
                                    }
                                if (!ismove)
                                    continue;
                            } else
                                continue;
                        }
                    } else {
                        try {
                            chooseInGame();
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (isLink) {
                    try {
                        lock.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isWaitForRelease = true;
                    if (Constant.BOARD_RECT.contains(cursorPress) && turn == myturn) {
                        if (period == 1) {
                            for (int a = 0; a != players.get(turn).pieces.size(); a++)
                                if (players.get(turn).pieces.get(a).area.contains(new Point2D(cursorPress.x, cursorPress.y))) {
                                    nextIndexPosition = board.returnNextIndexPosition(players.get(turn).pieces.get(a));
                                    nextPositionCorrect(players.get(turn).pieces.get(a).toPoint());
                                    if (nextIndexPosition.isEmpty()) {
                                        period = 0;
                                        break;
                                    }
                                    players.get(turn).pieces.get(a).ifClickedThenChange();
                                    launchTips = true;
                                    repaint();
                                    orderOfPieceClicked = a;

                                    break;
                                }

                            //等待鼠标松开
                            LockSupport.park();

                            if (orderOfPieceClicked != -1) {
                                Point indexPoint = Board.getIndexPoint(cursorRelease);
                                if (indexPoint.equals(players.get(turn).pieces.get(orderOfPieceClicked).toIndexPoint()))
                                    period = 2;
                                else {
                                    boolean ismove = false;
                                    for (int a = 0; a != nextIndexPosition.size(); a++)
                                        if (indexPoint.equals(nextIndexPosition.get(a))) {
                                            pieceMove(indexPoint);
                                            ismove = true;
                                            if (isHost) {
                                                host.setData(Board.clone(board.getSituationOfBoard()));
                                            } else {
                                                guest.update(Board.clone(board.getSituationOfBoard()));
                                            }
                                            break;
                                        }
                                    if (!ismove) {
                                        players.get(turn).pieces.get(orderOfPieceClicked).ifClickedThenChange();
                                        launchTips = false;
                                        orderOfPieceClicked = -1;
                                        repaint();
                                        period = 0;
                                    }
                                }
                            }
                        }
                        if (period == 2)//第二种移动方法
                        {
                            //等待鼠标再次按下
                            try {
                                lock.acquire();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            isWaitForRelease = true;
                            Point indexPoint = Board.getIndexPoint(cursorPress);

                            //等待鼠标松开
                            LockSupport.park();

                            if (indexPoint.equals(Board.getIndexPoint(cursorRelease)))//若 松开的坐标和按下的坐标一致
                            {
                                if (indexPoint.equals(players.get(turn).pieces.get(orderOfPieceClicked).toIndexPoint()))//取消选定
                                {
                                    players.get(turn).pieces.get(orderOfPieceClicked).ifClickedThenChange();
                                    launchTips = false;
                                    repaint();
                                    orderOfPieceClicked = -1;
                                    period = 0;
                                    continue;
                                }
                                boolean ismove = false;
                                for (int a = 0; a != nextIndexPosition.size(); a++)
                                    if (indexPoint.equals(nextIndexPosition.get(a))) {
                                        pieceMove(indexPoint);
                                        if (isHost) {
                                            host.setData(Board.clone(board.getSituationOfBoard()));
                                        } else {
                                            guest.update(Board.clone(board.getSituationOfBoard()));
                                        }
                                        ismove = true;
                                        break;
                                    }
                                if (!ismove)
                                    continue;
                            } else
                                continue;
                        }
                    } else {
                        try {
                            chooseInGame();
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        chooseInSaveOrLoad();
                    } catch (InterruptedException | IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            while (aliveFlag) {
                try {
                    gameOverThenChoose();
                    while (canRewind) {
                        rewindThenChoose();
                        Thread.sleep(30);
                    }
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("controlOver");
        }
    }


    /*新线程：电脑操作类，在人机模式后初始化，用以对电脑类对象的实例方法的控制与调用，并*/
    class ComputerOperation extends Thread {
        int order = 0;

        ComputerOperation() {
            this.start();
        }

        @Override
        public void run() {
            int num = 2;
            while (continueGame && !computers.isEmpty()) {
                if (turn == computers.get(order).turn) {
                    isComputerTurn = true;
                    if (modeChoose[6] == 2)
                        computers.get(order).update(board, players.get(turn), 3 - num++ % 2);
                    else
                        computers.get(order).update(board, players.get(turn), modeChoose[6] + 1);
                    computers.get(order).run();
                    if (isComputerTurn) {
                        audioPlayIndex = 1;
                        repaint();
                        for (int a = 0; a != players.get(turn).pieces.size(); a++)
                            players.get(turn).isReachGoalArea(a);
                        ifVictory();
                        turnChange();
                        order = order == computers.size() - 1 ? 0 : order + 1;
                        if (computers.get(order).turn != turn) {
                            recordBoardEachStep.delete(recordBoardEachStep.length() - EACH_RECORD_LENGTH, recordBoardEachStep.length());
                            recordBoard();
                        }
                    } else {
                        isComputerTurn = true;
                        try {
                            sleep(80);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isComputerTurn = false;
                        continue;
                    }
                    isComputerTurn = false;
                } else
                    order = order == computers.size() - 1 ? 0 : order + 1;
                try {
                    do {
                        Thread.sleep(20);
                    } while (isSaveGame);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("computerOver");
        }
    }


    /*新线程：状态侦测类，暂时用以对游戏时间的侦测与更新*/
    class StatusDetect extends Thread {
        long lastSecond = 0;
        long time1, time2;

        public StatusDetect() {
            this.start();
        }

        @Override
        public void run() {
            while (MyChessFrame.this.aliveFlag) {
                try {
                    Thread.sleep(25);
                    if (isPlayDynamicBackground)
                        MyChessFrame.this.repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!isSaveGame && continueGame && startGame) {
                    try {
                        time1 = System.currentTimeMillis();
                        Thread.sleep(25);
                        if (isPlayDynamicBackground)
                            MyChessFrame.this.repaint();
                        lastSecond = timeInMili / 1000;
                        time2 = System.currentTimeMillis();
                        timeInMili += time2 - time1;
                        if (lastSecond != timeInMili / 1000) {
                            repaint();
                            date = new Date();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("statusOver");
        }
    }

    /*新线程：音频播放侦测类，用以侦测执行音效播放的信号，若有，则播放音效*/
    class AudioPlayDetect extends Thread {
        int lastClickNum = clickNum;
        float volumeAdjust = -20.0f;

        public AudioPlayDetect() {
            File[] BGM_File = new File("resource\\sound\\BGM").listFiles();
            if (BGM_File != null) {
                BGM_index = (int) (BGM_File.length * Math.random());
                try {
                    BGM = AudioSystem.getClip();
                    audioStream = AudioSystem.getAudioInputStream(BGM_File[BGM_index]);
                    BGM.open(audioStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ((FloatControl) BGM.getControl(FloatControl.Type.MASTER_GAIN)).setValue(volumeAdjust);
            BGM.loop(1000000);
            this.start();
        }

        private void chooseBGMEvent() throws Exception {
            if (clickNum > lastClickNum) {
                lastClickNum = clickNum;
                lock.acquire();
                isWaitForRelease = true;
                if (Constant.NEXT_BGM_BUTTON.contains(cursorPress)) {
                    if (period == 1)
                        period = 0;
                    BGM_choice = 1;
                }
                if (Constant.SWITCH_BGM_BUTTON.contains(cursorPress.x, cursorPress.y)) {
                    BGM_choice = 2;
                    if (period == 1)
                        period = 0;
                }
                if (Constant.BGM_UP_BUTTON.contains(cursorPress.x, cursorPress.y)) {
                    BGM_choice = 3;
                    if (period == 1)
                        period = 0;
                }
                if (Constant.BGM_DOWN_BUTTON.contains(cursorPress.x, cursorPress.y)) {
                    BGM_choice = 4;
                    if (period == 1)
                        period = 0;
                }

                repaint();
                if (BGM_choice == 0) {
                    return;
                }

                LockSupport.park();
                switch (BGM_choice) {
                    case 1:
                        if (Constant.NEXT_BGM_BUTTON.contains(cursorRelease)) {
                            audioPlayIndex = 3;
                            File[] BGM_File = new File("resource\\sound\\BGM").listFiles();
                            BGM_File = new File("resource\\sound\\BGM").listFiles();
                            int index = 0;
                            if (BGM_File.length > 1) {
                                while (BGM_index == (index = (Math.abs(new Random().nextInt() % BGM_File.length))))
                                    ;
                            }
                            BGM_index = index;
                            audioStream.close();
                            audioStream = AudioSystem.getAudioInputStream(BGM_File[BGM_index]);
                            BGM.close();
                            BGM.open(audioStream);
                            BGM.loop(10000);
                            if (!isPlayBGM)
                                BGM.stop();
                            else
                                BGM.loop(10000);
                        }
                        BGM_choice = 0;
                        repaint();
                        break;
                    case 2:
                        if (Constant.SWITCH_BGM_BUTTON.contains(cursorRelease.x, cursorRelease.y)) {

                            audioPlayIndex = 3;
                            isPlayBGM = !isPlayBGM;
                            if (isPlayBGM) {
                                BGM.loop(10000);
                            } else
                                BGM.stop();
                        }
                        BGM_choice = 0;
                        repaint();
                        break;
                    case 3:
                        if (Constant.BGM_UP_BUTTON.contains(cursorRelease.x, cursorRelease.y)) {
                            audioPlayIndex = 3;
                            volumeAdjust += 3;
                            if (volumeAdjust >= 6)
                                volumeAdjust = 6;
                            ((FloatControl) BGM.getControl(FloatControl.Type.MASTER_GAIN)).setValue(volumeAdjust);
                        }
                        BGM_choice = 0;
                        repaint();
                        break;
                    case 4:
                        if (Constant.BGM_DOWN_BUTTON.contains(cursorRelease.x, cursorRelease.y)) {
                            audioPlayIndex = 3;
                            volumeAdjust -= 3;
                            if (volumeAdjust <= -70)
                                volumeAdjust = -70;
                            ((FloatControl) BGM.getControl(FloatControl.Type.MASTER_GAIN)).setValue(volumeAdjust);
                        }
                        BGM_choice = 0;
                        repaint();
                        break;
                    default:
                        BGM_choice = 0;
                        repaint();
                }
            }
        }

        /*根据检测到的执行音效的信号，播放相应音效*/
        private void audioPlay() {
            switch (audioPlayIndex) {
                case 1:
                    soundOfPieceMove.play();
                    break;
                case 2:
                    initialButtonSound.play();
                    break;
                case 3:
                    commonButtonSound.play();
                    break;
                default:
                    break;
            }
            audioPlayIndex = 0;
        }

        @Override
        public void run() {
            while (aliveFlag) {
                if (audioPlayOn)
                    audioPlay();
                try {
                    chooseBGMEvent();
                    Thread.sleep(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            BGM.close();
            System.out.println("audioOver");
        }
    }

    /*更新自动回放的回放速度*/
    private void updateRewindSpeed() {
        if (isUpdateRewindSpeed) {
            key = new KeyBoard();
            addKeyListener(key);

        } else {
            if (key != null)
                removeKeyListener(key);
        }
    }

    /*新线程：回放类，在游戏结束，点击回放按钮后启动，用以对回放功能的具体实现*/
    class Rewind extends Thread {
        public Rewind() {
            rewindSpeed = 1;
            hideTips = false;
            this.start();
        }

        @Override
        public void run() {
            while (canRewind) {
                String[] records = recordBoardEachStep.toString().split("~");
                Scanner in;
                int[][] goalBoard = new int[16][16];
                ;
                board = new Board();
                for (int i = 0; i != records.length; i++) {
                    in = new Scanner(records[i]);
                    if (in.hasNext()) {
                        orderOfPieceClicked = -1;
                        turn = in.nextInt();
                        stepNum = in.nextInt();
                        for (int a = 0; a != 4; a++)
                            board.setTipPosition(new Point(in.nextInt(), in.nextInt()), new Point(in.nextInt(), in.nextInt()), a);
                        int[] index = new int[4];
                        for (int a = 0; a != 16; a++)
                            for (int b = 0; b != 16; b++) {
                                goalBoard[a][b] = in.nextInt();
                                if (i == 0) {
                                    for (int n = 0; n != players.size(); n++)
                                        if (goalBoard[a][b] == players.get(n).turn) {
                                            players.get(n).pieces.get(index[n]++).set1(Board.indexToCoordinate(new Point(a, b)));
                                            break;
                                        }
                                }
                            }
                        if (i == 0)
                            board.setSituationOfBoard(goalBoard);
                        else
                            movePiecesFromTwoDiffBoard(board.getSituationOfBoard(), goalBoard, players);
                        System.out.print(board);
                        repaint();
                    }
                    in.close();
                    if (canRewind) {
                        /*此处不是隐藏提示，仅仅借用了游戏时隐藏提示这个按钮的自动播放按钮
                         * 判断是否为自动播放模式，若是则自动播放*/
                        if (hideTips) {
                            try {
                                int count = 0;
                                while (count < 10000 && hideTips && canRewind) {
                                    count += (int) (rewindSpeed * 100);
                                    Thread.sleep(10);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            audioPlayIndex = 1;
                        }
                        if (!hideTips) {
                            isNextStep = false;
                            isLastStep = false;
                            /*用以等待玩家按下前进或后退的按钮*/
                            while (!isNextStep && (!isLastStep || i < 1)) {
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (hideTips) {
                                try {
                                    int count = 0;
                                    while (count < 10000 && hideTips && canRewind) {
                                        count += (int) (rewindSpeed * 100);
                                        Thread.sleep(10);
                                    }
                                    if (!hideTips) {
                                        i--;
                                        continue;
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                audioPlayIndex = 1;
                            }
                            /*用以实现回放时的后退一步*/
                            if (i >= 1 && isLastStep)
                                i -= 2;
                        }
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("rewindOver");
        }
    }

    /*裁决选择的人机模式是否合法*/
    private int judgeModeValid() {
        int voidNum = 0;
        for (int a = 0; a != 4; a++)
            if (modeChoose[a] == 0)
                voidNum++;
        if (voidNum != 2 && voidNum != 0)
            return 4;
        if (voidNum == 2 && (modeChoose[0] == 0 || modeChoose[2] == 0))
            return 4;
        return voidNum;
    }


    class Link extends Thread {
        public Link() {
            this.setName("Link");
            this.start();
        }

        private void beginGameForGuest() {
            MyChessFrame.this.remove(join);
            if (judgeModeValid() == 2) {
                audioPlayIndex = 2;
                isFourPlayers = false;
                isChooseMode = false;
                startGame = true;
                if (modeChoose[0] == 2)
                    computers.add(new ComputerPlayer(false, 0, board));
                if (modeChoose[2] == 2)
                    computers.add(new ComputerPlayer(false, 2, board));
            } else if (judgeModeValid() == 0) {
                audioPlayIndex = 2;
                isFourPlayers = true;
                startGame = true;
                isChooseMode = false;
                for (int a = 0; a != 4; a++)
                    if (modeChoose[a] == 2)
                        computers.add(new ComputerPlayer(true, a, board));
            } else {
                modeChoose[4] = 0;
                MyChessFrame.this.add(join);
            }
            if (isWaitForChoose && control.getState() == State.WAITING) {
                LockSupport.unpark(control);
            }
        }

        private int turnChange(int order) {
            if (isFourPlayers) {
                order = order == 3 ? 0 : order + 1;
                if (winOrder.contains(order)) {
                    stepNum--;
                    order = turnChange(order);
                }
            } else
                order = order == 2 ? 0 : 2;
            return order;
        }

        boolean matrixEquals(int[][] m1, int[][] m2) {
            boolean flag = true;
            for (int a = 0; a != m1.length; a++) {
                for (int b = 0; b != m1[0].length; b++) {
                    if (m1[a][b] != m2[a][b]) {
                        flag = false;
                    }
                }
            }
            return flag;
        }

        @Override
        public void run() {
            int[][] newBoard;
            while ((startGame || isChooseMode) && isLink) {
                if (isHost) {
                    Object temp = host.read();
                    if (!startGame) {
                        modeChoose = (int[]) temp;
                    } else {
                        newBoard = (int[][]) temp;
                        movePieceFromTwoDiffBoard(board.getSituationOfBoard(), newBoard, players.get(turn));
                        audioPlayIndex = 1;
                        recordBoard();
                        ifVictory();
                        MyChessFrame.this.turnChange();
                        orderOfPieceClicked = -1;
                    }
                    MyChessFrame.this.repaint();
                } else {
                    Object temp = guest.read();
                    if (!startGame) {
                        modeChoose = (int[]) temp;
                        if (modeChoose[4] != 0) {
                            beginGameForGuest();
                        }
                    } else {
                        newBoard = (int[][]) temp;
                        if (!matrixEquals(board.getSituationOfBoard(), newBoard)) {
                            movePieceFromTwoDiffBoard(board.getSituationOfBoard(), newBoard, players.get(turn));
                            audioPlayIndex = 1;
                            recordBoard();
                            ifVictory();
                            MyChessFrame.this.turnChange();
                            orderOfPieceClicked = -1;
                        }
                    }
                    MyChessFrame.this.repaint();
                }
            }
        }
    }

}

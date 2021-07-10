package game.sourcecode;

import javafx.scene.shape.Circle;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {
    private Constant() {
    }

    public static final int INIT_WINDOW_COORDINATE_X = 200;
    public static final int INIT_WINDOW_COORDINATE_Y = 50;
    public static final int WINDOW_HEIGHT = 950;
    public static final int WINDOW_WIDTH = 1500;
    public static final int BOARD_COORDINATE_X = 40;
    public static final int BOARD_COORDINATE_Y = 50;
    public static final int GRID_WIDTH = 55;
    public static final int GRID_HEIGHT = 55;
    public static final int INIT_GAME_WINDOW_COORDINATE_X = 0;
    public static final int INIT_GAME_WINDOW_COORDINATE_Y = 30;
    public static final int GAME_WINDOW_WIDTH = 1200;
    public static final int GAME_WINDOW_HEIGHT = 900;
    public static final int STATUS_WINDOW_COORDINATEX = 1201;
    public static final int STATUS_WINDOW_COORDINATEY = 0;
    public static final int STATUS_WINDOW_WIDTH = 299;
    public static final int STATUS_WINDOW_HEIGHT = 900;

    public static final class colorInTurn {

        private colorInTurn() {
        }

        public static Color get(int index) {
            if (index == 0)
                return Color.black;
            if (index == 1)
                return Color.red;
            if (index == 2)
                return Color.white;
            if (index == 3)
                return Color.green;
            return null;
        }
    }

    public static final String[] COLOR = {"黑色方", "红色方", "白色方", "绿色方"};

    public static final Rectangle WINDOW_RECT = new Rectangle(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    public static final Rectangle BOARD_RECT = new Rectangle(BOARD_COORDINATE_X, BOARD_COORDINATE_Y, GRID_WIDTH * 16, GRID_HEIGHT * 16);

    public static final Rectangle TWO_PLAYER_BUTTON = new Rectangle(600,250,280,50);
    public static final Rectangle FOUR_PLAYER_BUTTON = new Rectangle(600,350,280,50);
    public static final Rectangle AI_BUTTON = new Rectangle(600,450,280,50);
    public static final Rectangle LOAD_BUTTON = new Rectangle(600,550,280,50);

    public static final Rectangle HELP_ONESTEP_BUTTON = new Rectangle(1100, 380, 200, 50);
    public static final Rectangle REPEAL_BUTTON = new Rectangle(1100, 460, 200, 50);
    public static final Rectangle SAVE_GAME_BUTTON = new Rectangle(1100, 540, 200, 50);
    public static final Rectangle RETURN_MENU_BUTTON = new Rectangle(1100, 620, 200, 50);
    public static final Rectangle SWITCH_TIPS_BUTTON = new Rectangle(1210,701,80,48);
    public static final Rectangle HINT_LEVEL_BUTTON = new Rectangle(1100, 700, 200, 50);
    public static final Rectangle NEXT_BGM_BUTTON = new Rectangle(1100, 792, 65, 65);
    public static final Circle SWITCH_BGM_BUTTON = new Circle(1215, 824, 32);
    public static final Circle BGM_UP_BUTTON = new Circle(1278, 800, 19);
    public static final Circle BGM_DOWN_BUTTON = new Circle(1278, 850, 19);

    public static final ArrayList<Rectangle> RECTANGLE_ARRAY_LIST = new ArrayList<>(6);

    static {
        RECTANGLE_ARRAY_LIST.add(new Rectangle(20, 50, 400, 430));
        RECTANGLE_ARRAY_LIST.add(new Rectangle(470, 50, 400, 430));
        RECTANGLE_ARRAY_LIST.add(new Rectangle(920, 50, 400, 430));
        RECTANGLE_ARRAY_LIST.add(new Rectangle(20, 490, 400, 430));
        RECTANGLE_ARRAY_LIST.add(new Rectangle(470, 490, 400, 430));
        RECTANGLE_ARRAY_LIST.add(new Rectangle(920, 490, 400, 430));
        RECTANGLE_ARRAY_LIST.add(new Rectangle(1380,80,78,78));
    }

    public static final Rectangle[] CHOOSE_MODE_BUTTONS = new Rectangle[]{
            new Rectangle(BOARD_COORDINATE_X, BOARD_COORDINATE_Y, 6 * GRID_WIDTH, 6 * GRID_HEIGHT),
            new Rectangle(BOARD_COORDINATE_X, BOARD_COORDINATE_Y + 10 * GRID_HEIGHT, 6 * GRID_WIDTH, 6 * GRID_HEIGHT),
            new Rectangle(BOARD_COORDINATE_X + 10 * GRID_WIDTH, BOARD_COORDINATE_Y + 10 * GRID_HEIGHT, 6 * GRID_WIDTH, 6 * GRID_HEIGHT),
            new Rectangle(BOARD_COORDINATE_X + 10 * GRID_WIDTH, BOARD_COORDINATE_Y, 6 * GRID_WIDTH, 6 * GRID_HEIGHT),
            new Rectangle(1240, 180, 250, 80),
            new Rectangle(1320, 80, 80, 80),
            new Rectangle(1100, 500, 300, 100)
    };

    public static final int[] INIT_PIECES_LOCATIONS_X = {0, 0, 1, 0, 1, 2, 0, 1, 2, 3, 1, 2, 3, 0, 1, 2, 3, 4, 4};
    public static final int[] INIT_PIECES_LOCATIONS_Y = {0, 1, 0, 2, 1, 0, 3, 2, 1, 0, 3, 2, 1, 4, 4, 3, 2, 1, 0};

}

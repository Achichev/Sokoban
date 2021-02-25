package sokoban.model;

import sokoban.controller.EventListener;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Model {
    public static final int FIELD_CELL_SIZE = 20;
    
    private GameObjects gameObjects;
    private int currentLevel = 1;
    private EventListener eventListener;
    LevelLoader levelLoader;

    public Model() {
        try {
            levelLoader = new LevelLoader(Paths.get(getClass().getResource("../res/levels.txt").toURI()));
        } catch (URISyntaxException ignored) {
        }
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void move(Direction direction) {
        if (checkWallCollision(gameObjects.getPlayer(), direction) || checkBoxCollisionAndMoveIfAvailable(direction)) {
            return;
        }
        int dx = dx(direction);
        int dy = dy(direction);
        gameObjects.getPlayer().move(dx, dy);
        
        checkCompletion();
    }
    
    public GameObjects getGameObjects() {
        return gameObjects;
    }

    public void restartLevel(int level) {
        gameObjects = levelLoader.getLevel(level);
    }

    public void restart() {
        restartLevel(currentLevel);
    }

    public void startNextLevel() {
        currentLevel++;
        restartLevel(currentLevel);
    }

    //checks if the gameObject collides with a wall
    public boolean checkWallCollision(CollisionObject gameObject, Direction direction) {
        for (Wall wall: gameObjects.getWalls()) {
            if (gameObject.isCollision(wall, direction)) return true;
        }
        return false;
    }


    //Checks if the player can be moved in the specified direction.
    //A box, standing at the next cell in that direction, will be moved by FIELD_CELL_SIZE value if it can be moved.
    public boolean checkBoxCollisionAndMoveIfAvailable(Direction direction) {
        //checking if boxes collide with other objects
        for (Box box: gameObjects.getBoxes()) {
            if (gameObjects.getPlayer().isCollision(box, direction)) {
                for (Box nextBox: gameObjects.getBoxes()) {
                    if (!box.equals(nextBox)) {
                        if (box.isCollision(nextBox, direction)) {
                            return true;
                        }
                    }
                    if (checkWallCollision(box, direction)) return true;
                }
                //there is an empty cell or a home place behind the box, the box can be moved
                int dx = dx(direction);
                int dy = dy(direction);
                box.move(dx, dy);
            }
        }
        return false;
    }

    //checks if level completed (all boxes are in home places and their coordinates match)
    //and informs the eventListener that the currentLevel completed
    public void checkCompletion() {
        int count = 0;
        for (Home home: gameObjects.getHomes()) {
            for (Box box: gameObjects.getBoxes()) {
                if (home.getX() == box.getX() && home.getY() == box.getY()) count++;
            }
        }
        if (count == gameObjects.getHomes().size()) {
            eventListener.levelCompleted(currentLevel);
        }
    }

    //a helper method for determining the amount of movement along XY coordinates
    public int dx(Direction direction) {
        int x = 0;
        switch (direction) {
            case LEFT:
                x = -FIELD_CELL_SIZE;
                break;
            case RIGHT:
                x = FIELD_CELL_SIZE;
                break;
        }
        return x;
    }

    //a helper method for determining the amount of movement along XY coordinates
    public int dy(Direction direction) {
        int y = 0;
        switch (direction) {
            case UP:
                y = -FIELD_CELL_SIZE;
                break;
            case DOWN:
                y = FIELD_CELL_SIZE;
                break;
        }
        return y;
    }
}

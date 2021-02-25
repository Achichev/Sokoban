package sokoban.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class LevelLoader {
    private Path levels;

    public LevelLoader(Path levels) {
        this.levels = levels;
    }

    //reads required level out of Path levels and returns a new GameObjects object
    //with all of the elements of the game and their coordinates
    public GameObjects getLevel(int level) {
        if (level > 60) { level %= 60; }
        try (BufferedReader in = new BufferedReader(new FileReader(levels.toString()))) {
            int width, height;

            while (true) {
                String s = in.readLine();
                if (s.startsWith("Maze:") && Integer.parseInt(s.substring(6)) == level) break;
            }

            //skipping 6 lines
            for (int i = 0; i < 6; i++) { in.readLine();}
            Set<Wall> walls = new HashSet<>();
            Set<Box> boxes = new HashSet<>();
            Set<Home> homes = new HashSet<>();
            Player player = null;
            int x = Model.FIELD_CELL_SIZE / 2;
            int y = Model.FIELD_CELL_SIZE / 2;

            while (true) {
                String s = in.readLine();
                if (s.length() == 0) break;
                for (char ch: s.toCharArray()) {
                    switch (ch) {
                        case 'X' :
                            walls.add(new Wall(x, y));
                            break;
                        case '*':
                            boxes.add(new Box(x, y));
                            break;
                        case '.':
                            homes.add(new Home(x, y));
                            break;
                        case '&':
                            boxes.add(new Box(x, y));
                            homes.add(new Home(x, y));
                            break;
                        case '@':
                            player = new Player(x, y);
                    }
                    x += Model.FIELD_CELL_SIZE;
                }
                x = Model.FIELD_CELL_SIZE / 2;
                y += Model.FIELD_CELL_SIZE;
            }
            return new GameObjects(walls, boxes, homes, player);
        } catch (IOException e) {
        }
        return null;
    }
}

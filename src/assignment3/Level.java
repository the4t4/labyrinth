package assignment3;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.ImageIcon;

public class Level {
    private final int TILE_WIDTH = 40;
    private final int TILE_HEIGHT = 30;
    Sprite[][] world;
    
    public Level(String levelPath) throws IOException {
        loadLevel(levelPath);
    }
    
    /**
     * Generates a grid of sprites from a text file containing zeros and empty spaces where zero indicates a wall and empty space indicates a floor.
     * @param levelPath location of the text file of the level
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void loadLevel(String levelPath) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(levelPath));
        world = new Sprite[19][19];
        int y = 0;
        String line;
        while ((line = br.readLine()) != null) {
            int x = 0;
            for (char spriteType : line.toCharArray()) {
                if (spriteType == '0') {
                    Image image = new ImageIcon("data/world/wall.png").getImage();
                    world[y][x] = new Sprite(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, image, true);
                }
                else {
                    Image image = new ImageIcon("data/world/floor.png").getImage();
                    world[y][x] = new Sprite(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, image, false);
                }
                x++;
            }
            y++;
        }
    }

    /**
     * Draws the grid of sprites
     * @param g Graphics object
     */
    public void draw(Graphics g) {
        for (Sprite[] row : world){
            for(Sprite sprite : row){
                sprite.draw(g);
            }
        }
    }
    
    /**
     * Checks if the given direction next to the given point is a wall.
     * @param coords Point object
     * @param d Direction object
     * @return a boolean value
     */
    public boolean collides(Point coords, Direction d) {
        if(coords.x + d.x > 18) return true;
        Sprite object = world[coords.y + d.y][coords.x + d.x];
        return object.isWall;
    }
    
    /**
     * Checks if the given point is a wall.
     * @param coords Point object
     * @return a boolean value
     */
    public boolean collides(Point coords) {
        Sprite object = world[coords.y][coords.x];
        return object.isWall;
    }
   
}

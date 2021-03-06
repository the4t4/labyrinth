package assignment3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class Enemy extends Sprite {
    
    private Direction direction;
    private Timer animation;

    /**
     * Sets the position and the sprite of the dragon. Also starts an animation for the sprite of the dragon.
     * @param x
     * @param y 
     */
    public Enemy(int x, int y) {
        super(x, y, 40, 30, new ImageIcon("data/enemy/walkS/enemy_walkA_0000.png").getImage(), false);
        chooseDirection();
        animation = new Timer(1000/28, new Animate());
        animation.start();
    }
    
    public void move(){
        this.x += direction.x*40;
        this.y += direction.y*30;
    }
    
    /**
     * Randomly chooses a direction
     */
    public void chooseDirection(){
        Direction direction = Direction.SOUTH;
        int randomDirection = (int) Math.round(Math.random()*3) + 1;
        switch(randomDirection){
            case 1: direction = Direction.SOUTH; break;
            case 2: direction = Direction.EAST; break;
            case 3: direction = Direction.NORTH; break;
            case 4: direction = Direction.WEST;
        }
        this.direction = direction;
    }
    
    /**
     * Checks if the coordinates of the neighboring tiles match with the player's position. 
     * @param hero The player
     * @return a boolean value
     */
    public boolean catches(Hero hero){
        Point heroCoords = hero.getCoords();
        Point enemyCoords = this.getCoords();
        
        if(enemyCoords.equals(heroCoords) ||
           enemyCoords.addDirection(Direction.SOUTH).equals(heroCoords) ||
           enemyCoords.addDirection(Direction.SW).equals(heroCoords)    ||
           enemyCoords.addDirection(Direction.WEST).equals(heroCoords)  ||
           enemyCoords.addDirection(Direction.NW).equals(heroCoords)    ||
           enemyCoords.addDirection(Direction.NORTH).equals(heroCoords) ||
           enemyCoords.addDirection(Direction.NE).equals(heroCoords)    ||
           enemyCoords.addDirection(Direction.EAST).equals(heroCoords)  ||
           enemyCoords.addDirection(Direction.SE).equals(heroCoords) ) 
             return true;
        else return false;
    }
    
    public Direction getDirection(){
        return direction;
    }
    
    /**
     * Used in a Timer object to loop through 13 pictures of the dragon twice every second.
     */
    class Animate implements ActionListener{
        private int frame = 0;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            frame = (frame+1)%14;
            String stringPath = frame >= 10 ? String.valueOf(frame) : "0"+frame;
            image = new ImageIcon("data/enemy/walkS/enemy_walkA_00" + stringPath + ".png").getImage();
        }
                
    }
}

package assignment3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class Hero extends Sprite {
    private Timer animation;
    
    /**
     * Sets the position and the sprite of the player. Also starts an animation for the sprite of the player.
     */
    public Hero() {
        super(40, 510, 40, 30, new ImageIcon("data/hero/idleS/hero_idleA_0000.png").getImage(), false);
        animation = new Timer(1000/34, new Animate());
        animation.start();
    }
    
    public void move(Direction d){
        this.x += d.x*40;
        this.y += d.y*30;
    }
    
    /**
     * Used in a Timer object to loop through 16 pictures of the player twice every second.
     */
    class Animate implements ActionListener{
        private int frame = 0;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            frame = (frame+1)%17;
            String stringPath = frame >= 10 ? String.valueOf(frame) : "0"+frame;
            image = new ImageIcon("data/hero/idleS/hero_idleA_00" + stringPath + ".png").getImage();
        }
                
    }   

}

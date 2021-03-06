package assignment3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Darkness {
    private final int SCREEN_WIDTH = 775;
    private final int SCREEN_HEIGHT = 615;

    /**
     * Coordinates of the upper left corner of the ellipse
     */
    private int x;
    private int y;
    private Area outter;
    private Ellipse2D.Double inner;
   
    public void setCoords(Point p){
        this.x = (p.x - 3)*40+10;
        this.y = (p.y - 3)*30;
    }
    
    /**
     * Calculates the area of an ellipse on the player with a radius of 3 tiles and then finds the difference of the whole frame area and the ellipse area and fills it with black.
     * @param g Graphics object 
     */
    public void draw(Graphics g){  
        Graphics2D g2d = (Graphics2D) g;
        outter = new Area(new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT));
        g2d.setColor(new Color(0,0,0,120));
        g2d.fill(outter);
        inner = new Ellipse2D.Double(x,y, SCREEN_WIDTH/3, SCREEN_HEIGHT/3);
        outter.subtract(new Area(inner)); // remove the ellipse from the original area
        g2d.setColor(Color.BLACK);
        g2d.fill(outter);
    }
    
}

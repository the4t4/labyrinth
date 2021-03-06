package assignment3;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameEngine extends JPanel{
    
    private final int SCREEN_WIDTH = 775;
    private final int SCREEN_HEIGHT = 610;
    private final int FPS = 240;
    private final JPanel screen = this;
    private final Database database;
    
    private boolean paused = false;
    private Sprite pausedImage = new Sprite(SCREEN_WIDTH/2-200, SCREEN_HEIGHT/2-125, 400, 200, new ImageIcon("data/world/paused.png").getImage(),false);

    private int levelNum = 0;
    private Level level;
    private Hero hero;
    private Enemy enemy;
    private Darkness darkness;
    private Timer enemyMovementTimer;
    private Timer newFrameTimer;
    private JLabel timeLabel;
    private long startTime;
    private Timer timer;
    private long stoppedTime;
    private long elapsedTime;
    private double elapsedTimeInSeconds;

    /**
     * Starts the game, binds a key listener for character movement, starts frame refreshing, starts enemy movement, starts timer, and establishes a connection to the database
     * @throws SQLException 
     */
    public GameEngine() throws SQLException {
        this.addKeyListener(new CharacterMovement());
        restart(0);
        newFrameTimer = new Timer(1000/FPS, new NewFrameListener());
        newFrameTimer.start();
        enemyMovementTimer = new Timer(800, new StartEnemyWalkListener());
        enemyMovementTimer.start();
        database = new Database();
        startTimer();
    }
    
    /**
     * Starts a timer that updates every 100ms and puts it on the frame
     */
    public void startTimer(){
        timeLabel = new JLabel(" ");
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        startTime = System.currentTimeMillis();
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime = System.currentTimeMillis() - startTime;
                elapsedTimeInSeconds = (double)elapsedTime/1000;
                timeLabel.setText( elapsedTimeInSeconds + " s");
            }
        });
        timer.start();
    }
    
    public void restartTimer(){
        startTime = System.currentTimeMillis();
        timer.restart();
    }
    
    public JLabel getTimer(){
        return timeLabel;
    }
    
    public Database getDatabase(){
        return database;
    }
    
    /**
     * Takes level number and generates the appropriate level by call, puts the character to the lower left corner of the level, puts the darkness around the player, and puts the dragon in a random position on the map.
     * @param levelNum Number of the level to be generated.
     */
    public void restart(int levelNum) {
        this.levelNum = levelNum;
        paused = false;
        try {
            level = new Level("data/levels/level" + levelNum + ".txt");
        } catch (IOException ex) {
            Logger.getLogger(GameEngine.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        hero = new Hero();
        
        darkness = new Darkness();
        darkness.setCoords(hero.getCoords());
        
        enemy = new Enemy(0,0);
        do{
            int x = (int) Math.round(Math.random()*16) + 1;
            int y = (int) Math.round(Math.random()*16) + 1;
            enemy = new Enemy(x*40,y*30);
        } while(level.collides(enemy.getCoords()) || enemy.catches(hero));
        
    }
    
    /**
     * Checks if the player reached the top right corner of the level.
     * @return a boolean value
     */
    public boolean isOver(){
        Point coords = hero.getCoords();
        return coords.x == 18 && coords.y == 1;
    }
    
    /**
     * Pauses the timer and sets the game state to paused. A second call undoes these.
     */
    public void pause(){
        paused = !paused;
        if(paused) {
            timer.stop();
            stoppedTime = System.currentTimeMillis();
        }
        else {
            startTime += System.currentTimeMillis() - stoppedTime;
            timer.restart();
        }
    }
    
    /**
     * Draws all the sprites on the frame
     * @param g Graphics object
     */
    @Override
    protected void paintComponent(Graphics g) {
        level.draw(g);
        hero.draw(g);
        enemy.draw(g);
        darkness.draw(g);
        if(paused) pausedImage.draw(g);
    }
    
    /**
     * Used in a Timer object to update the dragon's location every 800ms
     */
    class StartEnemyWalkListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!paused) {
                if(level.collides(enemy.getCoords(), enemy.getDirection())){
                    do{
                        enemy.chooseDirection();
                    }while(level.collides(enemy.getCoords(),enemy.getDirection()));
                }
                enemy.move();
            }
        }
    }
    
    /**
     * Used in a Timer object to refresh the frame and listens to end of game conditions 240 times a second. Resets the state of the game accordingly.
     */
    class NewFrameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            repaint();
            if(enemy.catches(hero)){
                timer.stop();
                String name = JOptionPane.showInputDialog(screen, "Enter your name: ", "You couldn't escape...", JOptionPane.INFORMATION_MESSAGE);
                if(name != null) database.putHighScore(name, levelNum, elapsedTimeInSeconds);
                int option = JOptionPane.showConfirmDialog(screen, "Start again?", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                if (option == JOptionPane.OK_OPTION) {
                    restart(0);
                    restartTimer();
                }
                else System.exit(0);
            }
            else if(isOver()){
                levelNum += 1;
                if(levelNum > 9){
                    timer.stop();
                    String name = JOptionPane.showInputDialog(screen, "You escaped in " + elapsedTimeInSeconds + " seconds! Enter your name: ", "You won!", JOptionPane.INFORMATION_MESSAGE);
                    if(name != null) database.putHighScore(name, levelNum, elapsedTimeInSeconds);
                    int option = JOptionPane.showConfirmDialog(screen, "Play again?" , "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                    if (option == JOptionPane.OK_OPTION) {
                        restart(0);
                        startTime = System.currentTimeMillis();
                        timer.restart();
                    }
                    else System.exit(0);
                }
                else restart(levelNum);
            }
            repaint();
        }
    }
    
    /**
     * Listens to the W, A, S, and D keys for player movement. Also repositions the darkness to the player's new position.
     */
    class CharacterMovement extends KeyAdapter {
       
        @Override
        public void keyPressed(KeyEvent key) {
            int kc = key.getKeyCode();
            Point coords = hero.getCoords();
            switch (kc){
                case KeyEvent.VK_A: {
                    if(paused) return;
                    if (!level.collides(coords, Direction.WEST)) {
                        hero.move(Direction.WEST);
                    }
                    break;
                }
                case KeyEvent.VK_D: {
                    if(paused) return;
                    if (!level.collides(coords, Direction.EAST)) {
                        hero.move(Direction.EAST);
                    }
                    break;
                }
                case KeyEvent.VK_W: {
                    if(paused) return;
                    if (!level.collides(coords, Direction.NORTH)) {
                        hero.move(Direction.NORTH);
                    }
                    break;
                }
                case KeyEvent.VK_S: {
                    if(paused) return;
                    if (!level.collides(coords, Direction.SOUTH)) {
                        hero.move(Direction.SOUTH);
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    pause();
                }
            }
            
            darkness.setCoords(hero.getCoords());
           
        }
    }
}

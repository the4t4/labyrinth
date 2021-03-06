package assignment3;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

public class LabyrinthGUI {
    private JFrame frame;
    private GameEngine gameArea;
    private Database database;
    private JFrame highscoresFrame;
    private JMenuBar menuBar;
    private JMenu menu;
    
    /**
     * Creates the main frame, creates a menu, and creates a secondary frame for high scores which is initially invisible.
     */
    public LabyrinthGUI() {
        frame = new JFrame("Labyrinth");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            gameArea = new GameEngine();
            database = gameArea.getDatabase();
            //database.emptyTheTable();
            updateHighscoresFrame();
            
        } catch (SQLException ex) {
            Logger.getLogger(LabyrinthGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        menu = new JMenu("Menu");
        
        JMenuItem highscores = new JMenuItem("Highscores");
        highscores.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                gameArea.pause();
                updateHighscoresFrame();
                highscoresFrame.setVisible(true);
            }
        });
        menu.add(highscores);
        JMenuItem pause = new JMenuItem("Pause");
        pause.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                gameArea.pause();
            }
        });
        menu.add(pause);
        JMenuItem restart = new JMenuItem("Restart");
        restart.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               gameArea.restart(0);
               gameArea.restartTimer();
            }
        });
        menu.add(restart);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               System.exit(0);
            }
        });
        menu.add(exit);
        menuBar.add(menu);
        
        gameArea.setFocusable(true);
        frame.getContentPane().add(gameArea, BorderLayout.CENTER);
        frame.getContentPane().add(gameArea.getTimer(), BorderLayout.SOUTH);
        
        frame.setPreferredSize(new Dimension(775, 650));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    /** 
     * Creates a new frame which contains a table of the top 10 high scores.
     */
    private void updateHighscoresFrame(){
        try {
            highscoresFrame = new JFrame("Highscores");
            JTable table = new JTable(database.getDataMatrix(),database.getColumnNamesArray());
            table.setEnabled(false);
            table.setRowHeight(50);
            JScrollPane sp = new JScrollPane(table);
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(50);
            columnModel.getColumn(1).setPreferredWidth(230);
            columnModel.getColumn(2).setPreferredWidth(120);
            columnModel.getColumn(3).setPreferredWidth(200);
            DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
            cellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            columnModel.getColumn(0).setCellRenderer(cellRenderer);
            columnModel.getColumn(1).setCellRenderer(cellRenderer);
            columnModel.getColumn(2).setCellRenderer(cellRenderer);
            columnModel.getColumn(3).setCellRenderer(cellRenderer);
            highscoresFrame.add(sp);
            highscoresFrame.setSize(new Dimension(600, 400));
            highscoresFrame.setLocationRelativeTo(null);
        } catch (SQLException ex) {
            Logger.getLogger(LabyrinthGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

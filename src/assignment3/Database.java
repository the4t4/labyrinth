package assignment3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private final int maxScores = 10;
    private Statement stmt;
    private PreparedStatement insertStatement;
    private PreparedStatement deleteStatement;
    private Connection connection;
    /**
     * Tries to connect to a database called highscores. If it fails, it will create the database and prepare statements for inserting and deleting entries.
     * @throws SQLException 
     */
    public Database() throws SQLException{    
        String dbURL = "jdbc:derby:highscores";
        try {
            connection = DriverManager.getConnection(dbURL);
            stmt = connection.createStatement();
        } catch (SQLException ex) {
            dbURL += ";create=true";
            connection = DriverManager.getConnection(dbURL);
            stmt = connection.createStatement();
            stmt.execute("CREATE TABLE HIGHSCORES(NAME VARCHAR(20), LEVEL INT, TIME DOUBLE)");
        }
        String insertQuery = "INSERT INTO HIGHSCORES (NAME, LEVEL, TIME) VALUES (?, ?, ?)";
        insertStatement = connection.prepareStatement(insertQuery);
        String deleteQuery = "DELETE FROM HIGHSCORES WHERE LEVEL=? AND TIME=?";
        deleteStatement = connection.prepareStatement(deleteQuery);
    }
    
    /** 
     * Creates an ArrayList of Highscore objects from the database and returns it;
     * @return an ArrayList of Highscore objects
     * @throws SQLException 
     */
    public ArrayList<Highscore> getHighScores() throws SQLException {
        String query = "SELECT * FROM HIGHSCORES";
        ArrayList<Highscore> highscores = new ArrayList<>();
        ResultSet results = stmt.executeQuery(query);
        while (results.next()) {
            String name = results.getString("NAME");
            int level = results.getInt("LEVEL");
            double time = results.getDouble("TIME");
            highscores.add(new Highscore(name, level, time));
        }
        Collections.sort(highscores);
        return highscores;
    }
    /**
     * @return a String array with the names of column headers
     */
    public String[] getColumnNamesArray (){
        String[] columnNames = {"#", "Name", "Levels completed", "Time"};
        return columnNames;
    }
    
    /**
     * @return a String matrix with all the data in the database
     */
    public String[][] getDataMatrix () throws SQLException{
        String[][] columnNames = new String[10][4];
        ArrayList<Highscore> highscores = getHighScores();
        int cnt = 0;
        for(Highscore hs : highscores){
            columnNames[cnt][0] = String.valueOf(cnt+1);
            columnNames[cnt][1] = hs.getName();
            columnNames[cnt][2] = String.valueOf(hs.getLevel());
            columnNames[cnt][3] = String.valueOf(hs.getTime()) + " s";
            cnt++;
        }
        for(;cnt < 10; cnt++){
            columnNames[cnt][0] = String.valueOf(cnt+1);
            columnNames[cnt][1] = "";
            columnNames[cnt][2] = "";
            columnNames[cnt][3] = "";
        }
        return columnNames;
    }

    /**
     * Puts a new high score in the database if there is an empty place or if it beats a previous high score
     * @param name Player name
     * @param level Levels completed
     * @param time Elapsed time since the beginning of the game
     */
    public void putHighScore(String name, int level, double time) {
        try {
            ArrayList<Highscore> highScores = getHighScores();
            if (highScores.size() < maxScores) {
                insertScore(name, level, time);
            }
            else {
                int leastLevel = highScores.get(highScores.size() - 1).getLevel();
                double leastTime = highScores.get(highScores.size() - 1).getTime();
                if (leastLevel < level || (leastLevel == level && leastTime > time) ) {
                    deleteScores(leastLevel, leastTime);
                    insertScore(name, level, time);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Runs SQL commands to insert a new entry into the database 
     */
    private void insertScore(String name, int level, double time) {
        try {
            insertStatement.setString(1, name);
            insertStatement.setInt(2, level);
            insertStatement.setDouble(3, time);
            insertStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Runs SQL commands to delete an entry from the database 
     */
    private void deleteScores(int level, double time) throws SQLException {
        deleteStatement.setInt(1, level);
        deleteStatement.setDouble(2, time);
        deleteStatement.executeUpdate();
    }
    /**
     * Destroys current table and creates a new ones
     */
    public void emptyTheTable(){
        try {
            stmt.execute("DROP TABLE HIGHSCORES");
            stmt.execute("CREATE TABLE HIGHSCORES(NAME VARCHAR(20), LEVEL INT, TIME DOUBLE)");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

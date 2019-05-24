/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package muzix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Naman
 */
public class Song implements Comparable<Song> {
    String name;
    String id;
    String album_id;
    String genre;
    String artists;
    String status;
    String album;
    int play_count;

    public Song(String id, String name, String album_id, String genre, String artists, int play_count) throws SQLException {
        this.name = name;
        this.id = id;
        this.album_id = album_id;
        this.setAlbum();
        this.genre = genre;
        this.artists = artists;
        this.play_count = play_count;
        status = "Play";
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public String getGenre() {
        return genre;
    }

    public String[] getRow() {
        String ar[] = new String[5];
        ar[0] = name;
        ar[1] = album;
        ar[2] = genre;
        ar[3] = artists;
        ar[4] = "Play";
        return ar;
    }

    public String[] getRowAdmin() {
        String ar[] = new String[6];
        ar[0] = name;
        ar[1] = album;
        ar[2] = genre;
        ar[3] = artists;
        ar[4] = "Play";
        ar[5] = "Delete";
        return ar;
    }
    
    public void toggleStatus() {
        if(status=="Play") status = "Stop";
        else status = "Play";
    }

    public int getPlay_count() {
        return play_count;
    }

    @Override
    public int compareTo(Song s) {
        return s.getPlay_count() - this.getPlay_count();
    }

    private void setAlbum() throws SQLException {
        try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminView.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //step2 create  the connection object
            Connection con=DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:newdatabase","system","not12345");
            
            //step3 create the statement object
            PreparedStatement ps = con.prepareStatement ("select name from album where id=?");
            ps.setString(1, album_id);  
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                album = rs.getString(1);
            }
    }
}

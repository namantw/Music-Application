/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package muzix;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.Clip; 
import javax.sound.sampled.LineUnavailableException; 
import javax.sound.sampled.UnsupportedAudioFileException; 
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Naman
 */
public class UserViewFrame extends javax.swing.JFrame {

    /**
     * Creates new form AddStudent
     */
    private JFrame prevView;
    private ArrayList<Song> songs;
    private DefaultTableModel model;
    private User user;
    private TableRowSorter<TableModel> sorter;
    AudioInputStream audioInputStream;
    Long currentFrame; 
    Clip clip; 
    String status;
    JTable prevPlaying;
    int lastRow;
    int lastCol;
    
    public UserViewFrame(JFrame prevView, User user) throws IOException {
        this.user = user;
        this.prevView = prevView;
        prevPlaying = null;
        initComponents();
        this.setTitle("Musix");
        this.setSize(prevView.getSize());
        this.setLocation(prevView.getLocation());
        welcomeText.setText("Welcome, "+user.getName());
        welcomeText.setBackground(new Color(0xF7F7F7));
        
        searchButton.setBorderPainted(false);
        searchButton.setBorder(new RoundedBorder(8)); //10 is the radius
        searchButton.setFocusPainted(false);
        searchButton.setOpaque(false);
        searchButton.setBackground(new Color(0x90CCF4));
        
        searchButton.setBorderPainted(false);
        searchButton.setBorder(new RoundedBorder(8)); //10 is the radius
        searchButton.setFocusPainted(false);
        searchButton.setBackground(new Color(0x90CCF4));
        
        logout.setBorderPainted(false);
        logout.setBorder(new RoundedBorder(8)); //10 is the radius
        logout.setFocusPainted(false);
        logout.setOpaque(false);
        logout.setBackground(new Color(0x90CCF4));
        
        profileButton.setBorderPainted(false);
        profileButton.setBorder(new RoundedBorder(8)); //10 is the radius
        profileButton.setFocusPainted(false);
        profileButton.setBackground(new Color(0x90CCF4));
        
        String col[] = {"Song Name", "Album", "Genre", "Artists", ""};
        
        songs = this.getSongs();
        DefaultTableModel model = new DefaultTableModel(col, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        for(Song song: songs) {
            model.addRow(song.getRow());
            System.out.println(song.getRow()[1]);
        }
        
        JTable myTable = new JTable(model);
        /**
         * The following code has been indirectly taken from 
//         * https://stackoverflow.com/questions/33162795/how-to-add-action-listener-to-jtable
//         */
        myTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int col = target.getSelectedColumn();
                if(col==4) {
                    if(target.getValueAt(row, col)=="Stop") {
                        clip.stop();
                        target.setValueAt("Play", row, col);
                        songs.get(row).toggleStatus();
                    }
                    else {
                        try{
                            try {
                                clip.stop();
                                prevPlaying.setValueAt("Play", lastRow, lastCol);
                                songs.get(lastRow).toggleStatus();
                            }
                            catch(NullPointerException ne){}
                            Class.forName("oracle.jdbc.driver.OracleDriver");

                            Connection con=DriverManager.getConnection(
                                                "jdbc:oracle:thin:@localhost:1521:newdatabase","system","not12345");

                            PreparedStatement ps=con.prepareStatement("select * from song where id = ?"); 
                            ps.setString(1, songs.get(row).getId());
                            ResultSet rs=ps.executeQuery();  
                            String filename = "C:\\Users\\lenovo\\Desktop\\temp.wav";

                            File file = new File(filename);
                            FileOutputStream fos = new FileOutputStream(file);

                            System.out.println("Writing BLOB to file " + file.getAbsolutePath());
                            while (rs.next()) {
                                InputStream input = rs.getBinaryStream(7);
                                byte[] buffer = new byte[1024];
                                while (input.read(buffer) > 0) {
                                    fos.write(buffer);
                                }
                            }
                            CallableStatement cs = con.prepareCall ("{call addsongtohistory(?, ?)}");
                            cs.setString(1,user.getUsername());  
                            cs.setString(2, songs.get(row).getId());
                            cs.executeUpdate();
                            cs.close();

                            con.close();  

                            System.out.println("success");  

                            // TODO add your handling code here:
                            audioInputStream = 
                                    AudioSystem.getAudioInputStream(new File("C:\\Users\\lenovo\\Desktop\\temp.wav").getAbsoluteFile());
                        } catch (UnsupportedAudioFileException ex) {
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (SQLException ex) {
                            Logger.getLogger(UserViewFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(UserViewFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        try {
                            // create clip reference
                            clip = AudioSystem.getClip();
                        } catch (LineUnavailableException ex1) {
                            ex1.printStackTrace();
                        }

                        try {
                            // open audioInputStream to the clip
                            clip.open(audioInputStream);
                        } catch (LineUnavailableException ex2) {
                            ex2.printStackTrace();
                        } catch (IOException ex3) {
                            ex3.printStackTrace();
                        }

                        clip.loop(Clip.LOOP_CONTINUOUSLY); 
                        clip.start(); 
                        target.setValueAt("Stop", row, col);
                        lastRow = row;
                        lastCol = col;
                        songs.get(lastRow).toggleStatus();
                        prevPlaying = target;
                    }
                }
                
            }
        });
        myTable.setBackground(new Color(0xF7F7F7));
        myTable.getTableHeader().setBackground(new Color(0xF7F7F7));
        jScrollPane1.setViewportView(myTable);
        sorter = new TableRowSorter<TableModel>(myTable.getModel());
        myTable.setRowSorter(sorter);
        jScrollPane1.validate();
        jScrollPane1.setVisible(true);
        jScrollPane1.getViewport().setBackground(new Color(0xF7F7F7));
        this.getContentPane().setBackground(new Color(0xF7F7F7));
        this.validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchButton = new javax.swing.JButton();
        searchText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        panel = new javax.swing.JPanel();
        profileButton = new javax.swing.JButton();
        welcomeText = new java.awt.Label();
        logout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        searchText.setText(" ");

        panel.setForeground(new java.awt.Color(240, 240, 240));

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 427, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panel);

        profileButton.setText("My Profile");
        profileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileButtonActionPerformed(evt);
            }
        });

        welcomeText.setText("label1");

        logout.setText("Logout");
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(welcomeText, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                .addComponent(logout)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profileButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchText, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(searchButton)
                        .addComponent(searchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(profileButton))
                    .addComponent(welcomeText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        // TODO add your handling code here:
        String searchString = searchText.getText().trim();
        if (searchString.length() == 0) {
            sorter.setRowFilter(null);
        }
        else {
            sorter.setRowFilter(RowFilter.regexFilter(searchString));
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void profileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileButtonActionPerformed
        // TODO add your handling code here:
        try {
            clip.stop();
        }
        catch(Exception e) {
            
        }
        UserProfile ob;
        try {
            ob = new UserProfile(this, user);
            ob.setVisible(true);
            this.setVisible(false); 
        } catch (IOException ex) {
            Logger.getLogger(UserViewFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_profileButtonActionPerformed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        // TODO add your handling code here:
        try {
            clip.stop();
        }
        catch(Exception e) {
            
        }
        UserLogin ob = new UserLogin(this);
        ob.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_logoutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton logout;
    private javax.swing.JPanel panel;
    private javax.swing.JButton profileButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchText;
    private java.awt.Label welcomeText;
    // End of variables declaration//GEN-END:variables


    private ArrayList<Song> getSongs() throws IOException {
        ArrayList<Song> songs = new ArrayList<>();
        try {                        
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            
            //step2 create  the connection object
            Connection con=DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:newdatabase","system","not12345");
            
            //step3 create the statement object
            Statement stmt=con.createStatement();
            
            //step4 execute query
            ResultSet rs=stmt.executeQuery("select * from song");
            while(rs.next()) {
                String artists = "";
                PreparedStatement ps=con.prepareStatement("select artist_id from song_artist where song_id=?");
                ps.setString(1, ""+rs.getString(1));
                ResultSet rs1=ps.executeQuery();
                if(rs1.next()) {
                    PreparedStatement ps1=con.prepareStatement("select name from artist where id=?");
                    ps1.setString(1, rs1.getString(1));
                    ResultSet rs2=ps1.executeQuery();
                    rs2.next();
                    artists = rs2.getString(1);
                }
                while(rs1.next()) {
                    PreparedStatement ps1=con.prepareStatement("select name from artist where id=?");
                    ps1.setString(1, rs1.getString(1));
                    ResultSet rs2=ps1.executeQuery();
                    rs2.next();
                    artists = artists + ", " + rs2.getString(1);
                }
                songs.add(new Song(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), artists, Integer.parseInt(rs.getString(5))));
            }
            //step5 close the connection object
            con.close();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return songs;
    }
//
//    private void updateTable() {
//        return;
//    }

}

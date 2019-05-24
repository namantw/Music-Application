package muzix;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;  

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author iiita
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableModel;
import oracle.sql.CLOB;
public class AdminView extends javax.swing.JFrame {
    
    /**
     * Creates new form ResultTable
     */
    private JFrame prevView;
    private DefaultTableModel model;
    private TableRowSorter<TableModel> sorter;
    private final User user;
    private ArrayList<Song> songs;
    AudioInputStream audioInputStream;
    Long currentFrame; 
    Clip clip; 
    String status;
    JTable prevPlaying;
    int lastRow;
    int lastCol;
    
    public AdminView(JFrame prevView, User user) throws IOException {
        
        this.user = user;
        this.prevView = prevView;
        initComponents();
        this.setSize(prevView.getSize());
        this.setLocation(prevView.getLocation());
        
        addButton.setBorderPainted(false);
        addButton.setBorder(new RoundedBorder(8)); //10 is the radius
        addButton.setFocusPainted(false);
        addButton.setBackground(new Color(0x90CCF4));
        
        
        addArtist.setBorderPainted(false);
        addArtist.setBorder(new RoundedBorder(8)); //10 is the radius
        addArtist.setFocusPainted(false);
        addArtist.setBackground(new Color(0x90CCF4));
        
        
        backButton.setBorderPainted(false);
        backButton.setBorder(new RoundedBorder(8)); //10 is the radius
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(0x90CCF4));
        
        
        addAlbum.setBorderPainted(false);
        addAlbum.setBorder(new RoundedBorder(8)); //10 is the radius
        addAlbum.setFocusPainted(false);
        addAlbum.setBackground(new Color(0x90CCF4));
        
        
        updateAccountInfo.setBorderPainted(false);
        updateAccountInfo.setBorder(new RoundedBorder(8)); //10 is the radius
        updateAccountInfo.setFocusPainted(false);
        updateAccountInfo.setBackground(new Color(0x90CCF4));
        
        
        searchButton.setBorderPainted(false);
        searchButton.setBorder(new RoundedBorder(8)); //10 is the radius
        searchButton.setFocusPainted(false);
        searchButton.setBackground(new Color(0x90CCF4)); 
        
        this.getContentPane().setBackground(new Color(0xF7F7F7));
        this.validate();
    }
    
    public void updateTable() throws IOException {

        String col[] = {"Song Name", "Album", "Genre", "Artists", "", ""};
        
        songs = this.getSongs();
        DefaultTableModel model = new DefaultTableModel(col, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        for(Song song: songs) {
            model.addRow(song.getRowAdmin());
            System.out.println(song.getRowAdmin()[1]);
        }
        
        JTable myTable = new JTable(model);
        
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
                if(col==5) {
                    try {
                        Class.forName("oracle.jdbc.driver.OracleDriver");
                        
                        Connection con=DriverManager.getConnection(
                                "jdbc:oracle:thin:@localhost:1521:newdatabase","system","not12345");
                        PreparedStatement ps=con.prepareStatement("delete from song where id = ?");
                        ps.setString(1, songs.get(row).getId());  
                        ResultSet rs=ps.executeQuery();
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(AdminView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        updateTable();
                    } catch (IOException ex) {
                        Logger.getLogger(AdminView.class.getName()).log(Level.SEVERE, null, ex);
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
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        myTable = new javax.swing.JTable();
        backButton = new javax.swing.JButton();
        searchText = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        updateAccountInfo = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        addArtist = new javax.swing.JButton();
        addAlbum = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Result Table");
        setMinimumSize(new java.awt.Dimension(800, 500));

        myTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll Number", "Title 2"
            }
        ));
        myTable.setRequestFocusEnabled(false);
        myTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                myTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(myTable);

        backButton.setText("Logout");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        searchText.setText("Enter Search String");
        searchText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchTextMouseClicked(evt);
            }
        });
        searchText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTextActionPerformed(evt);
            }
        });

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        updateAccountInfo.setText("Account Settings");
        updateAccountInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateAccountInfoActionPerformed(evt);
            }
        });

        addButton.setText("Add Song");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        addArtist.setText("Add Artist");
        addArtist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addArtistActionPerformed(evt);
            }
        });

        addAlbum.setText("Add Album");
        addAlbum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAlbumActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(addAlbum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addArtist)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateAccountInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchText, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backButton)
                    .addComponent(searchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton)
                    .addComponent(updateAccountInfo)
                    .addComponent(addButton)
                    .addComponent(addArtist)
                    .addComponent(addAlbum))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // TODO add your handling code here:
        try {
            clip.stop();
        }
        catch(Exception e) {
            
        }
        
        prevView.setSize(this.getSize());
        prevView.setLocation(this.getLocation());
        prevView.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_backButtonActionPerformed

    private void searchTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTextActionPerformed
        // TODO add your handling code here:\
        String searchString = searchText.getText().trim();
        if (searchString.length() == 0) {
            sorter.setRowFilter(null);
        }
        else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchString)); //Ingnore case
        }
    }//GEN-LAST:event_searchTextActionPerformed

    private void searchTextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchTextMouseClicked
        
        searchText.setText("");
    }//GEN-LAST:event_searchTextMouseClicked

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        // TODO add your handling code here:
        try {
            clip.stop();
        }
        catch(Exception e) {

        }
        String searchString = searchText.getText().trim();
        if (searchString.length() == 0) {
            sorter.setRowFilter(null);
        }
        else {
            sorter.setRowFilter(RowFilter.regexFilter(searchString));
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void updateAccountInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateAccountInfoActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            searchText.setText("");
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminView.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            // TODO add your handling code here:
            JTextField name = new JTextField();
            name.setText(user.getName());
            JTextField oldPassword = new JPasswordField();
            JTextField password = new JPasswordField();
            
            Object[] input = {
                "Name : ", name,
                "Old Password :", oldPassword,
                "New Password :", password,
            };
            int option = JOptionPane.showConfirmDialog(this, input, "Enter Song details", JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION) {
                    Connection con=DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:newdatabase","system","not12345");

                    String query = "select password, name from adminDetails where username=?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, user.getUsername());

                    ResultSet rs = ps.executeQuery();
                    //ResultSet rs = stmt.executeQuery("select password, name from adminDetails where username="+username.getText().trim().toLowerCase());

                    if(!rs.next()) {
                        System.out.println("Fail - "+password.getText()+" "+oldPassword.getText()+" " +user.getUsername());
                        JOptionPane.showMessageDialog(this, "Error!\n");
                        return;
                    }
                    else {
                        if(rs.getString(1).equalsIgnoreCase(oldPassword.getText()) && !(password.getText().equals(oldPassword))) {
                            ps = con.prepareStatement("update adminDetails set password=? where username=?");
                            ps.setString(1, password.getText().trim());
                            ps.setString(2, user.getUsername().toLowerCase());
                            ps.execute();
                        }
                        if(rs.getString(1).equalsIgnoreCase(oldPassword.getText()) && !(name.getText().equals(user.getName()))) {
                            ps = con.prepareStatement("update adminDetails set name=? where username=?");
                            ps.setString(1, password.getText().trim());
                            ps.setString(2, user.getUsername().toLowerCase());
                            ps.execute();
                        }
                        if(!rs.getString(1).equalsIgnoreCase(oldPassword.getText())) {
                            JOptionPane.showMessageDialog(this, "Old password did not match!\n");
                        }
                    }
                    
                    con.close();  

                } 
                JOptionPane.showMessageDialog(this, "Success!\n");
            }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error!\n"+e.getMessage());
        }
    }//GEN-LAST:event_updateAccountInfoActionPerformed

    private void myTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_myTableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_myTableMouseClicked
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            // TODO add your handling code here:
            try {
                clip.stop();
            }
            catch(Exception e) {

            }
            
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminView.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //step2 create  the connection object
            Connection con=DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:newdatabase","system","not12345");
            
            //step3 create the statement object
            Statement stmt=con.createStatement();
            
            //step4 execute query
            ResultSet rs=stmt.executeQuery("select * from album");
            ArrayList<String> albumList = new ArrayList<String>();
            HashMap<String, String> albumID = new HashMap<>();
            
            while(rs.next()) {
                albumList.add((rs.getString(2)));
                albumID.put(rs.getString(2), rs.getString(1));
            }
            
            
            //step4 execute query
            rs=stmt.executeQuery("select * from artist");
            ArrayList<String> artistList = new ArrayList<String>();
            HashMap<String, String> artistID = new HashMap<>();
            
            while(rs.next()) {
                artistList.add((rs.getString(2)));
                artistID.put(rs.getString(2), rs.getString(1));
            }
            //step5 close the connection object
            con.close();
            
            
            // TODO add your handling code here:
//            JTextField id = new JTextField();
            JTextField name = new JTextField();
            JTextField genre = new JTextField();
            JTextField song_num = new JTextField();
            JComboBox<String> albums = new JComboBox<String>(albumList.toArray(new String[0]));
            JList artists = new JList(artistList.toArray(new String[0]));
            artists.setSelectionMode(
            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            int[] select = {19, 20, 22};
            
            Object[] input = {
//                "Id : ", id,
                "Name : ", name,
                "Genre :", genre,
                "Artist :", artists,
                "Song Number in Album :", song_num,
                "Album :", albums,
            };
            int option = JOptionPane.showConfirmDialog(this, input, "Enter Song details", JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION) {
                JFileChooser jfc = new JFileChooser();

		int returnValue = jfc.showOpenDialog(null);
		// int returnValue = jfc.showSaveDialog(null);
                String filePath = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    System.out.println(selectedFile.getAbsolutePath());
                    filePath = selectedFile.getAbsolutePath();
                }
                 try{  
                    

                    File f=new File(filePath);  
                    
                    Class.forName("oracle.jdbc.driver.OracleDriver");  
                    con = DriverManager.getConnection(
                             "jdbc:oracle:thin:@localhost:1521:newdatabase","system","not12345");
                     
                    PreparedStatement ps = con.prepareStatement ("insert into song values(?, ?, ?, ?, ?, ?, ?)");
                    ps.setString(1, null);  
                    ps.setString(2, name.getText());
                    ps.setString(3, albumID.get(albums.getSelectedItem().toString()));
                    ps.setString(4, genre.getText());
                    ps.setInt(5, 0);
                    ps.setInt(6, Integer.parseInt(song_num.getText()));

                    System.out.println(""+f.length());
                    ps.setBytes(7, readFile(filePath));

                    ps.executeUpdate();
                    ps.close();
                    
                    PreparedStatement ps1 = con.prepareStatement ("select id from song where name=? and album_id=?");
                    ps1.setString(1, name.getText());  
                    ps1.setString(2, albumID.get(albums.getSelectedItem().toString()));

                    //step4 execute query
                    ResultSet rs1=ps1.executeQuery();
                    String id = "";
                    while(rs1.next()) {
                        id = rs1.getString(1);
                    }
                    ps1.close();

                    for(Object a:artists.getSelectedValuesList()) {
                        ps = con.prepareCall ("{call addsongartist(?, ?)}");
                        ps.setString(1, id);  
                        ps.setString(2, artistID.get(a.toString()));
                        ps.executeUpdate();
                        ps.close();
                    }
                            
                    con.close();  

                } catch (Exception e) {e.printStackTrace();}  
                this.updateTable();
                JOptionPane.showMessageDialog(this, "Success!\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error!\n"+e.getMessage());
        }
        
        
    }//GEN-LAST:event_addButtonActionPerformed

    private void addArtistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addArtistActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            searchText.setText("");
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminView.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            // TODO add your handling code here:
//            JTextField id = new JTextField();
            JTextField name = new JTextField();
            JTextField about = new JTextField();
            
            Object[] input = {
//                "Id : ", id,
                "Name : ", name,
                "About : ", about,
            };
            int option = JOptionPane.showConfirmDialog(this, input, "Enter Artist details", JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION) {
                 try{  
                    Class.forName("oracle.jdbc.driver.OracleDriver");  
                     Connection con = DriverManager.getConnection(
                             "jdbc:oracle:thin:@localhost:1521:newdatabase","system","not12345");
                     
                    CallableStatement ps = con.prepareCall ("{call addartist(?, ?, ?)}");
                    ps.setString(1,null);  
                    ps.setString(2, name.getText());
                    ps.setString(3, about.getText());
                    ps.executeUpdate();
                    ps.close();

                    con.close();  

                } catch (Exception e) {e.printStackTrace();}  
                JOptionPane.showMessageDialog(this, "Success!\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error!\n"+e.getMessage());
        }
    }//GEN-LAST:event_addArtistActionPerformed

    private void addAlbumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAlbumActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminView.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            // TODO add your handling code here:
//            JTextField id = new JTextField();
            JTextField name = new JTextField();
            
            Object[] input = {
//                "Id : ", id,
                "Name : ", name,
            };
            int option = JOptionPane.showConfirmDialog(this, input, "Enter Album details", JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION) {
                 try{  
                    Class.forName("oracle.jdbc.driver.OracleDriver");  
                    Connection con = DriverManager.getConnection(
                             "jdbc:oracle:thin:@localhost:1521:newdatabase","system","not12345");
                     
                    CallableStatement ps = con.prepareCall ("{call addalbum(?, ?)}");
                    ps.setString(1, null);  
                    ps.setString(2, name.getText());
                    ps.executeUpdate();
                    ps.close();

                    con.close();  

                } catch (Exception e) {e.printStackTrace();}  
                JOptionPane.showMessageDialog(this, "Success!\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error!\n"+e.getMessage());
        }
    }//GEN-LAST:event_addAlbumActionPerformed

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        try {
            this.updateTable();
        } catch (IOException ex) {
            Logger.getLogger(AdminView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAlbum;
    private javax.swing.JButton addArtist;
    private javax.swing.JButton addButton;
    private javax.swing.JButton backButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable myTable;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchText;
    private javax.swing.JButton updateAccountInfo;
    // End of variables declaration//GEN-END:variables
    private byte[] readFile(String file) {
        ByteArrayOutputStream bos = null;
        try {
            File f = new File(file);
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1;) {
                bos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e2) {
            System.err.println(e2.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }
    
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
}

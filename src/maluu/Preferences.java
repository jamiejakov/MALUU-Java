
package maluu;

import org.jdesktop.application.Action;
//import java.io.*;
import java.io.IOException; 
import java.io.BufferedReader; 
import java.io.InputStreamReader;
import javax.swing.JOptionPane;

public class Preferences extends javax.swing.JDialog {
    public static final String verifyLink="http://myanimelist.net/api/account/verify_credentials.xml";
    public static final String curl ="curl";
    private char[] twitPass ;
    private static String malUname, twitUname, malUpass, twitUpass;
    private static String malID = "";
    private static boolean loggedin,fail;
    public JOptionPane loginSuxcess, loginFail, internetFail;
    private FileHandling fh;
    private javax.swing.JRadioButton[] rbut = new javax.swing.JRadioButton[6];
    
    public Preferences(java.awt.Frame parent, int i) {
        super(parent);
        initComponents();
        rbut[0]=nameRadioButton;
        rbut[1]=episodeRadioButton;
        rbut[2]=watchedRadioButton;
        rbut[3]=scoreRadioButton;
        rbut[4]=typeRadioButton;
        rbut[5]=seasonRadioButton;
        fh = new FileHandling();
        if (loggedin) setButtonAndtextFalse();
        getRootPane().setDefaultButton(malSaveButton); //closeButton);
        tabs.setSelectedIndex(3);
        if (fh.getAnimeID()!=0) setButtonAndtextFalse();
        tableRadioButtonLayout(fh.getTableLayout());
        
    }


    /**
     * procedure that disables the button and text fields after entering the correct password.
     */
    private void setButtonAndtextFalse(){
        malSaveButton.setEnabled(false);
        malUsernameField.setText(fh.getMalUname());
        malPasswordField.setText("password");
        malUsernameField.setEditable(false);
        malPasswordField.setEditable(false);
    }
    private void tableRadioButtonLayout(int code){
        String tmp = code+"";
        int[] collumn = new int[6];
        for (int i=0; i<6; i++){       
            rbut[i].setSelected( (Integer.parseInt(tmp.charAt(i)+"")) ==1);
        }
    }
    
    /**
     * all the getters for all the fields 
     */
   /* public static String getMALID(){
        return malID;
    }
    public static String getMALPass(){
        return temp;
    }
    public static String getMALUname(){
        return malUname;
    }*/
    
    /**
     * Action that closes the preferences window
     */
    @Action public void closePreferences() {
        dispose();
    }
    /**
     * the set Username and Password method 
     */
    private void setUnamePass(int i){     // setUnamePass(1);
        switch(i){
            case 1:                       // Case 1 for MAL
                char[] tmp = malPasswordField.getPassword();
                String temp= "";
                for (int j=0; j<tmp.length; j++) temp = temp + tmp[j];
                fh.writeToFile(temp, 2);
                fh.writeToFile(malUsernameField.getText(), 1);

            case 2:                       // Case 2 for Twitter
                twitPass= twitterPasswordField.getPassword();
                twitUname= twitterUsernameField.getText();
                twitUpass = twitUpass = twitUname+":"+twitPass;
            default:
                i=0;
        }  
    }
    /**
     *    the SET and CHECK MAL Login Method
     */
    
    @Action public void setAndCheckMALlogin() {
        Runtime rt = Runtime.getRuntime(); 
    
        Process auth=null; 
        BufferedReader input=null; 
        String line=null; 
        setUnamePass(1);
        //temp="";
        //for (int i=0; i<malPass.length; i++) temp = temp + malPass[i];            
        malUpass = fh.getMalUname()+":"+fh.getMalPass();
        String command = curl + " -u " + malUpass + " " + verifyLink;

                // executes the curl command and recieves the data
        try { 
            auth= rt.exec(command); 
            input = new BufferedReader(new InputStreamReader(auth.getInputStream())); 
            line=input.readLine();
            if (line.indexOf("Invalid")>-1){        
                JOptionPane.showMessageDialog(loginFail, "You have failed to log into MyAnimeList. Please check your username and/or password");
                deleteMALlogin();
                
                fail=true;
            }else{
                 JOptionPane.showMessageDialog(loginSuxcess, "You have logged into MyAnimeList");
                 setButtonAndtextFalse();
                 fail=false;
            }
            
        } catch (IOException e1) { 
            JOptionPane.showMessageDialog(internetFail, "Either MAL.net or your internet is dead.");
        }
        
        
        if (!fail){
            try { 
                while( (line=input.readLine())!=null){
                    if ( line.indexOf( "id" ) > -1 ){
                        line = line.replaceAll(" ", "");
                        line = line.replaceAll("<id>","");
                        line = line.replaceAll("</id>", "");
                        fh.writeToFile(line, 0);
                        
                    }               
                } 
            } catch (IOException e1) { 
                e1.printStackTrace();   
                System.exit(0); 
            }   
        }


    }  
    /**
     * The remove all the information and details method
     */
    @Action public void deleteMALlogin() {
        malID=null;
        malUname=null;
        //malPass=null;
        malUpass=null;
        fh.writeToFile("0", 0);
        fh.writeToFile(" ", 1);
        fh.writeToFile(" ", 2);
        malSaveButton.setEnabled(true);
        malUsernameField.setEditable(true);
        malPasswordField.setEditable(true);
        malUsernameField.setText("");
        malPasswordField.setText("");
        
    }  
    
    
    
    
    
    
    
    
    
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        closeButton = new javax.swing.JButton();
        tabs = new javax.swing.JTabbedPane();
        generalPane = new javax.swing.JLayeredPane();
        tablesPane = new javax.swing.JLayeredPane();
        nameRadioButton = new javax.swing.JRadioButton();
        episodeRadioButton = new javax.swing.JRadioButton();
        watchedRadioButton = new javax.swing.JRadioButton();
        scoreRadioButton = new javax.swing.JRadioButton();
        typeRadioButton = new javax.swing.JRadioButton();
        seasonRadioButton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        loginPane = new javax.swing.JLayeredPane();
        malPasswordField = new javax.swing.JPasswordField();
        malUsernameField = new javax.swing.JTextField();
        malUsernameLabel = new javax.swing.JLabel();
        malPasswordLabel = new javax.swing.JLabel();
        malLoginExplanationLabel = new javax.swing.JLabel();
        myAnimeListLogoLabel = new javax.swing.JLabel();
        malSaveButton = new javax.swing.JButton();
        malLogoutButton = new javax.swing.JButton();
        videoPane = new javax.swing.JLayeredPane();
        twitterPane = new javax.swing.JLayeredPane();
        twitterLogoLabel = new javax.swing.JLabel();
        twitterUsernameField = new javax.swing.JTextField();
        twitterPasswordField = new javax.swing.JPasswordField();
        twitterPasswordLabel = new javax.swing.JLabel();
        twitterUsernameLabel = new javax.swing.JLabel();
        twitterSaveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(maluu.MALUUApp.class).getContext().getResourceMap(Preferences.class);
        setTitle(resourceMap.getString("preferencesBox.title")); // NOI18N
        setBounds(new java.awt.Rectangle(83, 22, 320, 350));
        setMinimumSize(new java.awt.Dimension(320, 370));
        setModal(true);
        setName("preferencesBox"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(maluu.MALUUApp.class).getContext().getActionMap(Preferences.class, this);
        closeButton.setAction(actionMap.get("closePreferences")); // NOI18N
        closeButton.setText(resourceMap.getString("closeButton.text")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        getContentPane().add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 310, -1, -1));

        tabs.setBackground(resourceMap.getColor("tabs.background")); // NOI18N
        tabs.setName("tabs"); // NOI18N

        generalPane.setName("generalPane"); // NOI18N
        tabs.addTab(resourceMap.getString("generalPane.TabConstraints.tabTitle"), generalPane); // NOI18N

        tablesPane.setName("tablesPane"); // NOI18N

        nameRadioButton.setAction(actionMap.get("nameRadioButtonCheck")); // NOI18N
        nameRadioButton.setSelected(true);
        nameRadioButton.setText(resourceMap.getString("nameRadioButton.text")); // NOI18N
        nameRadioButton.setEnabled(false);
        nameRadioButton.setName("nameRadioButton"); // NOI18N
        nameRadioButton.setBounds(40, 80, 110, 23);
        tablesPane.add(nameRadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        episodeRadioButton.setAction(actionMap.get("episodeRadioButtonCheck")); // NOI18N
        episodeRadioButton.setSelected(true);
        episodeRadioButton.setText(resourceMap.getString("episodeRadioButton.text")); // NOI18N
        episodeRadioButton.setName("episodeRadioButton"); // NOI18N
        episodeRadioButton.setBounds(40, 110, 290, 23);
        tablesPane.add(episodeRadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        watchedRadioButton.setAction(actionMap.get("watchedRadioButtonCheck")); // NOI18N
        watchedRadioButton.setSelected(true);
        watchedRadioButton.setText(resourceMap.getString("watchedRadioButton.text")); // NOI18N
        watchedRadioButton.setName("watchedRadioButton"); // NOI18N
        watchedRadioButton.setBounds(40, 140, 390, 23);
        tablesPane.add(watchedRadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        scoreRadioButton.setAction(actionMap.get("scoreRadioButtonCheck")); // NOI18N
        scoreRadioButton.setSelected(true);
        scoreRadioButton.setText(resourceMap.getString("scoreRadioButton.text")); // NOI18N
        scoreRadioButton.setEnabled(false);
        scoreRadioButton.setName("scoreRadioButton"); // NOI18N
        scoreRadioButton.setBounds(40, 170, 210, 23);
        tablesPane.add(scoreRadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        typeRadioButton.setAction(actionMap.get("typeRadioButtonCheck")); // NOI18N
        typeRadioButton.setSelected(true);
        typeRadioButton.setText(resourceMap.getString("typeRadioButton.text")); // NOI18N
        typeRadioButton.setName("typeRadioButton"); // NOI18N
        typeRadioButton.setBounds(40, 200, 340, 23);
        tablesPane.add(typeRadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        seasonRadioButton.setAction(actionMap.get("seasonRadioButtonCheck")); // NOI18N
        seasonRadioButton.setSelected(true);
        seasonRadioButton.setText(resourceMap.getString("seasonRadioButton.text")); // NOI18N
        seasonRadioButton.setName("seasonRadioButton"); // NOI18N
        seasonRadioButton.setBounds(40, 230, 410, 23);
        tablesPane.add(seasonRadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setBounds(80, 10, 350, 60);
        tablesPane.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabs.addTab(resourceMap.getString("tablesPane.TabConstraints.tabTitle"), tablesPane); // NOI18N

        loginPane.setForeground(resourceMap.getColor("loginPane.foreground")); // NOI18N
        loginPane.setName("loginPane"); // NOI18N

        malPasswordField.setText(resourceMap.getString("malPasswordField.text")); // NOI18N
        malPasswordField.setName("malPasswordField"); // NOI18N
        malPasswordField.setBounds(200, 180, 260, 30);
        loginPane.add(malPasswordField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        malUsernameField.setForeground(resourceMap.getColor("malUsernameField.foreground")); // NOI18N
        malUsernameField.setText(resourceMap.getString("malUsernameField.text")); // NOI18N
        malUsernameField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        malUsernameField.setName("malUsernameField"); // NOI18N
        malUsernameField.setBounds(200, 140, 260, 30);
        loginPane.add(malUsernameField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        malUsernameField.getAccessibleContext().setAccessibleName(resourceMap.getString("jTextField1.AccessibleContext.accessibleName")); // NOI18N

        malUsernameLabel.setFont(resourceMap.getFont("malUsernameLabel.font")); // NOI18N
        malUsernameLabel.setText(resourceMap.getString("malUsernameLabel.text")); // NOI18N
        malUsernameLabel.setName("malUsernameLabel"); // NOI18N
        malUsernameLabel.setBounds(90, 150, 100, 20);
        loginPane.add(malUsernameLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        malPasswordLabel.setFont(resourceMap.getFont("malPasswordLabel.font")); // NOI18N
        malPasswordLabel.setText(resourceMap.getString("malPasswordLabel.text")); // NOI18N
        malPasswordLabel.setName("malPasswordLabel"); // NOI18N
        malPasswordLabel.setBounds(90, 180, 90, 20);
        loginPane.add(malPasswordLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        malLoginExplanationLabel.setFont(resourceMap.getFont("malLoginExplanationLabel.font")); // NOI18N
        malLoginExplanationLabel.setText(resourceMap.getString("malLoginExplanationLabel.text")); // NOI18N
        malLoginExplanationLabel.setName("malLoginExplanationLabel"); // NOI18N
        malLoginExplanationLabel.setBounds(60, 50, 400, 90);
        loginPane.add(malLoginExplanationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        myAnimeListLogoLabel.setIcon(resourceMap.getIcon("myAnimeListLogoLabel.icon")); // NOI18N
        myAnimeListLogoLabel.setText(resourceMap.getString("myAnimeListLogoLabel.text")); // NOI18N
        myAnimeListLogoLabel.setName("myAnimeListLogoLabel"); // NOI18N
        myAnimeListLogoLabel.setBounds(140, 10, 260, 60);
        loginPane.add(myAnimeListLogoLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        malSaveButton.setAction(actionMap.get("setAndCheckMALlogin")); // NOI18N
        malSaveButton.setText(resourceMap.getString("malSaveButton.text")); // NOI18N
        malSaveButton.setName("malSaveButton"); // NOI18N
        malSaveButton.setBounds(380, 230, 110, 29);
        loginPane.add(malSaveButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        malLogoutButton.setAction(actionMap.get("deleteMALlogin")); // NOI18N
        malLogoutButton.setText(resourceMap.getString("malLogoutButton.text")); // NOI18N
        malLogoutButton.setName("malLogoutButton"); // NOI18N
        malLogoutButton.setBounds(80, 230, 110, 29);
        loginPane.add(malLogoutButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabs.addTab(resourceMap.getString("loginPane.TabConstraints.tabTitle"), loginPane); // NOI18N

        videoPane.setName("videoPane"); // NOI18N
        tabs.addTab(resourceMap.getString("videoPane.TabConstraints.tabTitle"), videoPane); // NOI18N

        twitterPane.setName("twitterPane"); // NOI18N

        twitterLogoLabel.setIcon(resourceMap.getIcon("twitterLogoLabel.icon")); // NOI18N
        twitterLogoLabel.setText(resourceMap.getString("twitterLogoLabel.text")); // NOI18N
        twitterLogoLabel.setName("twitterLogoLabel"); // NOI18N
        twitterLogoLabel.setBounds(70, -30, 190, 170);
        twitterPane.add(twitterLogoLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        twitterUsernameField.setText(resourceMap.getString("twitterUsernameField.text")); // NOI18N
        twitterUsernameField.setName("twitterUsernameField"); // NOI18N
        twitterUsernameField.setBounds(10, 120, 120, 28);
        twitterPane.add(twitterUsernameField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        twitterPasswordField.setText(resourceMap.getString("twitterPasswordField.text")); // NOI18N
        twitterPasswordField.setName("twitterPasswordField"); // NOI18N
        twitterPasswordField.setBounds(170, 120, 120, 28);
        twitterPane.add(twitterPasswordField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        twitterPasswordLabel.setText(resourceMap.getString("twitterPasswordLabel.text")); // NOI18N
        twitterPasswordLabel.setName("twitterPasswordLabel"); // NOI18N
        twitterPasswordLabel.setBounds(200, 70, 80, 50);
        twitterPane.add(twitterPasswordLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        twitterUsernameLabel.setText(resourceMap.getString("twitterUsernameLabel.text")); // NOI18N
        twitterUsernameLabel.setName("twitterUsernameLabel"); // NOI18N
        twitterUsernameLabel.setBounds(10, 80, 80, 40);
        twitterPane.add(twitterUsernameLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        twitterSaveButton.setText(resourceMap.getString("twitterSaveButton.text")); // NOI18N
        twitterSaveButton.setName("twitterSaveButton"); // NOI18N
        twitterSaveButton.setBounds(100, 160, 120, 29);
        twitterPane.add(twitterSaveButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabs.addTab(resourceMap.getString("twitterPane.TabConstraints.tabTitle"), twitterPane); // NOI18N

        getContentPane().add(tabs, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 540, 320));
        tabs.getAccessibleContext().setAccessibleName(resourceMap.getString("jTabbedPane1.AccessibleContext.accessibleName")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changePreff(int num){
        int tableLayout = fh.getTableLayout();
        tableLayout+=num;
        String tmp="";
        if (num==-100000) tmp="0"+tableLayout;
        else tmp = tableLayout+"";
        fh.writeToFile(tmp,3);
    }
    @Action public void nameRadioButtonCheck() {
        int tmp =1;
        if (nameRadioButton.isSelected()) tmp = 1;
        else tmp =-1;
        changePreff(100000*tmp); 
    }
    @Action public void episodeRadioButtonCheck() {
        int tmp =1;
        if (episodeRadioButton.isSelected()) tmp = 1;
        else tmp =-1;
        changePreff(10000*tmp); 
    }
    @Action public void watchedRadioButtonCheck() {
        int tmp =1;
        if (watchedRadioButton.isSelected()) tmp = 1;
        else tmp =-1;
        changePreff(1000*tmp); 
    }
    @Action public void scoreRadioButtonCheck() {
        int tmp =1;
        if (scoreRadioButton.isSelected()) tmp = 1;
        else tmp =-1;
        changePreff(100*tmp); 
    }
    @Action public void typeRadioButtonCheck() {
        int tmp =1;
        if (typeRadioButton.isSelected()) tmp = 1;
        else tmp =-1;
        changePreff(10*tmp); 
    }
    @Action public void seasonRadioButtonCheck() {
        int tmp =1;
        if (seasonRadioButton.isSelected()) tmp = 1;
        else tmp =-1;
        changePreff(1*tmp); 
    }
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JRadioButton episodeRadioButton;
    private javax.swing.JLayeredPane generalPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane loginPane;
    private javax.swing.JLabel malLoginExplanationLabel;
    private javax.swing.JButton malLogoutButton;
    private javax.swing.JPasswordField malPasswordField;
    private javax.swing.JLabel malPasswordLabel;
    private javax.swing.JButton malSaveButton;
    private javax.swing.JTextField malUsernameField;
    private javax.swing.JLabel malUsernameLabel;
    private javax.swing.JLabel myAnimeListLogoLabel;
    private javax.swing.JRadioButton nameRadioButton;
    private javax.swing.JRadioButton scoreRadioButton;
    private javax.swing.JRadioButton seasonRadioButton;
    private javax.swing.JLayeredPane tablesPane;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JLabel twitterLogoLabel;
    private javax.swing.JLayeredPane twitterPane;
    private javax.swing.JPasswordField twitterPasswordField;
    private javax.swing.JLabel twitterPasswordLabel;
    private javax.swing.JButton twitterSaveButton;
    private javax.swing.JTextField twitterUsernameField;
    private javax.swing.JLabel twitterUsernameLabel;
    private javax.swing.JRadioButton typeRadioButton;
    private javax.swing.JLayeredPane videoPane;
    private javax.swing.JRadioButton watchedRadioButton;
    // End of variables declaration//GEN-END:variables
}

/*
 * MALUUView.java
 */

package maluu;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.awt.Dimension;
import java.net.*;
import java.util.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.xml.sax.InputSource;

/**
 * The application's main frame.
 */
public class MALUUView extends FrameView {

    private static String malUname; // ="jamiejakov";
   // private static String malPass; // = "3071jamieMAL";
    private static int malID; // = "1";
    private static int animeID =0;
    private int[] aniNum = new int[5];
    private int selectedtable = -1;
    private javax.swing.JTable[] tables = new javax.swing.JTable[5];
    private double days = 0.0;
    private boolean updated = false;
    private JOptionPane loginSuxcess, loginFail, internetFail;
    private FileHandling fh;


    // <editor-fold defaultstate="collapsed" desc="Constructor"> 
    public MALUUView(SingleFrameApplication app) {
        super(app);
        initComponents();   
        fh = new FileHandling();
        tables[0]=watchingTable;
        tables[1]=onholdTable;
        tables[2]=ptwTable;
        tables[3]=compleatedTable;
        tables[4]=droppedTable;
        
        for (int j=0;j<5;j++){
           /* culumnWidth(j,"Name", 200);
            culumnWidth(j,"Episode", 120);
            culumnWidth(j,"Watched", 60);
            culumnWidth(j,"Score", 25);
            culumnWidth(j,"Type", 50);
            culumnWidth(j,"Season", 90);*/
            culumnWidth(j,"id", 0);
            culumnWidth(j,"link", 0);
            TableColumn myCol = tables[j].getColumnModel().getColumn(1);
            myCol.setCellRenderer(new ProgressCellRenderer());
            ((DefaultRowSorter)tables[j].getRowSorter()).setSortable(2, false);
        }
        malID = fh.getAnimeID();
        if (malID!=0) update();
        /*else{
            //showPreferences();
            //update();
        }*/
    }
  
    private void culumnWidth(int i, String culumn, int size){
        tables[i].getColumn(culumn).setWidth(size);
        tables[i].getColumn(culumn).setPreferredWidth(size);
        tables[i].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        if (size==0){
            tables[i].getColumn(culumn).setMinWidth(size);
            tables[i].getColumn(culumn).setMaxWidth(size);  
        }
        else if (size<30){
            tables[i].getColumn(culumn).setMinWidth(size);
            tables[i].getColumn(culumn).setMaxWidth(size); 
            tables[i].getColumn(culumn).setResizable(false);
        }else{
            tables[i].getColumn(culumn).setMinWidth(size-50);
            tables[i].getColumn(culumn).setMaxWidth(size+50);
            tables[i].getColumn(culumn).setResizable(true);
        }
    
    }
    private void tableLayout(int code){
        String tmp = code+"";
        int[] collumn = new int[6];
        for (int i=0; i<6; i++){           
            collumn[i]=Integer.parseInt(tmp.charAt(i)+"");
        }
        
        if (collumn[0]==0) for (int j=0;j<5;j++) culumnWidth(j,"Name", 1);
        else for (int j=0;j<5;j++) culumnWidth(j,"Name", 200);
        if (collumn[1]==0) for (int j=0;j<5;j++) culumnWidth(j,"Episode", 1);  // Name, Episode, Watched, Score, Type, Season
        else for (int j=0;j<5;j++) culumnWidth(j,"Episode", 120);
        if (collumn[2]==0) for (int j=0;j<5;j++) culumnWidth(j,"Watched", 1);
        else for (int j=0;j<5;j++) culumnWidth(j,"Watched", 60);
        if (collumn[3]==0) for (int j=0;j<5;j++) culumnWidth(j,"Score", 1);
        else for (int j=0;j<5;j++) culumnWidth(j,"Score", 25);
        if (collumn[4]==0) for (int j=0;j<5;j++) culumnWidth(j,"Type", 1);
        else for (int j=0;j<5;j++) culumnWidth(j,"Type", 50);
        if (collumn[5]==0) for (int j=0;j<5;j++) culumnWidth(j,"Season", 1);
        else for (int j=0;j<5;j++) culumnWidth(j,"Season", 90);
    }
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="File"> 
    private String readFile(){
        malID = fh.getAnimeID();
        malUname = fh.getMalUname();
        tableLayout(fh.getTableLayout());
        return fh.getMalPass();
    }
    // </editor-fold> 
            
    // <editor-fold defaultstate="collapsed" desc="Preferences"> 
    private void pref(int i){
        if (preferencesBox == null) {
            JFrame mainFrame = MALUUApp.getApplication().getMainFrame();
            preferencesBox = new Preferences(mainFrame, i);
            preferencesBox.setLocationRelativeTo(mainFrame);
        }
        MALUUApp.getApplication().show(preferencesBox); 
        
    }
    @Action public void showPreferencesGeneral() {
        pref(0);
    }
    @Action public void showPreferencesLogin() {
        pref(1);
    }
    @Action public void showPreferencesPlayers() {
        pref(2);
    }
    @Action public void showPreferencesFolders() {
        pref(3);
    }
    @Action public void showPreferencesTwitter() {
        pref(4);
    }
    
    
   /* public static void setUnamePassID(){
        malUname= maluu.Preferences.getMALUname();
        //maluu.Preferences.getMALPass();
        malID= maluu.Preferences.getMALID();
        
    }*/
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="XML Parsing"> 

    private String deleteXml(String linein, String xml){
       linein = linein.replaceAll("<"+ xml +">","");
       linein = linein.replaceAll("</"+ xml +">", ""); 
       return linein;
    }
    
    private void cleanTable(javax.swing.JTable table){
        int rowcount =table.getRowCount();
        while (rowcount>0){
            ((DefaultTableModel)table.getModel()).removeRow(rowcount-1);
            rowcount=table.getRowCount();    
        }
    } 
    private void readFromXML(){
        int linecounter =1;
        int animecounter =0;
        String line =null;
        int parse;
        int i=0;
        BufferedReader reader=null;

        try {
 
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
            boolean[] b = new boolean[10];
            String [] a = new String[9];

            public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {

                //System.out.println("Start Element :" + qName);
                if (qName.equalsIgnoreCase("my_status")) b[0] = true;
                else if (qName.equalsIgnoreCase("series_title")) b[1] = true;
                else if (qName.equalsIgnoreCase("my_watched_episodes")) b[2] = true;
                else if (qName.equalsIgnoreCase("series_episodes")) b[3] = true;
                else if (qName.equalsIgnoreCase("my_score")) b[4] = true;
                else if (qName.equalsIgnoreCase("series_type")) b[5] = true;
                else if (qName.equalsIgnoreCase("series_image")) b[6] = true;
                else if (qName.equalsIgnoreCase("series_animedb_id")) b[7] = true;
                else if (qName.equalsIgnoreCase("series_start")) b[8] = true;
                else if (qName.equalsIgnoreCase("user_days_spent_watching")) b[9] = true;

            }

            public void endElement(String uri, String localName,String qName) throws SAXException {
                int i = 0;    
                //System.out.println("Testing : "+qName);
                if (qName.indexOf("anime")>-1 && a[0]!=null){
                    if (Integer.parseInt(a[0])==1) i=0;
                    else if (Integer.parseInt(a[0])==2) i=3;
                    else if (Integer.parseInt(a[0])==3) i=1;
                    else if (Integer.parseInt(a[0])==4) i=4;
                    else if (Integer.parseInt(a[0])==6) i=2;
                    double a2= Integer.parseInt(a[2]);
                    double a3;
                    if (a[3].indexOf("?")==-1) a3=Integer.parseInt(a[3]);
                    else a3=a2+12;
                    double percent =(a2/a3)*100;
                    String per;
                    if (percent<10) per="0"+(int)percent+"%";
                    else per = (int) percent+"%";
                    int ii= Integer.parseInt(a[4]);
                    Object[] data = {a[1],per,a[2]+"/"+a[3],ii,a[5],a[8], a[6], a[7]};
                                   // 0   1        2        3    4    5    6     7
                    ((DefaultTableModel)tables[i].getModel()).addRow(data); 
                    aniNum[i]++;
                    a[0]=null;
                } 
            }

            public void characters(char ch[], int start, int length) throws SAXException {
                
                if (b[0]) {
                    a[0] = new String(ch,start,length);                   
                    b[0] = false;
                }
                if (b[1]) {
                    a[1] = new String(ch,start,length); 
                    b[1] = false;
                }
                if (b[2]) {
                    a[2] = new String(ch,start,length);
                    b[2] = false;
                }
                if (b[3]) {
                    String s= new String(ch,start,length);
                    int parse = Integer.parseInt(s);
                    if (parse==0) s ="?";
                    a[3] = s;
                    //System.out.println("Out of EP : " + new String(ch, start, length));
                    b[3] = false;
                }
                if (b[4]) {
                    a[4] = new String(ch,start, length);
                        //System.out.println("Score : " + new String(ch, start, length));
                    b[4] = false;
                }
                if (b[5]) {
                    int type = Integer.parseInt(new String(ch,start,length));
                    switch (type){
                        case 1: a[5]= "TV"; break;
                        case 2: a[5]= "OVA"; break;
                        case 3: a[5]= "Movie"; break;
                        case 4: a[5]= "Special"; break;
                        case 5: a[5]= "ONA"; break;
                        case 6: a[5]= "Music"; break;   
                    }
                    //System.out.println("Type : " + new String(ch, start, length));
                    b[5] = false;
                }
                if (b[6]) {
                    a[6] = new String(ch,start,length);
                    //System.out.println("Image URL : " + new String(ch, start, length));
                    b[6] = false;
                }
                if (b[7]) {
                    a[7] = new String(ch, start, length);
                    //System.out.println("ID : " + new String(ch, start, length));
                    b[7] = false;
                }
                if (b[8]) {
                    String k = new String(ch, start, length);
                    String[] split = k.split("-");
                    String season ="";
                    if (split[0].indexOf("0000")>-1) split[0] = "No Data";
                    else{
                        if (Integer.parseInt(split[1])>2 && Integer.parseInt(split[1])<6) season= " 1-Spring";
                        if (Integer.parseInt(split[1])>5 && Integer.parseInt(split[1])<9) season= " 2-Summer";
                        if (Integer.parseInt(split[1])>8 && Integer.parseInt(split[1])<12) season= " 3-Fall";
                        if (Integer.parseInt(split[1])>11){
                            int a = Integer.parseInt(split[0]);
                            split[0] = a+"";
                            season= " 4-Winter";
                        }
                        if (Integer.parseInt(split[1])<3){
                            int a = Integer.parseInt(split[0])-1;
                            split[0] = a+"";
                            season= " 4-Winter";
                        }
                    }
                    a[8] = split[0]+season;
                    //System.out.println("Season : " + new String(ch, start, length));
                    b[8] = false;
                }
                if (b[9]) {
                    days = Double.parseDouble(new String(ch,start,length));
                    //System.out.println("daysWatching : " + new String(ch, start, length));
                    b[9] = false;
                }
            }
         };
        InputStream inputStream= new URL("http://myanimelist.net/malappinfo.php?u="+malUname+"&status=all&type=anime").openStream();
        Reader reader1 = new InputStreamReader(inputStream,"UTF-8");
        InputSource is = new InputSource(reader1);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);


         } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(internetFail, "Either MAL.net or your internet is dead.");
         }
    }
    // </editor-fold> 
    
    @Action public void update() {
        readFile();
        days = 0.0;
        for (int i=0; i<5; i++) aniNum[i] =0;
        if (updated) for (int i=0; i<5; i++) tables[i].getRowSorter().toggleSortOrder(0);
        for (int j=0; j<5; j++) if (tables[j].getRowCount()!=0) cleanTable(tables[j]);
        if (malUname!=null){
            readFromXML();
            //sort the whole table!!!!
            for (int i=0; i<5; i++){
                tables[i].getRowSorter().toggleSortOrder(0);
                String title = null;
                switch(i){
                    case 0: title = "Watching ("; break;
                    case 1: title = "On-hold ("; break;
                    case 2: title = "PTW ("; break;
                    case 3: title = "Completed ("; break;
                    case 4: title = "Dropped ("; break;
                }
                jTabbedPane1.setTitleAt(i, title+aniNum[i]+")"); 
            }
            if (tables[0].getRowCount()!=0){
                tables[0].getSelectionModel().setSelectionInterval(0,0);
                selectedtable = 0;
                changeTopOnRowSelected();
            }
        }
        updated=true;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Change Episode, Score, Status on button pressed on top"> 
    String statusBoxFix(){
        int tmp = statusBox.getSelectedIndex();
        String a="";
        switch (tmp){
            case 0: 
                a="watching"; 
                break;
            case 1:
                a="on-hold";
                break;
            case 2:
                a="plan to watch";
                break;
            case 3:
                a="completed";
                break;
            case 4:
                a="dropped";
                break;
            default: 
                break;
        }
        return a;
        
    }
    String changeData(int number, String st, int sc){
        String tmp="";
        String command = "";
        try{
            tmp += URLEncoder.encode("episodes", "UTF-8")+"="+URLEncoder.encode(number+"", "UTF-8");
            tmp += "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(st, "UTF-8");
            tmp += "&" + URLEncoder.encode("score", "UTF-8") + "=" + URLEncoder.encode(sc+"", "UTF-8");
            command = "curl -X PUT -u "+malUname+":"+readFile()+" -d "+tmp+" http://mal-api.com/animelist/anime/"+animeID;
        }catch (IOException e1) { 
                e1.printStackTrace();
        }
        return command; 
        
    }
    
    void changeEpStSc(int ep, String st, int sc){
        plusEpisodeButton.setEnabled(true);
        minusEpisodeButton.setEnabled(true);
        boolean fail=false;
        if (animeID!=0){
            BufferedReader input=null; 
            String tmp="";
            int number= Integer.parseInt(watchedNumberTextField.getText());
            int numberof=0;
            int score = 10-scoreBox.getSelectedIndex();
            try {
                
                if (outofNumberLabel.getText().indexOf("?")>-1) numberof = number + 12;
                else numberof = Integer.parseInt(outofNumberLabel.getText());
                number=number+ep;
                String[] a ={""};
                String status = statusBoxFix();
                if (score!=sc) sc=score;
                if (st.indexOf(status)==-1) st=status;
                //if (st.indexOf("plan to watch")>-1) number=0;
                String command = changeData(number, st, sc);
                Process execute = Runtime.getRuntime().exec(command);
                input = new BufferedReader(new InputStreamReader(execute.getInputStream())); 
                if (number==numberof){
                    plusEpisodeButton.setEnabled(false);
                    if (sc!=0 && ep!=0){
                        command = changeData(number, "completed", sc);
                        execute = Runtime.getRuntime().exec(command);
                        input = new BufferedReader(new InputStreamReader(execute.getInputStream())); 
                        //update();
                        jTabbedPane1.setSelectedIndex(3);
                        //update();
                    }else if (sc==0){
                        command = changeData(number, "watching", sc);
                        execute = Runtime.getRuntime().exec(command);
                        input = new BufferedReader(new InputStreamReader(execute.getInputStream())); 
                        jTabbedPane1.setSelectedIndex(0);
                    }
                }else if (number ==0){
                    minusEpisodeButton.setEnabled(false);
                    command = changeData(number, "plan to watch", sc);
                    execute = Runtime.getRuntime().exec(command);
                    input = new BufferedReader(new InputStreamReader(execute.getInputStream()));
                    jTabbedPane1.setSelectedIndex(2);
                }
                if (number==1 && ep==1){
                    command = changeData(number, "watching", sc);
                    execute = Runtime.getRuntime().exec(command);
                    input = new BufferedReader(new InputStreamReader(execute.getInputStream())); 
                    jTabbedPane1.setSelectedIndex(0);
                }
            
            } catch (IOException e1) { 
                e1.printStackTrace();
                //System.exit(1); 
                fail=true;
                JOptionPane.showMessageDialog(internetFail, "Either MAL.net or your internet is dead.");
            }

            
            
            if (!fail){
                watchedNumberTextField.setText(number+"");
                progressBar.setValue(progressBar.getValue()+ep);
                int rowselected = tables[selectedtable].getSelectedRow();
                String data = number+"/"+outofNumberLabel.getText();
                tables[selectedtable].setValueAt(data, rowselected, 2);
                double a =0.000;
                double n1 = number;
                double n2 = numberof;
                a= (n1/n2)*100;
                data = (int)a+"%";
                tables[selectedtable].setValueAt(data, rowselected, 1);
                data=sc+"";
                tables[selectedtable].setValueAt(data, rowselected, 3);
            }

        }        
    
    }
    @Action public void plusEpisode() {
        changeEpStSc(1,"",0);
    }
    @Action public void minusEpisode() {
        changeEpStSc(-1,"",0);
    }
    @Action public void changeSt() {
        changeEpStSc(0,statusBoxFix(),0);
    }
    @Action public void changeSc() {
        int tmp = scoreBox.getSelectedIndex();
        changeEpStSc(0,"",10-tmp);
    }
    @Action public void changeEp() {
        changeEpStSc(0,"",0);
    }
    // </editor-fold> 
    
    private void changeTopOnRowSelected(){
        plusEpisodeButton.setEnabled(true);
        minusEpisodeButton.setEnabled(true);
        int rowselected = tables[selectedtable].getSelectedRow();
        animeID=0;
        statusBox.setSelectedIndex(selectedtable);
        for (int i = 0; i<=7;i++){
            String value = tables[selectedtable].getValueAt(rowselected, i).toString();
            switch (i){
                case 0:
                    nameLabel.setText(value);
                    break;
                case 2:
                    String[] split = value.split("/");
                    watchedNumberTextField.setText(split[0]);
                    outofNumberLabel.setText(split[1]);
                    boolean b =false;
                    if (split[1].indexOf("?")>-1) b=true;
                    int int0 =Integer.parseInt(split[0]);
                    int int1 = -1;
                    if (!b) int1 =Integer.parseInt(split[1]);
                    int setmax =12;
                    if (b){ 
                        if (int0>12) setmax=int0+12;
                        progressBar.setMaximum(setmax);
                    }
                    else progressBar.setMaximum(int1);
                    if (int0==int1) plusEpisodeButton.setEnabled(false);
                    else if (int0 ==0) minusEpisodeButton.setEnabled(false);
                    progressBar.setValue(int0);
                    break;
                case 3:
                    int skor;
                    if (value.indexOf(" ")>-1) skor=0;
                    else  skor = Integer.parseInt(value);
                    scoreBox.setSelectedIndex(10-skor);
                    break;
                case 6:
                    String link = "<html><img src=" + '"' +value+'"'+ "height=" +'"'+ "155" + '"'+ "width="+ '"' + "111" +'"'+ " />"; 
                    pictureLabel.setText(link);     
                    break;
                case 7:
                    animeID = Integer.parseInt(value);
                    break;
                default: break;
            }
        } 
        for (int i =0; i<5; i++){
            if (selectedtable!=i) tables[i].getSelectionModel().removeSelectionInterval(0,aniNum[i]-1); 
        }
    
    }
    

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        updatesMI = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        tabsMenu = new javax.swing.JMenu();
        aniemListMI = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mangaListMI = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        nowPlayingMI = new javax.swing.JMenuItem();
        mainPanel = new javax.swing.JPanel();
        updateButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        watchingTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        onholdTable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        ptwTable = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        compleatedTable = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        droppedTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        nameLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        watchedNumberTextField = new javax.swing.JTextField();
        plusEpisodeButton = new javax.swing.JButton();
        slashLabel = new javax.swing.JLabel();
        scoreWordLabel = new javax.swing.JLabel();
        scoreBox = new javax.swing.JComboBox();
        minusEpisodeButton = new javax.swing.JButton();
        outofNumberLabel = new javax.swing.JLabel();
        statusBox = new javax.swing.JComboBox();
        statusWordLabel = new javax.swing.JLabel();
        episodeWordLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        pictureLabel = new javax.swing.JLabel();

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(maluu.MALUUApp.class).getContext().getResourceMap(MALUUView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(maluu.MALUUApp.class).getContext().getActionMap(MALUUView.class, this);
        aboutMenuItem.setAction(actionMap.get("showPreferencesGeneral")); // NOI18N
        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COMMA, java.awt.event.InputEvent.META_MASK));
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        fileMenu.add(aboutMenuItem);

        updatesMI.setAction(actionMap.get("showPreferencesLogin")); // NOI18N
        updatesMI.setText(resourceMap.getString("updatesMI.text")); // NOI18N
        updatesMI.setName("updatesMI"); // NOI18N
        fileMenu.add(updatesMI);

        jSeparator5.setName("jSeparator5"); // NOI18N
        fileMenu.add(jSeparator5);

        jMenuItem1.setAction(actionMap.get("showPreferencesPlayers")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        fileMenu.add(jMenuItem1);

        jMenuItem2.setAction(actionMap.get("showPreferencesFolders")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        fileMenu.add(jMenuItem2);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        jMenuItem3.setAction(actionMap.get("showPreferencesTwitter")); // NOI18N
        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        fileMenu.add(jMenuItem3);

        menuBar.add(fileMenu);

        tabsMenu.setText(resourceMap.getString("tabsMenu.text")); // NOI18N
        tabsMenu.setName("tabsMenu"); // NOI18N

        aniemListMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.META_MASK));
        aniemListMI.setText(resourceMap.getString("aniemListMI.text")); // NOI18N
        aniemListMI.setName("aniemListMI"); // NOI18N
        tabsMenu.add(aniemListMI);

        jSeparator3.setName("jSeparator3"); // NOI18N
        tabsMenu.add(jSeparator3);

        mangaListMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.META_MASK));
        mangaListMI.setText(resourceMap.getString("mangaListMI.text")); // NOI18N
        mangaListMI.setName("mangaListMI"); // NOI18N
        tabsMenu.add(mangaListMI);

        jSeparator4.setName("jSeparator4"); // NOI18N
        tabsMenu.add(jSeparator4);

        nowPlayingMI.setText(resourceMap.getString("nowPlayingMI.text")); // NOI18N
        nowPlayingMI.setName("nowPlayingMI"); // NOI18N
        tabsMenu.add(nowPlayingMI);

        menuBar.add(tabsMenu);

        mainPanel.setMinimumSize(new java.awt.Dimension(450, 400));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        updateButton.setAction(actionMap.get("update")); // NOI18N
        updateButton.setText(resourceMap.getString("updateButton.text")); // NOI18N
        updateButton.setName("updateButton"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(0, 0));

        jScrollPane2.setAutoscrolls(true);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        watchingTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Episode", "Watched", "Score", "Type", "Season", "link", "id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        watchingTable.setAutoCreateRowSorter(true);
        watchingTable.setAutoscrolls(false);
        watchingTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        watchingTable.setName("watchingTable"); // NOI18N
        watchingTable.setShowGrid(false);
        watchingTable.getTableHeader().setReorderingAllowed(false);
        watchingTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                watchingTableMouseReleased(evt);
            }
        });
        watchingTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                watchingTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(watchingTable);
        watchingTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        watchingTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("watchingTable.columnModel.title0")); // NOI18N
        watchingTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        watchingTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("watchingTable.columnModel.title1")); // NOI18N
        watchingTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        watchingTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("watchingTable.columnModel.title2")); // NOI18N
        watchingTable.getColumnModel().getColumn(3).setPreferredWidth(25);
        watchingTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("watchingTable.columnModel.title3")); // NOI18N
        watchingTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        watchingTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("animeTable.columnModel.title4")); // NOI18N
        watchingTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        watchingTable.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("animeTable.columnModel.title5")); // NOI18N
        watchingTable.getColumnModel().getColumn(6).setResizable(false);
        watchingTable.getColumnModel().getColumn(6).setPreferredWidth(0);
        watchingTable.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("watchingTable.columnModel.title6")); // NOI18N
        watchingTable.getColumnModel().getColumn(7).setResizable(false);
        watchingTable.getColumnModel().getColumn(7).setPreferredWidth(0);
        watchingTable.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("watchingTable.columnModel.title7")); // NOI18N
        watchingTable.getAccessibleContext().setAccessibleName(resourceMap.getString("watchingTable.AccessibleContext.accessibleName")); // NOI18N

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        jScrollPane3.setAutoscrolls(true);
        jScrollPane3.setName("jScrollPane3"); // NOI18N

        onholdTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Episode", "Watched", "Score", "Type", "Season", "link", "id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        onholdTable.setAutoCreateRowSorter(true);
        onholdTable.setName("onholdTable"); // NOI18N
        onholdTable.setShowHorizontalLines(false);
        onholdTable.setShowVerticalLines(false);
        onholdTable.getTableHeader().setReorderingAllowed(false);
        onholdTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onholdTableMouseReleased(evt);
            }
        });
        onholdTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                onholdTableKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(onholdTable);
        onholdTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        onholdTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("animeTable.columnModel.title0")); // NOI18N
        onholdTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        onholdTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("animeTable.columnModel.title1")); // NOI18N
        onholdTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        onholdTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("animeTable.columnModel.title2")); // NOI18N
        onholdTable.getColumnModel().getColumn(3).setPreferredWidth(25);
        onholdTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("animeTable.columnModel.title3")); // NOI18N
        onholdTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        onholdTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("animeTable.columnModel.title4")); // NOI18N
        onholdTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        onholdTable.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("animeTable.columnModel.title5")); // NOI18N
        onholdTable.getColumnModel().getColumn(6).setResizable(false);
        onholdTable.getColumnModel().getColumn(6).setPreferredWidth(0);
        onholdTable.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("onholdTable.columnModel.title6")); // NOI18N
        onholdTable.getColumnModel().getColumn(7).setResizable(false);
        onholdTable.getColumnModel().getColumn(7).setPreferredWidth(0);
        onholdTable.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("onholdTable.columnModel.title7")); // NOI18N

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane3.TabConstraints.tabTitle"), jScrollPane3); // NOI18N

        jScrollPane4.setAutoscrolls(true);
        jScrollPane4.setName("jScrollPane4"); // NOI18N

        ptwTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Episode", "Watched", "Score", "Type", "Season", "link", "id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ptwTable.setAutoCreateRowSorter(true);
        ptwTable.setName("ptwTable"); // NOI18N
        ptwTable.setShowHorizontalLines(false);
        ptwTable.setShowVerticalLines(false);
        ptwTable.getTableHeader().setReorderingAllowed(false);
        ptwTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ptwTableMouseReleased(evt);
            }
        });
        ptwTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ptwTableKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(ptwTable);
        ptwTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        ptwTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("animeTable.columnModel.title0")); // NOI18N
        ptwTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        ptwTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("animeTable.columnModel.title1")); // NOI18N
        ptwTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        ptwTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("animeTable.columnModel.title2")); // NOI18N
        ptwTable.getColumnModel().getColumn(3).setPreferredWidth(25);
        ptwTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("animeTable.columnModel.title3")); // NOI18N
        ptwTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        ptwTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("animeTable.columnModel.title4")); // NOI18N
        ptwTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        ptwTable.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("animeTable.columnModel.title5")); // NOI18N
        ptwTable.getColumnModel().getColumn(6).setPreferredWidth(0);
        ptwTable.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("ptwTable.columnModel.title6")); // NOI18N
        ptwTable.getColumnModel().getColumn(7).setResizable(false);
        ptwTable.getColumnModel().getColumn(7).setPreferredWidth(0);
        ptwTable.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("ptwTable.columnModel.title7")); // NOI18N

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane4.TabConstraints.tabTitle"), jScrollPane4); // NOI18N

        jScrollPane5.setAutoscrolls(true);
        jScrollPane5.setName("jScrollPane5"); // NOI18N

        compleatedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Episode", "Watched", "Score", "Type", "Season", "link", "id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        compleatedTable.setAutoCreateRowSorter(true);
        compleatedTable.setName("compleatedTable"); // NOI18N
        compleatedTable.setShowHorizontalLines(false);
        compleatedTable.setShowVerticalLines(false);
        compleatedTable.getTableHeader().setReorderingAllowed(false);
        compleatedTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                compleatedTableMouseReleased(evt);
            }
        });
        compleatedTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                compleatedTableKeyReleased(evt);
            }
        });
        jScrollPane5.setViewportView(compleatedTable);
        compleatedTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        compleatedTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("animeTable.columnModel.title0")); // NOI18N
        compleatedTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        compleatedTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("animeTable.columnModel.title1")); // NOI18N
        compleatedTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        compleatedTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("animeTable.columnModel.title2")); // NOI18N
        compleatedTable.getColumnModel().getColumn(3).setPreferredWidth(25);
        compleatedTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("animeTable.columnModel.title3")); // NOI18N
        compleatedTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        compleatedTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("animeTable.columnModel.title4")); // NOI18N
        compleatedTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        compleatedTable.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("animeTable.columnModel.title5")); // NOI18N
        compleatedTable.getColumnModel().getColumn(6).setResizable(false);
        compleatedTable.getColumnModel().getColumn(6).setPreferredWidth(0);
        compleatedTable.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("compleatedTable.columnModel.title6")); // NOI18N
        compleatedTable.getColumnModel().getColumn(7).setResizable(false);
        compleatedTable.getColumnModel().getColumn(7).setPreferredWidth(0);
        compleatedTable.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("compleatedTable.columnModel.title7")); // NOI18N

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane5.TabConstraints.tabTitle"), jScrollPane5); // NOI18N

        jScrollPane6.setAutoscrolls(true);
        jScrollPane6.setName("jScrollPane6"); // NOI18N

        droppedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Episode", "Watched", "Score", "Type", "Season", "link", "id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        droppedTable.setAutoCreateRowSorter(true);
        droppedTable.setName("droppedTable"); // NOI18N
        droppedTable.setShowHorizontalLines(false);
        droppedTable.setShowVerticalLines(false);
        droppedTable.getTableHeader().setReorderingAllowed(false);
        droppedTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                droppedTableMouseReleased(evt);
            }
        });
        droppedTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                droppedTableKeyReleased(evt);
            }
        });
        jScrollPane6.setViewportView(droppedTable);
        droppedTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        droppedTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("animeTable.columnModel.title0")); // NOI18N
        droppedTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        droppedTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("animeTable.columnModel.title1")); // NOI18N
        droppedTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        droppedTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("animeTable.columnModel.title2")); // NOI18N
        droppedTable.getColumnModel().getColumn(3).setPreferredWidth(25);
        droppedTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("animeTable.columnModel.title3")); // NOI18N
        droppedTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        droppedTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("animeTable.columnModel.title4")); // NOI18N
        droppedTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        droppedTable.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("animeTable.columnModel.title5")); // NOI18N
        droppedTable.getColumnModel().getColumn(6).setResizable(false);
        droppedTable.getColumnModel().getColumn(6).setPreferredWidth(0);
        droppedTable.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("droppedTable.columnModel.title6")); // NOI18N
        droppedTable.getColumnModel().getColumn(7).setResizable(false);
        droppedTable.getColumnModel().getColumn(7).setPreferredWidth(0);
        droppedTable.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("droppedTable.columnModel.title7")); // NOI18N

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane6.TabConstraints.tabTitle"), jScrollPane6); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        nameLabel.setFont(resourceMap.getFont("nameLabel.font")); // NOI18N
        nameLabel.setText(resourceMap.getString("nameLabel.text")); // NOI18N
        nameLabel.setName("nameLabel"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
            .add(nameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(nameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        watchedNumberTextField.setText(resourceMap.getString("watchedNumberTextField.text")); // NOI18N
        watchedNumberTextField.setAction(actionMap.get("changeEp")); // NOI18N
        watchedNumberTextField.setName("watchedNumberTextField"); // NOI18N
        jPanel2.add(watchedNumberTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 40, 20));

        plusEpisodeButton.setAction(actionMap.get("plusEpisode")); // NOI18N
        plusEpisodeButton.setFont(resourceMap.getFont("plusEpisodeButton.font")); // NOI18N
        plusEpisodeButton.setText(resourceMap.getString("plusEpisodeButton.text")); // NOI18N
        plusEpisodeButton.setFocusable(false);
        plusEpisodeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        plusEpisodeButton.setName("plusEpisodeButton"); // NOI18N
        plusEpisodeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(plusEpisodeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 5, 40, 30));

        slashLabel.setFont(resourceMap.getFont("slashLabel.font")); // NOI18N
        slashLabel.setText(resourceMap.getString("slashLabel.text")); // NOI18N
        slashLabel.setName("slashLabel"); // NOI18N
        jPanel2.add(slashLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 10, 20));

        scoreWordLabel.setFont(resourceMap.getFont("scoreWordLabel.font")); // NOI18N
        scoreWordLabel.setText(resourceMap.getString("scoreWordLabel.text")); // NOI18N
        scoreWordLabel.setName("scoreWordLabel"); // NOI18N
        jPanel2.add(scoreWordLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, -1, 20));

        scoreBox.setMaximumRowCount(11);
        scoreBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10", "9", "8", "7", "6", "5", "4", "3", "2", "1", " " }));
        scoreBox.setAction(actionMap.get("changeSc")); // NOI18N
        scoreBox.setName("scoreBox"); // NOI18N
        jPanel2.add(scoreBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 70, -1));

        minusEpisodeButton.setAction(actionMap.get("minusEpisode")); // NOI18N
        minusEpisodeButton.setFont(resourceMap.getFont("minusEpisodeButton.font")); // NOI18N
        minusEpisodeButton.setText(resourceMap.getString("minusEpisodeButton.text")); // NOI18N
        minusEpisodeButton.setFocusable(false);
        minusEpisodeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        minusEpisodeButton.setName("minusEpisodeButton"); // NOI18N
        minusEpisodeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(minusEpisodeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 5, 40, 30));

        outofNumberLabel.setText(resourceMap.getString("outofNumberLabel.text")); // NOI18N
        outofNumberLabel.setName("outofNumberLabel"); // NOI18N
        jPanel2.add(outofNumberLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, -1, 20));

        statusBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Watching", "On-Hold", "Plan to Watch", "Completed", "Dropped" }));
        statusBox.setAction(actionMap.get("changeSt")); // NOI18N
        statusBox.setName("statusBox"); // NOI18N
        jPanel2.add(statusBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 130, 30));

        statusWordLabel.setFont(resourceMap.getFont("statusWordLabel.font")); // NOI18N
        statusWordLabel.setText(resourceMap.getString("statusWordLabel.text")); // NOI18N
        statusWordLabel.setName("statusWordLabel"); // NOI18N
        jPanel2.add(statusWordLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1, 30));

        episodeWordLabel.setFont(resourceMap.getFont("episodeWordLabel.font")); // NOI18N
        episodeWordLabel.setText(resourceMap.getString("episodeWordLabel.text")); // NOI18N
        episodeWordLabel.setName("episodeWordLabel"); // NOI18N
        jPanel2.add(episodeWordLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, 20));

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pictureLabel.setText(resourceMap.getString("pictureLabel.text")); // NOI18N
        pictureLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pictureLabel.setMinimumSize(new java.awt.Dimension(130, 202));
        pictureLabel.setName("pictureLabel"); // NOI18N
        pictureLabel.setPreferredSize(new java.awt.Dimension(130, 202));
        jPanel3.add(pictureLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 111, 155));
        pictureLabel.getAccessibleContext().setAccessibleName(resourceMap.getString("pictureLabel.AccessibleContext.accessibleName")); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(15, 15, 15)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 94, Short.MAX_VALUE)
                        .add(updateButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(updateButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 162, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void watchingTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_watchingTableMouseReleased
        selectedtable = 0;
        changeTopOnRowSelected();
    }//GEN-LAST:event_watchingTableMouseReleased

    private void onholdTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onholdTableMouseReleased
        selectedtable = 1;
        changeTopOnRowSelected();  
    }//GEN-LAST:event_onholdTableMouseReleased

    private void ptwTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ptwTableMouseReleased
        selectedtable = 2;
        changeTopOnRowSelected(); 
    }//GEN-LAST:event_ptwTableMouseReleased

    private void compleatedTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compleatedTableMouseReleased
        selectedtable = 3;
        changeTopOnRowSelected();
    }//GEN-LAST:event_compleatedTableMouseReleased

    private void droppedTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_droppedTableMouseReleased
        selectedtable = 4;
        changeTopOnRowSelected();
    }//GEN-LAST:event_droppedTableMouseReleased

    private void watchingTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_watchingTableKeyReleased
        selectedtable = 0;
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP || evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) changeTopOnRowSelected();
    }//GEN-LAST:event_watchingTableKeyReleased

    private void onholdTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_onholdTableKeyReleased
        selectedtable = 1;
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP || evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) changeTopOnRowSelected();    
    }//GEN-LAST:event_onholdTableKeyReleased

    private void ptwTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ptwTableKeyReleased
        selectedtable = 2;
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP || evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) changeTopOnRowSelected();
    }//GEN-LAST:event_ptwTableKeyReleased

    private void compleatedTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_compleatedTableKeyReleased
        selectedtable = 3;
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP || evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) changeTopOnRowSelected();
    }//GEN-LAST:event_compleatedTableKeyReleased

    private void droppedTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_droppedTableKeyReleased
        selectedtable = 4;
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP || evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) changeTopOnRowSelected();
    }//GEN-LAST:event_droppedTableKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aniemListMI;
    private javax.swing.JTable compleatedTable;
    private javax.swing.JTable droppedTable;
    private javax.swing.JLabel episodeWordLabel;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuItem mangaListMI;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton minusEpisodeButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JMenuItem nowPlayingMI;
    private javax.swing.JTable onholdTable;
    private javax.swing.JLabel outofNumberLabel;
    private javax.swing.JLabel pictureLabel;
    private javax.swing.JButton plusEpisodeButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTable ptwTable;
    private javax.swing.JComboBox scoreBox;
    private javax.swing.JLabel scoreWordLabel;
    private javax.swing.JLabel slashLabel;
    private javax.swing.JComboBox statusBox;
    private javax.swing.JLabel statusWordLabel;
    private javax.swing.JMenu tabsMenu;
    private javax.swing.JButton updateButton;
    private javax.swing.JMenuItem updatesMI;
    private javax.swing.JTextField watchedNumberTextField;
    private javax.swing.JTable watchingTable;
    // End of variables declaration//GEN-END:variables


    private JDialog preferencesBox;
}

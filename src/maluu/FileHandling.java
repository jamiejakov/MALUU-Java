/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maluu;

import java.io.*;

/**
 *
 * @author jamiejakov
 */
public class FileHandling {
    private final int MALUU_PREF_LINES_NUM = 7;
    private final String LOCATION = System.getProperty("user.home") + "/.maluuPref.txt";
    public FileHandling(){
        
    
    }
    
    
    private String readFile(int line){
        String tmp = null;
        File f= new File(LOCATION);
        if (!f.exists()){
            try {f.createNewFile();}
            catch(Exception a){};
            writeToFile("", -1);
        }
	try{
            
            FileInputStream fstream = new FileInputStream(LOCATION);
            DataInputStream in = new DataInputStream(fstream);
            
     // data reading from file       

            String animeID = in.readLine();
            String malUname = in.readLine();
            String malPass = in.readLine();
            String tableLayout = in.readLine();
            // player
            // twitter login
            // twitter pass
            
            in.close();
            switch (line){
                case 1: return animeID;
                case 2: return malUname;
                case 3: return malPass;
                case 4: return tableLayout;
            }
        }catch(Exception a){}
        return "";
    }
    
    public void writeToFile(String text, int line){
        FileOutputStream out; // declare a file output object
        PrintStream p; // declare a print stream object
        
        FileInputStream fstream;
        DataInputStream in;
        String tmp ="";
        try{
            fstream = new FileInputStream(LOCATION);
            in = new DataInputStream(fstream);
            String[] fileOld = new String[7];
            for (int j=0; j< 7; j++){
                fileOld[j] = in.readLine();
                //System.out.println(fileOld[j]);
            }
            
            
            out = new FileOutputStream(LOCATION);
            p = new PrintStream( out );
            
            if (line>=0){
                int i=0;
                for (i=0;i<=MALUU_PREF_LINES_NUM; i++){
                    if (i==line) p.println(text);
                    else p.println(fileOld[i]);  
                }
                
            }else{

                p.println ("0");
                p.println ("tmp1");
                p.println ("tmp2");
                p.println ("111111");
                p.println ("tmp3");
                p.println ("tmp4");
                p.println ("tmp5");
            }
            out.close();
            p.close();
            p = null;
            out =null;
            in.close();
            fstream.close();
        }catch(Exception e){
            
            
        }
        
    }
    
    
    public int getAnimeID(){
        return Integer.parseInt(readFile(1)); 
    }
    public String getMalUname(){
        return readFile(2); 
    }
    public String getMalPass(){
        return readFile(3); 
    }
    public int getTableLayout(){
        return Integer.parseInt(readFile(4)); 
    }
}

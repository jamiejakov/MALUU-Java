/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maluu;

import java.awt.Component;
import javax.swing.BorderFactory; 
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
 
public class ProgressCellRenderer extends JProgressBar
                        implements TableCellRenderer {
 
  /**
   * Creates a JProgressBar with the range 0,100.
   */
  public ProgressCellRenderer(){
    super(0, 100);
    setValue(0);
    setBorderPainted(false);
    //setString("");
    //setStringPainted(true);
    //setOpaque(true);
    //setSize(5,10);
    setBorder(BorderFactory.createEmptyBorder(0,0,0,2));
  }
 
  public Component getTableCellRendererComponent(
                                    JTable table,
                                    Object value,
                                    boolean isSelected,
                                    boolean hasFocus,
                                    int row,
                                    int column) {
 
    //value is a percentage e.g. 95%
    if (value!=null){
        final String sValue = value.toString();
        int index = sValue.indexOf('%');
        if (index != -1) {
        int p = 0;
        try{
            p = Integer.parseInt(sValue.substring(0, index));
        }
        catch(NumberFormatException e){
        }
        setValue(p);

      //setString(sValue);
        }
    }

    return this;
  }
}
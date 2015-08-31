/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maluu;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
/**
 *
 * @author jamiejakov
 */
class ColumnSorter implements Comparator {
  int colIndex;

  ColumnSorter(int colIndex) {
    this.colIndex = colIndex;
  }

  public int compare(Object a, Object b) {
    Vector v1 = (Vector) a;
    Vector v2 = (Vector) b;
    Object o1 = v1.get(colIndex);
    Object o2 = v2.get(colIndex);
    
    if (colIndex==3) ;
    if (o1.getClass().isAssignableFrom(Integer.class));
    
    
    if (o1 instanceof String && ((String) o1).length() == 0) {
      o1 = null;
    }
    if (o2 instanceof String && ((String) o2).length() == 0) {
      o2 = null;
    }

    if (o1 == null && o2 == null) {
      return 0;
    } else if (o1 == null) {
      return 1;
    } else if (o2 == null) {
      return -1;
    } else if (o1 instanceof Comparable) {

      return ((Comparable) o1).compareTo(o2);
    } else {

      return o1.toString().compareTo(o2.toString());
    }
  }
}
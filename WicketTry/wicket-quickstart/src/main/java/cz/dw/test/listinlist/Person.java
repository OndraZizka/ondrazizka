package cz.dw.test.listinlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ondrej Zizka
 */
public class Person implements Cloneable, Serializable {

  // Static to keep the example simple. Would come from a DB in real app.
  private static final List persons = new Vector();
  public static List getPersons() {    return persons;  }



  private Long id;
  
  private String jmeno;
  public String getJmeno() {    return jmeno;  }
  public void setJmeno( String jmeno ) {    this.jmeno = jmeno;  }

  private String prijmeni;
  public String getPrijmeni() {    return prijmeni;  }
  public void setPrijmeni( String prijmeni ) {    this.prijmeni = prijmeni;  }

  private Person managedBy;
  
  private List<Person> children = new ArrayList();
  public List<Person> getChildren() {    return /*children*/ new ArrayList<Person>(){{ add(new Person(5L, "Childovic")); }}; }
  public void setChildren( List<Person> children ) {    this.children = children;  }

  

  public Person( Long id, String name ) {
    this.id = id;
    this.jmeno = name;
  }

  public Person() {
  }


  


  /**
   * Used to display the person.
   */
  public String toString() {
    return jmeno + " " + prijmeni;
  }


  /**
   * Gets the list of possible managers from the database.
   */
  public static List<Person> getManagers() {
    List man = new ArrayList();
    man.add( new Person( 1L, "Adam" ) );
    man.add( new Person( 2L, "Beata" ) );
    return man;
  }


  @Override public Object clone() {
    Object o = null;
    try {
      o = super.clone();
    }
    catch( CloneNotSupportedException ex ) {
    }
    return o;
  }

}
// class Person


package cz.dw.test.ddmenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ondøej Žižka
 */
public class Person implements Cloneable, Serializable {

  private static final List persons = new Vector();
  public static List getPersons() {    return persons;  }



  private Long id;
  private String jmeno;
  private String prijmeni;
  private Person managedBy;

  public Person( Long id, String name ) {
    this.id = id;
    this.jmeno = name;
  }

  public Person() {
  }

  public String getJmeno() {    return jmeno;  }
  public void setJmeno( String jmeno ) {    this.jmeno = jmeno;  }

  public String getPrijmeni() {    return prijmeni;  }
  public void setPrijmeni( String prijmeni ) {    this.prijmeni = prijmeni;  }
  
  


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



package cz.dw.wicket.stack.ent;


import java.io.Serializable;
import java.util.*;
import java.util.logging.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 *
 * @author Ondrej Zizka
 */
@Entity
public class TestEntity implements Serializable
{
  private static final long serialVersionUID = 1L;


  // ID
  @Id @GeneratedValue( strategy = GenerationType.AUTO )
  private Long id;
  public Long getId() {    return id;  }
  public void setId( Long id ) {    this.id = id;  }


  // Value
  protected String value;
  public String getValue() {    return value;  }
  public void setValue( String value ) {    this.value = value;  }



  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }


  @Override
  public boolean equals( Object object ) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if( !(object instanceof TestEntity) ){
      return false;
    }
    TestEntity other = (TestEntity) object;
    if( (this.id == null && other.id != null) || (this.id != null && !this.id.equals( other.id )) ){
      return false;
    }
    return true;
  }


  @Override
  public String toString() {
    return "TestEntity[id=" + id + "]";
  }

}// class TestEntity

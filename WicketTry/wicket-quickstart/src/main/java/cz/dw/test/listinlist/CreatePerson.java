package cz.dw.test.listinlist;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author Ondrej Zizka
 */
public class CreatePerson extends WebPage {

  public CreatePerson() {
    add( new PersonForm("create_person") );
  }





  public class PersonForm extends org.apache.wicket.markup.html.form.Form {

    protected Person person = new Person();

    public Person getPerson() {      return person;    }
    public void setPerson( Person person ) {      this.person = person;    }

    public PersonForm( String id ) {
      super( id );
      add( new TextField( "jmeno", new PropertyModel( this.person, "jmeno" ) ) );
      add( new TextField( "prijmeni", new PropertyModel( this.person, "prijmeni" ) ) );
    }


    public final void onSubmit() {
      //final Person newPerson = new Person();
      //newPerson.setJmeno( this.person.getJmeno() );
      final Person newPerson;
      newPerson = (Person) this.person.clone();

      Person.getPersons().add( 0, newPerson );
      //commentListView.modelChanged();

      this.person.setJmeno("A");
      this.person.setPrijmeni("B");
    }


  }
}// class CreatePerson


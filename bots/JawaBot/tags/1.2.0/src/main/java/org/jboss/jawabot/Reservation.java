package org.jboss.jawabot;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.*;


/**
 *
 * @author Ondrej Zizka
 */
//@Entity
//@XmlAccessorType(XmlAccessType.FIELD)
public class Reservation implements Comparable<Reservation>, Serializable  {


	/** Entity ID. */
   @XmlTransient
	@Id private Long id;
	public Long getId() {		return id;	}
	public void setId(Long id) {		this.id = id;	}


	/** Which user is this reservation for. */
	public String getForUser() {		return forUser;	}
   @XmlAttribute
	private String forUser;

	/** First day of the reservation. */
	@Temporal(javax.persistence.TemporalType.DATE)
	@Column(name="fromDate")
   public Date getFrom() {      return from;   }
   @XmlAttribute
  	private Date from;



	/** Last day of the reservation. */
	@Temporal(javax.persistence.TemporalType.DATE)
	@Column(name="toDate")
	public Date getTo() {		return to;	}
   @XmlAttribute
	private Date to;


	public Reservation(String forUser, Date from, Date to) {
      if( from.after(to) )
         throw new IllegalArgumentException("End date precedes start date.");
		this.forUser = forUser;
		this.from = from;
		this.to = to;
	}

	/** For JPA */
	public Reservation() {	}


   

	/**
	 * Does the other reservation collide with this?
	 * @returns true if the other reservation collides, which means that they overlap somehow.
	 */
	public boolean collidesWith( Reservation other ){

		// This starts after other ends.
		if( this.from.after( other.to ))
			return false;

		// This ends before other starts.
		if( this.to.before( other.from ))
			return false;
		
		// All other cases overlap.
		return true;

	}


	/**
	 * Does given date belong into this period?
	 * @returns true if it does.
	 */
	public boolean contains( Date date ){
		// This starts after other ends.
		if( this.from.after( date ))
			return false;

		// This ends before other starts.
		if( this.to.before( date ))
			return false;

		// All other cases overlap.
		return true;
	}

   
   @Override
   public int compareTo( Reservation reservation ) {
		if( this.from.after( reservation.to ))
			return 1;
		if( this.to.before( reservation.from ))
			return -1;
		return 0; // Overlaps
   }

}// class 




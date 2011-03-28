package org.jboss.jawabot;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author Ondrej Zizka
 */
@Entity
public class Reservation {


	/** Entity ID. */
	@Id private Long id;


	/** Which user is this reservation for. */
	public String getForUser() {		return forUser;	}
	private String forUser;

	/** First day of the reservation. */
	@Temporal(javax.persistence.TemporalType.DATE)
	@Column(name="fromDate")
	private Date from;
	public Date getFrom() {		return from;	}

	/** Last day of the reservation. */
	@Temporal(javax.persistence.TemporalType.DATE)
	@Column(name="toDate")
	private Date to;
	public Date getTo() {		return to;	}


	public Reservation(String forUser, Date from, Date to) {
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}// class 




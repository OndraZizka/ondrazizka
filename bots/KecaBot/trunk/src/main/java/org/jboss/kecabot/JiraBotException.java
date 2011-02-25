package org.jboss.kecabot;


/**
 *
 * @author Ondrej Zizka
 */
public class JiraBotException extends Exception {

	public JiraBotException(Throwable cause) {
		super(cause);
	}

	public JiraBotException(String message, Throwable cause) {
		super(message, cause);
	}

	public JiraBotException(String message) {
		super(message);
	}

}// class

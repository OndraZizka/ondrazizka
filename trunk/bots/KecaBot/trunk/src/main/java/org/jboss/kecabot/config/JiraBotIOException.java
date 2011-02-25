
package org.jboss.kecabot.config;

import org.jboss.kecabot.JiraBotException;

/**
 *
 * @author Ondrej Zizka
 */
public class JiraBotIOException extends JiraBotException {

	public JiraBotIOException(String message) {
		super(message);
	}

	public JiraBotIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public JiraBotIOException(Throwable cause) {
		super(cause);
	}

	

}// class

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.jawabot.config;

import org.jboss.jawabot.JawaBotIOException;
import org.jboss.jawabot.JawaBot;

/**
 *
 * @author Ondrej Zizka
 */
public interface ConfigPersister {

   public JawaBot load() throws JawaBotIOException;

	public void save( JawaBot bot ) throws JawaBotIOException;


}// interface





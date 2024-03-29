
//package de.laliluna.transactions;
package org.jboss.weld.environment.se.jpa;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The @InterceptorBinding tells Weld that this annotation can be used with an interceptor.
 */

@InterceptorBinding
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JpaTransactional {
   
   public static enum Type {
      REQUIRED,
      REQUIRES_NEW
   }
   
   Type value() default Type.REQUIRED;

}// class


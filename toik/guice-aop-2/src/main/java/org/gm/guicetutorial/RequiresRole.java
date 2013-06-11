package org.gm.guicetutorial;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A markup annation used to signal, that a session bean wants to make use of
 * the Guice DI container.
 * 
 * @author Gunnar Morlng
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {

	String value() default "";

}
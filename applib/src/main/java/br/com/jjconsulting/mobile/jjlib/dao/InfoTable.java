package br.com.jjconsulting.mobile.jjlib.dao;

/**
 * Created by jjconsulting on 27/02/2018.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //on class level
public @interface InfoTable {

    String tableName() default "";
}
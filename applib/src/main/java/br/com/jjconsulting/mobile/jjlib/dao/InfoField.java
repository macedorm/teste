package br.com.jjconsulting.mobile.jjlib.dao;

/**
 * Created by jjconsulting on 27/02/2018.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InfoField {
    boolean isPK() default false;

    boolean isNull() default true;

    String fieldName() default "";

    TypeDbInfo type() default TypeDbInfo.TEXT;
}

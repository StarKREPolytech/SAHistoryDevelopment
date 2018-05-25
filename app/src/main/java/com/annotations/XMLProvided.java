package com.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by StarKRE on 13.05.2018.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface XMLProvided {

    String layout();
}

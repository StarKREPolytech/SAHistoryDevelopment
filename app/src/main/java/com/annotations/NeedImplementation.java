package com.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * @author Игорь Гулькин 28.04.2018.
 *
 * Аннотация NeedImplementation указывает разработчику,
 * что этот метод необходимо реализовать, и вместо него
 * может существовать другой иммитированный-реализованный
 * метод, на который указывает значение поля value().
 */

@Temporary
@Retention(RetentionPolicy.SOURCE)
@Target(METHOD)
public @interface NeedImplementation {

    /**
     * value()
     *
     * @return имя иммитированного метода,
     * по умолчанию пустая строка.
     */

    String value() default "";
}

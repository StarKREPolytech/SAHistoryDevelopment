package com.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Игорь Гулькин сделал не помню когда...
 *
 * Аннотация NotFinal явно указыват, что класс или метод может быть переопределен,
 * если в будущем необходимо расширить функциональность приложения.
 */

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NotFinal {
}
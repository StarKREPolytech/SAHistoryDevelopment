package com.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Игорь Гулькин 13.05.2018.
 *
 * Так как в проекте нет IoC && Dependency Injection Principle,
 * то над всеми классами, которые должны были быть "заинжекчены"
 * будут стоять эти аннотации.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface FuckingStaticSingleton {
}
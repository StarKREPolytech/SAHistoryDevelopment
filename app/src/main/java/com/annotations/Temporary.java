package com.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * @author Игорь Гулькин 28.04.2018.
 *
 * Аннотация Temporary указывает, что данный элемент временный,
 * и он создан в процессе разработки приложения. В большинстве случаев
 * поля и методы помеченные этой аннотацией иммитируют какой-либо
 * процесс или ситуацию. Как только будет создана реальная реализация
 * этого элемента, то необходимо удалить аннотированный метод.
 * Следовательно, как только будет готово приложение, удалите этот класс =)
 */

@Temporary
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE , METHOD, CONSTRUCTOR, ANNOTATION_TYPE, TYPE})
public @interface Temporary {
}

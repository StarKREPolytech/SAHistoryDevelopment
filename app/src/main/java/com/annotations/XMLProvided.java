package com.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Игорь Гулькин 13.05.2018.
 *
 * Методы, помеченные аннотацией XMLProvided, обрабатывают события в приложении.
 * Вместо того, чтобы писать view.setOn..Listener(...), на XML объекте прописываеся свойство,
 * например, android:onClick="methodName" со аргументом view: View.
 * Когда Вы будете использовать такой подход, другие разработчики поймут,
 * куда "проведен" этот метод.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface XMLProvided {

    /**
     * Необходимо указать путь к ресурсам.
     *
     * @return путь к layout.xml
     */

    String layout();
}
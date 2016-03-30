package org.popkit.core.annotation;

import org.popkit.core.enums.LeapProtocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-03-20:15:21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MobileSupport {

    LeapProtocol[] leapProtocol() default {LeapProtocol.JSON};

}

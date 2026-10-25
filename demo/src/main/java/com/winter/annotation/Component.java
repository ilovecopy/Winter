package com.winter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)//表示这个注解可以用在类上
@Retention(RetentionPolicy.RUNTIME)//运行期要用反射读它
public @interface Component {
    String value() default "";
}

package com.winter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)//表示这个注解可以用在字段上
@Retention(RetentionPolicy.RUNTIME)//运行期要用反射读它
public @interface Autowired {
    boolean required() default true;
}

package com.winter.annotation;

import com.winter.core.ClassScanner;
import com.winter.ioc.BeanContainer;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ClassTest {

    @Test
    void test() {
        Set<Class<?>> set = new ClassScanner().scan("com.winter");
        set.forEach(System.out::println);
        System.out.println("共 " + set.size() + " 个");
    }

    @Test
    void test2() {
        BeanContainer beanContainer = new BeanContainer();
        beanContainer.scan("com.winter.demo");
        beanContainer.refresh();
    }
}

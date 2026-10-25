package com.winter.annotation;

import com.winter.core.ClassScanner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

public class ClassTest {

    @Test
    void test() throws URISyntaxException, IOException, ClassNotFoundException {
        Set<Class<?>> set = new ClassScanner().scan("com.winter");
        set.forEach(System.out::println);
        System.out.println("共 " + set.size() + " 个");
    }
}

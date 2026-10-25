package com.winter.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter//被注册进容器就不该改
@AllArgsConstructor
public class BeanDefinition {
    private final String beanName;
    private final Class<?> beanClass;
}

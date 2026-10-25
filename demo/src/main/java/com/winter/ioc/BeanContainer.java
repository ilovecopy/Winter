package com.winter.ioc;

import com.winter.core.BeanDefinition;
import com.winter.core.ClassScanner;
import lombok.extern.slf4j.Slf4j;

import java.beans.Introspector;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BeanContainer {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();

    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();

    private final Set<String> singletonCurrentlyInCreation = ConcurrentHashMap.newKeySet();

    private final ClassScanner classScanner = new ClassScanner();


    public void scan(String basePackage) {
        Set<Class<?>> classes = classScanner.scan(basePackage);
        for (Class<?> clazz : classes) {
            if (clazz.isInterface()) {
                continue;
            }
            if (clazz.isAnnotationPresent(com.winter.annotation.Component.class)) {
                String beanName = resolveBeanName(clazz);
                BeanDefinition beanDefinition = new BeanDefinition(beanName, clazz);
                beanDefinitionMap.put(beanName, beanDefinition);
                log.info("[扫描]注册Bean: {} ——> {}", beanName, clazz.getSimpleName());
            }
        }
    }

    private String resolveBeanName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(com.winter.annotation.Component.class)) {
            com.winter.annotation.Component component = clazz.getAnnotation(com.winter.annotation.Component.class);
            String beanName = component.value();
            if (!beanName.isEmpty()) {
                return beanName;
            }
        }
        return Introspector.decapitalize(clazz.getSimpleName());
    }

    /**
     * 把所有的BeanDefinition实例化为单例对象，并放入一级缓存中
     */
    public void refresh() {
        log.info("[刷新]容器开始");
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            log.info("[实例化]Bean: {}", beanDefinition.getBeanName());
            getBean(beanDefinition.getBeanName());
        }
        log.info("[实例化]Bean: beanContainer");
        singletonObjects.put("beanContainer", this);
        log.info("[刷新]容器结束，单例数: {}", singletonObjects.size());
    }

    public Object getBean(String beanName) {
        if (singletonObjects.containsKey(beanName)) {
            return singletonObjects.get(beanName);
        }
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new RuntimeException("没有找到Bean: " + beanName);
        }
        try {
            return createBean(beanName);
        } catch (Exception e) {
            throw new RuntimeException("创建Bean失败: " + beanName, e);
        }
    }

    private Object createBean(String beanName) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new RuntimeException("没有找到Bean: " + beanName);
        }
        Object bean = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
        singletonObjects.put(beanName, bean);
        return bean;
    }
}

package com.winter.ioc;

import com.winter.core.BeanDefinition;
import com.winter.core.ClassScanner;
import lombok.extern.slf4j.Slf4j;

import java.beans.Introspector;
import java.lang.reflect.Field;
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
        populateBean(bean);
        singletonObjects.put(beanName, bean);
        return bean;
    }

    public Object getBean(Class<?> type) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            if (type.isAssignableFrom(entry.getValue().getBeanClass())) {
                return getBean(entry.getKey());
            }
        }
        throw new RuntimeException("没有找到Bean: " + type.getName());
    }

    private void populateBean(Object bean) {
        // 这里可以进行依赖注入等操作
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(com.winter.annotation.Autowired.class)) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                Object dependency = getBean(fieldType);
                try {
                    field.set(bean, dependency);
                    log.info("[注入]依赖: {} -> {}", field.getName(), dependency.getClass().getSimpleName());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("注入依赖失败: " + field.getName(), e);
                }
            }
        }
    }
}

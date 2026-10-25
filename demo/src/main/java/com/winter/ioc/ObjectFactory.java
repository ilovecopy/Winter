package com.winter.ioc;

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject();
}

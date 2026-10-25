package com.winter.demo;

import com.winter.annotation.Component;

@Component
public class UserServiceImpl implements UserService {
    @Override
    public String hello(String name) {
        return "hello";
    }
}

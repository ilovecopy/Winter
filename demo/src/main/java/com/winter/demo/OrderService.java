package com.winter.demo;

import com.winter.annotation.Autowired;
import com.winter.annotation.Component;
import lombok.Getter;

@Component
@Getter
public class OrderService {
    @Autowired
    private UserService userService;
}

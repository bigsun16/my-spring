package com.qihui.sun.service;

import com.qihui.sun.spring.AutoWire;
import com.qihui.sun.spring.Aware;
import com.qihui.sun.spring.InitializingBean;
import com.qihui.sun.spring.Service;

@Service
public class UserService implements Aware, InitializingBean {

    public UserService() {
        System.out.println("userService构造函数");
    }

//    @AutoWire
    private OrderService orderService;

    public void show() {
        System.out.println(orderService);
    }

    @Override
    public void doSomethingAfterCreateBean() {
        System.out.println("创建userService对象后做些事情");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("afterPropertiesSet.....");
    }
}

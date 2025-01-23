package com.qihui.sun.service;

import com.qihui.sun.spring.Scope;
import com.qihui.sun.spring.Service;

@Service
//@Scope("prototype")
public class OrderService implements IOrderService{
    public OrderService() {
        System.out.println("order service constructor......");
    }
    @Override
    public void show() {
        System.out.println("order service show......");
    }
}

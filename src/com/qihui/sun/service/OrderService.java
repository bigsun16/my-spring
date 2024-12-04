package com.qihui.sun.service;

import com.qihui.sun.spring.Scope;
import com.qihui.sun.spring.Service;

@Service
@Scope("prototype")
public class OrderService implements IOrderService{
    @Override
    public void show() {
        System.out.println("order service show......");
    }
}

package com.qihui.sun.service;

import com.qihui.sun.spring.ApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
        IOrderService bean = (IOrderService) applicationContext.getBean("orderService");
        bean.show();
        System.out.println(bean);
    }
}
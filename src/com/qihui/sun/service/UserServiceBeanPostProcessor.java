package com.qihui.sun.service;

import com.qihui.sun.spring.BeanPostProcessor;
import com.qihui.sun.spring.Service;

@Service
public class UserServiceBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessorBeforeInitialization(String beanName, Object bean) {
        if (bean instanceof UserService)
            System.out.println(beanName + "对象初始化之前做些事情");
        return bean;
    }

    @Override
    public Object postProcessorAfterInitialization(String beanName, Object bean) {
        if (bean instanceof UserService)
            System.out.println(beanName + "对象初始化之后做些事情");
        return bean;
    }
}

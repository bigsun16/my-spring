package com.qihui.sun.service;

import com.qihui.sun.spring.BeanPostProcessor;
import com.qihui.sun.spring.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Service
public class OrderServiceBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessorBeforeInitialization(String beanName, Object bean) {
        return bean;
    }

    @Override
    public Object postProcessorAfterInitialization(String beanName, Object bean) {
        if (bean instanceof OrderService) {
            return Proxy.newProxyInstance(this.getClass().getClassLoader(), OrderService.class.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("增强orderService方法调用");
                    return method.invoke(bean, args);
                }
            });
        }
        return bean;
    }
}

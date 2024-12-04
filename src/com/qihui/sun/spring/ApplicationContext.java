package com.qihui.sun.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> singletonMap = new ConcurrentHashMap<>();
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public ApplicationContext(Class<?> configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan annotation = configClass.getAnnotation(ComponentScan.class);
            // com.qihui.sun.service
            String scanPackage = annotation.value();
            //通过scanPackage获取类路径目录

            initDefinitionMap(scanPackage);
        }

        // 初始化单例池
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            if ("singleton".equals(beanDefinition.getScope()) && singletonMap.get(beanName) == null) {
                Object bean = createBean(beanName, beanDefinition);
                singletonMap.put(beanName, bean);
            }
        });
    }

    private void initDefinitionMap(String scanPackage) {
        // com/qihui/sun/service
        String scanPackagePath = scanPackage.replace(".", "/");
        ClassLoader classLoader = ApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(scanPackagePath);
        assert resource != null;
        File classDirectory = new File(resource.getFile());
        if (classDirectory.isDirectory()) {
            Arrays.stream(Objects.requireNonNull(classDirectory.listFiles()))
                    .forEach(file -> {
                        // D:\dev\code\my-spring\out\production\my-spring\com\qihui\sun\service\User.class
                        String absoluteFile = file.getAbsolutePath();
                        // com.qihui.sun.service.User
                        String className = absoluteFile.substring(absoluteFile.indexOf("com")).replace('\\', '.').replace(".class", "");
                        //根据类名加载类
                        try {
                            Class<?> aClass = classLoader.loadClass(className);
                            if (aClass.isAnnotationPresent(Service.class)) {
                                String beanName = aClass.getSimpleName();
//                                    将beanName首字母小写
//                                    beanName = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                                beanName = Introspector.decapitalize(beanName);
                                //收集beanPostProcessors
                                if (BeanPostProcessor.class.isAssignableFrom(aClass)) {
                                    BeanPostProcessor beanPostProcessor = (BeanPostProcessor) aClass.getDeclaredConstructor().newInstance();
                                    beanPostProcessors.add(beanPostProcessor);
                                    singletonMap.put(beanName, beanPostProcessor);
                                }
                                //创建BeanDefinition
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(aClass);
                                //判断类的单例还是多例
                                if (aClass.isAnnotationPresent(Scope.class)
                                        && "prototype".equals(aClass.getAnnotation(Scope.class).value())) {
                                    beanDefinition.setScope("prototype");
                                } else {
                                    beanDefinition.setScope("singleton");
                                }

                                beanDefinitionMap.put(beanName, beanDefinition);
                            }
                        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                                 IllegalAccessException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        try {
            Class<?> type = beanDefinition.getType();
            Object bean = type.getDeclaredConstructor().newInstance();

//            依赖注入
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(AutoWire.class)) {
                    field.setAccessible(true);
                    try {
                        field.set(bean, getBean(field.getName()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            //回调钩子函数
            if (bean instanceof Aware aware) {
                aware.doSomethingAfterCreateBean();
            }
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                bean = beanPostProcessor.postProcessorBeforeInitialization(beanName, bean);
            }
            // 初始化钩子函数
            if (bean instanceof InitializingBean bean1) {
                bean1.afterPropertiesSet();
            }
            //初始化后aop
            //动态代理对象
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                bean = beanPostProcessor.postProcessorAfterInitialization(beanName, bean);
            }

            return bean;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new RuntimeException("can not find a bean named '" + beanName + "'");
        } else {
            if ("singleton".equals(beanDefinition.getScope())) {
                return singletonMap.get(beanName);
            } else {
                return createBean(beanName,beanDefinition);
            }
        }
    }
}

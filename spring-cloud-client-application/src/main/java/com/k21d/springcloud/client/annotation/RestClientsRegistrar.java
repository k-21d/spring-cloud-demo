package com.k21d.springcloud.client.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class RestClientsRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
    private BeanFactory beanFactory;
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata,
                                        BeanDefinitionRegistry beanDefinitionRegistry) {
        ClassLoader classLoader = annotationMetadata.getClass().getClassLoader();
        Map<String, Object> attributes =
                annotationMetadata.getAnnotationAttributes(EnableRestClients.class.getName());
        Class<?>[] clients = (Class<?>[])attributes.get("clients");

        Stream.of(clients)
                .filter(Class::isInterface)
                .filter(interfaceClass->
                        findAnnotation(interfaceClass,RestClient.class)!=null)
                .forEach(restClientClass->{
                    RestClient restClient = findAnnotation(restClientClass, RestClient.class);
                    String serviceName = restClient.name();
                    //RestTemplate->serviceName/uri?param=?
                    //JDK动态代理
                    Object proxy = Proxy.newProxyInstance(classLoader, new Class[]{restClientClass},
                            new RequestMappingMethodInvocationHandler(serviceName, beanFactory));

                    //将@RestClient接口代理实现注册为Bean
                    String beanName = "RestClient."+serviceName;
                    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.
                            genericBeanDefinition(RestClientClassFactoryBean.class);
                    beanDefinitionBuilder.addConstructorArgValue(proxy);
                    beanDefinitionBuilder.addConstructorArgValue(restClientClass);
                    beanDefinitionRegistry.registerBeanDefinition(beanName,beanDefinitionBuilder.getBeanDefinition());
                });


    }
    private static class RestClientClassFactoryBean implements FactoryBean{
        private final Object proxy;
        private final Class<?> restClientClass;
        public RestClientClassFactoryBean( Object proxy, Class<?> restClientClass) {
            this.restClientClass = restClientClass;
            this.proxy = proxy;
        }

        @Override
        public Object getObject() throws Exception {
            return null;
        }

        @Override
        public Class<?> getObjectType() {
            return restClientClass;
        }
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}

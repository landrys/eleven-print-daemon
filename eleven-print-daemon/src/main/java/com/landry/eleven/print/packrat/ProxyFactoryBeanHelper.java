package com.landry.eleven.print.packrat;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;


public class ProxyFactoryBeanHelper {

	/*
    public static HessianProxyFactoryBean hessianProxyFactoryBean(int port) {
        HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
        factory.setServiceUrl(String.format("http://localhost:%s/httpIn", port));
        factory.setServiceInterface(FooService.class);
        return factory;
    }
    */

    public static HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean(int port) {
        HttpInvokerProxyFactoryBean factory = new HttpInvokerProxyFactoryBean();
        factory.setServiceUrl(String.format("http://localhost:%s/printJobHttpInvokerService", port));
        factory.setServiceInterface(PrintJobApi.class);
        return factory;
    }
}

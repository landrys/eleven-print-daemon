package com.landry.eleven.print.packrat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;


// Turn off for now...@Configuration
class HttpInvokerConfig {
	@Bean
	public HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean() {
		return ProxyFactoryBeanHelper.httpInvokerProxyFactoryBean(8010);
	}

}

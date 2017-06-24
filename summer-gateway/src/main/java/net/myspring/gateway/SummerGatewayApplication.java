package net.myspring.gateway;

import net.myspring.gateway.filter.CookieFilter;
import net.myspring.gateway.filter.LoginFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class SummerGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SummerGatewayApplication.class, args);
	}

	@Bean
	public LoginFilter loginFilter() {
		return new LoginFilter();
	}

	@Bean
	public CookieFilter cookieFilter() {
		return new CookieFilter();
	}



}

package net.myspring.future;

import net.myspring.future.common.repository.BaseRepositoryImpl;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableOAuth2Client
@EnableDiscoveryClient
@EnableFeignClients
@EntityScan(basePackageClasses = {WsFutureApplication.class, Jsr310JpaConverters.class})
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class WsFutureApplication {

	public static void main(String[] args) {
		SpringApplication.run(WsFutureApplication.class, args);
	}
}

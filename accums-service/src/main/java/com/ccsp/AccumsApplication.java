package com.ccsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.ccsp.common.aspect.LoggingAspect;

/**
 * @author nnarayanaperumaln
 * This class should be on the root package as it is used for swagger configurations.
 *
 */
@EnableAspectJAutoProxy
@SpringBootApplication
@ComponentScan
public class AccumsApplication extends SpringBootServletInitializer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(AccumsApplication.class, args);
	}
	
	@Bean
	public LoggingAspect getAspect() {
		return new LoggingAspect();
	}
	
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AccumsApplication.class);
    }
}
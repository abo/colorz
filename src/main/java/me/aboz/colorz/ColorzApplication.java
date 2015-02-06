package me.aboz.colorz;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class ColorzApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ColorzApplication.class, args);
    }
    
    @Configuration
    protected static class WebMvcConfig extends WebMvcConfigurerAdapter{

		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController("").setViewName("index");
		}

    }
}

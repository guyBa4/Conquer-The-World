package Application.Configurations;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@org.springframework.context.annotation.Configuration
@EnableJpaRepositories("Application.DataAccessLayer.Repositories")
@ComponentScan(basePackages = {"Application.APILayer", "Application.ServiceLayer", "Application.DataAccessLayer"})
@EntityScan("Application.Entities")
@CrossOrigin(origins = "*")
public class Configuration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("http://3.144.45.152:80", "http://3.144.45.152:3000", "http://conquertheworldfiaj.shop")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
    
    public static final Long defaultSseEmitterTimeout = 120000L;
}

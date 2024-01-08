package Application;

import Application.Configurations.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Import(Configuration.class)
public class CtwServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CtwServerApplication.class, args);
	}

}

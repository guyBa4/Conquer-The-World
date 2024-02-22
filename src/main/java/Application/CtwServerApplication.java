package Application;

import Application.Configurations.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import(Configuration.class)
public class CtwServerApplication {


	public static void main(String[] args) {
		SpringApplication.run(CtwServerApplication.class, args);
	}

}

package idatt2105.erlinssl.ikhovind.reserved;

import idatt2105.erlinssl.ikhovind.reserved.model.User;
import idatt2105.erlinssl.ikhovind.reserved.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
@SpringBootApplication
public class ReservedApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservedApplication.class, args);
	}
}

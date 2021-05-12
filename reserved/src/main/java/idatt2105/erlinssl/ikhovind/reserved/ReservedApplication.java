package idatt2105.erlinssl.ikhovind.reserved;

import idatt2105.erlinssl.ikhovind.reserved.model.User;
import idatt2105.erlinssl.ikhovind.reserved.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
@SpringBootApplication
public class ReservedApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservedApplication.class, args);
		User user = new User("hei","he", "3", "5", true);
		UserService userService = new UserService();
		userService.registerNewUserAccount(user);
	}
}

package idatt2105.erlinssl.ikhovind.reserved.util;

import idatt2105.erlinssl.ikhovind.reserved.model.User;
import idatt2105.erlinssl.ikhovind.reserved.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

@Configuration
@EnableJpaRepositories
@ComponentScan
@EntityScan
public class Config {
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DataSource springJpaDataSource()
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql-ait.stud.idi.ntnu.no/erlinssl");
        dataSource.setUsername("erlinssl");
        dataSource.setPassword("jbPtXUg4");
        return dataSource;
    }
    @Bean
    public EntityManagerFactory entityManagerFactory() throws IOException {

        Properties prop = new Properties();
        HashMap<String, String> newProperties = new HashMap<>();
        //loads the local .properties file
        InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");
        // load a properties file
        prop.load(input);
        assert input != null;
        input.close();

        String jdbcUrl = "jdbc:mysql://" + prop.getProperty("RDSHOSTNAME") + ":" + prop.getProperty("RDSPORT") + "/" + prop.getProperty("RDSDBNAME");
        newProperties.put("javax.persistence.jdbc.url", jdbcUrl);
        newProperties.put("javax.persistence.jdbc.user", prop.getProperty("RDSUSERNAME"));
        newProperties.put("javax.persistence.jdbc.password", prop.getProperty("RDSPASSWORD"));

        return javax.persistence.Persistence.createEntityManagerFactory("DatabasePU", newProperties);
    }
}

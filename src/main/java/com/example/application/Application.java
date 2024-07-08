package com.example.application;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import com.example.application.data.repository.RoleRepository;
import com.example.application.data.repository.UserRepository;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

import java.util.HashSet;

import javax.sql.DataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "noodle")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource,
            SqlInitializationProperties properties, UserRepository UserRepository, RoleRepository RoleRepository,
            BCryptPasswordEncoder passwordEncoder) {
        // This bean ensures the database is only initialized when empty
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() {
                if (UserRepository.count() == 0L) {
                    boolean isInitialized = super.initializeDatabase();

                    // Create default role
                    Role adminRole = new Role();
                    adminRole.setName("ADMIN");

                    RoleRepository.save(adminRole);

                    // Create default admin user
                    User admin = new User();
                    admin.setUsername("admin");
                    admin.setHashedPassword(passwordEncoder.encode("admin"));
                    admin.setRoles(new HashSet<>());
                    admin.getRoles().add(adminRole);
                    UserRepository.save(admin);

                    return isInitialized;
                }
                return false;
            }
        };
    }
}

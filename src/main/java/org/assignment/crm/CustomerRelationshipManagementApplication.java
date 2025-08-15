package org.assignment.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication
public class CustomerRelationshipManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerRelationshipManagementApplication.class, args);
    }

}

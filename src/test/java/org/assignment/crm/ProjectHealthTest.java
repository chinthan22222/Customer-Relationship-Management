package org.assignment.crm;

import org.assignment.crm.config.SecurityConfiguration;
import org.assignment.crm.controller.*;
import org.assignment.crm.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProjectHealthTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Project health: core beans are present in the Spring context")
    void coreBeansAreLoaded() {
        assertThat(applicationContext.getBeanNamesForType(UserController.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(CustomerController.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(SaleController.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(ReportController.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(CustomerInteractionController.class)).isNotEmpty();

        assertThat(applicationContext.getBeanNamesForType(UserService.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(CustomerService.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(SaleService.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(ReportService.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(CustomerInteractionService.class)).isNotEmpty();

        assertThat(applicationContext.getBeanNamesForType(SecurityConfiguration.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(BCryptPasswordEncoder.class)).isNotEmpty();
    }
}

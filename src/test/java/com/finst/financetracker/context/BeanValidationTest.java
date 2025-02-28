package com.finst.financetracker.context;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
 class BeanValidationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
     void testEntityManagerFactoryBean() {
        assertThat(applicationContext.containsBean("entityManagerFactory")).isTrue();
        assertThat(applicationContext.containsBean("transactionService")).isTrue();
        assertThat(applicationContext.containsBean("transactionsController")).isTrue();
    }
}
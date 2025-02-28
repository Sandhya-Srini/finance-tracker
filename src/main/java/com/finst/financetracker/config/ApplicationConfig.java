package com.finst.financetracker.config;

import com.finst.financetracker.repository.TransactionRepository;
import com.finst.financetracker.service.TransactionService;
import com.finst.financetracker.serviceimpl.TransactionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    TransactionService transactionService(TransactionRepository transactionRepository){
        return new TransactionServiceImpl(transactionRepository);
    }


}

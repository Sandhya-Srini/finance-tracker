package com.finst.financetracker.repository;

import com.finst.financetracker.entity.TransactionEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class JPATransactionSpecification {
    private JPATransactionSpecification() {
    }

    public static Specification<TransactionEntity> withId(String id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id);
    }
    public static Specification<TransactionEntity> withUserId(String userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("userId"), userId);
    }
    public static Specification<TransactionEntity> withType(String type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<TransactionEntity> withCategory(String category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category"), category);
    }
    public static Specification<TransactionEntity> withTransactionDate(String transactionDate) {
        Instant startRange = LocalDate.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endRange = LocalDate.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(23,59,59).toInstant(ZoneOffset.UTC);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("transactionDate"), startRange, endRange);
    }


    public static Specification<TransactionEntity> withAmount(String amount) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("amount"), amount);
    }
}

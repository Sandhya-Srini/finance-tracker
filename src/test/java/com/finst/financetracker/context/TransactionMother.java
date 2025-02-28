package com.finst.financetracker.context;

import com.finst.financetracker.entity.TransactionEntity;
import com.finst.financetracker.model.Transaction;
import com.finst.financetracker.resource.TransactionResource;
import com.finst.financetracker.vocabulary.TransactionCategory;
import com.finst.financetracker.vocabulary.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TransactionMother {

    public static TransactionResource incomeTransactionResource()
    {
        return new TransactionResource("1","user-1",new BigDecimal(1000), OffsetDateTime.now(),null,"INCOME","SALARY","transactions description");
    }

    public static TransactionResource incorrectTransactionResource()
    {
        return new TransactionResource("1",null,null, null,null,"INCOME","SALARY","transactions description");
    }

    public static TransactionResource expenseTransactionResource()
    {
        return new TransactionResource("2","user-1",new BigDecimal(1400), OffsetDateTime.now(),null,"EXPENSE","UTILITIES","transactions description");
    }

    public static Transaction incomeTransaction() {
        return Transaction.builder()
                .id(Long.valueOf("1"))
                .amount(BigDecimal.valueOf(1000))
                .type(TransactionType.INCOME)
                .category(TransactionCategory.SALARY)
                .transactionDate(OffsetDateTime.now())
                .updatedDate(null)
                .description("test transaction")
                .userId("user-1")
                .build();
    }


    public static TransactionEntity transactionEntity() {
        return new TransactionEntity(Long.valueOf("1"), new BigDecimal(1000), Instant.now(), null, TransactionType.INCOME, TransactionCategory.SALARY, "test transaction entity", "user-1");
    }

    public static TransactionEntity expenseTransactionEntity() {
        return new TransactionEntity(Long.valueOf("2"), new BigDecimal(500), Instant.now(), null, TransactionType.EXPENSE, TransactionCategory.SALARY, "test transaction entity", "user-1");
    }

    public static TransactionResource updateTransactionResource() {
        return new TransactionResource(null, null,BigDecimal.valueOf(2000),null,null,"EXPENSE",null,null);
    }
}

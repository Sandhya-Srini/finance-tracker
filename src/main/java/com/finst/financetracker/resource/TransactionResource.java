package com.finst.financetracker.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finst.financetracker.model.Transaction;
import com.finst.financetracker.vocabulary.TransactionCategory;
import com.finst.financetracker.vocabulary.TransactionType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResource(String id,
                                  @NotNull(message = "UserId is required")
                                  String userId,
                                  @NotNull(message = "Amount is required")
                                  BigDecimal amount,
                                  @NotNull(message = "Transaction date is required")
                                  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
                                  OffsetDateTime transactionDate,
                                  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
                                  OffsetDateTime updatedDate,
                                  String type,
                                  String category,
                                  String description) {

    public Transaction toTransaction()
    {
        return Transaction.builder()
                .amount(this.amount)
                .transactionDate(this.transactionDate)
                .userId(this.userId)
                .type(TransactionType.valueOf(this.type))
                .category(TransactionCategory.valueOf(this.category))
                .description(this.description)
                .build();
    }

}

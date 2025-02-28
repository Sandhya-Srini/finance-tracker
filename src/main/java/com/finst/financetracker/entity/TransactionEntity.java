package com.finst.financetracker.entity;

import com.finst.financetracker.model.Transaction;
import com.finst.financetracker.vocabulary.TransactionCategory;
import com.finst.financetracker.vocabulary.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private BigDecimal amount;
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private Instant transactionDate;
    @Column(name = "updated_date")
    private Instant updatedDate;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Enumerated(EnumType.STRING)
    private TransactionCategory category;
    private String description;
    private String userId;

    public Transaction toTransaction()
    {
       return Transaction.builder()
               .id(this.getId())
               .amount(this.getAmount())
               .type(this.getType())
               .category(this.getCategory())
               .transactionDate(OffsetDateTime.ofInstant(this.getTransactionDate(), ZoneOffset.UTC))
               .updatedDate(this.getUpdatedDate()!=null ? OffsetDateTime.ofInstant(this.getUpdatedDate(), ZoneOffset.UTC) : null )
               .description(this.getDescription())
               .userId(this.getUserId())
               .build();
    }
}

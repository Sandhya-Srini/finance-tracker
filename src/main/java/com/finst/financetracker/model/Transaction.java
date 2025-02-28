package com.finst.financetracker.model;

import com.finst.financetracker.entity.TransactionEntity;
import com.finst.financetracker.resource.TransactionResource;
import com.finst.financetracker.vocabulary.TransactionCategory;
import com.finst.financetracker.vocabulary.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
@Setter
public class Transaction {

    private  Long id;
    private BigDecimal amount;
    private OffsetDateTime transactionDate;
    private OffsetDateTime updatedDate;
    private TransactionType type;
    private TransactionCategory category;
    private String description;
    private String userId;


    public TransactionResource toTransactionResource()
    {
        return new TransactionResource(this.id.toString(), this.userId, this.amount,
                this.transactionDate,this.updatedDate,this.type.toString(), this.category.toString(), this.description);
    }

    public TransactionEntity toTransactionEntity()
    {
       TransactionEntity transactionEntity = new TransactionEntity();
       transactionEntity.setId(this.id);
       transactionEntity.setAmount(this.amount);
       transactionEntity.setType(this.type);
       transactionEntity.setCategory(this.category);
       transactionEntity.setDescription(this.description);
       transactionEntity.setTransactionDate(this.transactionDate.toInstant());
       transactionEntity.setUpdatedDate(this.updatedDate!= null ? this.updatedDate.toInstant(): null);
       transactionEntity.setUserId(this.userId);
       return transactionEntity;
    }




}

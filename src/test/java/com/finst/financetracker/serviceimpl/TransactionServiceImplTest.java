package com.finst.financetracker.serviceimpl;

import com.finst.financetracker.context.TransactionMother;
import com.finst.financetracker.entity.TransactionEntity;
import com.finst.financetracker.repository.TransactionRepository;
import com.finst.financetracker.resource.TransactionResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
 @Mock
 private TransactionRepository transactionRepository;

 @InjectMocks
 private TransactionServiceImpl transactionService;

    @Test
    void returnTransactionIdOnSuccessfulCreateTransaction(){
        TransactionResource newTransaction = TransactionMother.incomeTransactionResource();
        when(transactionRepository.saveAndFlush(any())).thenReturn(TransactionMother.transactionEntity());
        var transactionId =  transactionService.addTransaction(newTransaction);
        assertEquals("1", transactionId);
    }

    @Test
    void returnUpdatedTransactionOnSuccessfulUpdateTransaction(){
        TransactionResource updateTransaction = TransactionMother.updateTransactionResource();
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(TransactionMother.transactionEntity()));
        var updatedTransaction =  transactionService.updateTransaction(String.valueOf(1),updateTransaction);
        assertEquals("1", updatedTransaction.getId().toString());
        assertEquals("2000", updatedTransaction.getAmount().toString());
        assertEquals("EXPENSE", updatedTransaction.getType().toString());
    }

    @Test
    void returnTransactionsFilteredAndSortedByConditions(){
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("amount").descending());
        Page<TransactionEntity> mockPage = new PageImpl<>(Collections.singletonList(TransactionMother.transactionEntity()), pageRequest, 1);
        when(transactionRepository.findAll(Specification.where(any()),any(PageRequest.class))).thenReturn(mockPage);
        var filteredTransactions =  transactionService.getTransactions(List.of("type.eq(INCOME)"), "+amount",1, 10);
        assertEquals("1", filteredTransactions.transactions().get(0).id());
        assertEquals("INCOME", filteredTransactions.transactions().get(0).type());
    }

    @Test
    void returnCurrentBalanceOfAnUser(){
        when(transactionRepository.findByUserId(any())).thenReturn(List.of(TransactionMother.transactionEntity(), TransactionMother.expenseTransactionEntity()));
        var balance =  transactionService.getCurrentBalance("user-1");
        assertEquals(new BigDecimal(500), balance);
    }

    @Test
    void returnCurrentBalanceForADate(){
        when(transactionRepository.findAll(Specification.where(any()))).thenReturn(List.of(TransactionMother.transactionEntity(), TransactionMother.expenseTransactionEntity()));
        var balance =  transactionService.getCurrentBalanceForDate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        assertEquals(new BigDecimal(500), balance);
    }



}

package com.finst.financetracker.service;


import com.finst.financetracker.model.Transaction;
import com.finst.financetracker.resource.TransactionResource;
import com.finst.financetracker.resource.TransactionSummaryResource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionService {


    TransactionSummaryResource getTransactions(List<String> filter, String sort, Integer page, Integer limit);

    String addTransaction(TransactionResource transactionResource);

    Transaction updateTransaction(String transactionId, TransactionResource transactionResource);

    BigDecimal getCurrentBalance(String userId);

    BigDecimal getCurrentBalanceForDate(String date);
}

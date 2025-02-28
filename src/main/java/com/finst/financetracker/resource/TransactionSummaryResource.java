package com.finst.financetracker.resource;

import java.math.BigDecimal;
import java.util.List;

public record TransactionSummaryResource(long totalNumberOfTransactions, List<TransactionResource> transactions, BigDecimal netBalance) {
}

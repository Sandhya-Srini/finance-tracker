package com.finst.financetracker.resource;

import java.math.BigDecimal;

public record TransactionBalanceResource (String transactionId,String userId, BigDecimal netBalance){
}

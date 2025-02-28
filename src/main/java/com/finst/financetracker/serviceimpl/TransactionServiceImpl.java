package com.finst.financetracker.serviceimpl;

import com.finst.financetracker.exception.InvalidDateException;
import com.finst.financetracker.model.Transaction;
import com.finst.financetracker.entity.TransactionEntity;
import com.finst.financetracker.repository.JPATransactionSpecification;
import com.finst.financetracker.repository.TransactionRepository;
import com.finst.financetracker.resource.TransactionResource;
import com.finst.financetracker.resource.TransactionSummaryResource;
import com.finst.financetracker.service.TransactionService;
import com.finst.financetracker.util.FilterAndSortUtil;
import com.finst.financetracker.vocabulary.Filter;
import com.finst.financetracker.vocabulary.TransactionCategory;
import com.finst.financetracker.vocabulary.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public TransactionSummaryResource getTransactions(List<String> filter, String sort, Integer page, Integer limit) {
        log.info("Viewing Transactions");
        var util = new FilterAndSortUtil();
        List<Filter> filterConditions = util.extractFilters(filter);
        Specification<TransactionEntity> specification = Specification.where(null);
        if(filterConditions!=null) {
            for (Filter f : filterConditions) {
                switch (f.key()) {
                    case "id" -> specification = specification.and(JPATransactionSpecification.withId(f.value()));
                    case "userId" -> specification = specification.and(JPATransactionSpecification.withUserId(f.value()));
                    case "amount" -> specification = specification.and(JPATransactionSpecification.withAmount(f.value()));
                    case "type" -> specification = specification.and(JPATransactionSpecification.withType(f.value()));
                    case "category" -> specification = specification.and(JPATransactionSpecification.withCategory(f.value()));
                    case "transactionDate" -> specification = specification.and(JPATransactionSpecification.withTransactionDate(f.value()));

                    default -> throw new IllegalStateException("Unexpected value: " + f.key());
                }
            }
        }
        String sortField = util.mapToSortField(sort).getFirst();
        String sortOrder = util.mapToSortField(sort).getSecond();
        Sort sorting = Sort.by(sortField);
        sorting = sortOrder.equalsIgnoreCase("desc") ? sorting.descending() : sorting.ascending();

        PageRequest pageRequest = PageRequest.of(page,limit,sorting);
        Page<TransactionEntity> transactionPage = transactionRepository.findAll(specification,pageRequest);
        List<TransactionResource> transactionResult = transactionPage.stream().map(TransactionEntity::toTransaction).toList()
                .stream().map(Transaction::toTransactionResource).toList();
        return new TransactionSummaryResource(transactionPage.getTotalElements(),
                transactionResult,calculateNetBalance(transactionResult));

    }

    private BigDecimal calculateNetBalance(List<TransactionResource> transactionResult) {
        return transactionResult.stream()
                .map(transaction -> "INCOME".equals(transaction.type()) ? transaction.amount() : transaction.amount().negate())
                .reduce(BigDecimal.ZERO , BigDecimal :: add);
    }
    @Transactional
    @Override
    public String addTransaction(TransactionResource transactionResource) {
        log.info("Adding Transaction for user {}", transactionResource.userId());
        Transaction transaction = transactionResource.toTransaction();
        return transactionRepository.saveAndFlush(transaction.toTransactionEntity()).getId().toString();
    }
    @Transactional
    @Override
    public Transaction updateTransaction(String transactionId, TransactionResource transactionResource) {
        TransactionEntity transactionToBeUpdated = transactionRepository.findById(Long.valueOf(transactionId))
                .orElseThrow(()-> new NoSuchElementException("Transaction with id" + transactionId +  "not found"));
        log.info("Updating Transaction with id {} for user {}", transactionId, transactionToBeUpdated.getUserId());
        Transaction transaction = transactionToBeUpdated.toTransaction();
        if(transactionResource.amount()!= null) transaction.setAmount(transactionResource.amount());
        if(transactionResource.type()!=null) transaction.setType(TransactionType.valueOf(transactionResource.type()));
        if(transactionResource.category()!=null) transaction.setCategory(TransactionCategory.valueOf(transactionResource.category()));
        if(transactionResource.description()!=null )transaction.setDescription(transactionResource.description());
        transaction.setUpdatedDate(OffsetDateTime.now());
        transactionRepository.saveAndFlush(transaction.toTransactionEntity());
        return transaction;
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public BigDecimal getCurrentBalance(String userId) {
        log.info("Calculating Balance for user {} ", userId);
       List<TransactionEntity> transactionsForUser = transactionRepository.findByUserId(userId);
        if (transactionsForUser == null || transactionsForUser.isEmpty()) {
            throw new NoSuchElementException("No transactions found for user "+ userId );
        }
       return transactionsForUser.stream()
               .map(transaction -> TransactionType.INCOME.equals(transaction.getType()) ? transaction.getAmount() : transaction.getAmount().negate())
               .reduce(BigDecimal.ZERO , BigDecimal :: add);
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public BigDecimal getCurrentBalanceForDate(String date) {
        if(isDateInFuture(date)){
            throw new InvalidDateException("Date cannot be in future");
        }
        log.info("Calculating Balance for date {} ", date);
        Specification<TransactionEntity> specification = Specification.where(JPATransactionSpecification.withTransactionDate(date));
        List<TransactionEntity> transactionsForDate = transactionRepository.findAll(specification);
        if (transactionsForDate.isEmpty()) {
            throw new NoSuchElementException("No transactions found for given date "+ date );
        }
        return transactionsForDate.stream()
                .map(transaction -> TransactionType.INCOME.equals(transaction.getType()) ? transaction.getAmount() : transaction.getAmount().negate())
                .reduce(BigDecimal.ZERO , BigDecimal :: add);
    }

    private boolean isDateInFuture(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return localDate.isAfter(LocalDate.now());
    }
}

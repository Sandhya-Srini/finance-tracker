package com.finst.financetracker.controller;


import com.finst.financetracker.exception.InvalidDateException;
import com.finst.financetracker.model.Transaction;
import com.finst.financetracker.resource.TransactionBalanceResource;
import com.finst.financetracker.resource.TransactionResource;
import com.finst.financetracker.resource.TransactionSummaryResource;
import com.finst.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionsController {
    private final TransactionService transactionService;

    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping()
    public ResponseEntity<TransactionSummaryResource> getTransactions(@RequestParam(value = "filter", required = false) List<String> filter,
                                                                      @RequestParam(value = "sort", required = false) String sort,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(transactionService.getTransactions(filter, sort,page,limit));
    }

    @PostMapping()
    public ResponseEntity<TransactionBalanceResource> addTransaction(@Valid @RequestBody TransactionResource transactionResource) {
        return ResponseEntity.ok(new TransactionBalanceResource(transactionService.addTransaction(transactionResource),
                transactionResource.userId(), transactionService.getCurrentBalance(transactionResource.userId())));
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionBalanceResource> updateTransaction(@PathVariable String transactionId,
                                                                        @RequestBody TransactionResource transactionResource) {
        Transaction updatedTransaction = transactionService.updateTransaction(transactionId,transactionResource);
        return ResponseEntity.ok(new TransactionBalanceResource(updatedTransaction.getId().toString(),
                updatedTransaction.getUserId(), transactionService.getCurrentBalance(updatedTransaction.getUserId())));
    }

    @GetMapping("/current-balance/user/{userId}")
    public ResponseEntity<BigDecimal> getCurrentBalance(@PathVariable String userId) {
        return ResponseEntity.ok(transactionService.getCurrentBalance(userId));
    }
    @GetMapping("/current-balance/date/{date}")
    public ResponseEntity<BigDecimal> getCurrentBalanceForDate(@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in yyyy-MM-dd format")
                                                                   @PathVariable String date) {
        return ResponseEntity.ok(transactionService.getCurrentBalanceForDate(date));
    }



    @ExceptionHandler({
            IllegalArgumentException.class,
    })
    public ResponseEntity<String> handleInvalidFieldException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<String> handleDateParseException(Exception e) {
        return ResponseEntity.badRequest().body("Transaction Date must be in Offset Date Time format 'yyyy-dd-MMThh:mm:ssZ'");
    }
    @ExceptionHandler({
            InvalidDateException.class
    })
    public ResponseEntity<String> handleInvalidDateException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({
            NoSuchElementException.class
    })
    public ResponseEntity<String> handleNoSuchElementException(Exception e) {
        return ResponseEntity.notFound().build();
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}

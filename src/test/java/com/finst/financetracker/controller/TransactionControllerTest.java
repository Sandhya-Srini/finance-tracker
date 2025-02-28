package com.finst.financetracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finst.financetracker.context.TransactionMother;
import com.finst.financetracker.resource.TransactionResource;
import com.finst.financetracker.resource.TransactionSummaryResource;
import com.finst.financetracker.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TransactionsController.class})
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
     void testViewTransactions() throws Exception {

        given(transactionService.getTransactions(anyList(),anyString(),any(),any())).willReturn(getTransactionsResponse());
        var actual = getTransactions(List.of("userId.eq(userId)","type.eq(INCOME)"));

        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfTransactions").value(2))
                .andExpect(jsonPath("$.netBalance").value( 1000))
                .andExpect(jsonPath("$.transactions[0].id").value( 1));
        verify(transactionService, times(1)).getTransactions(any(),any(),any(),any());
    }

    @Test
    void testCreateTransaction() throws Exception {
        given(transactionService.addTransaction(any())).willReturn("1");
        given(transactionService.getCurrentBalance(any())).willReturn(BigDecimal.valueOf(1000));
        var actual = addTransaction(getJson(TransactionMother.incomeTransactionResource()));

        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.userId").value( "user-1"))
                .andExpect(jsonPath("$.netBalance").value( 1000));
        verify(transactionService, times(1)).addTransaction(any());
    }

    @Test
    void testCreateTransactionWithNullValues() throws Exception {
        given(transactionService.addTransaction(any())).willReturn("1");
        given(transactionService.getCurrentBalance(any())).willReturn(BigDecimal.valueOf(1000));
        var actual = addTransaction(getJson(TransactionMother.incorrectTransactionResource()));

        actual.andExpect(status().isBadRequest());
        verify(transactionService, times(0)).addTransaction(any());
    }

    private String getJson(TransactionResource transactionResource) throws JsonProcessingException {
        return objectMapper.writeValueAsString(transactionResource);
    }

    @Test
    void testUpdateTransaction() throws Exception {
        given(transactionService.updateTransaction(any(), any())).willReturn(TransactionMother.incomeTransaction());
        given(transactionService.getCurrentBalance(any())).willReturn(BigDecimal.valueOf(1000));
        var actual = updateTransaction(getUpdateTransactionJson());

        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.userId").value( "user-1"))
                .andExpect(jsonPath("$.netBalance").value( 1000));
        verify(transactionService, times(1)).updateTransaction(any(),any());
    }

    @Test
    void testCalculateNetBalanceForUser() throws Exception {
        given(transactionService.getCurrentBalance(any())).willReturn(BigDecimal.valueOf(1000));
        var actual = getCurrentBalanceForUser();
        actual.andExpect(status().isOk());
        assertEquals("1000", actual.andReturn().getResponse().getContentAsString());
        verify(transactionService, times(1)).getCurrentBalance(any());

    }

    @Test
    void testCalculateNetBalanceForUserWhenUserNotFound() throws Exception {
        given(transactionService.getCurrentBalance(any())).willThrow(NoSuchElementException.class);
        var actual = getCurrentBalanceForUser();
        actual.andExpect(status().isNotFound());
    }

    @Test
    void testCalculateNetBalanceForDate() throws Exception {
        given(transactionService.getCurrentBalanceForDate(any())).willReturn(BigDecimal.valueOf(1000));
        var actual = getCurrentBalanceForDate("2025-02-28");
        actual.andExpect(status().isOk());
        assertEquals("1000", actual.andReturn().getResponse().getContentAsString());
        verify(transactionService, times(1)).getCurrentBalanceForDate(any());
    }

    @Test
    void testCalculateNetBalanceWhenNoTransactionsFoundForDate() throws Exception {
        given(transactionService.getCurrentBalanceForDate(any())).willThrow(NoSuchElementException.class);
        var actual = getCurrentBalanceForDate("2025-02-28");
        actual.andExpect(status().isNotFound());
    }

    @Test
    void testCalculateNetBalanceWhenIncorrectDateFormatPassed() throws Exception {
        given(transactionService.getCurrentBalanceForDate(any())).willReturn(BigDecimal.valueOf(1000));
        var actual = getCurrentBalanceForDate("02-12-2025");
        actual.andExpect(status().isBadRequest());
    }

    @Test
    void testCalculateNetBalanceWhenDateInFuturePassed() throws Exception {
        given(transactionService.getCurrentBalanceForDate(any())).willReturn(BigDecimal.valueOf(1000));
        var actual = getCurrentBalanceForDate("02-12-2030");
        actual.andExpect(status().isBadRequest());
    }

    private ResultActions getCurrentBalanceForDate(String date) throws Exception {
        return mockMvc.perform(get("/v1/transactions/current-balance/date/"+date)
                .header("X-API-Key", "d352e8d6-f5b7-4f34-916b-91353e64d16e")
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions getCurrentBalanceForUser() throws Exception {
        return mockMvc.perform(get("/v1/transactions/current-balance/user/user-1")
                .header("X-API-Key", "d352e8d6-f5b7-4f34-916b-91353e64d16e")
                .accept(MediaType.APPLICATION_JSON));
    }
    private ResultActions addTransaction(String transactionJson) throws Exception {
        return mockMvc.perform(post("/v1/transactions")
                .content(transactionJson)
                .header("X-API-Key", "d352e8d6-f5b7-4f34-916b-91353e64d16e")
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions updateTransaction(String updateTransactionJson) throws Exception {
        return mockMvc.perform(patch("/v1/transactions/1")
                .content(updateTransactionJson)
                .header("X-API-Key", "d352e8d6-f5b7-4f34-916b-91353e64d16e")
                .contentType(MediaType.APPLICATION_JSON));
    }
    private ResultActions getTransactions(List<String> filter) throws Exception {
        return mockMvc.perform(get("/v1/transactions")
                .param("filter", String.valueOf(filter))
                .param("sort", "-amount")
                .param("page", String.valueOf(1))
                .param("limit", String.valueOf(10))
                .header("X-API-Key", "d352e8d6-f5b7-4f34-916b-91353e64d16e")
                .accept(MediaType.APPLICATION_JSON));

    }
    private String getUpdateTransactionJson() {
        return """
        {
            "amount": "125",
            "type" : "EXPENSE"
        }""";
    }
    private TransactionSummaryResource getTransactionsResponse() {
        return new TransactionSummaryResource(2,List.of(TransactionMother.incomeTransactionResource(),TransactionMother.expenseTransactionResource()),new BigDecimal(1000));
    }

    
}

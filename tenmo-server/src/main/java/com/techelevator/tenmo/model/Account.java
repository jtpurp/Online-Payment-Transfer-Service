package com.techelevator.tenmo.model;

import com.techelevator.tenmo.exceptions.InsufficientFundsException;

import java.math.BigDecimal;

public class Account {

    private Long accountId;
    private Long userId;
    private BigDecimal balance;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Account() {};

    public Account (Long id, Long userId, BigDecimal balance) {
        this.accountId = id;
        this.userId = userId;
        this.balance = balance;
    }

}

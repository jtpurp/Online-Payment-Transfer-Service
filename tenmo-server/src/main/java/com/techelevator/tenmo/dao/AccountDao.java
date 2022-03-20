package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.security.Principal;

public interface AccountDao {

    Account getAccount();

    Account getAccountByUserId(Long userId);

    Account getAccountByAccountId(Long accountId);

    BigDecimal getBalance(Long userId);

    void updateToAddBalanceByUserId(Account account, BigDecimal balance, Long userId);

    void updateToSubtractBalanceByUserId(Account account, BigDecimal amount, Long userId);

}

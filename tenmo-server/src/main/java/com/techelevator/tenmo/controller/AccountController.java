package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public Account getAccount() {
        return accountDao.getAccount();
    }

    @RequestMapping(path = "/account/user/{userId}", method = RequestMethod.GET)
    public Account getAccountByUserId(@PathVariable Long userId) {
        return accountDao.getAccountByUserId(userId);
    }

    @RequestMapping(path = "/account/{accountId}", method = RequestMethod.GET)
    public Account getAccountByAccountId(@PathVariable Long accountId) {
        return accountDao.getAccount();
    }

    @RequestMapping(path = "/account/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {

        Long principalId = getPrincipalId(principal);

        return accountDao.getAccountByUserId(principalId).getBalance();
    }

    @RequestMapping(path = "/account/updateAdd/user/{userId}", method = RequestMethod.PUT)
    public void updateToAddBalanceByUserId(@RequestBody Account account, BigDecimal balance, @PathVariable Long userId) {
        accountDao.updateToAddBalanceByUserId(account, balance, userId);
    }

    @RequestMapping(path = "/account/updateSubtract/user/{userId}", method = RequestMethod.PUT)
    public void updateToSubtractBalanceByUserId(@RequestBody Account account, BigDecimal amount, @PathVariable Long userId) {
        accountDao.updateToSubtractBalanceByUserId(account, amount, userId);
    }

    private Long getPrincipalId(Principal principal) {
        return userDao.findByUsername(principal.getName()).getId();
    }

}

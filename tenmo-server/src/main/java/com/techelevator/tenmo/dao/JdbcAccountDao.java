package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.exceptions.AccountNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    //JDBC Template Instantiation
    private JdbcTemplate jdbcTemplate;

    //Constructor
    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccount() {
        Account account = null;

        String sql = "SELECT account_id, user_id, balance FROM account;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        if(result.next()) {
            account = mapRowToAccount(result);
        } else {
            throw new AccountNotFoundException();
        }
        return account;
    }

    @Override
    public Account getAccountByAccountId(Long accountId) {
        Account account = null;

        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountId);
        if(result.next()) {
            account = mapRowToAccount(result);
        } else {
            throw new AccountNotFoundException();
        }
        return account;
    }

    @Override
    public Account getAccountByUserId(Long userId) {
        Account account = null;

        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        if(result.next()) {
            account = mapRowToAccount(result);
        } else {
            throw new AccountNotFoundException();
        }
        return account;
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        BigDecimal balance = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        if (result.next()) {
            balance = mapRowToAccount(result).getBalance();
        } else {
            throw new AccountNotFoundException();
        }
        return balance;
    }

    //TODO ----- balance is null and violated not null constraint, cannot update -----

    @Override
    public void updateToAddBalanceByUserId(Account account, BigDecimal balance, Long userId) {
        String sql = "UPDATE account SET balance = ? " +
                     "WHERE user_id = ?;";
        jdbcTemplate.update(sql, balance, userId);
    }

    //TODO ----- balance is null and violated not null constraint, cannot update -----

    @Override
    public void updateToSubtractBalanceByUserId(Account account, BigDecimal amount, Long userId) {
        String sql = "UPDATE account SET balance = ((SELECT balance FROM account WHERE user_id = ?) - ?) " +
                "WHERE user_id = ?;";
        jdbcTemplate.update(sql, userId, amount, userId);
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account act = new Account();
        act.setAccountId(rs.getLong("account_id"));
        act.setUserId(rs.getLong("user_id"));
        act.setBalance(rs.getBigDecimal("balance"));
        return act;
    }

}

package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl;
    private AuthenticatedUser currentUser;

    //Setter
    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    //Constructor
    public AccountService(String baseUrl, AuthenticatedUser currentUser) {
        this.baseUrl = baseUrl;
        this.currentUser = currentUser;
    }

    public Account getAccount() {
        Account act = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "account", HttpMethod.GET, makeAuthEntity(), Account.class);
            act = response.getBody();
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
        return act;
    }

    public Account getAccountByUserId(Long userId) {
        Account act = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "account/user/" + userId, HttpMethod.GET, makeAuthEntity(), Account.class);
            act = response.getBody();
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
        return act;
    }

    public BigDecimal getBalance() {
        BigDecimal accountBalance = null;
        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(API_BASE_URL + "account/balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            accountBalance = response.getBody();
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
        return accountBalance;
    }

    //TODO ----- not working ----- issue with balance coming up null / most likely server side
    public void updateToAddBalanceByUserId(Account account, BigDecimal amount, Long userId) {
        HttpEntity<Account> entity = makeAccountEntity(account);

        try {
            restTemplate.put(API_BASE_URL + "account/updateAdd/user/" + userId, entity);
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
    }

    //TODO ----- not working ----- issue with balance coming up null / most likely server side
    public void updateToSubtractBalanceByUserId(Account account, BigDecimal amount, Long userId) {
        HttpEntity<Account> entity = makeAccountEntity(account);

        try {
            restTemplate.put(API_BASE_URL + "account/subtractAdd/user/" + userId, entity);
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(account, headers);
    }

}

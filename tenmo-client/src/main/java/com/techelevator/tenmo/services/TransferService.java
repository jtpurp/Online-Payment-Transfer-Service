package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransferService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl;
    private AuthenticatedUser currentUser;

    //Setter
    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    //Constructor
    public TransferService(String baseUrl, AuthenticatedUser currentUser) {
        this.baseUrl = baseUrl;
        this.currentUser = currentUser;
    }

    public void sendTransfer(Transfer transfer, Long userFromId, Long userToId, BigDecimal amount) {
        HttpEntity<Transfer> transferEntity = makeTransferEntity(transfer);

        try {
            transfer.setTransferTypeId(2L);
            transfer.setTransferStatusId(2L);
            transfer.setAmount(amount);
            transfer.setAccountFrom(userFromId + 1000);
            transfer.setAccountTo(userToId + 1000);
            restTemplate.exchange(API_BASE_URL + "transfer/send", HttpMethod.POST, transferEntity, Transfer.class);

        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public void requestTransfer(Transfer transfer, Long userFromId, Long userToId, BigDecimal amount) {
        HttpEntity<Transfer> transferEntity = makeTransferEntity(transfer);

        try {
            transfer.setTransferTypeId(1L); 
            transfer.setTransferStatusId(1L);
            transfer.setAmount(amount);
            transfer.setAccountFrom(userFromId + 1000);
            transfer.setAccountTo(userToId + 1000);

            restTemplate.exchange(API_BASE_URL + "transfer/request", HttpMethod.POST, transferEntity, Transfer.class);
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public List<Transfer> listTransfers() {
        List<Transfer> transfers = null;

        try{
            ResponseEntity<List<Transfer>> response = restTemplate.exchange(API_BASE_URL + "transfers", HttpMethod.GET, makeAuthEntity(), new ParameterizedTypeReference<>() {});
            transfers = response.getBody();
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    public Transfer getTransfersByTransferId(Long transferId) {
        Transfer transfer = null;

        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfer/" + transferId, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(transfer, headers);
    }
}

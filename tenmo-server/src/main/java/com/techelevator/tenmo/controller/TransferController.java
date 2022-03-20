package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

// I MADE A CHANGE

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    //Instantiations
    private TransferDao transferDao;
    private AccountDao accountDao;

    //Constructor
    public TransferController(TransferDao transferDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer/send", method = RequestMethod.POST)
    public void sendTransfer(@RequestBody Transfer transfer, Long userFromId, Long userToId, BigDecimal amount) throws InsufficientFundsException {

            transferDao.sendTransfer(transfer, userFromId, userToId, amount);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer/request", method = RequestMethod.POST)
    public void requestTransfer(@RequestBody Transfer transfer, Long userFromId, Long userToId, BigDecimal amount) throws InsufficientFundsException {

            transferDao.requestTransfer(transfer, userFromId, userToId, amount);
    }

    @RequestMapping(path = "/transfers", method = RequestMethod.GET)
    public List<Transfer> listTransfers() {
        return transferDao.listTransfers();
    }

    @RequestMapping(path = "/transfer/{transferId}", method = RequestMethod.GET)
    public Transfer getTransferByTransferId(@PathVariable Long transferId) {
        return transferDao.getTransferByTransferId(transferId);
    }

}

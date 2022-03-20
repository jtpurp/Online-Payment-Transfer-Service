package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> listTransfers();

    Transfer getTransferByTransferId(Long transferId);

    void sendTransfer(Transfer transfer, Long userFromId, Long userToId, BigDecimal amount);

    void requestTransfer(Transfer transfer, Long userFromId, Long userToId, BigDecimal amount);


}

package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    //JDBC Template Instantiation
    private JdbcTemplate jdbcTemplate;

    //Constructor
    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> listTransfers() {
        List<Transfer> transferList = new ArrayList<>();

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transferList.add(transfer);
        }
        return transferList;
    }

    @Override
    public Transfer getTransferByTransferId(Long transferId) {
        Transfer tsf = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);

        if(results.next()) {
            tsf = mapRowToTransfer(results);
        } else {
            throw new TransferNotFoundException();
        }
        return tsf;
    }

    @Override
    public void sendTransfer(Transfer transfer, Long userFromId, Long userToId, BigDecimal amount) {
        String sql =
                    "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES (?, ?, ?, ?, ?);";

        jdbcTemplate.update(sql, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
    }

    @Override
    public void requestTransfer(Transfer transfer, Long userFromId, Long userToId, BigDecimal amount) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql,
                transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
    }

    //Map
    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer tsf = new Transfer();
        tsf.setTransferId(rs.getLong("transfer_id"));
        tsf.setTransferTypeId(rs.getLong("transfer_type_id"));
        tsf.setTransferStatusId(rs.getLong("transfer_status_id"));
        tsf.setAccountFrom(rs.getLong("account_from"));
        tsf.setAccountTo(rs.getLong("account_to"));
        tsf.setAmount(rs.getBigDecimal("amount"));

        return tsf;
    }


}

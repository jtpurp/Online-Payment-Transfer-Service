package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class App {

    private AuthenticatedUser currentUser;

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL, currentUser);
    private final TransferService transferService = new TransferService(API_BASE_URL, currentUser);
    private final UserService userService = new UserService(API_BASE_URL, currentUser);

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            accountService.setCurrentUser(currentUser);
            transferService.setCurrentUser(currentUser);
            userService.setCurrentUser(currentUser);
        }
    }
        //TODO ----- 0 to cancel at any point other than initial menu will throw errors
    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        System.out.println("...........................................");
        System.out.println("Your current balance is $" + accountService.getBalance());
        System.out.println("...........................................");
    }

	private void viewTransferHistory() {

        List<Transfer> transferHistory = transferService.listTransfers();
        String transferStatus = "";
        String transferType = "";

        System.out.println("...........................................");
        System.out.println("Transfers");
        System.out.println("ID          From/To          Amount");
        System.out.println("...........................................");

        for(Transfer tsf: transferHistory) {
            if (tsf.getTransferTypeId() == 2) {
                System.out.println(tsf.getTransferId() + "        To: " + userService.findUserByAccountId(tsf.getAccountTo()).getUsername() + "       $" + tsf.getAmount());
            } else if (tsf.getTransferTypeId() == 1) {
                System.out.println(tsf.getTransferId() + "        From: " + userService.findUserByAccountId(tsf.getAccountFrom()).getUsername() + "     $" + tsf.getAmount());
            }
        }

        System.out.print("Please enter transfer ID to view details (0 to cancel) : ");
        Long transferIdInput = (long)consoleService.promptForInt("");

        System.out.println("...........................................");
        System.out.println("Transfer Details");
        System.out.println("...........................................");

        if(transferService.getTransfersByTransferId(transferIdInput).getTransferTypeId() == 1) {
            transferType = "Request";
        } else if (transferService.getTransfersByTransferId(transferIdInput).getTransferTypeId() == 2) {
            transferType = "Send";
        }
        if(transferService.getTransfersByTransferId(transferIdInput).getTransferStatusId() == 1) {
            transferStatus = "Pending";
        } else if(transferService.getTransfersByTransferId(transferIdInput).getTransferStatusId() == 2) {
            transferStatus = "Approved";
        } else if(transferService.getTransfersByTransferId(transferIdInput).getTransferStatusId() == 3) {
            transferStatus = "Rejected";
        }

        System.out.println("ID: " + transferService.getTransfersByTransferId(transferIdInput).getTransferId() + '\n' +
                "From: " + userService.findUserByAccountId(transferService.getTransfersByTransferId(transferIdInput).getAccountFrom()).getUsername() + '\n' + //transferService.getTransfersByTransferId(transferIdInput).getAccountFrom() + '\n' +
                "To: " + userService.findUserByAccountId(transferService.getTransfersByTransferId(transferIdInput).getAccountTo()).getUsername() + '\n' + //transferService.getTransfersByTransferId(transferIdInput).getAccountTo() + '\n' +
                "Type: " + transferType + '\n' +
                "Status: " + transferStatus + '\n' +
                "Amount: $" + transferService.getTransfersByTransferId(transferIdInput).getAmount());

    }

	private void viewPendingRequests() {
        List<Transfer> pendingTransfers = transferService.listTransfers();

        System.out.println("...........................................");
        System.out.println("Pending Transfers");
        System.out.println("ID          To                Amount");
        System.out.println("...........................................");

        for(Transfer tsf: pendingTransfers) {
            if (tsf.getTransferStatusId() == 1) {
                System.out.println(tsf.getTransferId() + "        " + userService.findUserByAccountId(tsf.getAccountFrom()).getUsername() + "             $" + tsf.getAmount()); //userService.getUsernameByAccountId(tsf.getAccountFrom()) instead of tsf.getAccount
            }
        }
    }
        //TODO ----- does not update balance - server side issue -----
	private void sendBucks() {
        List<User> usersDisplay = userService.listUsers();
        Transfer transferToSend = new Transfer();

        System.out.println("...........................................");
        System.out.println("Users");
        System.out.println("ID          Name");
        System.out.println("...........................................");

        for(User user : usersDisplay) {
            System.out.println(user.getId() + "        " + user.getUsername());
        }

        System.out.println();
        System.out.print("Enter ID of user you are sending to (0 to cancel) : ");
        Long userToIdInput = (long)consoleService.promptForInt("");

        System.out.print("Enter amount : ");
        BigDecimal amountToInput = consoleService.promptForBigDecimal("");

        if(accountService.getBalance().compareTo(amountToInput) < 0) {
            System.out.println("Sorry you have insufficient funds for this transfer");
        } else if (amountToInput.compareTo(BigDecimal.valueOf(0)) == 0 ||
                    amountToInput.compareTo(BigDecimal.valueOf(0)) < 0) {
            System.out.println("Sorry you have entered zero or a negative value");
        } else if (userToIdInput.compareTo(currentUser.getUser().getId()) == 0) {
            System.out.println("Sorry you have tried sending a transfer to yourself");
        } else {
            BigDecimal updateToBalance = (accountService.getAccountByUserId(userToIdInput).getBalance()).add(amountToInput);
            BigDecimal updateFromBalance = (accountService.getBalance()).subtract(amountToInput);
                transferService.sendTransfer(transferToSend, currentUser.getUser().getId(), userToIdInput, amountToInput);
                accountService.updateToAddBalanceByUserId(accountService.getAccountByUserId(userToIdInput), updateToBalance, userToIdInput);
                accountService.updateToSubtractBalanceByUserId(accountService.getAccount(), updateFromBalance, currentUser.getUser().getId());
            System.out.println("Your transfer has been successfully sent!");
        }

	}

	private void requestBucks() {
        List<User> usersDisplay = userService.listUsers();
        Transfer transferToRequest = new Transfer();

        System.out.println("...........................................");
        System.out.println("Users");
        System.out.println("ID          Name");
        System.out.println("...........................................");

        for(User user : usersDisplay) {
            System.out.println(user.getId() + "        " + user.getUsername());
        }

        System.out.println();
        System.out.print("Enter ID of user you are requesting from (0 to cancel) : ");
        Long userFromIdInput = Long.valueOf(consoleService.promptForInt(""));

        System.out.print("Enter amount : ");
        BigDecimal amountFromInput = consoleService.promptForBigDecimal("");

        transferService.requestTransfer(transferToRequest, userFromIdInput, currentUser.getUser().getId(), amountFromInput);
        System.out.println("Your request has been sent and is pending approval");
	}

}

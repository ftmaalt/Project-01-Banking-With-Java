package com.ga.acmebank;

import com.ga.acmebank.account.CheckingAccount;
import com.ga.acmebank.account.SavingsAccount;

import java.io.IOException;
import java.util.Scanner;

import static com.ga.acmebank.LoginHandler.getUserName;

public class Actions implements UserAccountActions {
    //    extends LoginHandler
    Scanner actions_Scanner = new Scanner(System.in);

    double amount = 0.0;
//    @Override
//    public String userID {
//        return super.userID;
//    }
//
//    @Override
//    public void setUserID(String userID) {
//        super.setUserID(userID);
//    }


    public void accountActions(String userID) throws IOException {
        CheckingAccount checkingAccount = new CheckingAccount(userID);
        SavingsAccount savingsAccount = new SavingsAccount(userID);
        System.out.println("Welcome, " + getUserName());
        boolean logout = false;

        while (!logout) {
            System.out.println("Pick an Action: e.g. 1 to Deposit" + "\n1.Deposit\n2.Transfer\n3.Withdraw\n4.Check Your account Balance\n5.View Transactions History\n0. Logout");
            int action = actions_Scanner.nextInt();
            actions_Scanner.nextLine();

            switch (action) {
                case 0 -> {
                    System.out.println("Thank You for using the application");
                    System.exit(0);
                    logout=true;
                }
                case 1 -> depositActions(userID, checkingAccount, savingsAccount);
                case 2 -> transferActions(userID, checkingAccount, savingsAccount);
                case 3 -> withdrawActions(checkingAccount, savingsAccount);
                case 4 -> checkAccountBalance(userID, checkingAccount, savingsAccount);
                case 5 -> transactionsHistory(checkingAccount, savingsAccount);
                default -> System.out.println("No Action found. Try again...");
            }
        }
    }

    private void transactionsHistory(CheckingAccount checkingAccount, SavingsAccount savingsAccount) {
        System.out.println("View History for:\n1.Checking Account || 2. Savings Account");
        int choice= actions_Scanner.nextInt();
        actions_Scanner.nextLine();
        System.out.println("Filter by: today | yesterday | last 7 days | last week | last 30 days | last month | all");
        String filter= actions_Scanner.nextLine().strip();
        if (choice==1){
            System.out.println(checkingAccount.filterTransactionHistory(filter));
        }else if (choice==2){
            System.out.println(savingsAccount.filterTransactionHistory(filter));
        }else{
            System.out.println("No Action found. Try again...");
        }

    }


    private void depositActions(String userID, CheckingAccount checkingAccount, SavingsAccount savingsAccount) {
        System.out.println("1.Deposit to other account  || 2.Deposit to my account");
        int depositAction = actions_Scanner.nextInt();
        actions_Scanner.nextLine();
        if (depositAction == 1) {
            System.out.println("Enter the Account ID you wish to deposit to (Checking account Only):");
            String depositTo_ID = actions_Scanner.nextLine().toUpperCase().strip();
            System.out.println("Enter the amount that you wish to deposit:");
            double balance = actions_Scanner.nextDouble();
            actions_Scanner.nextLine();
            System.out.println(checkingAccount.depositMoney(balance, depositTo_ID));
        } else if (depositAction == 2) {
            System.out.println("Deposit to:\n1.Checking Account || 2.Savings Account");
            int depositToSavingOrChecking = actions_Scanner.nextInt();
            actions_Scanner.nextLine();
            if (depositToSavingOrChecking == 1) {
                System.out.println("Enter the amount that you wish to deposit:");
                double balance = actions_Scanner.nextDouble();
                actions_Scanner.nextLine();
                System.out.println(checkingAccount.depositMoney(balance, checkingAccount.checkingAccountID(userID)));

            } else if (depositToSavingOrChecking == 2) {
                System.out.println("Enter the amount that you wish to deposit:");
                double balance = actions_Scanner.nextDouble();
                actions_Scanner.nextLine();
                System.out.println(savingsAccount.depositMoney(balance, savingsAccount.savingAccountID(userID)));
            } else {
                System.out.println("Action not found, please try again.");

            }
        }
    }

    private void transferActions(String userID, CheckingAccount checkingAccount, SavingsAccount savingsAccount) {
        System.out.println("1.Transfer to other account  || 2.Transfer to my account");
        int transferAction = actions_Scanner.nextInt();
        actions_Scanner.nextLine();
        if (transferAction == 1) {
            System.out.println("1.Transfer from Checking account || 2. Transfer From Savings account");
            int fromWhichAcct = actions_Scanner.nextInt();
            actions_Scanner.nextLine();
            System.out.println("Enter the Account ID you wish to Transfer to:");
            String transferTo_ID = actions_Scanner.nextLine().toUpperCase().strip();
            System.out.println("Enter the amount that you wish to transfer:");
            double balance = actions_Scanner.nextDouble();
            actions_Scanner.nextLine();
            if (fromWhichAcct == 1) {
                System.out.println(checkingAccount.transferMoney(balance, transferTo_ID));
            } else if (fromWhichAcct == 2) {
                System.out.println(savingsAccount.transferMoney(balance, transferTo_ID));
            } else{
                System.out.println("Action not found, please try again.");
            }
        }else if (transferAction==2){
            System.out.println("1.Checking -> Savings || 2.Savings -> Checking");
            int transferActownAct= actions_Scanner.nextInt();
            actions_Scanner.nextLine();
            System.out.println("Enter the amount that you wish to transfer:");
            double balance = actions_Scanner.nextDouble();
            actions_Scanner.nextLine();
            if (transferActownAct==1){
                System.out.println(checkingAccount.transferMoney(balance, savingsAccount.savingAccountID(userID)));
            } else if (transferActownAct==2) {
                System.out.println(savingsAccount.transferMoney(balance, checkingAccount.checkingAccountID(userID)));
            }else{
                System.out.println("Action not found, please try again.");
            }

        }else{
            System.out.println("Action not found, please try again.");
        }
    }

    private void withdrawActions(CheckingAccount checkingAccount, SavingsAccount savingsAccount) {
        System.out.println("1.Withdraw from checking account  || 2.Withdraw from savings account");
        int withdrawAction = actions_Scanner.nextInt();
        actions_Scanner.nextLine();
        System.out.println("Enter the amount that you wish to withdraw:");
        double balance = actions_Scanner.nextDouble();
        actions_Scanner.nextLine();
        if (withdrawAction == 1) {
            System.out.println(checkingAccount.withdrawMoney(balance));
        } else if (withdrawAction == 2) {
            System.out.println(savingsAccount.withdrawMoney(balance));
        } else {
                System.out.println("Action not found, please try again.");

            }
        }
    private void checkAccountBalance(String userID, CheckingAccount checkingAccount, SavingsAccount savingsAccount) throws IOException {
        System.out.println("Check Balance for:\n1.Checking Account || 2. Savings Account || 3.Both");
        int checking_choice=actions_Scanner.nextInt();
        actions_Scanner.nextLine();
        if (checking_choice==1 || checking_choice==3){
            System.out.println(checkingAccount.getActBalance(userID,"checking"));
        }
        if (checking_choice==2 || checking_choice==3){
            System.out.println(savingsAccount.getActBalance(userID, "saving"));
        }
        if (checking_choice != 1 && checking_choice != 2 && checking_choice != 3) {
            System.out.println("Action not found, please try again.");
        }

    }


    }


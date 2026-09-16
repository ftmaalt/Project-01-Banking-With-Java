package com.ga.acmebank.account;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

public abstract class Account implements BankAccount {
    protected double acct_balance = 0.0;
    protected final String userID;
    LocalDateTime transaction_time;

    protected Account(String userID) {
        this.userID = userID;
    }

    public double getAcct_balance() {
        return acct_balance;
    }

    public void setAcct_balance(double acct_balance) {
        this.acct_balance = acct_balance;
    }

    protected abstract String section();
    protected abstract boolean matchesActType(String actType);
    protected abstract String ownActID();
    protected boolean hasWithdrawsRemaining(){
        return true;
    }
    protected void onWithdrawalOrTransfer(){

    }


    @Override
    public double getActBalance(String userID, String actType) throws IOException {
        return 0;
    }
    protected boolean allowOverDraft(){
        return true;
    }

    @Override
    public void updateCurrentBalance(String actID, double balance) {

    }

    @Override
    public String addToTransactionHistory(LocalDateTime dateTime, String transaction_type, String fromAct, String toAct, double transaction_balance, double balancePostTransaction) {
            try {
                Path file = AccountFileHelper.findFileFromId(userID)
                        .orElseThrow(() -> new IOException("User file not found."));
                String line = AccountFileHelper.formatTransaction(dateTime, transaction_type, fromAct, toAct,
                        transaction_balance, balancePostTransaction);
                AccountFileHelper.addNewTransaction(file, section(), line);
                return line;
            } catch (IOException e) {
                return "";
            }
        }


    @Override
    public String UpdateCardType() {
        return "";
    }

    @Override
    public String depositMoney(double balance, String iD) {
        if (balance<=0) return "Deposit cannot be performed when deposited balance is negative";
        try{
            boolean ownAcct=userID.equals(ownActID());
            Path file;
            if (ownAcct){
                file=AccountFileHelper.findFileFromId(iD).orElse(null);
            }else{
                file= AccountFileHelper.findFileFromAccountID(iD).orElse(null);
            }
            if (file==null){
                return "Account with ID: "+ iD +" not found.";
            }
            double currentBalance= AccountFileHelper.readBalance(file, section());
            double newBalance= currentBalance+ balance;


            transaction_time= LocalDateTime.now();
            AccountFileHelper.updateBalance(file, section(), newBalance);
            AccountFileHelper.addNewTransaction(file, section(), AccountFileHelper.formatTransaction(transaction_time,"Deposit", iD,iD, balance,newBalance));



            if (ownAcct)
                acct_balance= newBalance;
            return "Deposit Successful. Deposited Amount: "+balance+ " to Account "+ iD;
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public String withdrawMoney(double balance) {
        if (!hasWithdrawsRemaining())
            return "Sorry, You have reached your withdraw/transfer limit for this month";
        if (balance<=0)
            return "Withdrawl amount must be positive.";
        // overdraft
        try{
            Path file= AccountFileHelper.findFileFromId(userID).orElseThrow(()->new IOException("File not found"));
            String myActID= ownActID();
            double currentBalance= AccountFileHelper.readBalance(file, section());
            if (currentBalance<0 && balance>100){
                return "Error performing withdrawal. Please resolve any OverDraw in your account and try again.";
            }
            double newBalance= currentBalance- balance;

            if(newBalance<0 && !allowOverDraft()){
                return "Error performing withdrawal. You have insufficient funds.";
            }
            // another overdraft action
            transaction_time= LocalDateTime.now();
            AccountFileHelper.updateBalance(file, section(), newBalance);
            AccountFileHelper.addNewTransaction(file, section(), AccountFileHelper.formatTransaction(transaction_time,"Withdraw", myActID,myActID, balance,newBalance));
            acct_balance=newBalance;
            onWithdrawalOrTransfer();
            return "Withdrawal Successful. Withdrawn Amount:"+balance+ ".New Account Balance: "+ newBalance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String transferMoney(double balance, String accountID) {
        if (!hasWithdrawsRemaining())
            return "Sorry, You have reached your withdraw/transfer limit for this month";
        if (balance<=0)
            return "Transfer amount must be positive.";
        // overdraft
        try{
            Path fromFile = AccountFileHelper.findFileFromId(userID).orElseThrow(()->new IOException("File not found"));
            String myActID= ownActID();
            double currentFromBalance= AccountFileHelper.readBalance(fromFile, section());
            if (currentFromBalance<0 && balance>100){
                return "Error performing withdrawal. Please resolve any OverDraw in your account and try again.";
            }
            Path toFile= AccountFileHelper.findFileFromId(accountID).orElse(null);
            if (toFile==null){
                return "Error finding the account you are trying to transfer to. Please try again.";
            }
            String toSection= AccountFileHelper.sectionFinderByID(accountID);
            double newFromBalance= currentFromBalance- balance;

            if(newFromBalance<0 && !allowOverDraft()){
                return "Error performing withdrawal. You have insufficient funds.";
            }
            // apply overdraft action
            transaction_time= LocalDateTime.now();
            AccountFileHelper.updateBalance(fromFile, section(), newFromBalance);

            double toBalance= AccountFileHelper.readBalance(toFile, toSection);
            double newToBalance= toBalance+balance;
            AccountFileHelper.updateBalance(toFile, toSection, newToBalance);

            AccountFileHelper.addNewTransaction(fromFile, section(), AccountFileHelper.formatTransaction(transaction_time,"Transfer Out", myActID,accountID, balance,newFromBalance));
            acct_balance=newFromBalance;
            onWithdrawalOrTransfer();
            AccountFileHelper.addNewTransaction(toFile, toSection, AccountFileHelper.formatTransaction(transaction_time,"Transfer In", myActID,accountID, balance,newToBalance));

            return "Transfer Successful. Transferred Amount:"+balance+ " to Account: "+accountID+".New Account Balance: "+ newFromBalance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}


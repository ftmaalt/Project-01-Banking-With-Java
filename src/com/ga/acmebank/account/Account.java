package com.ga.acmebank.account;

import com.ga.acmebank.cards.Card;
import com.ga.acmebank.cards.MasterCard;
import com.ga.acmebank.cards.MasterCardPlatinum;
import com.ga.acmebank.cards.MasterCardTitanium;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public abstract class Account implements BankAccount {
    protected double acct_balance = 0.0;
    protected final String userID;
    LocalDateTime transaction_time;

    protected Account(String userID) {
        this.userID = userID;
    }



    protected abstract String section();

    protected abstract boolean matchesActType(String actType);

    protected abstract String ownActID();

    protected boolean hasWithdrawsRemaining(){
        return true;
    }

    protected void onWithdrawalOrTransfer(){

    }
    protected boolean allowOverDraft(){
        return true;
    }



    protected boolean activationRequired(){
        return false;
    }
    protected double activationBalance(){
        return 0;
    }


    @Override
    public double getActBalance(String userID, String actType) throws IOException {
        if(!matchesActType(actType)) return 0;
        Path file= AccountFileHelper.findFileFromId(userID).orElseThrow(()->new IOException("File not found."));
        acct_balance=AccountFileHelper.readBalance(file, section());
        return acct_balance;
    }



    @Override
    public void updateCurrentBalance(String actID, double balance) {
        try {
            Path file = AccountFileHelper.findFileFromId(userID)
                    .orElseThrow(() -> new IOException("User file not found."));
            AccountFileHelper.updateBalance(file, section(), balance);
            acct_balance=balance;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
// returns current card name
        protected String checkCardName() {
            String card = "MasterCard";
            try (Scanner fileReader = new Scanner(new File("data/users.txt"))) {
                while (fileReader.hasNextLine()) {
                    String data = fileReader.nextLine();
                    String[] userInfoSplitter = data.split("\\s*\\|\\|\\s*");
                    if (userInfoSplitter.length > 4 && userID.equals(userInfoSplitter[0])) {
                        card = userInfoSplitter[4];
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Couldn't Find card.");
            }
            return card;
        }


        protected Card checkCard(){
            return switch (checkCardName()){
                case "MasterCardTitanium" -> new MasterCardTitanium();
                case "MasterCardPlatinum" -> new MasterCardPlatinum();
                default-> new MasterCard();
            };
        }


    @Override
    public String depositMoney(double balance, String iD) {
        if (balance<=0) return "Deposit cannot be performed when deposited balance is negative";
        try{
            boolean ownAcct=iD.equals(ownActID());
            Path file;
            if (ownAcct){
                file=AccountFileHelper.findFileFromId(userID).orElse(null);
            }else{
                file= AccountFileHelper.findFileFromAccountID(iD).orElse(null);
            }
            if (file==null){
                return "Account with ID: "+ iD +" not found.";
            }
            Card card= checkCard();
            boolean ownAcctDeposit = file.equals(AccountFileHelper.findFileFromId(userID).orElse(null));
            double depositLimit;
            if (ownAcctDeposit){
                depositLimit=card.getDeposit_limit();
            }else{
                depositLimit=card.getDepositDifAccount_limit();
            }


            double depositToday = AccountFileHelper.accumulatedDailyDeposit(file, section(), Set.of("Deposit"));
            if (depositToday + balance> depositLimit){
                return "You have exceeded your deposit limit today. Please try again tomorrow.";
            }
            double currentBalance= AccountFileHelper.readBalance(file, section());
            double newBalance= currentBalance+ balance;

            transaction_time= LocalDateTime.now();
            AccountFileHelper.updateBalance(file, section(), newBalance);
            AccountFileHelper.addNewTransaction(file, section(), AccountFileHelper.formatTransaction(transaction_time,"Deposit", iD,iD, balance,newBalance));

            // resolve any overDraft and reactivate account
            if (ownAcct) {
                new OverdraftActions().reactivateResolved(userID, section(), newBalance);
                acct_balance = newBalance;
            }
            String cardUpdateStatus = UpdateCardType();
            if (cardUpdateStatus.startsWith("Congratulations")) {
                return "Deposit Successful. Deposited Amount: " + balance + " to Account " + iD + "\n" + cardUpdateStatus;
            }
            return "Deposit Successful. Deposited Amount: " + balance + " to Account " + iD;
        } catch (IOException e) {
        return "There was an error while performing this action, please try again...";
        }
    }

    @Override
    public String withdrawMoney(double balance) {
       if (!hasWithdrawsRemaining())
            return "Sorry, You have reached your withdraw/transfer limit for this month";
        if (balance<=0)
            return "Withdrawal amount must be positive.";
        try{
            OverdraftActions overDraft= new OverdraftActions();
            if (overDraft.isDeactivated(userID,section())){
                return "Account is deactivated due to overdraft. Please resolve the negative balance and try again.";
            }
            Path file= AccountFileHelper.findFileFromId(userID).orElseThrow(()->new IOException("File not found"));
            String myActID= ownActID();
            Card card= checkCard();
            double withdrawnToday= AccountFileHelper.accumulatedDailyDeposit(file, section(), Set.of("Withdraw"));
            if (withdrawnToday+ balance> card.getWithdraw_limit()){
                return "You have exceeded your withdraw limit today. Please try again tomorrow.";
            }

            double currentBalance= AccountFileHelper.readBalance(file, section());
            OverdraftActions overdraft = new OverdraftActions();
            boolean active = !overdraft.isDeactivated(userID, section());
            if (activationRequired() && !active && currentBalance < activationBalance()) {
                return "Please deposit a minimum of " + activationBalance() + " to activate this account.";
            }
            if (currentBalance<0 && balance>100){
                return "Error performing withdrawal. Please resolve any OverDraw in your account and try again.";
            }
            double newBalance= currentBalance- balance;

            if(newBalance<0 && !allowOverDraft()){
                return "Error performing withdrawal. You have insufficient funds.";
            }
            if (newBalance<0){
                newBalance= overDraft.applyOverDraftFree(userID, section(), newBalance);

            }
            transaction_time= LocalDateTime.now();
            AccountFileHelper.updateBalance(file, section(), newBalance);
            AccountFileHelper.addNewTransaction(file, section(), AccountFileHelper.formatTransaction(transaction_time,"Withdraw", myActID,myActID, balance,newBalance));
            acct_balance=newBalance;
            onWithdrawalOrTransfer();
            return "Withdrawal Successful. Withdrawn Amount:"+balance+ ".New Account Balance: "+ newBalance;
        } catch (IOException e) {
            return "There was an error while performing this action, please try again...";
        }
    }

    @Override
    public String transferMoney(double balance, String accountID) {
        if (!hasWithdrawsRemaining())
            return "Sorry, You have reached your withdraw/transfer limit for this month";
        if (balance<=0)
            return "Transfer amount must be positive.";
        try{
            OverdraftActions overDraft= new OverdraftActions();
            if (overDraft.isDeactivated(userID, section())){
                return "Account is deactivated due to overdraft. Please resolve the negative balance and try again.";
            }
            Path fromFile = AccountFileHelper.findFileFromId(userID).orElseThrow(()->new IOException("File not found"));
            String myActID= ownActID();
            double currentFromBalance= AccountFileHelper.readBalance(fromFile, section());

            OverdraftActions overdraft = new OverdraftActions();
            boolean active = !overdraft.isDeactivated(userID, section());
            if (activationRequired() && !active && currentFromBalance < activationBalance()) {
                return "Please deposit a minimum of " + activationBalance() + " to activate this account.";
            }
            if (currentFromBalance<0 && balance>100){
                return "Error performing Transfer. Please resolve any OverDraw in your account and try again.";
            }
            Path toFile= AccountFileHelper.findFileFromAccountID(accountID).orElse(null);
            if (toFile==null){
                return "Error finding the account you are trying to transfer to. Please try again.";
            }
            Card card= checkCard();
            boolean ownAcctTransfer= toFile.equals(AccountFileHelper.findFileFromId(userID).orElse(null));

            double transferLimit;
            if (ownAcctTransfer){
                transferLimit=card.getTransfer_limit();
            }else{
                transferLimit=card.getTransferDifAccount_limit();
            }
            double transferredToday = AccountFileHelper.accumulatedDailyDeposit(fromFile, section(), Set.of("Transfer Out"));
            if (transferredToday + balance> transferLimit){
                return "Your Transfer Limit has been exceeded for today.";
            }
            String toSection= AccountFileHelper.sectionFinderByID(accountID);


           double newFromBalance= currentFromBalance- balance;

            if(newFromBalance<0 && !allowOverDraft()){
                return "Error performing transfer. You have insufficient funds.";
            }
            if (newFromBalance<0){
                newFromBalance= overDraft.applyOverDraftFree(userID, section(), newFromBalance);
            }
            transaction_time= LocalDateTime.now();
            AccountFileHelper.updateBalance(fromFile, section(), newFromBalance);


            // update the reciever and sender amounts and files
            double toBalance= AccountFileHelper.readBalance(toFile, toSection);
            double newToBalance= toBalance+balance;
            AccountFileHelper.updateBalance(toFile, toSection, newToBalance);

            AccountFileHelper.addNewTransaction(fromFile, section(), AccountFileHelper.formatTransaction(transaction_time,"Transfer Out", myActID,accountID, balance,newFromBalance));
            acct_balance=newFromBalance;
            onWithdrawalOrTransfer();
            AccountFileHelper.addNewTransaction(toFile, toSection, AccountFileHelper.formatTransaction(transaction_time,"Transfer In", myActID,accountID, balance,newToBalance));

            return "Transfer Successful. Transferred Amount:"+balance+ " to Account: "+accountID+".New Account Balance: "+ newFromBalance;
        } catch (Exception e) {
            return "There was an error while performing this action, please try again...";
        }
    }

    public String filterTransactionHistory(String filterBy){
        try{
            Path file= AccountFileHelper.findFileFromId(userID).orElseThrow(()->new IOException("File not found."));
            List<String> results= switch ((filterBy.toLowerCase())){
                case "today" ->AccountFileHelper.filterByToday(file,section());
                case "yesterday" ->AccountFileHelper.filterByYesterday(file,section());
                case "last 7 days" ->AccountFileHelper.filterByLast7Days(file,section());
                case "last week" -> AccountFileHelper.filterByLastWeek(file, section());
                case "last 30 days" -> AccountFileHelper.filterByLast30Days(file, section());
                case "last month" ->AccountFileHelper.filterByLastMonth(file,section());
                case "all"-> AccountFileHelper.getAllTransactions(file,section());
                default -> throw new IllegalStateException("Unexpected value: " + (filterBy.toLowerCase()));
            };
            if (results.isEmpty()) return "No transactions found for: " + filterBy;
            return String.join("\n",results);
        } catch (IOException e) {
            return "There was an error while performing this action, please try again...";
        }

    }
    public String detailedStatement() {
        try {
            Path file = AccountFileHelper.findFileFromId(userID).orElseThrow(() -> new IOException("File not found."));
            double balance = AccountFileHelper.readBalance(file, section());
            List<String> allTransactions = AccountFileHelper.getAllTransactions(file, section());
            StringBuilder sb = new StringBuilder();
            sb.append("======= Account Statement: ").append(ownActID()).append(" =======\n");
//            sb.append("Current Balance: ").append(String.format("%.2f", balance)).append("\n");
//            sb.append("DATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE\n");
            if (allTransactions.isEmpty()) {
                sb.append("No transactions recorded yet.");
            } else {
                for (String t : allTransactions) {
                    sb.append(t).append("\n");
                }
            }
            return sb.toString();
        } catch (IOException e) {
            return "There was an error generating the statement, please try again...";
        }
    }


}


package com.ga.acmebank.account;

import java.io.IOException;
import java.time.LocalDateTime;

public interface BankAccount {
    String defaultcard= "MasterCard";
    double getActBalance(String userID, String actType) throws IOException;
    void updateCurrentBalance(String actID, double balance);
    String addToTransactionHistory(LocalDateTime dateTime, String transaction_type, String fromAct, String toAct, double transaction_balance, double balancePostTransaction);
    String UpdateCardType();
    String depositMoney(double balance, String userID);
    String withdrawMoney(double balance);
    String transferMoney(double balance,String accountID );

}

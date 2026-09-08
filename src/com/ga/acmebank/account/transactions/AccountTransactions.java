package com.ga.acmebank.account.transactions;

public interface AccountTransactions {
    double withdrawMoney(double balance);
    double depositMoney(double balance);
    double transferMoney(String toAccount, double balance);
}

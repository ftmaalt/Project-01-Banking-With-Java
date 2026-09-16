package com.ga.acmebank;

import java.io.IOException;

public interface UserAccountActions {
    void accountActions(String userID) throws IOException;
    boolean createSavingsAccount();
}

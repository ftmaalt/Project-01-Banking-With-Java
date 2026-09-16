package com.ga.acmebank.account;

import com.ga.acmebank.cards.Card;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import java.time.*;

public class CheckingAccount extends Account {

    public static String section_header=AccountFileHelper.CH_Section_Header;

    // constructor
    public CheckingAccount(String userID) {
        super(userID);
    }

    @Override
    protected boolean matchesActType(String actType) {
        if (actType.equalsIgnoreCase("Checking")|| actType.equalsIgnoreCase("Checkings")){
            return true;
        }
        return false;
    }

    @Override
    protected String ownActID() {
        return checkingAccountID(userID);
    }

    @Override
    protected String section() {
        return section_header;
    }

    @Override
    public String checkingAccountID(String userID) {
        String userCHID = null;
        try (Scanner fileReader = new Scanner(new File("data/users.txt"))) {
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                String[] userinfoSplitter = data.split("\\s*\\|\\|\\s*");
                if (userID.equals(userinfoSplitter[0])) {
                    userCHID = userinfoSplitter[5];
                    return userCHID;
                }
            }
        } catch (Exception e) {
            System.out.println("Account Not Found.");
            throw new RuntimeException(e);
        }

        return userCHID;
    }


}

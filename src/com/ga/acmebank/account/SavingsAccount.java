package com.ga.acmebank.account;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.io.*;
import java.util.stream.Stream;

// needs to have a method that saves the amount in the user file
public class SavingsAccount extends Account{
    public static String section_header=AccountFileHelper.SV_Section_Header;
    int withdrawLimit=6;
    public SavingsAccount(String userID) {
        super(userID);
    }
    @Override
    protected boolean matchesActType(String actType) {
        if (actType.equalsIgnoreCase("Saving")|| actType.equalsIgnoreCase("Savings")){
            return true;
        }
        return false;
    }

    @Override
    protected String ownActID() {
        return savingAccountID(userID);
    }

    @Override
    protected String section() {
        return section_header;
    }


    public String savingAccountID(String userID){
        // only returns true if savings account has balance in it/ otherwise the account does not exist even if the ID is there.
        String userSVID=null;
        try(Scanner fileReader= new Scanner(new File("data/users.txt"))) {
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                String[] userinfoSplitter = data.split("\\s*\\|\\|\\s*");
                if (userID.equals(userinfoSplitter[0])) {

//                    if (!AccountFileHelper.readBalance(AccountFileHelper.findFileFromAccountID(userID),section() )) { //should check if account has balance
                        userSVID = userinfoSplitter[6];
                        return userSVID;
//                    } else {
//                        System.out.println("You don't have a savings account, would you like to create one?");
//                        return userSVID;
//                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Account Not Found.");
            throw new RuntimeException(e);
        }

        return userSVID;
    }


}

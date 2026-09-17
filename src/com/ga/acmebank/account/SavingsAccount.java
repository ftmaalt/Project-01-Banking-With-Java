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
    private final static int M_withdrawLimit=6;
    private int withdrawLimit= M_withdrawLimit;
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

    @Override
    protected boolean hasWithdrawsRemaining() {
        return withdrawLimit>0;
    }

    @Override
    protected void onWithdrawalOrTransfer() {
        withdrawLimit--;
    }

    @Override
    protected boolean allowOverDraft() {
        return false;
    }

    public String savingAccountID(String userID){
        String userSVID="";
        try(Scanner fileReader= new Scanner(new File("data/users.txt"))) {
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                String[] userinfoSplitter = data.split("\\s*\\|\\|\\s*");
                if (userinfoSplitter.length > 6 && userID.equals(userinfoSplitter[0])) {

                        userSVID = userinfoSplitter[6];
                        return userSVID;
                }
            }
        } catch (Exception e) {
            System.out.println("Account Not Found.");
            throw new RuntimeException(e);
        }

        return userSVID;
    }

    @Override
    protected double activationBalance() {
        return 100;
    }

    @Override
    protected boolean activationRequired() {
        return true;
    }

    @Override
    protected String checkCardName() {
        return"MasterCard";
    }

    @Override
    public String UpdateCardType() {
        return "";
    }
}
